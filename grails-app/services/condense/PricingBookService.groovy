package condense

import grails.transaction.Transactional
import groovy.transform.Synchronized;

import org.hibernate.SessionFactory

@Transactional
class PricingBookService {
	
	SessionFactory sessionFactory
	def propertyInstanceMap = org.codehaus.groovy.grails.plugins.DomainClassGrailsPlugin.PROPERTY_INSTANCE_MAP
	
	@Transactional
	@Synchronized
	def importPricingBook(Date inEffectFrom, String csvFileContent) {
		this.cleanUpGorm()
		
		def newPricingBook = new PricingBook(fromDate: inEffectFrom)//.save flush: true
		
		def allCategories = Category.list()
		def allSubCategories = Subcategory.list()
		def allRegions = Region.list()
		def allProducts = Product.list()
		
		def newCategories = []
		def newSubcategories = []
		def newRegions = []
		def newProductsRepresentations = []
		def newTierDefinitionRepresentations = []
		
		def firstRowSkipped = false;
		csvFileContent.eachCsvLine { tokens ->
			if (firstRowSkipped) {
				def categoryName
				def newCategory = new Category(name: tokens[1])
				if (!allCategories.contains(newCategory)) {
					newCategories << newCategory
				}
				
				def newSubcategory = new Subcategory(name: tokens[2])
				if(!allSubCategories.contains(newSubcategory)) {
					newSubcategories << newSubcategory
				}
				
				tokens[3] = tokens[3].isEmpty() ? "Unspecified region" : tokens[3]
				def newRegion = new Region(name: tokens[3])
				if (!allRegions.contains(newRegion)) {
					newRegions << newRegion
				}
				
				def existingProduct = allProducts.find {it.guid == tokens[5]}
				def existingProductRepresentation = newProductsRepresentations.find { it.guid == tokens[5] }
				if (existingProduct == null && existingProductRepresentation == null) {
					newProductsRepresentations << [
						name: tokens[4],
						guid: tokens[5],
						categoryName: tokens[1],
						subcategoryName: tokens[2],
						regionName: tokens[3],
					]
				}
				
				newTierDefinitionRepresentations << [
						includedQuantity: tokens[7],
						startQuantity: tokens[8],
						price: tokens[23],
						productGuid: tokens[5]
					]
			}
			firstRowSkipped = true
		}
		
		//start saving the new entities that are acting as Owning type of entities
		newCategories.eachWithIndex  { newEntity, i ->
			newEntity.save()
			if (i % 100 == 0) {
				cleanUpGorm()
			}
		}
		
		newSubcategories.eachWithIndex  { newEntity, i ->
			newEntity.save()
			if (i % 100 == 0) {
				cleanUpGorm()
			}
		}
		
		newRegions.eachWithIndex  { newEntity, i ->
			newEntity.save()
			if (i % 100 == 0) {
				cleanUpGorm()
			}
		}
		
		// update the Entities lists (the "all" lists), but only if needed
		if (newCategories.size() > 0) {
			allCategories = Category.list()
		}
		
		if (newSubcategories.size() > 0) {
			allSubCategories = Subcategory.list()
		}
		
		if (newRegions.size() > 0) {
			allRegions = Region.list()
		}
		
		// now we can build and save product entities as they are belong (are owned by) to the Owning ones
		newProductsRepresentations.eachWithIndex  { newEntity, i ->
			def newProduct = new Product(name: newEntity.name, guid: newEntity.guid)
			
			def newProductCategory = allCategories.find { it['name'] == newEntity.categoryName }
			def newProductSubcategory = allSubCategories.find { it['name'] == newEntity.subcategoryName }
			def newProductRegion = allRegions.find { it['name'] == newEntity.regionName }
			
			newProduct.category = newProductCategory
			newProduct.subcategory = newProductSubcategory
			newProduct.region = newProductRegion
			
			newProduct.save(failOnError: true)
					
			if (i % 100 == 0) {
				cleanUpGorm()
			}
		}
		
		// update the allProducts list if needed
		if (newProductsRepresentations.size() > 0) {
			allProducts = Product.list()
		}
		
		//ok, we can finally build all the tiers
		newTierDefinitionRepresentations.eachWithIndex { newEntity, i ->
			def tierProduct = allProducts.find { it.guid ==  newEntity.productGuid }
			def newTier = new TierDefinition(includedQuantity: newEntity.includedQuantity,
				startQuantity: newEntity.startQuantity, price: newEntity.price, product: tierProduct)
			//newTier.save(failOnError: true)
//			if (i % 100 == 0) {
//				cleanUpGorm()
//			}
			newPricingBook.addToTierDefinitions(newTier)
		}
		
		newPricingBook.save flush:true
		
		return newPricingBook
	}
	
	@Synchronized
	def cleanUpGorm() {
		def session = sessionFactory.currentSession
		session.flush()
		session.clear()
		propertyInstanceMap.get().clear()
	}
}

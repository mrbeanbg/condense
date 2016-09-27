package condense

import grails.transaction.Transactional

@Transactional
class PricingBookService {
	
	def sessionFactory
	def propertyInstanceMap = org.codehaus.groovy.grails.plugins.DomainClassGrailsPlugin.PROPERTY_INSTANCE_MAP

    def importPricingBook(Date inEffectFrom, String csvFileContent) {
		this.cleanUpGorm()
		
		def newPricingBook = new PricingBook(fromDate: inEffectFrom).save flush: true
		
		def allCategories = Category.list()
		def allSubCategories = Subcategory.list()
		def allRegions = Region.list()
		def allProducts = Product.list()
		print allProducts
		def firstRowSkipped = false;
		
		csvFileContent.eachCsvLine { tokens ->
			if (firstRowSkipped) {
				def newCategory = new Category(name: tokens[1])
				if (!allCategories.contains(newCategory)) {
					newCategory.save()
					allCategories << newCategory
				}
				def newSubcategory = new Subcategory(name: tokens[2])
				if(!allSubCategories.contains(newSubcategory)) {
					newSubcategory.save()
					allSubCategories << newSubcategory
				}
				tokens[3] = tokens[3].isEmpty() ? "Unspecified region" : tokens[3]
				def newRegion = new Region(name: tokens[3])
				if (!allRegions.contains(newRegion)) {
					newRegion.save()
					allRegions << newRegion
				}
				
				def newProduct = new Product(name: tokens[4], guid: tokens[5])
				
				if (!allProducts.contains(newProduct)) {
					
					def newProductCategory = allCategories.find { it.name == tokens[1]}
					def newProductSubcategory = allSubCategories.find { it.name == tokens[2]}
					def newProductRegion = allRegions.find { it.name == tokens[3]}
					
					newProduct.category = newProductCategory
					newProduct.subcategory = newProductSubcategory
					newProduct.region = newProductRegion
					
					newProduct.save()
					allProducts << newProduct
				}
				
				def tierProduct = allProducts.find { it.guid ==  tokens[5]}
				def newTier = new TierDefinition(includedQuantity: tokens[7],
					startQuantity: tokens[8], price: tokens[23], product: tierProduct,
					pricingBook: newPricingBook)
				//newTier.save flush: true
				newPricingBook.addToTierDefinitions(newTier)
			}
			firstRowSkipped = true
		}
		
		newPricingBook.save flush:true
		
		this.cleanUpGorm()
		
		return newPricingBook
    }
	
	def cleanUpGorm() {
		def session = sessionFactory.currentSession
		session.flush()
		session.clear()
		propertyInstanceMap.get().clear()
	}
}

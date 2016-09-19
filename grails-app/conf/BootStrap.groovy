import condense.PricingBookService
import condense.Category
import condense.Region
import condense.Subcategory

class BootStrap {
	
	PricingBookService pricingBookService

    def init = { servletContext ->
		new Category(name: "Cloud Services").save()
		new Subcategory(name: "API Management").save()
		new Region(name: "AU East").save()
		
		//pricingBookService.importPricingBook(new Date())
    }
    def destroy = {
    }
}

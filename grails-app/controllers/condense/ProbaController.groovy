package condense

class ProbaController {
	PricingBookService pricingBookService

    def index() {
		return pricingBookService.importPricingBook(new Date())
	}
}

class UrlMappings {

	static mappings = {
        "/$controller/$action?/$id?(.$format)?"{
            constraints {
                // apply constraints here
            }
        }

		"/" ( controller:'customer', action:'index' )
        //"/"(view:"/index")
        "500"(view:'/error')
		
		"/rest/customers"(resources: 'restCustomers') {
			"/subscriptions" (resources: 'restSubscriptions') {
				action = [GET:"index", POST:"save"]
			}
			action = [GET:"index", PUT:"update", POST:"save"]
		}
		"/rest/subscriptions" (resources: 'restSubscriptions') {
			"/billing" (resources: 'restBilling') {
				action = [GET:"index"]
			}
			"/monthly_billing" (resources: 'restMonthlyBilling') {
				action = [GET:"index"]
			}
			action = [GET:"index", PUT:"update", POST:"save"]
		}
		"/rest/supportPlans" (resources: 'restSupportPlans')
	}
}

package condense



import grails.test.mixin.*
import spock.lang.*

@TestFor(PricingSetController)
@Mock(PricingSet)
class PricingSetControllerSpec extends Specification {

    def populateValidParams(params) {
        assert params != null
        // TODO: Populate valid properties like...
        //params["name"] = 'someValidName'
    }

    void "Test the index action returns the correct model"() {

        when:"The index action is executed"
            controller.index()

        then:"The model is correct"
            !model.pricingSetInstanceList
            model.pricingSetInstanceCount == 0
    }

    void "Test the create action returns the correct model"() {
        when:"The create action is executed"
            controller.create()

        then:"The model is correctly created"
            model.pricingSetInstance!= null
    }

    void "Test the save action correctly persists an instance"() {

        when:"The save action is executed with an invalid instance"
            request.contentType = FORM_CONTENT_TYPE
            request.method = 'POST'
            def pricingSet = new PricingSet()
            pricingSet.validate()
            controller.save(pricingSet)

        then:"The create view is rendered again with the correct model"
            model.pricingSetInstance!= null
            view == 'create'

        when:"The save action is executed with a valid instance"
            response.reset()
            populateValidParams(params)
            pricingSet = new PricingSet(params)

            controller.save(pricingSet)

        then:"A redirect is issued to the show action"
            response.redirectedUrl == '/pricingSet/show/1'
            controller.flash.message != null
            PricingSet.count() == 1
    }

    void "Test that the show action returns the correct model"() {
        when:"The show action is executed with a null domain"
            controller.manage(null)

        then:"A 404 error is returned"
            response.status == 404

        when:"A domain instance is passed to the show action"
            populateValidParams(params)
            def pricingSet = new PricingSet(params)
            controller.manage(pricingSet)

        then:"A model is populated containing the domain instance"
            model.pricingSetInstance == pricingSet
    }

    void "Test that the edit action returns the correct model"() {
        when:"The edit action is executed with a null domain"
            controller.edit(null)

        then:"A 404 error is returned"
            response.status == 404

        when:"A domain instance is passed to the edit action"
            populateValidParams(params)
            def pricingSet = new PricingSet(params)
            controller.edit(pricingSet)

        then:"A model is populated containing the domain instance"
            model.pricingSetInstance == pricingSet
    }

    void "Test the update action performs an update on a valid domain instance"() {
        when:"Update is called for a domain instance that doesn't exist"
            request.contentType = FORM_CONTENT_TYPE
            request.method = 'PUT'
            controller.update(null)

        then:"A 404 error is returned"
            response.redirectedUrl == '/pricingSet/index'
            flash.message != null


        when:"An invalid domain instance is passed to the update action"
            response.reset()
            def pricingSet = new PricingSet()
            pricingSet.validate()
            controller.update(pricingSet)

        then:"The edit view is rendered again with the invalid instance"
            view == 'edit'
            model.pricingSetInstance == pricingSet

        when:"A valid domain instance is passed to the update action"
            response.reset()
            populateValidParams(params)
            pricingSet = new PricingSet(params).save(flush: true)
            controller.update(pricingSet)

        then:"A redirect is issues to the show action"
            response.redirectedUrl == "/pricingSet/show/$pricingSet.id"
            flash.message != null
    }

    void "Test that the delete action deletes an instance if it exists"() {
        when:"The delete action is called for a null instance"
            request.contentType = FORM_CONTENT_TYPE
            request.method = 'DELETE'
            controller.delete(null)

        then:"A 404 is returned"
            response.redirectedUrl == '/pricingSet/index'
            flash.message != null

        when:"A domain instance is created"
            response.reset()
            populateValidParams(params)
            def pricingSet = new PricingSet(params).save(flush: true)

        then:"It exists"
            PricingSet.count() == 1

        when:"The domain instance is passed to the delete action"
            controller.delete(pricingSet)

        then:"The instance is deleted"
            PricingSet.count() == 0
            response.redirectedUrl == '/pricingSet/index'
            flash.message != null
    }
}

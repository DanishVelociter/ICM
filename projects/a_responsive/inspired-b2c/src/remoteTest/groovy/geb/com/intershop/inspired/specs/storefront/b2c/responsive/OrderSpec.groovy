package geb.com.intershop.inspired.specs.storefront.b2c.responsive

import geb.com.intershop.inspired.pages.storefront.responsive.*
import geb.com.intershop.inspired.pages.storefront.responsive.account.*;
import geb.com.intershop.inspired.pages.storefront.responsive.checkout.*;
import geb.com.intershop.inspired.pages.storefront.responsive.shopping.*;
import geb.com.intershop.inspired.testdata.TestDataUsage
import geb.spock.GebReportingSpec
import spock.lang.*;

/**
 * Storefront tests for Order Management at inSPIRED,
 * based on old Smoke Tests.
 * @author Sebastian Glass
 *
 */
class OrderSpec extends GebReportingSpec implements TestDataUsage
{
    // Stack for Test: "existing Lineitems"
    private static existingPLIs = new Stack()

    def cleanup()
    {
        def zeroQuantity = "0"
        if(!existingPLIs.empty)
        {
            when: "I go to the homepage..."
                to HomePage
                at HomePage


            then: "... open basket ..."
                header.showMiniCart()
                header.viewCartMiniCart()

            and: "... and remove all line items"
                at CartPage
                while ( !existingPLIs.empty )
                {
                    def name = existingPLIs.pop()
                    productCartTable(name).removeLink.click()
                }
        }
    }

    /**
     * Checkout as Registered Customer<p>
     *
     * Old Smoke Test:
     * testCheckoutAsRegisteredConsumer(...)
     */
    def "Checkout as Registered Customer"()
    {
        when: "I go to the signIn page and log in..."
        to HomePage
        at HomePage
        pressLogIn()

        then:
        at AccountLoginPage

        when:
        login user,password

        then: "... then I'm logged in."
        at AccountPage

        when: "I go to the home page, search for product..."
        to HomePage
        at HomePage
        header.search searchTerm

        then: "... find it at the Detail Page."
        at ProductDetailPage
        lookedForSKU searchTerm

        when: "I add it to Cart..."
        addToCart()

        then: "... and check it."
        at CartPage
        checkProduct productName
        existingPLIs.push productName

        when: "Now I will checkout..."
        checkOut()

        and: "...choose Payment if asked..."
        at CheckoutPaymentPage
        cashOnDelivery()


        then: "... review my Order..."
        at CheckoutReviewPage

        when: "...and submit."
        agreeCheckBox.click()
        submitButton.click()

        then:
        at CheckoutReceiptPage
        existingPLIs.pop()

        where:
        searchTerm  << testData.get("defaultProduct.default.sku")
        productName << testData.get("defaultProduct.default.name")
      
        user        =  testData.get("checkoutUser.login.eMail")[0]
        password    =  testData.get("checkoutUser.login.password")[0]
      
    }

    /**
     * Checkout as Unregistered Customer<p>
     *
     * Old Smoke Test:
     * testCheckoutAsUnregisteredConsumer(...)
     */
    def "Checkout as Unregistered Customer"()
    {
        when: "I go to the home page, search for product..."
        to HomePage
        at HomePage
        header.search searchTerm

        then: "... find it at the Detail Page."
        at ProductDetailPage
        lookedForSKU searchTerm

        when: "I add it to Cart..."
        addToCart()

        then: "... and check it."
        at CartPage
        checkProduct productName
        existingPLIs.push productName

        when: "Now I will checkout..."
        checkOut()

        then: "...add address,..."
        at NewUserAddressPage

        when:
        fillData country,fName,lName,address,city,postal,eMail
        continueButton.click()

        then: "...choose shipping method,..."
        at CheckoutShippingPage

        when:
        continueClick()

        then:
        at CheckoutPaymentPage

        when: "...choose payment method,... "
        cashOnDelivery()

        then: "... review my Order..."
        at CheckoutReviewPage

        when: "...and submit."
        agreeCheckBox.click()
        submitButton.click()

        then:
        at CheckoutReceiptPage
        existingPLIs.pop()

        where:
        searchTerm  << testData.get("defaultProduct.default.sku")
        productName << testData.get("defaultProduct.default.name")
      
        eMail   = testData.get("RegisterIndividualCustomer.eMail")[0]
        country = testData.get("defaultUser.address.country")[0]
        fName   = testData.get("RegisterIndividualCustomer.fName")[0]
        lName   = testData.get("RegisterIndividualCustomer.lName")[0]
        address = testData.get("defaultUser.address.address1")[0]
        postal  = testData.get("defaultUser.address.zip")[0]
        city    = testData.get("defaultUser.address.city")[0]
    }
}

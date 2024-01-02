package geb.com.intershop.b2x.specs.storefront.responsive.order

import geb.com.intershop.b2x.model.storefront.responsive.demo.DemoAddress
import geb.com.intershop.b2x.model.storefront.responsive.demo.DemoProduct
import geb.com.intershop.b2x.model.storefront.responsive.demo.DemoUser
import geb.com.intershop.b2x.model.storefront.responsive.util.AddressHelper
import geb.com.intershop.b2x.pages.storefront.responsive.HomePage
import geb.com.intershop.b2x.pages.storefront.responsive.checkout.CartPage
import geb.com.intershop.b2x.pages.storefront.responsive.checkout.CheckoutPaymentPage
import geb.com.intershop.b2x.pages.storefront.responsive.checkout.CheckoutReceiptPage
import geb.com.intershop.b2x.pages.storefront.responsive.checkout.CheckoutReviewPage
import geb.com.intershop.b2x.pages.storefront.responsive.checkout.CheckoutShippingPage
import geb.com.intershop.b2x.pages.storefront.responsive.checkout.NewUserAddressPage
import geb.com.intershop.b2x.pages.storefront.responsive.shopping.ProductDetailPage
import geb.com.intershop.b2x.specs.storefront.responsive.features.Authentication
import geb.com.intershop.b2x.testdata.TestDataUsage
import geb.spock.GebReportingSpec

class OrderSpec extends GebReportingSpec implements Authentication, TestDataUsage
{
   def "Check addresses during checkout and on order details"()
    {
        when: "Create new preferred invoice Address"
                // Login as regular buyer user
                logInUser(DemoUser.ONLY_RESERVED_FOR_ORDER_SPEC.user)
                .clickSavedAddresses()

                clickAddNewAddress()
                setAddress(invoiceAddress)
                setAsPreferredInvoiceAddress()
                saveAddress()
        and: "Create new preferred shipping Address"
                clickAddNewAddress()
                setAddress(shippingAddress)
                setAsPreferredShippingAddress()
                saveAddress()
        and: "Add product to Cart"
                to HomePage
                header.search product.id
                at ProductDetailPage
                addToCart()
                at CartPage
                checkOut()
        then: "Check address on payment Page"
                at CheckoutPaymentPage
                orderAddressSummary.isShipToAddress(shippingAddress.company,shippingAddress.firstName,shippingAddress.lastName,shippingAddress.addressLine,shippingAddress.city,shippingAddress.postCode)
                orderAddressSummary.isInvoiceToAddress(invoiceAddress.company,invoiceAddress.firstName,invoiceAddress.lastName,invoiceAddress.addressLine,invoiceAddress.city,invoiceAddress.postCode)
                
        when: "Select Payment Method and continue Checkout"
                cashOnDelivery()
                
        then: "Check address on review Page"
              at CheckoutReviewPage
              orderShippingAddressSlot.isShipToAddress(shippingAddress.company,shippingAddress.firstName,shippingAddress.lastName,shippingAddress.addressLine,shippingAddress.city,shippingAddress.postCode)
              orderInvoiceAddressSlot.isInvoiceToAddress(invoiceAddress.company,invoiceAddress.firstName,invoiceAddress.lastName,invoiceAddress.addressLine,invoiceAddress.city,invoiceAddress.postCode)
        
        when: "Submit order"
              agreeCheckBox.click()
              submitButton.click()
              
        then: "Check address on receipt Page"
              at CheckoutReceiptPage
              orderShippingAddressSlot.isShipToAddress(shippingAddress.company,shippingAddress.firstName,shippingAddress.lastName,shippingAddress.addressLine,shippingAddress.city,shippingAddress.postCode)
              orderInvoiceAddressSlot.isInvoiceToAddress(invoiceAddress.company,invoiceAddress.firstName,invoiceAddress.lastName,invoiceAddress.addressLine,invoiceAddress.city,invoiceAddress.postCode)
        
        where:
            invoiceAddress  = AddressHelper.getRandomUSAddress()
            shippingAddress = AddressHelper.getRandomUSAddress()
            product         << DemoProduct.ACER_PREDATOR_G3_605.product;
            
    }

    /**
     * Checkout as Unregistered Customer
     */
    def "Checkout as Unregistered Customer"()
    {
        when: "Search for a product from a Home Page"
            to HomePage
            at HomePage
            header.search product.id

        then: "Open a product detail page and add it into a cart"
            at ProductDetailPage
            lookedForSKU product.id
            addToCart()

        when: "... and check it"
            at CartPage
            checkOut()

        then: "Add an address"
            at NewUserAddressPage
            setAddress(address, addressEmail)
            continueButton.click()

        then: "Choose shipping method"
            at CheckoutShippingPage

        when:
            continueClick()

        then:
            at CheckoutPaymentPage

        when: "Choose payment method"
            cashOnDelivery()

        then: "Review an Order"
            at CheckoutReviewPage

        when: "...and submit"
            agreeCheckBox.click()
            submitButton.click()

        then:
            at CheckoutReceiptPage

        where:
            product      = DemoProduct.ACER_PREDATOR_G3_605.product;
            address      = DemoAddress.BULGARIA_SOFIA.address;
            addressEmail = DemoUser.BUYER.user.email;

    }
}

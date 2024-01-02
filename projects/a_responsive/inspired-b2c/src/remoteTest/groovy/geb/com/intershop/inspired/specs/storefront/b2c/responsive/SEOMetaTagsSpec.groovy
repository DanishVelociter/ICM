package geb.com.intershop.inspired.specs.storefront.b2c.responsive
import geb.com.intershop.inspired.pages.storefront.responsive.*
import geb.com.intershop.inspired.pages.storefront.responsive.account.*
import geb.com.intershop.inspired.pages.storefront.responsive.checkout.*
import geb.com.intershop.inspired.pages.storefront.responsive.shopping.*
import geb.com.intershop.inspired.testdata.TestDataUsage
import geb.spock.GebReportingSpec
import spock.lang.*


/**
 * Storefront tests for SEO Meta Tags
 *
 */
class SEOMetaTagsSpec extends GebReportingSpec implements TestDataUsage
{
    /**
     * Check SEO Meta Tags on certain pages
     *
     */
    def "Check SEO Meta Tags on certain pages"()
    {
        println "*********************************************************"
        println "* SEOMetaTagsSpec: Check SEO Meta Tags on certain pages *"
        println "*********************************************************"
        
        when: "I go to the homepage"
            println "Goto Homepage"
            to HomePage
            println "Current URL is " + getBrowser().getCurrentUrl()
            
        then: "I am at the homepage"
            at HomePage
            println "Homepage showed"
            println "Current URL is " + getBrowser().getCurrentUrl()
        and: "robots meta tag is 'index,follow'"
            assert metaRobots() == "index,follow"
        
        when: "Clicking a the Cameras category link"
            println "Clicking on category " + categoryIdIndexFollow   
            clickCategoryLink(categoryIdIndexFollow)
        
        then: "I am at the Cameras category page"
            at CategoryPage
            println "CategoryPage " + categoryIdIndexFollow + " showed"
            println "Current URL is " + getBrowser().getCurrentUrl()
            withCategory(categoryIdIndexFollow)
            
        and: "The title is set and meta tags are not default values"
        assert title == categoryTitleIndexFollow
        assert metaDescription() != categoryDefaultTag
        assert metaRobots() == "index,follow"
        
		//same for category where noindex,nofollow is set in demo data
        when: "Clicking a the Computers category link"
            println "Clicking on category " + categoryIdNoindexNofollow
            clickCategoryLink(categoryIdNoindexNofollow)
        
        then: "I am at the Computers category page"
            at CategoryPage
            println "CategoryPage " + categoryIdNoindexNofollow + " showed"
            println "Current URL is " + getBrowser().getCurrentUrl()
            withCategory(categoryIdNoindexNofollow)
            
        
        and: "The title and meta tags are always set also for non-indexed pages (see IS-22640)"
        assert title == categoryTitleNoindexNofollow
        assert metaDescription() != categoryDefaultTag 
        assert metaRobots() == "noindex,nofollow"		
		
        when: "I select a filter on the category page"
            $("a[data-testing-id='" + fitlerId + "']").click()
        then: 
            at CategoryPage
            assert metaRobots() == "noindex,follow"
        
        when: "I search for products"
            header.search searchTerm
        then:
            assert metaRobots() == "noindex,follow"

        when:"I search for a specific product to see the product detail page"
            header.search productIdIndexFollow
        then: 
            at ProductDetailPage
            assert metaRobots() == "index,follow"
        
        when:"I search for a specific product to see the product detail page"
            header.search productIdNoindexNofollow
        then: 
            at ProductDetailPage
            assert metaRobots() == "noindex,nofollow"

        when: "I add the product to Cart..."
            addToCart()
        then: "... and check the meta tags."
            at CartPage
            assert metaRobots() == "noindex,nofollow"

        where:
		categoryIdIndexFollow = testData.get("seo.metaTags.category.id")[0]
        categoryTitleIndexFollow  = testData.get("seo.metaTags.category.title")[0]
		
        categoryIdNoindexNofollow = testData.get("seo.metaTags.category.id")[1]
        categoryTitleNoindexNofollow  = testData.get("seo.metaTags.category.title")[1]
		
        categoryDefaultTag = testData.get("seo.metaTags.category.defaultTags")[0]
        
        searchTerm = testData.get("seo.metaTags.searchTerm")[0]

        productIdIndexFollow  = testData.get("seo.metaTags.product.id")[0]
        productIdNoindexNofollow  = testData.get("seo.metaTags.product.id")[1]
		
        fitlerId = testData.get("seo.metaTags.filter.id")[0]
    }
	
	/**
     * Check meta tag on certain pages
     *
     */
    def "Check prev and next link on catalog pages"()
    {
        when: "I go to the signIn page and log in..."
            to HomePage
        then: "I am at the homepage"
            at HomePage
        
        when: "Clicking a the 'Cameras' category link"
            clickCategoryLink(categoryId1)
            assert linkRelNext().size() == 0
            assert linkRelPrev().size() == 0

        then: "I am at the 'Cameras' category page"
            at CategoryPage
            withCategory(categoryId1)

        and: "No prev or next links are set"
            assert linkRelNext().size() == 0
            assert linkRelPrev().size() == 0

        and: "A subcategory 'Digital Cameras' exists"
            assert subCategoryLink(categoryId2).size() > 0

        when: "Goto 'Digital Cameras' page"
            subCategoryLink(categoryId2).click()
        
        then: "I am at the 'Digital Cameras' category page"
            at CategoryPage

        and: "next tag is set (because more then 15 products are shown)"
            assert linkRelNext().size() > 0
            assert linkRelPrev().size() == 0

        where:
        categoryId1 = testData.get("seo.linkRelsPagination.category1.id")[0]
        categoryId2 = testData.get("seo.linkRelsPagination.category2.id")[0]
    }
}

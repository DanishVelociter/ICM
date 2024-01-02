package geb.com.intershop.inspired.pages.storefront.responsive.shopping

import geb.com.intershop.inspired.pages.storefront.responsive.StorefrontPage
import geb.com.intershop.inspired.pages.storefront.responsive.modules.CategoryTile

class CategoryPage extends StorefrontPage
{

    static at=
    {
       waitFor{ familyTiles.size()>0 }
    }

    static content=
    {
        contentSlot   { $("div",class:"category-page") }
        navigationBar { contentSlot.find("div",class:"category-panel") }
        categoryName  { contentSlot.@"data-testing-id" }
        categoryList  { contentSlot.$("ul",class:contains("category-list")) }
        familyTiles   { term ->  module(new CategoryTile(categoryID: term)) }
        richContent   { contentSlot.$('[data-testing-id="category-rich-content"]') }
    }

    //------------------------------------------------------------
    // Page checks
    //------------------------------------------------------------
    def withCategory(name)
    {
        categoryName==name
    }
    
    
    // select a filter with specified value - see also FamilyPage
    def selectFilterInNavigationBar(filterValue)
    {
        navigationBar.$("li",class:contains("filter-item"),text:iContains(filterValue)).find("a").click()
    }

    def clickCategoryLink(categoryId) {
        $('[data-testing-id="'+categoryId+'-link"]').click()
        waitFor{$("div", "class": "category-page", "data-testing-id":categoryId).size()>0}
    }

    def subCategoryLink(name)
    {
        return $('[data-testing-id="category-'+name+'"] a');
    }
    
    def subCategoryImage(name, image)
    {
        return $('[data-testing-id="category-image-'+name+'"]', src:contains(image));
    }
}
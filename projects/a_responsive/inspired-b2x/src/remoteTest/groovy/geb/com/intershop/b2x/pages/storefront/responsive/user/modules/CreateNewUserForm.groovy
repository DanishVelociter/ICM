package geb.com.intershop.b2x.pages.storefront.responsive.user.modules
import geb.Module
import geb.com.intershop.b2x.model.storefront.responsive.User
import geb.com.intershop.b2x.pages.storefront.responsive.user.CreateNewUserPage
import geb.com.intershop.b2x.pages.storefront.responsive.user.UsersPage
import geb.module.Select

class CreateNewUserForm extends Module {
    static content = {

        submitButton(to: [UsersPage, CreateNewUserPage]) {$("button", type: "submit", name:"Create")}
    }
        lastNameInput.value user.lastName
        loginInput.value user.password
        emailInput.value user.email
        if (user.language != null ) {
            languageSelector.selected = user.language
        }
    }
}
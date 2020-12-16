package com.testapp.utils

data class BillingIdentifiers(
        val email: String,
        val zipCode: String,
        val idNumber: String,
        val firstName: String,
        val lastName: String,
        val title: String,
        val address: String,
        val city: String,
        val state: String,
        val phone: String,
        val birthday: String,
        val birthday2: String
) {

    companion object {
        val ANDROID = BillingIdentifiers(
                "//input[@data-cid=\"am.email\"]",
                "//input[@data-cid=\"am.postal_code\"]",
                "//input[@data-cid=\"am.national_identification_number\"]",
                "//input[@data-cid=\"am.given_name\"]",
                "//input[@data-cid=\"am.family_name\"]",
                "//input[@id=\"withAutofillProps(Component)-59355024-fc88-419f-91b4-81689c73b3d2\"]",
                "//input[@data-cid=\"am.street_address\"]",
                "//input[@data-cid=\"am.city\"]",
                "//input[@data-cid=\"am.region\"]",
                "//input[@data-cid=\"am.phone\"]",
                "//input[contains(@id,'date_of_birth')]",
                "//input[contains(@id,'date-of-birth')]"
        )
        val IOS = BillingIdentifiers(
                "//XCUIElementTypeStaticText[contains(@name,'Email address')]",
                "//XCUIElementTypeStaticText[contains(@name,'Postcode')]",
                "//XCUIElementTypeStaticText[contains(@name,'ID number')]",
                "//XCUIElementTypeStaticText[contains(@name,'First Name')]",
                "//XCUIElementTypeStaticText[contains(@name,'Last Name')]",
                "//XCUIElementTypeStaticText[contains(@name,'Title')]",
                "//XCUIElementTypeOther[contains(@value,'Street name and number')]",
                "//XCUIElementTypeOther[contains(@value,'City')]",
                "//XCUIElementTypeStaticText[contains(@name,'State')]",
                "//XCUIElementTypeStaticText[contains(@name,'Mobile phone')]",
                "//XCUIElementTypeStaticText[contains(@name,'Date of Birth')]",
                "//XCUIElementTypeStaticText[contains(@name,'Date of Birth')]"
        )

        val IOS_US_UK = BillingIdentifiers(
                "//XCUIElementTypeStaticText[contains(@name,'Email address')]",
                "//XCUIElementTypeStaticText[contains(@name,'Zip code')]",
                "//XCUIElementTypeStaticText[contains(@name,'ID number')]",
                "//XCUIElementTypeStaticText[contains(@name,'First Name')]",
                "//XCUIElementTypeStaticText[contains(@name,'Last Name')]",
                "//XCUIElementTypeStaticText[contains(@name,'Title')]",
                "//XCUIElementTypeOther[contains(@value,'Address')]",
                "//XCUIElementTypeOther[contains(@value,'City')]",
                "//XCUIElementTypeStaticText[contains(@name,'State')]",
                "//XCUIElementTypeStaticText[contains(@name,'Mobile phone')]",
                "//XCUIElementTypeStaticText[contains(@name,'Date of Birth')]",
                "//XCUIElementTypeStaticText[contains(@name,'Date of Birth')]"
        )

        val IOS_SE_NO = BillingIdentifiers(
                "//XCUIElementTypeStaticText[contains(@name,'Email address')]",
                "//XCUIElementTypeStaticText[contains(@name,'Postal code')]",
                "//XCUIElementTypeStaticText[contains(@name,'ID number')]",
                "//XCUIElementTypeStaticText[contains(@name,'First Name')]",
                "//XCUIElementTypeStaticText[contains(@name,'Last Name')]",
                "//XCUIElementTypeStaticText[contains(@name,'Title')]",
                "//XCUIElementTypeOther[contains(@value,'Address')]",
                "//XCUIElementTypeOther[contains(@value,'City')]",
                "//XCUIElementTypeStaticText[contains(@name,'State')]",
                "//XCUIElementTypeStaticText[contains(@name,'Mobile phone')]",
                "//XCUIElementTypeStaticText[contains(@name,'Date of Birth')]",
                "//XCUIElementTypeStaticText[contains(@name,'Date of Birth')]"
        )


        val IOS_FI = BillingIdentifiers(
                "//XCUIElementTypeStaticText[contains(@name,'Email address')]",
                "//XCUIElementTypeStaticText[contains(@name,'Postcode')]",
                "//XCUIElementTypeStaticText[contains(@name,'ID number')]",
                "//XCUIElementTypeStaticText[contains(@name,'First Name')]",
                "//XCUIElementTypeStaticText[contains(@name,'Last Name')]",
                "//XCUIElementTypeStaticText[contains(@name,'Title')]",
                "//XCUIElementTypeOther[contains(@value,'Address')]",
                "//XCUIElementTypeOther[contains(@value,'Town or city')]",
                "//XCUIElementTypeStaticText[contains(@name,'State')]",
                "//XCUIElementTypeStaticText[contains(@name,'Mobile phone')]",
                "//XCUIElementTypeStaticText[contains(@name,'Date of Birth')]",
                "//XCUIElementTypeStaticText[contains(@name,'Date of Birth')]"
        )

    }

}
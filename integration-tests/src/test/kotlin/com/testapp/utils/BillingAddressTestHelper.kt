package com.testapp.utils

internal object BillingAddressTestHelper {
    const val EMAIL_KEY = "//input[@data-cid=\"am.email\"]"
    const val ZIPCODE_KEY = "//input[@data-cid=\"am.postal_code\"]"
    const val ID_KEY = "//input[@data-cid=\"am.national_identification_number\"]"
    const val FIRST_NAME_KEY = "//input[@data-cid=\"am.given_name\"]"
    const val LAST_NAME_KEY = "//input[@data-cid=\"am.family_name\"]"
    const val TITLE_KEY = "//input[@id=\"withAutofillProps(Component)-59355024-fc88-419f-91b4-81689c73b3d2\"]"
    const val ADDRESS_KEY = "//input[@data-cid=\"am.street_address\"]"
    const val CITY_KEY = "//input[@data-cid=\"am.city\"]"
    const val STATE_KEY = "//input[@data-cid=\"am.region\"]"
    const val PHONE_KEY = "//input[@data-cid=\"am.phone\"]"
    const val BIRTHDAY_KEY = "//input[contains(@id,'date_of_birth')]"
    const val BIRTHDAY_KEY_2 = "//input[contains(@id,'date-of-birth')]"

    const val EMAIL_FLAG_REJECTED = "+rejected"

    fun getBillingInfoSE() =
            createBillingOptions(
                    email = "test-user@inapp-test.klarna.com",
                    zipCode = "26030",
                    id = "6104084980",
                    firstName = "Myrtle",
                    lastName = "Isaacs",
                    address = "Mjölkalånga 62",
                    city = "VALLÅKRA",
                    phone = "0428193990"
            )

    fun getBillingInfoNO() =
            createBillingOptions(
                    email = "test-user@inapp-test.klarna.com",
                    zipCode = "3730",
                    id = "01087000571",
                    firstName = "Sylvia",
                    lastName = "A. Walton",
                    address = "Dag Eilivssons gate 237",
                    city = "SKIEN",
                    phone = "95525598"
            )

    fun getBillingInfoFI() =
            createBillingOptions(
                    email = "test-user@inapp-test.klarna.com",
                    zipCode = "28400",
                    id = "280486-573Y",
                    firstName = "Ralph",
                    lastName = "S. Johnson",
                    address = "Ilmalankuja 22",
                    city = "ULVILA",
                    phone = "0441370119"
            )

    fun getBillingInfoUS() =
            createBillingOptions(
                    email = "test-user@inapp-test.klarna.com",
                    zipCode = "01103",
                    firstName = "Marta",
                    lastName = "Pryor",
                    address = "2894 Leverton Cove Rd",
                    city = "Springfield",
                    state = "MA",
                    phone = "3614565348"
            )

    fun getBillingInfoUK() =
            createBillingOptions(
                    email = "test-user@inapp-test.klarna.com",
                    zipCode = "CA10 0WE",
                    firstName = "Helen",
                    lastName = "Mozingo",
                    title = "Ms",
                    address = "11 Boughton Rd",
                    city = "WICKERSLACK",
                    phone = "07981835008",
                    birthday = "13061999"
            )

    fun getBillingInfoAT() =
            createBillingOptions(
                    email = "test-user@inapp-test.klarna.com",
                    zipCode = "5442",
                    firstName = "Lena",
                    lastName = "V. Hohl",
                    title = "Ms",
                    address = "Wurmbrandgasse 82",
                    city = "SEETRATTEN",
                    phone = "06809711342",
                    birthday = "19011985"
            )

    fun getBillingInfoDE() =
            createBillingOptions(
                    email = "test-user@inapp-test.klarna.com",
                    zipCode = "83684",
                    firstName = "Olga",
                    lastName = "Jackson",
                    title = "Ms",
                    address = "Waldowstr. 39",
                    city = "Tegernsee",
                    phone = "08022249242",
                    birthday = "05041994"
            )

    private fun createBillingOptions(
            email: String,
            zipCode: String,
            id: String? = null,
            firstName: String,
            lastName: String,
            title: String? = null,
            address: String,
            city: String,
            state: String? = null,
            phone: String,
            birthday: String? = null
    ) =
            linkedMapOf(
                    EMAIL_KEY to email,
                    ZIPCODE_KEY to zipCode,
                    ID_KEY to id,
                    LAST_NAME_KEY to lastName,
                    FIRST_NAME_KEY to firstName,
                    TITLE_KEY to title,
                    ADDRESS_KEY to address,
                    CITY_KEY to city,
                    STATE_KEY to state,
                    PHONE_KEY to phone,
                    BIRTHDAY_KEY to birthday,
                    BIRTHDAY_KEY_2 to birthday
            )

    fun setEmailFlag(options: LinkedHashMap<String, String?>, flag: String): LinkedHashMap<String, String?> {
        var email = options[EMAIL_KEY]
        val emailParts = email?.split("@")
        emailParts?.let {
            email = it[0] + flag + "@" + it[1]
        }
        return options.apply { this[EMAIL_KEY] = email }
    }
}
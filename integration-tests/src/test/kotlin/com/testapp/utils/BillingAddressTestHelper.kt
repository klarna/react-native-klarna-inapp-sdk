package com.testapp.utils

import com.testapp.base.Platform

internal object BillingAddressTestHelper {

    const val EMAIL_FLAG_REJECTED = "+rejected"

    fun getBillingInfoSE() =
        createBillingOptions(
            identifiers = getIdentifiers(ios = BillingIdentifiers.IOS_SE_NO),
            options = BillingOptions(
                email = "test-user@inapp-test.klarna.com",
                zipCode = "26030",
                idNumber = "6104084980",
                firstName = "Myrtle",
                lastName = "Isaacs",
                address = "Mjölkalånga 62",
                city = "VALLÅKRA",
                phone = "0428193990"
            )
        )

    fun getBillingInfoNO() =
        createBillingOptions(
            identifiers = getIdentifiers(ios = BillingIdentifiers.IOS_SE_NO),
            options = BillingOptions(
                email = "test-user@inapp-test.klarna.com",
                zipCode = "3730",
                idNumber = "01087000571",
                firstName = "Sylvia",
                lastName = "A. Walton",
                address = "Dag Eilivssons gate 237",
                city = "SKIEN",
                phone = "95525598"
            )
        )

    fun getBillingInfoFI() =
        createBillingOptions(
            identifiers = getIdentifiers(ios = BillingIdentifiers.IOS_FI),
            options = BillingOptions(
                email = "test-user@inapp-test.klarna.com",
                zipCode = "28400",
                idNumber = "280486-573Y",
                firstName = "Ralph",
                lastName = "S. Johnson",
                address = "Ilmalankuja 22",
                city = "ULVILA",
                phone = "0441370119"
            )
        )

    fun getBillingInfoUS() =
        createBillingOptions(
            identifiers = getIdentifiers(ios = BillingIdentifiers.IOS_US_UK),
            options = BillingOptions(
                email = "test-user@inapp-test.klarna.com",
                zipCode = "01103",
                firstName = "Marta",
                lastName = "Pryor",
                address = "2894 Leverton Cove Rd",
                city = "Springfield",
                state = "MA",
                phone = "3614565348"
            )
        )

    fun getBillingInfoUK() =
        createBillingOptions(
            identifiers = getIdentifiers(ios = BillingIdentifiers.IOS_FI),
            options = BillingOptions(
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
        )

    fun getBillingInfoAT() =
        createBillingOptions(
            options = BillingOptions(
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
        )

    fun getBillingInfoDE() =
        createBillingOptions(
            options = BillingOptions(
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
        )

    private fun createBillingOptions(
        identifiers: BillingIdentifiers = getIdentifiers(),
        options: BillingOptions
    ): BillingInfo {
        return BillingInfo(identifiers, options)
    }

    fun setEmailFlag(options: BillingInfo, flag: String): BillingInfo {
        var email = options.options.email
        val emailParts = email?.split("@")
        emailParts?.let {
            email = it[0] + flag + "@" + it[1]
        }
        return options.apply { this.options.email = email }
    }

    fun getIdentifiers(
        android: BillingIdentifiers = BillingIdentifiers.ANDROID,
        ios: BillingIdentifiers = BillingIdentifiers.IOS
    ): BillingIdentifiers {
        return when (Platform.getSystemConfiguration()) {
            Platform.ANDROID -> android
            Platform.IOS -> ios
        }
    }
}
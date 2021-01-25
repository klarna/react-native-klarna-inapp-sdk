package com.testapp.utils

data class BillingInfo(
    val identifiers: BillingIdentifiers,
    val options: BillingOptions
) {
    fun linkedMap(): LinkedHashMap<String, String?> {
        return linkedMapOf(
            identifiers.email to options.email,
            identifiers.zipCode to options.zipCode,
            identifiers.idNumber to options.idNumber,
            identifiers.lastName to options.lastName,
            identifiers.firstName to options.firstName,
            identifiers.title to options.title,
            identifiers.address to options.address,
            identifiers.city to options.city,
            identifiers.state to options.state,
            identifiers.phone to options.phone,
            identifiers.birthday to options.birthday,
            identifiers.birthday2 to options.birthday
        )
    }
}

package com.testapp.utils

data class BillingOptions(
    var email: String,
    var zipCode: String,
    var idNumber: String? = null,
    var firstName: String,
    var lastName: String,
    var title: String? = null,
    var address: String,
    var city: String,
    var state: String? = null,
    var phone: String,
    var birthday: String? = null
)

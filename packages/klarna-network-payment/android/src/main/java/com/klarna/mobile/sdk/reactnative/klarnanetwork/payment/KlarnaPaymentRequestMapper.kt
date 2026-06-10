package com.klarna.mobile.sdk.reactnative.klarnanetwork.payment

import com.facebook.react.bridge.ReadableArray
import com.facebook.react.bridge.ReadableMap
import com.klarna.mobile.sdk.klarna.network.core.api.models.KlarnaAddress
import com.klarna.mobile.sdk.klarna.network.payment.api.models.KlarnaBillingPlan
import com.klarna.mobile.sdk.klarna.network.payment.api.models.KlarnaCollectCustomerProfileType
import com.klarna.mobile.sdk.klarna.network.payment.api.models.KlarnaFreeTrial
import com.klarna.mobile.sdk.klarna.network.payment.api.models.KlarnaInterval
import com.klarna.mobile.sdk.klarna.network.payment.api.models.KlarnaLineItem
import com.klarna.mobile.sdk.klarna.network.payment.api.models.KlarnaOndemandService
import com.klarna.mobile.sdk.klarna.network.payment.api.models.KlarnaPartnerCustomer
import com.klarna.mobile.sdk.klarna.network.payment.api.models.KlarnaPaymentRequestData
import com.klarna.mobile.sdk.klarna.network.payment.api.models.KlarnaRequestCustomerToken
import com.klarna.mobile.sdk.klarna.network.payment.api.models.KlarnaRequestCustomerTokenScope
import com.klarna.mobile.sdk.klarna.network.payment.api.models.KlarnaShipping
import com.klarna.mobile.sdk.klarna.network.payment.api.models.KlarnaShippingConfig
import com.klarna.mobile.sdk.klarna.network.payment.api.models.KlarnaShippingConfigMode
import com.klarna.mobile.sdk.klarna.network.payment.api.models.KlarnaShippingOption
import com.klarna.mobile.sdk.klarna.network.payment.api.models.KlarnaShippingRecipient
import com.klarna.mobile.sdk.klarna.network.payment.api.models.KlarnaShippingType
import com.klarna.mobile.sdk.klarna.network.payment.api.models.KlarnaShippingTypeAttribute
import com.klarna.mobile.sdk.klarna.network.payment.api.models.KlarnaSubscription
import com.klarna.mobile.sdk.klarna.network.payment.api.models.KlarnaSupplementaryPurchaseData
import com.klarna.mobile.sdk.klarna.network.payment.api.presentation.models.KlarnaPaymentPresentationData
import com.klarna.mobile.sdk.klarna.network.payment.api.presentation.models.KlarnaPaymentPresentationIntent
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone

internal object KlarnaPaymentRequestMapper {

    // KlarnaPaymentRequestData
    private const val AMOUNT = "amount"
    private const val CURRENCY = "currency"
    private const val PAYMENT_OPTION_ID = "paymentOptionId"
    private const val PAYMENT_REQUEST_REFERENCE = "paymentRequestReference"
    private const val REQUEST_CUSTOMER_TOKEN = "requestCustomerToken"
    private const val SHIPPING_CONFIG = "shippingConfig"
    private const val COLLECT_CUSTOMER_PROFILE = "collectCustomerProfile"
    private const val SUPPLEMENTARY_PURCHASE_DATA = "supplementaryPurchaseData"

    // KlarnaPaymentPresentationData
    private const val INTENT = "intent"
    private const val PAYMENT_PROGRAM_ENABLEMENT_CODES = "paymentProgramEnablementCodes"
    private const val SUBSCRIPTION_BILLING_INTERVAL = "subscriptionBillingInterval"
    private const val SUBSCRIPTION_BILLING_INTERVAL_FREQUENCY = "subscriptionBillingIntervalFrequency"

    // KlarnaRequestCustomerToken
    private const val SCOPES = "scopes"
    private const val CUSTOMER_TOKEN_REFERENCE = "customerTokenReference"

    // KlarnaShippingConfig
    private const val MODE = "mode"
    private const val SUPPORTED_COUNTRIES = "supportedCountries"

    // KlarnaSupplementaryPurchaseData
    private const val CUSTOMER = "customer"
    private const val LINE_ITEMS = "lineItems"
    private const val PURCHASE_REFERENCE = "purchaseReference"
    private const val SHIPPING = "shipping"
    private const val ONDEMAND_SERVICE = "ondemandService"
    private const val SUBSCRIPTIONS = "subscriptions"

    // KlarnaPartnerCustomer / KlarnaShippingRecipient (shared keys)
    private const val ADDRESS = "address"
    private const val EMAIL = "email"
    private const val FAMILY_NAME = "familyName"
    private const val GIVEN_NAME = "givenName"
    private const val PHONE = "phone"

    // KlarnaAddress
    private const val CITY = "city"
    private const val COUNTRY = "country"
    private const val POSTAL_CODE = "postalCode"
    private const val REGION = "region"
    private const val STREET_ADDRESS = "streetAddress"
    private const val STREET_ADDRESS2 = "streetAddress2"

    // KlarnaLineItem
    private const val IMAGE_URL = "imageUrl"
    private const val LINE_ITEM_REFERENCE = "lineItemReference"
    private const val NAME = "name"
    private const val PRODUCT_IDENTIFIER = "productIdentifier"
    private const val PRODUCT_URL = "productUrl"
    private const val QUANTITY = "quantity"
    private const val SHIPPING_REFERENCE = "shippingReference"
    private const val SUBSCRIPTION_REFERENCE = "subscriptionReference"
    private const val TOTAL_AMOUNT = "totalAmount"
    private const val TOTAL_TAX_AMOUNT = "totalTaxAmount"
    private const val UNIT_PRICE = "unitPrice"

    // KlarnaShipping
    private const val RECIPIENT = "recipient"
    private const val SHIPPING_OPTION = "shippingOption"

    // KlarnaShippingRecipient
    private const val ATTENTION = "attention"

    // KlarnaShippingOption
    private const val SHIPPING_TYPE = "shippingType"
    private const val SHIPPING_TYPE_ATTRIBUTES = "shippingTypeAttributes"
    private const val SHIPPING_CARRIER = "shippingCarrier"

    // KlarnaOndemandService
    private const val AVERAGE_AMOUNT = "averageAmount"
    private const val MINIMUM_AMOUNT = "minimumAmount"
    private const val MAXIMUM_AMOUNT = "maximumAmount"
    private const val PURCHASE_INTERVAL = "purchaseInterval"
    private const val PURCHASE_INTERVAL_FREQUENCY = "purchaseIntervalFrequency"

    // KlarnaSubscription
    private const val FREE_TRIAL = "freeTrial"
    private const val BILLING_PLANS = "billingPlans"

    // KlarnaBillingPlan
    private const val FROM = "from"
    private const val INTERVAL = "interval"
    private const val INTERVAL_FREQUENCY = "intervalFrequency"
    private const val BILLING_AMOUNT = "billingAmount"

    fun buildPaymentRequestData(data: ReadableMap): KlarnaPaymentRequestData {
        require(data.hasKey(AMOUNT)) { "Amount is required" }
        val currency = data.getString(CURRENCY)
        require(!currency.isNullOrEmpty()) { "Currency is required" }
        return KlarnaPaymentRequestData(
            amount = data.getDouble(AMOUNT).toLong(),
            currency = currency,
            paymentOptionId = data.getString(PAYMENT_OPTION_ID),
            paymentRequestReference = data.getString(PAYMENT_REQUEST_REFERENCE),
            requestCustomerToken = data.getMap(REQUEST_CUSTOMER_TOKEN)?.let { buildRequestCustomerToken(it) },
            shippingConfig = data.getMap(SHIPPING_CONFIG)?.let { buildShippingConfig(it) },
            collectCustomerProfile = data.getArray(COLLECT_CUSTOMER_PROFILE)?.let { buildCollectCustomerProfile(it) },
            supplementaryPurchaseData = data.getMap(SUPPLEMENTARY_PURCHASE_DATA)?.let { buildSupplementaryPurchaseData(it) },
        )
    }

    fun buildPresentationData(map: ReadableMap): KlarnaPaymentPresentationData {
        return KlarnaPaymentPresentationData(
            amount = map.getDouble(AMOUNT).toLong(),
            currency = map.getString(CURRENCY) ?: "",
            intent = map.getString(INTENT)?.let { raw ->
                KlarnaPaymentPresentationIntent.entries.find { it.toString() == raw }
            },
            paymentProgramEnablementCodes = map.getArray(PAYMENT_PROGRAM_ENABLEMENT_CODES)?.let { arr ->
                (0 until arr.size()).mapNotNull { arr.getString(it) }
            },
            subscriptionBillingInterval = map.getString(SUBSCRIPTION_BILLING_INTERVAL)?.let { raw ->
                KlarnaInterval.entries.find { it.toString() == raw }
            },
            subscriptionBillingIntervalFrequency = if (map.hasKey(SUBSCRIPTION_BILLING_INTERVAL_FREQUENCY) &&
                !map.isNull(SUBSCRIPTION_BILLING_INTERVAL_FREQUENCY))
                map.getInt(SUBSCRIPTION_BILLING_INTERVAL_FREQUENCY) else null,
        )
    }

    private fun buildRequestCustomerToken(map: ReadableMap): KlarnaRequestCustomerToken {
        val scopes = map.getArray(SCOPES)?.let { arr ->
            (0 until arr.size()).mapNotNull { i ->
                KlarnaRequestCustomerTokenScope.entries.find { it.toString() == arr.getString(i) }
            }
        } ?: emptyList()
        return KlarnaRequestCustomerToken(
            scopes = scopes,
            customerTokenReference = map.getString(CUSTOMER_TOKEN_REFERENCE),
        )
    }

    private fun buildShippingConfig(map: ReadableMap): KlarnaShippingConfig {
        val mode = KlarnaShippingConfigMode.entries.find { it.toString() == map.getString(MODE) }
            ?: KlarnaShippingConfigMode.EDITABLE
        val supportedCountries = map.getArray(SUPPORTED_COUNTRIES)?.let { arr ->
            (0 until arr.size()).map { arr.getString(it) ?: "" }
        }
        return KlarnaShippingConfig(supportedCountries = supportedCountries, mode = mode)
    }

    private fun buildCollectCustomerProfile(arr: ReadableArray): List<KlarnaCollectCustomerProfileType> {
        return (0 until arr.size()).mapNotNull { i ->
            KlarnaCollectCustomerProfileType.entries.find { it.toString() == arr.getString(i) }
        }
    }

    private fun buildSupplementaryPurchaseData(map: ReadableMap): KlarnaSupplementaryPurchaseData {
        return KlarnaSupplementaryPurchaseData(
            customer = map.getMap(CUSTOMER)?.let { buildPartnerCustomer(it) },
            lineItems = map.getArray(LINE_ITEMS)?.let { arr ->
                (0 until arr.size()).mapNotNull { i -> arr.getMap(i)?.let { buildLineItem(it) } }
            },
            purchaseReference = map.getString(PURCHASE_REFERENCE),
            shipping = map.getArray(SHIPPING)?.let { arr ->
                (0 until arr.size()).mapNotNull { i -> arr.getMap(i)?.let { buildShipping(it) } }
            },
            ondemandService = map.getMap(ONDEMAND_SERVICE)?.let { buildOndemandService(it) },
            subscriptions = map.getArray(SUBSCRIPTIONS)?.let { arr ->
                (0 until arr.size()).mapNotNull { i -> arr.getMap(i)?.let { buildSubscription(it) } }
            },
        )
    }

    private fun buildPartnerCustomer(map: ReadableMap): KlarnaPartnerCustomer {
        return KlarnaPartnerCustomer(
            address = map.getMap(ADDRESS)?.let { buildAddress(it) },
            email = map.getString(EMAIL),
            familyName = map.getString(FAMILY_NAME),
            givenName = map.getString(GIVEN_NAME),
            phone = map.getString(PHONE),
        )
    }

    private fun buildAddress(map: ReadableMap): KlarnaAddress {
        return KlarnaAddress(
            city = map.getString(CITY),
            country = map.getString(COUNTRY),
            postalCode = map.getString(POSTAL_CODE),
            region = map.getString(REGION),
            streetAddress = map.getString(STREET_ADDRESS),
            streetAddress2 = map.getString(STREET_ADDRESS2),
        )
    }

    private fun buildLineItem(map: ReadableMap): KlarnaLineItem? {
        val name = map.getString(NAME) ?: return null
        if (!map.hasKey(TOTAL_AMOUNT) || map.isNull(TOTAL_AMOUNT)) return null
        if (!map.hasKey(QUANTITY) || map.isNull(QUANTITY)) return null
        return KlarnaLineItem(
            currency = map.getString(CURRENCY),
            imageUrl = map.getString(IMAGE_URL),
            lineItemReference = map.getString(LINE_ITEM_REFERENCE),
            name = name,
            productIdentifier = map.getString(PRODUCT_IDENTIFIER),
            productUrl = map.getString(PRODUCT_URL),
            quantity = map.getInt(QUANTITY),
            shippingReference = map.getString(SHIPPING_REFERENCE),
            subscriptionReference = map.getString(SUBSCRIPTION_REFERENCE),
            totalAmount = map.getDouble(TOTAL_AMOUNT).toLong(),
            totalTaxAmount = if (map.hasKey(TOTAL_TAX_AMOUNT) && !map.isNull(TOTAL_TAX_AMOUNT)) map.getDouble(TOTAL_TAX_AMOUNT).toLong() else null,
            unitPrice = if (map.hasKey(UNIT_PRICE) && !map.isNull(UNIT_PRICE)) map.getDouble(UNIT_PRICE).toLong() else null,
        )
    }

    private fun buildShipping(map: ReadableMap): KlarnaShipping {
        return KlarnaShipping(
            address = map.getMap(ADDRESS)?.let { buildAddress(it) },
            recipient = map.getMap(RECIPIENT)?.let { buildShippingRecipient(it) },
            shippingOption = map.getMap(SHIPPING_OPTION)?.let { buildShippingOption(it) },
            shippingReference = map.getString(SHIPPING_REFERENCE),
        )
    }

    private fun buildShippingRecipient(map: ReadableMap): KlarnaShippingRecipient {
        return KlarnaShippingRecipient(
            attention = map.getString(ATTENTION),
            email = map.getString(EMAIL),
            familyName = map.getString(FAMILY_NAME) ?: "",
            givenName = map.getString(GIVEN_NAME) ?: "",
            phone = map.getString(PHONE),
        )
    }

    private fun buildShippingOption(map: ReadableMap): KlarnaShippingOption {
        val shippingType = KlarnaShippingType.entries.find { it.toString() == map.getString(SHIPPING_TYPE) }
            ?: KlarnaShippingType.TO_DOOR
        val attrs = map.getArray(SHIPPING_TYPE_ATTRIBUTES)?.let { arr ->
            (0 until arr.size()).mapNotNull { i ->
                KlarnaShippingTypeAttribute.entries.find { it.toString() == arr.getString(i) }
            }
        }
        return KlarnaShippingOption(
            shippingCarrier = map.getString(SHIPPING_CARRIER),
            shippingType = shippingType,
            shippingTypeAttributes = attrs,
        )
    }

    private fun buildOndemandService(map: ReadableMap): KlarnaOndemandService {
        return KlarnaOndemandService(
            currency = map.getString(CURRENCY),
            averageAmount = if (map.hasKey(AVERAGE_AMOUNT) && !map.isNull(AVERAGE_AMOUNT)) map.getDouble(AVERAGE_AMOUNT).toLong() else null,
            minimumAmount = if (map.hasKey(MINIMUM_AMOUNT) && !map.isNull(MINIMUM_AMOUNT)) map.getDouble(MINIMUM_AMOUNT).toLong() else null,
            maximumAmount = if (map.hasKey(MAXIMUM_AMOUNT) && !map.isNull(MAXIMUM_AMOUNT)) map.getDouble(MAXIMUM_AMOUNT).toLong() else null,
            purchaseInterval = map.getString(PURCHASE_INTERVAL)?.let { raw -> KlarnaInterval.entries.find { it.toString() == raw } },
            purchaseIntervalFrequency = if (map.hasKey(PURCHASE_INTERVAL_FREQUENCY) && !map.isNull(PURCHASE_INTERVAL_FREQUENCY)) map.getInt(PURCHASE_INTERVAL_FREQUENCY) else null,
        )
    }

    private fun buildSubscription(map: ReadableMap): KlarnaSubscription {
        return KlarnaSubscription(
            subscriptionReference = map.getString(SUBSCRIPTION_REFERENCE) ?: "",
            name = map.getString(NAME),
            freeTrial = map.getString(FREE_TRIAL)?.let { raw -> KlarnaFreeTrial.entries.find { it.toString() == raw } },
            billingPlans = map.getArray(BILLING_PLANS)?.let { arr ->
                (0 until arr.size()).mapNotNull { i -> arr.getMap(i)?.let { buildBillingPlan(it) } }
            },
        )
    }

    private fun buildBillingPlan(map: ReadableMap): KlarnaBillingPlan? {
        val fromStr = map.getString(FROM) ?: return null
        val interval = KlarnaInterval.entries.find { it.toString() == map.getString(INTERVAL) } ?: return null
        val from = parseIso8601Date(fromStr) ?: return null
        if (!map.hasKey(BILLING_AMOUNT) || !map.hasKey(INTERVAL_FREQUENCY)) return null
        return KlarnaBillingPlan(
            billingAmount = map.getDouble(BILLING_AMOUNT).toLong(),
            from = from,
            interval = interval,
            intervalFrequency = map.getInt(INTERVAL_FREQUENCY),
            currency = map.getString(CURRENCY),
        )
    }

    private fun parseIso8601Date(isoStr: String): java.util.Date? {
        val formats = listOf(
            "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'",
            "yyyy-MM-dd'T'HH:mm:ss'Z'",
        )
        return formats.firstNotNullOfOrNull { pattern ->
            try {
                SimpleDateFormat(pattern, Locale.US).apply {
                    isLenient = false
                    timeZone = TimeZone.getTimeZone("UTC")
                }.parse(isoStr)
            } catch (e: Exception) {
                null
            }
        }
    }
}

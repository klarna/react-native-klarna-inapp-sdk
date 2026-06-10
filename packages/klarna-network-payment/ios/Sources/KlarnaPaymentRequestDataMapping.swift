import Foundation
import KlarnaNetworkCore
import KlarnaNetworkPayment

// JS has no integer type — all numbers arrive as Double at the bridge boundary.
// Every integer SDK field is therefore parsed with `.map { Int64($0) }` / `.map { Int32($0) }`.
func klarnaPaymentRequestData(from dict: NSDictionary) -> KlarnaPaymentRequestData? {
    guard let amount = dict["amount"] as? NSNumber,
          let currency = dict["currency"] as? String else {
        return nil
    }
    return KlarnaPaymentRequestData(
        amount: amount.int64Value,
        currency: currency,
        paymentOptionId: dict["paymentOptionId"] as? String,
        paymentRequestReference: dict["paymentRequestReference"] as? String,
        requestCustomerToken: buildRequestCustomerToken(from: dict["requestCustomerToken"] as? [String: Any]),
        shippingConfig: buildShippingConfig(from: dict["shippingConfig"] as? [String: Any]),
        collectCustomerProfile: buildCollectCustomerProfile(from: dict["collectCustomerProfile"] as? [String]),
        supplementaryPurchaseData: buildSupplementaryPurchaseData(from: dict["supplementaryPurchaseData"] as? [String: Any])
    )
}

// MARK: - Top-level sub-objects

private func buildRequestCustomerToken(from dict: [String: Any]?) -> KlarnaRequestCustomerToken? {
    guard let dict,
          let rawScopes = dict["scopes"] as? [String], !rawScopes.isEmpty else {
        return nil
    }
    let scopes = rawScopes.compactMap { KlarnaRequestCustomerTokenScope(rawValue: $0) }
    return KlarnaRequestCustomerToken(
        scopes: scopes,
        customerTokenReference: dict["customerTokenReference"] as? String
    )
}

private func buildShippingConfig(from dict: [String: Any]?) -> KlarnaShippingConfig? {
    guard let dict,
          let rawMode = dict["mode"] as? String,
          let mode = KlarnaShippingConfigMode(rawValue: rawMode) else {
        return nil
    }
    return KlarnaShippingConfig(
        supportedCountries: dict["supportedCountries"] as? [String],
        mode: mode
    )
}

private func buildCollectCustomerProfile(from raw: [String]?) -> [KlarnaCollectCustomerProfileType]? {
    guard let raw, !raw.isEmpty else { return nil }
    let types = raw.compactMap { KlarnaCollectCustomerProfileType(rawValue: $0) }
    return types.isEmpty ? nil : types
}

// MARK: - KlarnaSupplementaryPurchaseData

private func buildSupplementaryPurchaseData(from dict: [String: Any]?) -> KlarnaSupplementaryPurchaseData? {
    guard let dict else { return nil }
    return KlarnaSupplementaryPurchaseData(
        customer: buildPartnerCustomer(from: dict["customer"] as? [String: Any]),
        lineItems: buildLineItems(from: dict["lineItems"] as? [[String: Any]]),
        purchaseReference: dict["purchaseReference"] as? String,
        shipping: buildShippingList(from: dict["shipping"] as? [[String: Any]]),
        ondemandService: buildOndemandService(from: dict["ondemandService"] as? [String: Any]),
        subscriptions: buildSubscriptions(from: dict["subscriptions"] as? [[String: Any]])
    )
}

// MARK: - KlarnaAddress

private func buildAddress(from dict: [String: Any]?) -> KlarnaAddress? {
    guard let dict else { return nil }
    return KlarnaAddress(
        city: dict["city"] as? String,
        country: dict["country"] as? String,
        postalCode: dict["postalCode"] as? String,
        region: dict["region"] as? String,
        streetAddress: dict["streetAddress"] as? String,
        streetAddress2: dict["streetAddress2"] as? String
    )
}

// MARK: - KlarnaPartnerCustomer

private func buildPartnerCustomer(from dict: [String: Any]?) -> KlarnaPartnerCustomer? {
    guard let dict else { return nil }
    return KlarnaPartnerCustomer(
        address: buildAddress(from: dict["address"] as? [String: Any]),
        email: dict["email"] as? String,
        familyName: dict["familyName"] as? String,
        givenName: dict["givenName"] as? String,
        phone: dict["phone"] as? String
    )
}

// MARK: - KlarnaLineItem

private func buildLineItems(from dicts: [[String: Any]]?) -> [KlarnaLineItem]? {
    guard let dicts, !dicts.isEmpty else { return nil }
    return dicts.compactMap { buildLineItem(from: $0) }
}

private func buildLineItem(from dict: [String: Any]) -> KlarnaLineItem? {
    guard let name = dict["name"] as? String,
          let totalAmount = (dict["totalAmount"] as? Double).map({ Int64($0) }),
          let quantity = (dict["quantity"] as? Double).map({ Int($0) }) else { return nil }
    return KlarnaLineItem(
        currency: dict["currency"] as? String,
        imageUrl: dict["imageUrl"] as? String,
        name: name,
        productIdentifier: dict["productIdentifier"] as? String,
        productUrl: dict["productUrl"] as? String,
        lineItemReference: dict["lineItemReference"] as? String,
        shippingReference: dict["shippingReference"] as? String,
        subscriptionReference: dict["subscriptionReference"] as? String,
        quantity: quantity,
        totalAmount: totalAmount,
        totalTaxAmount: (dict["totalTaxAmount"] as? Double).map { Int64($0) },
        unitPrice: (dict["unitPrice"] as? Double).map { Int64($0) }
    )
}

// MARK: - KlarnaShipping

private func buildShippingList(from dicts: [[String: Any]]?) -> [KlarnaShipping]? {
    guard let dicts, !dicts.isEmpty else { return nil }
    return dicts.map { buildShipping(from: $0) }
}

private func buildShipping(from dict: [String: Any]) -> KlarnaShipping {
    KlarnaShipping(
        address: buildAddress(from: dict["address"] as? [String: Any]),
        recipient: buildShippingRecipient(from: dict["recipient"] as? [String: Any]),
        shippingOption: buildShippingOption(from: dict["shippingOption"] as? [String: Any]),
        shippingReference: dict["shippingReference"] as? String
    )
}

private func buildShippingRecipient(from dict: [String: Any]?) -> KlarnaShippingRecipient? {
    guard let dict,
          let familyName = dict["familyName"] as? String,
          let givenName = dict["givenName"] as? String else {
        return nil
    }
    return KlarnaShippingRecipient(
        attention: dict["attention"] as? String,
        email: dict["email"] as? String,
        familyName: familyName,
        givenName: givenName,
        phone: dict["phone"] as? String
    )
}

private func buildShippingOption(from dict: [String: Any]?) -> KlarnaShippingOption? {
    guard let dict,
          let rawType = dict["shippingType"] as? String,
          let shippingType = KlarnaShippingType(rawValue: rawType) else {
        return nil
    }
    let attributes: [KlarnaShippingTypeAttribute]? = (dict["shippingTypeAttributes"] as? [String])
        .map { $0.compactMap { KlarnaShippingTypeAttribute(rawValue: $0) } }
    return KlarnaShippingOption(
        shippingCarrier: dict["shippingCarrier"] as? String,
        shippingType: shippingType,
        shippingTypeAttributes: attributes
    )
}

// MARK: - KlarnaOndemandService

private func buildOndemandService(from dict: [String: Any]?) -> KlarnaOndemandService? {
    guard let dict else { return nil }
    let interval: KlarnaInterval? = (dict["purchaseInterval"] as? String)
        .flatMap { KlarnaInterval(rawValue: $0) }
    return KlarnaOndemandService(
        currency: dict["currency"] as? String,
        averageAmount: (dict["averageAmount"] as? Double).map { Int64($0) },
        minimumAmount: (dict["minimumAmount"] as? Double).map { Int64($0) },
        maximumAmount: (dict["maximumAmount"] as? Double).map { Int64($0) },
        purchaseInterval: interval,
        purchaseIntervalFrequency: (dict["purchaseIntervalFrequency"] as? Double).map { Int32($0) }
    )
}

// MARK: - KlarnaSubscription / KlarnaBillingPlan

private func buildSubscriptions(from dicts: [[String: Any]]?) -> [KlarnaSubscription]? {
    guard let dicts, !dicts.isEmpty else { return nil }
    return dicts.compactMap { buildSubscription(from: $0) }
}

private func buildSubscription(from dict: [String: Any]) -> KlarnaSubscription? {
    guard let subscriptionReference = dict["subscriptionReference"] as? String else { return nil }
    return KlarnaSubscription(
        subscriptionReference: subscriptionReference,
        name: dict["name"] as? String,
        freeTrial: (dict["freeTrial"] as? String).flatMap { KlarnaFreeTrial(rawValue: $0) },
        billingPlans: buildBillingPlans(from: dict["billingPlans"] as? [[String: Any]])
    )
}

private func buildBillingPlans(from dicts: [[String: Any]]?) -> [KlarnaBillingPlan]? {
    guard let dicts, !dicts.isEmpty else { return nil }
    return dicts.compactMap { buildBillingPlan(from: $0) }
}

private func buildBillingPlan(from dict: [String: Any]) -> KlarnaBillingPlan? {
    guard let fromString = dict["from"] as? String,
          let fromDate = parseIso8601Date(from: fromString),
          let rawInterval = dict["interval"] as? String,
          let interval = KlarnaInterval(rawValue: rawInterval),
          let billingAmount = dict["billingAmount"] as? Double,
          let intervalFrequency = dict["intervalFrequency"] as? Double else {
        return nil
    }
    return KlarnaBillingPlan(
        billingAmount: Int64(billingAmount),
        currency: dict["currency"] as? String,
        from: fromDate,
        interval: interval,
        intervalFrequency: Int32(intervalFrequency)
    )
}

// Mirrors Android's parseIso8601Date: tries with fractional seconds first, then without.
private func parseIso8601Date(from string: String) -> Date? {
    let withMillis = ISO8601DateFormatter()
    withMillis.formatOptions = [.withInternetDateTime, .withFractionalSeconds]
    if let date = withMillis.date(from: string) { return date }
    return ISO8601DateFormatter().date(from: string)
}

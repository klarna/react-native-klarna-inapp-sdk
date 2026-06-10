import Foundation
import KlarnaNetworkCore
import KlarnaNetworkPayment

extension KlarnaPaymentRequest {
    func toDictionary() -> [String: Any] {
        [
            "paymentRequestId": paymentRequestId,
            "state": state.rawValue,
            "previousState": orNull(previousState?.rawValue),
            "stateReason": orNull(stateReason?.rawValue),
            "paymentRequestReference": orNull(paymentRequestReference),
            "stateContext": orNull(stateContext?.toDictionary()),
        ]
    }
}

private extension KlarnaPaymentRequestStateContext {
    func toDictionary() -> [String: Any] {
        [
            "klarnaNetworkSessionToken": orNull(klarnaNetworkSessionToken),
            "klarnaCustomer": orNull(klarnaCustomer?.toDictionary()),
            "shipping": orNull(shipping?.toDictionary()),
        ]
    }
}

private extension KlarnaCustomer {
    func toDictionary() -> [String: Any] {
        [
            "customerToken": orNull(customerToken),
            "customerTokenReference": orNull(customerTokenReference),
            "customerProfile": orNull(customerProfile?.toDictionary()),
        ]
    }
}

private extension KlarnaCustomerProfile {
    func toDictionary() -> [String: Any] {
        [
            "address": orNull(address?.toDictionary()),
            "customerId": orNull(customerId),
            "country": orNull(country),
            "email": orNull(email),
            "emailVerified": orNull(emailVerified.map { $0 as NSNumber }),
            "familyName": orNull(familyName),
            "givenName": orNull(givenName),
            "locale": orNull(locale),
            "phone": orNull(phone),
            "phoneVerified": orNull(phoneVerified.map { $0 as NSNumber }),
        ]
    }
}

private extension KlarnaShipping {
    func toDictionary() -> [String: Any] {
        [
            "address": orNull(address?.toDictionary()),
            "recipient": orNull(recipient?.toDictionary()),
            "shippingOption": orNull(shippingOption?.toDictionary()),
            "shippingReference": orNull(shippingReference),
        ]
    }
}

private extension KlarnaAddress {
    func toDictionary() -> [String: Any] {
        [
            "city": orNull(city),
            "country": orNull(country),
            "postalCode": orNull(postalCode),
            "region": orNull(region),
            "streetAddress": orNull(streetAddress),
            "streetAddress2": orNull(streetAddress2),
        ]
    }
}

private extension KlarnaShippingRecipient {
    func toDictionary() -> [String: Any] {
        [
            "attention": orNull(attention),
            "email": orNull(email),
            "familyName": familyName,
            "givenName": givenName,
            "phone": orNull(phone),
        ]
    }
}

private extension KlarnaShippingOption {
    func toDictionary() -> [String: Any] {
        [
            "shippingCarrier": orNull(shippingCarrier),
            "shippingType": shippingType.rawValue,
            "shippingTypeAttributes": orNull(shippingTypeAttributes?.map { $0.rawValue }),
        ]
    }
}

private func orNull(_ value: Any?) -> Any {
    value ?? NSNull()
}

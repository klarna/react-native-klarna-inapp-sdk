import Foundation
import KlarnaNetworkPayment

// MARK: - Input mapping (NSDictionary → KlarnaPaymentPresentationData)

func klarnaPaymentPresentationData(from dict: NSDictionary) -> KlarnaPaymentPresentationData? {
    guard let amount = dict["amount"] as? NSNumber,
          let currency = dict["currency"] as? String else { return nil }
    return KlarnaPaymentPresentationData(
        amount: amount.int64Value,
        currency: currency,
        intent: (dict["intent"] as? String).flatMap { KlarnaPaymentPresentationIntent(rawValue: $0) },
        paymentProgramEnablementCodes: dict["paymentProgramEnablementCodes"] as? [String],
        subscriptionBillingInterval: (dict["subscriptionBillingInterval"] as? String)
            .flatMap { KlarnaInterval(rawValue: $0) },
        subscriptionBillingIntervalFrequency: (dict["subscriptionBillingIntervalFrequency"] as? Double)
            .map { Int($0) }
    )
}

// MARK: - Output mapping (KlarnaPaymentPresentationContent → NSDictionary)

extension KlarnaPaymentPresentationContent {
    func toDictionary() -> NSDictionary {
        let dict = NSMutableDictionary()
        dict["instruction"] = instruction.rawValue
        dict["paymentStatus"] = paymentStatus?.rawValue ?? NSNull()
        dict["paymentOption"] = paymentOption?.toDictionary() ?? NSNull()
        dict["savedPaymentOption"] = savedPaymentOption?.toDictionary() ?? NSNull()
        return dict
    }
}

private extension KlarnaPaymentPresentationPaymentOption {
    func toDictionary() -> NSDictionary {
        let dict = NSMutableDictionary()
        dict["paymentOptionId"] = paymentOptionId
        dict["header"] = header?.toDictionary() ?? NSNull()
        dict["badge"] = badge?.toDictionary() ?? NSNull()
        dict["subheader"] = subheader?.toDictionary() ?? NSNull()
        dict["message"] = message?.toDictionary() ?? NSNull()
        dict["terms"] = terms?.toDictionary() ?? NSNull()
        dict["paymentButton"] = paymentButton?.toDictionary() ?? NSNull()
        dict["icon"] = icon?.toDictionary() ?? NSNull()
        return dict
    }
}

private extension KlarnaPaymentPresentationText {
    func toDictionary() -> NSDictionary {
        let dict = NSMutableDictionary()
        switch self {
        case .plainText(let text):
            dict["type"] = "plainText"
            dict["text"] = text
        case .attributedText(let parts):
            dict["type"] = "attributedText"
            dict["parts"] = parts?.map { $0.toDictionary() } ?? NSNull()
        }
        return dict
    }
}

private extension KlarnaPaymentPresentationTextPart {
    func toDictionary() -> NSDictionary {
        let dict = NSMutableDictionary()
        switch self {
        case .plain(let styles, let text):
            dict["type"] = "plain"
            dict["text"] = text
            dict["styles"] = styles?.map { $0.rawValue } ?? NSNull()
        case .link(let styles, let text, let url, let context):
            dict["type"] = "link"
            dict["text"] = text
            dict["url"] = url ?? NSNull()
            dict["context"] = context?.rawValue as Any? ?? NSNull()
            dict["styles"] = styles?.map { $0.rawValue } ?? NSNull()
        }
        return dict
    }
}

private extension KlarnaPaymentPresentationPaymentButton {
    func toDictionary() -> NSDictionary {
        let dict = NSMutableDictionary()
        dict["text"] = text
        dict["imageUrl"] = imageUrl ?? NSNull()
        dict["imageAlignment"] = imageAlignment?.rawValue ?? NSNull()
        return dict
    }
}

private extension KlarnaPaymentPresentationIcon {
    func toDictionary() -> NSDictionary {
        let dict = NSMutableDictionary()
        dict["alt"] = alt ?? NSNull()
        dict["badgeImageUrl"] = badgeImageUrl ?? NSNull()
        dict["rectangleImageUrl"] = rectangleImageUrl ?? NSNull()
        dict["squareImageUrl"] = squareImageUrl ?? NSNull()
        return dict
    }
}

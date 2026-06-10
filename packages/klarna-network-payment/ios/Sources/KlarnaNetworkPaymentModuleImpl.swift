import Foundation
import KlarnaNetworkPayment
@_spi(RNKlarnaNetworkCore) import react_native_klarna_network_core

// MARK: - PresentationContentStore

// Written and read from the JS thread and SDK callbacks (arbitrary threads).
// A serial queue serializes all access to prevent data races.
final class PresentationContentStore {
    private let queue = DispatchQueue(label: "com.klarna.rnnetworkpayment.presentationcontent")
    private var storage: [String: KlarnaPaymentPresentationContent] = [:]

    subscript(key: String) -> KlarnaPaymentPresentationContent? {
        get { queue.sync { storage[key] } }
        set { queue.sync { storage[key] = newValue } }
    }

    @discardableResult
    func removeValue(forKey key: String) -> KlarnaPaymentPresentationContent? {
        queue.sync { storage.removeValue(forKey: key) }
    }
}

// MARK: - KlarnaNetworkPaymentModuleImpl

@objc(KlarnaNetworkPaymentModuleImpl)
public class KlarnaNetworkPaymentModuleImpl: NSObject {

    private static let moduleName = "KlarnaNetworkPayment"
    private static let errorInstanceNotFound = "No instance found for the given instanceId. Call initialize first."
    private static let errorInvalidData = "Invalid payment request data."
    private static let errorNoPresentationContent = "No presentation content found. Call presentationFetch first."
    private static let errorAmountRequired = "Amount is required"
    private static let errorCurrencyRequired = "Currency is required"

    private static let storedPresentationContent = PresentationContentStore()

    @objc public func initiateWithId(
        instanceId: String,
        paymentRequestId: String,
        resolve: @escaping (Any?) -> Void,
        reject: @escaping (String?, String?, Error?) -> Void
    ) {
        guard let sdk = KlarnaNetworkCoreModuleImpl.getInstance(instanceId: instanceId) else {
            reject(Self.moduleName, Self.errorInstanceNotFound, nil)
            return
        }
        sdk.payment.initiate(paymentRequestId: paymentRequestId) { result in
            switch result {
            case .success(let paymentRequest):
                resolve(paymentRequest.toDictionary())
            case .failure(let error):
                reject(error.name, error.message, nil)
            }
        }
    }

    @objc public func initiateWithData(
        instanceId: String,
        data: NSDictionary,
        resolve: @escaping (Any?) -> Void,
        reject: @escaping (String?, String?, Error?) -> Void
    ) {
        guard let sdk = KlarnaNetworkCoreModuleImpl.getInstance(instanceId: instanceId) else {
            reject(Self.moduleName, Self.errorInstanceNotFound, nil)
            return
        }
        guard data["amount"] is NSNumber else {
            reject(Self.moduleName, Self.errorAmountRequired, nil)
            return
        }
        guard let currency = data["currency"] as? String, !currency.isEmpty else {
            reject(Self.moduleName, Self.errorCurrencyRequired, nil)
            return
        }
        guard let paymentRequestData = klarnaPaymentRequestData(from: data) else {
            reject(Self.moduleName, Self.errorInvalidData, nil)
            return
        }
        sdk.payment.initiate(paymentRequestData: paymentRequestData) { result in
            switch result {
            case .success(let paymentRequest):
                resolve(paymentRequest.toDictionary())
            case .failure(let error):
                reject(error.name, error.message, nil)
            }
        }
    }

    @objc public func fetch(
        instanceId: String,
        paymentRequestId: String,
        resolve: @escaping (Any?) -> Void,
        reject: @escaping (String?, String?, Error?) -> Void
    ) {
        guard let sdk = KlarnaNetworkCoreModuleImpl.getInstance(instanceId: instanceId) else {
            reject(Self.moduleName, Self.errorInstanceNotFound, nil)
            return
        }
        sdk.payment.fetch(paymentRequestId: paymentRequestId) { result in
            switch result {
            case .success(let paymentRequest):
                resolve(paymentRequest.toDictionary())
            case .failure(let error):
                reject(error.name, error.message, nil)
            }
        }
    }

    @objc public func cancel(
        instanceId: String,
        paymentRequestId: String,
        resolve: @escaping (Any?) -> Void,
        reject: @escaping (String?, String?, Error?) -> Void
    ) {
        guard let sdk = KlarnaNetworkCoreModuleImpl.getInstance(instanceId: instanceId) else {
            reject(Self.moduleName, Self.errorInstanceNotFound, nil)
            return
        }
        sdk.payment.cancel(paymentRequestId: paymentRequestId) { result in
            switch result {
            case .success(let paymentRequest):
                resolve(paymentRequest.toDictionary())
            case .failure(let error):
                reject(error.name, error.message, nil)
            }
        }
    }

    @objc public func presentationFetch(
        instanceId: String,
        data: NSDictionary,
        resolve: @escaping (Any?) -> Void,
        reject: @escaping (String?, String?, Error?) -> Void
    ) {
        guard let sdk = KlarnaNetworkCoreModuleImpl.getInstance(instanceId: instanceId) else {
            reject(Self.moduleName, Self.errorInstanceNotFound, nil)
            return
        }
        guard let presentationData = klarnaPaymentPresentationData(from: data) else {
            reject(Self.moduleName, Self.errorInvalidData, nil)
            return
        }
        DispatchQueue.main.async {
            sdk.payment.presentation.fetch(data: presentationData) { result in
                switch result {
                case .success(let content):
                    if KlarnaNetworkCoreModuleImpl.getInstance(instanceId: instanceId) != nil {
                        Self.storedPresentationContent[instanceId] = content
                    }
                    resolve(content.toDictionary())
                case .failure(let error):
                    reject(error.name, error.message, nil)
                }
            }
        }
    }

    @objc public func dispose(
        instanceId: String,
        resolve: @escaping (Any?) -> Void,
        reject: @escaping (String?, String?, Error?) -> Void
    ) {
        Self.storedPresentationContent.removeValue(forKey: instanceId)
        resolve(nil)
    }

    @objc public func presentationHandleLink(
        instanceId: String,
        url: String,
        resolve: @escaping (Any?) -> Void,
        reject: @escaping (String?, String?, Error?) -> Void
    ) {
        guard let sdk = KlarnaNetworkCoreModuleImpl.getInstance(instanceId: instanceId) else {
            reject(Self.moduleName, Self.errorInstanceNotFound, nil)
            return
        }
        guard let content = Self.storedPresentationContent[instanceId] else {
            reject(Self.moduleName, Self.errorNoPresentationContent, nil)
            return
        }
        DispatchQueue.main.async {
            sdk.payment.presentation.handleLink(content: content, url: url) { result in
                switch result {
                case .success(let newContent):
                    if KlarnaNetworkCoreModuleImpl.getInstance(instanceId: instanceId) != nil {
                        Self.storedPresentationContent[instanceId] = newContent
                    }
                    resolve(newContent.toDictionary())
                case .failure(let error):
                    reject(error.name, error.message, nil)
                }
            }
        }
    }
}

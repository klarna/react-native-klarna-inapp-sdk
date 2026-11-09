import Foundation
import KlarnaNetworkMessaging
@_spi(RNKlarnaNetworkCore) import react_native_klarna_network_core

// MARK: - KlarnaMessagingPrequalificationInitiating

/// Test seam over `KlarnaMessagingPrequalification`, which can't be subclassed or constructed outside the
/// native SDK (internal-only initializer). Tests inject a fake conforming to this protocol.
protocol KlarnaMessagingPrequalificationInitiating: AnyObject {
    func initiate(data: KlarnaMessagingPrequalificationData, callback: @escaping KlarnaMessagingPrequalificationCallback)
}

// The native method already has this exact signature, so the conformance needs no body.
extension KlarnaMessagingPrequalification: KlarnaMessagingPrequalificationInitiating {}

// MARK: - KlarnaNetworkMessagingModuleImpl

@objc(KlarnaNetworkMessagingModuleImpl)
public class KlarnaNetworkMessagingModuleImpl: NSObject {

    private static let moduleName = "KlarnaNetworkMessaging"
    private static let errorInstanceNotFound = "No instance found for the given instanceId. Call initialize first."
    private static let errorAmountRequired = "Amount is required"
    private static let errorCurrencyRequired = "Currency is required"

    private let initiatorResolver: (String) -> (any KlarnaMessagingPrequalificationInitiating)?

    // MARK: - Init

    public override init() {
        self.initiatorResolver = { instanceId in
            KlarnaNetworkCoreModuleImpl.getInstance(instanceId: instanceId)?.messaging.prequalification
        }
        super.init()
    }

    init(initiatorResolver: @escaping (String) -> (any KlarnaMessagingPrequalificationInitiating)?) {
        self.initiatorResolver = initiatorResolver
        super.init()
    }

    // MARK: - Prequalification

    @objc public func initiatePrequalification(
        instanceId: String,
        data: NSDictionary,
        resolve: @escaping (Any?) -> Void,
        reject: @escaping (String?, String?, Error?) -> Void
    ) {
        guard let initiator = initiatorResolver(instanceId) else {
            reject(Self.moduleName, Self.errorInstanceNotFound, nil)
            return
        }
        let prequalificationData: KlarnaMessagingPrequalificationData
        switch klarnaMessagingPrequalificationData(from: data) {
        case .success(let parsed):
            prequalificationData = parsed
        case .failure(.amountRequired):
            reject(Self.moduleName, Self.errorAmountRequired, nil)
            return
        case .failure(.currencyRequired):
            reject(Self.moduleName, Self.errorCurrencyRequired, nil)
            return
        }
        // The flow presents an authentication browser, so it must be started on the main thread.
        DispatchQueue.main.async {
            initiator.initiate(data: prequalificationData) { result in
                switch result {
                case .success(let prequalificationResult):
                    resolve(prequalificationResult.toDictionary())
                case .failure(let error):
                    reject(error.name, error.message, nil)
                }
            }
        }
    }

    // MARK: - Lifecycle

    @objc public func dispose(
        instanceId: String,
        resolve: @escaping (Any?) -> Void,
        reject: @escaping (String?, String?, Error?) -> Void
    ) {
        // No per-instance state is kept on the native side.
        resolve(nil)
    }
}

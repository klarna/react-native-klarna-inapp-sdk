import Foundation
import KlarnaNetworkCore

// MARK: - KlarnaNetworkCoreImplError

struct KlarnaNetworkCoreImplError: Error {
    let name: String
    let message: String
}

// MARK: - KlarnaNetworkCoreInstance

protocol KlarnaNetworkCoreInstance: AnyObject {
    func sessionToken(onSuccess: @escaping (String) -> Void, onFailure: @escaping (KlarnaNetworkCoreImplError) -> Void)
    func clearSession(onSuccess: @escaping () -> Void, onFailure: @escaping (KlarnaNetworkCoreImplError) -> Void)
    var integrationMetadata: KlarnaIntegrationMetadata? { get set }
}

// MARK: - Klarna + KlarnaNetworkCoreInstance

extension Klarna: KlarnaNetworkCoreInstance {
    func sessionToken(
        onSuccess: @escaping (String) -> Void,
        onFailure: @escaping (KlarnaNetworkCoreImplError) -> Void
    ) {
        network.session.token { result in
            switch result {
            case .success(let token): onSuccess(token)
            case .failure(let error): onFailure(KlarnaNetworkCoreImplError(name: error.name, message: error.message))
            }
        }
    }

    func clearSession(
        onSuccess: @escaping () -> Void,
        onFailure: @escaping (KlarnaNetworkCoreImplError) -> Void
    ) {
        network.session.clear { result in
            switch result {
            case .success: onSuccess()
            case .failure(let error): onFailure(KlarnaNetworkCoreImplError(name: error.name, message: error.message))
            }
        }
    }
}

// MARK: - InstanceStore

// Sibling network packages read this map from arbitrary threads via
// `getInstance(instanceId:)`, while initialize/dispose write from the JS
// thread. A serial queue serializes all access so concurrent reads can't
// observe a torn dictionary or race with a write.
final class InstanceStore {
    private let queue = DispatchQueue(label: "com.klarna.rnnetworkcore.instances")
    private var storage: [String: any KlarnaNetworkCoreInstance] = [:]

    subscript(key: String) -> (any KlarnaNetworkCoreInstance)? {
        get { queue.sync { storage[key] } }
        set { queue.sync { storage[key] = newValue } }
    }

    @discardableResult
    func removeValue(forKey key: String) -> (any KlarnaNetworkCoreInstance)? {
        queue.sync { storage.removeValue(forKey: key) }
    }

    func removeAll() {
        queue.sync { storage.removeAll() }
    }
}

// MARK: - KlarnaNetworkCoreModuleImpl

@objc(KlarnaNetworkCoreModuleImpl)
public class KlarnaNetworkCoreModuleImpl: NSObject {

    private static let moduleName = "KlarnaNetworkCore"
    private static let errorInstanceNotFound = "No instance found for the given instanceId. Call initialize first."
    private static let keyName = "name"
    private static let keySessionReference = "sessionReference"
    private static let keyModuleName = "moduleName"
    private static let keyModuleVersion = "moduleVersion"

    static let instances = InstanceStore()

    private let klarnaFactory: (KlarnaConfiguration) -> Result<any KlarnaNetworkCoreInstance, KlarnaNetworkCoreImplError>

    public override init() {
        self.klarnaFactory = { configuration in
            Klarna.initialize(configuration: configuration)
                .map { $0 as any KlarnaNetworkCoreInstance }
                .mapError { KlarnaNetworkCoreImplError(name: $0.name, message: $0.message) }
        }
        super.init()
    }

    init(klarnaFactory: @escaping (KlarnaConfiguration) -> Result<any KlarnaNetworkCoreInstance, KlarnaNetworkCoreImplError>) {
        self.klarnaFactory = klarnaFactory
        super.init()
    }

    @_spi(RNKlarnaNetworkCore)
    public static func getInstance(instanceId: String) -> Klarna? {
        instances[instanceId] as? Klarna
    }

    @objc public func initialize(
        instanceId: String,
        clientId: String,
        appReturnUrl: String,
        accountId: String?,
        locale: String?,
        klarnaNetworkSessionToken: String?,
        resolve: @escaping (Any?) -> Void,
        reject: @escaping (String?, String?, Error?) -> Void
    ) {
        let configuration = KlarnaConfiguration(
            accountId: accountId,
            clientId: clientId,
            locale: locale,
            appReturnUrl: appReturnUrl,
            klarnaNetworkSessionToken: klarnaNetworkSessionToken
        )
        let result = klarnaFactory(configuration)
        switch result {
        case .success(let instance):
            Self.instances[instanceId] = instance
            resolve(nil)
        case .failure(let error):
            reject(error.name, error.message, nil)
        }
    }

    @objc public func getSessionToken(
        instanceId: String,
        resolve: @escaping (Any?) -> Void,
        reject: @escaping (String?, String?, Error?) -> Void
    ) {
        guard let instance = Self.instances[instanceId] else {
            reject(Self.moduleName, Self.errorInstanceNotFound, nil)
            return
        }
        instance.sessionToken(
            onSuccess: { token in resolve(token) },
            onFailure: { error in reject(error.name, error.message, nil) }
        )
    }

    @objc public func clearSession(
        instanceId: String,
        resolve: @escaping (Any?) -> Void,
        reject: @escaping (String?, String?, Error?) -> Void
    ) {
        guard let instance = Self.instances[instanceId] else {
            reject(Self.moduleName, Self.errorInstanceNotFound, nil)
            return
        }
        instance.clearSession(
            onSuccess: { resolve(nil) },
            onFailure: { error in reject(error.name, error.message, nil) }
        )
    }

    @objc public func handleReturnUrl(
        urlString: String,
        resolve: @escaping (Any?) -> Void,
        reject: @escaping (String?, String?, Error?) -> Void
    ) {
        guard let url = URL(string: urlString) else {
            resolve(false)
            return
        }
        DispatchQueue.main.async {
            resolve(Klarna.handleReturnUrl(url: url))
        }
    }

    @objc public func setIntegrationMetadata(
        instanceId: String,
        integratorName: String,
        integratorSessionReference: String,
        integratorModuleName: String?,
        integratorModuleVersion: String?,
        originators: NSArray?
    ) {
        guard let instance = Self.instances[instanceId] else {
            return
        }
        let integrator = KlarnaIntegratorMetadata(
            name: integratorName,
            sessionReference: integratorSessionReference,
            moduleName: integratorModuleName,
            moduleVersion: integratorModuleVersion
        )
        let originatorsList: [KlarnaOriginatorMetadata]? = originators?.compactMap { item in
            guard let dict = item as? [String: String],
                  let name = dict[Self.keyName],
                  let sessionRef = dict[Self.keySessionReference] else { return nil }
            return KlarnaOriginatorMetadata(
                name: name,
                sessionReference: sessionRef,
                moduleName: dict[Self.keyModuleName],
                moduleVersion: dict[Self.keyModuleVersion]
            )
        }
        instance.integrationMetadata = KlarnaIntegrationMetadata(
            integrator: integrator,
            originators: originatorsList
        )
    }

    @objc public func dispose(
        instanceId: String,
        resolve: @escaping (Any?) -> Void,
        reject: @escaping (String?, String?, Error?) -> Void
    ) {
        Self.instances.removeValue(forKey: instanceId)
        resolve(nil)
    }
}

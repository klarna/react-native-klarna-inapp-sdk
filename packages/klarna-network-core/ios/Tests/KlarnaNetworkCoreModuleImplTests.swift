//
//  KlarnaNetworkCoreModuleImplTests.swift
//  KlarnaNetworkCoreTests
//

import XCTest
@_spi(RNKlarnaNetworkCore) @testable import react_native_klarna_network_core
import KlarnaNetworkCore

final class KlarnaNetworkCoreModuleImplTests: XCTestCase {

    private let instanceId = "test-instance-id"
    private var mockInstance: MockKlarnaNetworkCoreInstance!
    private var sut: KlarnaNetworkCoreModuleImpl!

    override func setUpWithError() throws {
        let mockInstance = MockKlarnaNetworkCoreInstance()
        self.mockInstance = mockInstance
        sut = KlarnaNetworkCoreModuleImpl { _ in .success(mockInstance) }
    }

    override func tearDownWithError() throws {
        KlarnaNetworkCoreModuleImpl.instances.removeAll()
        sut = nil
        mockInstance = nil
    }

    // MARK: - Helpers

    private func givenInitializeSucceeds() {
        KlarnaNetworkCoreModuleImpl.instances[instanceId] = mockInstance
    }

    // MARK: - initialize

    func test_initialize_withSuccessFactory_resolvesAndStoresInstance() {
        var didResolve = false
        sut.initialize(
            instanceId: instanceId,
            clientId: "client",
            appReturnUrl: "myapp://return",
            accountId: nil,
            locale: nil,
            klarnaNetworkSessionToken: nil,
            resolve: { _ in didResolve = true },
            reject: { _, _, _ in XCTFail("should not reject") }
        )
        XCTAssertTrue(didResolve)
        XCTAssertNotNil(KlarnaNetworkCoreModuleImpl.instances[instanceId])
    }

    func test_initialize_withFailureFactory_rejectsWithExpectedError() {
        let expectedMessage = "init failed"
        sut = KlarnaNetworkCoreModuleImpl { _ in .failure(KlarnaNetworkCoreImplError(name: "KlarnaNetworkCore", message: expectedMessage)) }
        var rejectedCode: String?
        var rejectedMessage: String?
        sut.initialize(
            instanceId: instanceId,
            clientId: "client",
            appReturnUrl: "myapp://return",
            accountId: nil,
            locale: nil,
            klarnaNetworkSessionToken: nil,
            resolve: { _ in XCTFail("should not resolve") },
            reject: { code, message, _ in
                rejectedCode = code
                rejectedMessage = message
            }
        )
        XCTAssertEqual(rejectedCode, "KlarnaNetworkCore")
        XCTAssertEqual(rejectedMessage, expectedMessage)
    }

    // MARK: - handleReturnUrl

    func test_handleReturnUrl_withInvalidUrl_resolvesWithFalse() {
        var resolved: Any?
        sut.handleReturnUrl(
            urlString: "http://[",
            resolve: { resolved = $0 },
            reject: { _, _, _ in XCTFail("should not reject") }
        )
        XCTAssertEqual(resolved as? Bool, false)
    }

    // MARK: - getSessionToken

    func test_getSessionToken_withNoInstance_rejectsWithExpectedError() {
        var rejectedCode: String?
        var rejectedMessage: String?
        sut.getSessionToken(
            instanceId: "nonexistent",
            resolve: { _ in XCTFail("should not resolve") },
            reject: { code, message, _ in
                rejectedCode = code
                rejectedMessage = message
            }
        )
        XCTAssertEqual(rejectedCode, "KlarnaNetworkCore")
        XCTAssertEqual(rejectedMessage, "No instance found for the given instanceId. Call initialize first.")
    }

    func test_getSessionToken_withSuccess_resolvesWithToken() {
        givenInitializeSucceeds()
        mockInstance.stubbedSessionToken = "expected-token"
        var resolved: Any?
        sut.getSessionToken(
            instanceId: instanceId,
            resolve: { resolved = $0 },
            reject: { _, _, _ in XCTFail("should not reject") }
        )
        XCTAssertEqual(resolved as? String, "expected-token")
    }

    func test_getSessionToken_withFailure_rejectsWithExpectedError() {
        givenInitializeSucceeds()
        mockInstance.stubbedSessionTokenError = KlarnaNetworkCoreImplError(name: "KlarnaNetworkCore", message: "token fetch failed")
        var rejectedCode: String?
        var rejectedMessage: String?
        sut.getSessionToken(
            instanceId: instanceId,
            resolve: { _ in XCTFail("should not resolve") },
            reject: { code, message, _ in
                rejectedCode = code
                rejectedMessage = message
            }
        )
        XCTAssertEqual(rejectedCode, "KlarnaNetworkCore")
        XCTAssertEqual(rejectedMessage, "token fetch failed")
    }

    // MARK: - clearSession

    func test_clearSession_withNoInstance_rejectsWithExpectedError() {
        var rejectedCode: String?
        var rejectedMessage: String?
        sut.clearSession(
            instanceId: "nonexistent",
            resolve: { _ in XCTFail("should not resolve") },
            reject: { code, message, _ in
                rejectedCode = code
                rejectedMessage = message
            }
        )
        XCTAssertEqual(rejectedCode, "KlarnaNetworkCore")
        XCTAssertEqual(rejectedMessage, "No instance found for the given instanceId. Call initialize first.")
    }

    func test_clearSession_withSuccess_resolves() {
        givenInitializeSucceeds()
        var didResolve = false
        sut.clearSession(
            instanceId: instanceId,
            resolve: { _ in didResolve = true },
            reject: { _, _, _ in XCTFail("should not reject") }
        )
        XCTAssertTrue(didResolve)
    }

    func test_clearSession_withFailure_rejectsWithExpectedError() {
        givenInitializeSucceeds()
        mockInstance.stubbedClearSessionError = KlarnaNetworkCoreImplError(name: "KlarnaNetworkCore", message: "clear failed")
        var rejectedCode: String?
        var rejectedMessage: String?
        sut.clearSession(
            instanceId: instanceId,
            resolve: { _ in XCTFail("should not resolve") },
            reject: { code, message, _ in
                rejectedCode = code
                rejectedMessage = message
            }
        )
        XCTAssertEqual(rejectedCode, "KlarnaNetworkCore")
        XCTAssertEqual(rejectedMessage, "clear failed")
    }

    // MARK: - setIntegrationMetadata

    func test_setIntegrationMetadata_withInstance_setsMetadataOnInstance() {
        givenInitializeSucceeds()
        sut.setIntegrationMetadata(
            instanceId: instanceId,
            integratorName: "MyApp",
            integratorSessionReference: "ref-123",
            integratorModuleName: "TestModule",
            integratorModuleVersion: "1.0",
            originators: nil
        )
        XCTAssertEqual(mockInstance.integrationMetadata?.integrator.name, "MyApp")
        XCTAssertEqual(mockInstance.integrationMetadata?.integrator.sessionReference, "ref-123")
        XCTAssertEqual(mockInstance.integrationMetadata?.integrator.moduleName, "TestModule")
        XCTAssertEqual(mockInstance.integrationMetadata?.integrator.moduleVersion, "1.0")
    }

    // MARK: - dispose

    func test_dispose_withNoInstance_resolves() {
        var didResolve = false
        sut.dispose(
            instanceId: "nonexistent",
            resolve: { _ in didResolve = true },
            reject: { _, _, _ in XCTFail("should not reject") }
        )
        XCTAssertTrue(didResolve)
    }

    func test_dispose_withInstance_removesInstanceAndResolves() {
        givenInitializeSucceeds()
        XCTAssertNotNil(KlarnaNetworkCoreModuleImpl.instances[instanceId])
        var didResolve = false
        sut.dispose(
            instanceId: instanceId,
            resolve: { _ in didResolve = true },
            reject: { _, _, _ in XCTFail("should not reject") }
        )
        XCTAssertTrue(didResolve)
        XCTAssertNil(KlarnaNetworkCoreModuleImpl.instances[instanceId])
    }

    // MARK: - getInstance

    func test_getInstance_withNoInstance_returnsNil() {
        XCTAssertNil(KlarnaNetworkCoreModuleImpl.getInstance(instanceId: instanceId))
    }

    func test_getInstance_withNonKlarnaInstance_returnsNil() {
        // MockKlarnaNetworkCoreInstance conforms to KlarnaNetworkCoreInstance but is not Klarna,
        // so the internal cast fails and nil is returned.
        givenInitializeSucceeds()
        XCTAssertNil(KlarnaNetworkCoreModuleImpl.getInstance(instanceId: instanceId))
    }
}

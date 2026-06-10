import XCTest
@testable import react_native_klarna_network_payment

final class KlarnaNetworkPaymentModuleImplTests: XCTestCase {

    private let instanceId = "test-instance-id"
    private let errorInstanceNotFound = "No instance found for the given instanceId. Call initialize first."
    private var sut: KlarnaNetworkPaymentModuleImpl!

    override func setUpWithError() throws {
        sut = KlarnaNetworkPaymentModuleImpl()
    }

    override func tearDownWithError() throws {
        sut = nil
    }

    // MARK: - Helpers

    private func assertRejectsWithInstanceNotFound(
        code: String?,
        message: String?,
        file: StaticString = #file,
        line: UInt = #line
    ) {
        XCTAssertEqual(code, "KlarnaNetworkPayment", file: file, line: line)
        XCTAssertEqual(message, errorInstanceNotFound, file: file, line: line)
    }

    // MARK: - initiateWithId

    func test_initiateWithId_rejectsWhenInstanceNotFound() {
        var rejectedCode: String?
        var rejectedMessage: String?
        sut.initiateWithId(
            instanceId: instanceId,
            paymentRequestId: "pr-1",
            resolve: { _ in XCTFail("should not resolve") },
            reject: { code, message, _ in
                rejectedCode = code
                rejectedMessage = message
            }
        )
        assertRejectsWithInstanceNotFound(code: rejectedCode, message: rejectedMessage)
    }

    // MARK: - initiateWithData

    func test_initiateWithData_rejectsWhenInstanceNotFound() {
        var rejectedCode: String?
        var rejectedMessage: String?
        sut.initiateWithData(
            instanceId: instanceId,
            data: NSDictionary(),
            resolve: { _ in XCTFail("should not resolve") },
            reject: { code, message, _ in
                rejectedCode = code
                rejectedMessage = message
            }
        )
        assertRejectsWithInstanceNotFound(code: rejectedCode, message: rejectedMessage)
    }

    // MARK: - fetch

    func test_fetch_rejectsWhenInstanceNotFound() {
        var rejectedCode: String?
        var rejectedMessage: String?
        sut.fetch(
            instanceId: instanceId,
            paymentRequestId: "pr-1",
            resolve: { _ in XCTFail("should not resolve") },
            reject: { code, message, _ in
                rejectedCode = code
                rejectedMessage = message
            }
        )
        assertRejectsWithInstanceNotFound(code: rejectedCode, message: rejectedMessage)
    }

    // MARK: - cancel

    func test_cancel_rejectsWhenInstanceNotFound() {
        var rejectedCode: String?
        var rejectedMessage: String?
        sut.cancel(
            instanceId: instanceId,
            paymentRequestId: "pr-1",
            resolve: { _ in XCTFail("should not resolve") },
            reject: { code, message, _ in
                rejectedCode = code
                rejectedMessage = message
            }
        )
        assertRejectsWithInstanceNotFound(code: rejectedCode, message: rejectedMessage)
    }

    // MARK: - presentationFetch

    func test_presentationFetch_rejectsWhenInstanceNotFound() {
        var rejectedCode: String?
        var rejectedMessage: String?
        sut.presentationFetch(
            instanceId: instanceId,
            data: NSDictionary(),
            resolve: { _ in XCTFail("should not resolve") },
            reject: { code, message, _ in
                rejectedCode = code
                rejectedMessage = message
            }
        )
        assertRejectsWithInstanceNotFound(code: rejectedCode, message: rejectedMessage)
    }

    // MARK: - dispose

    func test_dispose_resolves() {
        var resolved = false
        sut.dispose(
            instanceId: instanceId,
            resolve: { _ in resolved = true },
            reject: { _, _, _ in XCTFail("should not reject") }
        )
        XCTAssertTrue(resolved)
    }

    // MARK: - presentationHandleLink

    func test_presentationHandleLink_rejectsWhenInstanceNotFound() {
        var rejectedCode: String?
        var rejectedMessage: String?
        sut.presentationHandleLink(
            instanceId: instanceId,
            url: "https://example.com",
            resolve: { _ in XCTFail("should not resolve") },
            reject: { code, message, _ in
                rejectedCode = code
                rejectedMessage = message
            }
        )
        assertRejectsWithInstanceNotFound(code: rejectedCode, message: rejectedMessage)
    }
}

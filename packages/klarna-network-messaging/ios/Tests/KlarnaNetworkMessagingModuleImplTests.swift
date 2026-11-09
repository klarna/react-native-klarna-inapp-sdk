import XCTest
import KlarnaNetworkMessaging
// `KlarnaSDKError.init` is `@_spi(KLARNA)`; the SPI import is the supported way to build failure fixtures.
@_spi(KLARNA) import KlarnaNetworkCore
@testable import react_native_klarna_network_messaging

final class KlarnaNetworkMessagingModuleImplTests: XCTestCase {

    private let instanceId = "test-instance-id"
    private let moduleName = "KlarnaNetworkMessaging"
    private let errorInstanceNotFound = "No instance found for the given instanceId. Call initialize first."
    private var sut: KlarnaNetworkMessagingModuleImpl!
    private var fakeInitiator: FakePrequalificationInitiator!
    private var resolvedInstanceIds: [String] = []

    override func setUpWithError() throws {
        fakeInitiator = FakePrequalificationInitiator()
        resolvedInstanceIds = []
        sut = makeSUT(registeredInstanceId: instanceId)
    }

    override func tearDownWithError() throws {
        sut = nil
        fakeInitiator = nil
    }

    // MARK: - initiatePrequalification (instance resolution)

    func test_initiatePrequalification_defaultInit_rejectsWhenInstanceNotFound() {
        // Production init resolves through KlarnaNetworkCoreModuleImpl, where nothing is registered.
        let productionSut = KlarnaNetworkMessagingModuleImpl()
        var rejectedCode: String?
        var rejectedMessage: String?
        productionSut.initiatePrequalification(
            instanceId: instanceId,
            data: ["amount": 9999, "currency": "USD"],
            resolve: { _ in XCTFail("should not resolve") },
            reject: { code, message, _ in
                rejectedCode = code
                rejectedMessage = message
            }
        )
        XCTAssertEqual(rejectedCode, moduleName)
        XCTAssertEqual(rejectedMessage, errorInstanceNotFound)
    }

    func test_initiatePrequalification_unknownInstance_rejectsAndDoesNotInitiate() {
        var rejectedCode: String?
        var rejectedMessage: String?
        var rejectedError: Error?
        sut.initiatePrequalification(
            instanceId: "unknown-instance",
            data: ["amount": 9999, "currency": "USD"],
            resolve: { _ in XCTFail("should not resolve") },
            reject: { code, message, error in
                rejectedCode = code
                rejectedMessage = message
                rejectedError = error
            }
        )
        XCTAssertEqual(rejectedCode, moduleName)
        XCTAssertEqual(rejectedMessage, errorInstanceNotFound)
        XCTAssertNil(rejectedError)
        XCTAssertEqual(fakeInitiator.initiateCallCount, 0)
    }

    func test_initiatePrequalification_emptyDataWithUnknownInstance_rejectsInstanceNotFoundFirst() {
        var rejectedMessage: String?
        sut.initiatePrequalification(
            instanceId: "unknown-instance",
            data: NSDictionary(),
            resolve: { _ in XCTFail("should not resolve") },
            reject: { _, message, _ in rejectedMessage = message }
        )
        XCTAssertEqual(rejectedMessage, errorInstanceNotFound)
    }

    func test_initiatePrequalification_callsResolverWithInstanceId() {
        let done = expectation(description: "resolved")
        fakeInitiator.result = .success(.aborted)

        sut.initiatePrequalification(
            instanceId: instanceId,
            data: ["amount": 9999, "currency": "USD"],
            resolve: { _ in done.fulfill() },
            reject: { _, _, _ in XCTFail("should not reject") }
        )

        wait(for: [done], timeout: 2)
        XCTAssertEqual(resolvedInstanceIds, [instanceId])
    }

    // MARK: - initiatePrequalification (input validation)

    func test_initiatePrequalification_missingAmount_rejectsWithAmountRequiredAndDoesNotInitiate() {
        assertRejectsWithoutInitiating(data: ["currency": "USD"], expectedMessage: "Amount is required")
    }

    func test_initiatePrequalification_fractionalAmount_rejectsWithAmountRequiredAndDoesNotInitiate() {
        assertRejectsWithoutInitiating(
            data: ["amount": NSNumber(value: 99.99), "currency": "USD"],
            expectedMessage: "Amount is required"
        )
    }

    func test_initiatePrequalification_missingCurrency_rejectsWithCurrencyRequiredAndDoesNotInitiate() {
        assertRejectsWithoutInitiating(data: ["amount": 100], expectedMessage: "Currency is required")
    }

    func test_initiatePrequalification_emptyCurrency_rejectsWithCurrencyRequiredAndDoesNotInitiate() {
        assertRejectsWithoutInitiating(data: ["amount": 100, "currency": ""], expectedMessage: "Currency is required")
    }

    func test_initiatePrequalification_emptyData_rejectsWithAmountRequiredFirst() {
        assertRejectsWithoutInitiating(data: NSDictionary(), expectedMessage: "Amount is required")
    }

    // MARK: - initiatePrequalification (data forwarding)

    func test_initiatePrequalification_forwardsAmountAndCurrencyToSdk() throws {
        let done = expectation(description: "resolved")
        fakeInitiator.result = .success(.aborted)

        sut.initiatePrequalification(
            instanceId: instanceId,
            data: ["amount": 12500, "currency": "SEK"],
            resolve: { _ in done.fulfill() },
            reject: { _, _, _ in XCTFail("should not reject") }
        )

        wait(for: [done], timeout: 2)
        XCTAssertEqual(fakeInitiator.receivedData.count, 1)
        let forwarded = try XCTUnwrap(fakeInitiator.receivedData.first)
        XCTAssertEqual(forwarded.amount, 12500)
        XCTAssertEqual(forwarded.currency, "SEK")
    }

    func test_initiatePrequalification_largeAmount_forwardsInt64Unchanged() throws {
        let done = expectation(description: "resolved")
        fakeInitiator.result = .success(.aborted)

        sut.initiatePrequalification(
            instanceId: instanceId,
            data: ["amount": NSNumber(value: Int64(9_007_199_254_740_991)), "currency": "EUR"],
            resolve: { _ in done.fulfill() },
            reject: { _, _, _ in XCTFail("should not reject") }
        )

        wait(for: [done], timeout: 2)
        let forwarded = try XCTUnwrap(fakeInitiator.receivedData.first)
        XCTAssertEqual(forwarded.amount, Int64(9_007_199_254_740_991))
    }

    // MARK: - initiatePrequalification (success)

    func test_initiatePrequalification_approved_resolvesWithStatusAndIsoExpiresAt() throws {
        let done = expectation(description: "resolved")
        fakeInitiator.result = .success(.approved(expiresAt: Date(timeIntervalSince1970: 0)))
        var resolvedValue: Any?

        sut.initiatePrequalification(
            instanceId: instanceId,
            data: ["amount": 9999, "currency": "USD"],
            resolve: { value in
                resolvedValue = value
                done.fulfill()
            },
            reject: { _, _, _ in XCTFail("should not reject") }
        )

        wait(for: [done], timeout: 2)
        let dictionary = try XCTUnwrap(resolvedValue as? [String: Any])
        XCTAssertEqual(dictionary["status"] as? String, "APPROVED")
        XCTAssertEqual(dictionary["expiresAt"] as? String, "1970-01-01T00:00:00.000Z")
        XCTAssertEqual(Set(dictionary.keys), ["status", "expiresAt"])
    }

    func test_initiatePrequalification_aborted_resolvesWithStatusOnly() throws {
        let done = expectation(description: "resolved")
        fakeInitiator.result = .success(.aborted)
        var resolvedValue: Any?

        sut.initiatePrequalification(
            instanceId: instanceId,
            data: ["amount": 9999, "currency": "USD"],
            resolve: { value in
                resolvedValue = value
                done.fulfill()
            },
            reject: { _, _, _ in XCTFail("should not reject") }
        )

        wait(for: [done], timeout: 2)
        let dictionary = try XCTUnwrap(resolvedValue as? [String: Any])
        XCTAssertEqual(dictionary["status"] as? String, "ABORTED")
        XCTAssertEqual(dictionary.count, 1)
    }

    // MARK: - initiatePrequalification (failure)

    func test_initiatePrequalification_operationFailed_rejectsWithNameAndMessage() {
        assertFailure(name: "OPERATION_FAILED", message: "The prequalification operation failed.")
    }

    func test_initiatePrequalification_initiateAlreadyInProgress_rejectsWithNameAndMessage() {
        assertFailure(name: "INITIATE_ALREADY_IN_PROGRESS", message: "A prequalification is already in progress.")
    }

    // MARK: - initiatePrequalification (threading)

    func test_initiatePrequalification_calledOffMain_initiatesAsynchronouslyOnMainThread() {
        let initiated = expectation(description: "initiated")
        let resolved = expectation(description: "resolved")
        fakeInitiator.result = .success(.aborted)
        fakeInitiator.onInitiate = { initiated.fulfill() }
        var resolvedOnMain: Bool?

        // Resolution of the SDK instance happens on the calling thread; the SDK call must hop to main.
        DispatchQueue.global().async { [sut, instanceId] in
            sut!.initiatePrequalification(
                instanceId: instanceId,
                data: ["amount": 9999, "currency": "USD"],
                resolve: { _ in
                    resolvedOnMain = Thread.isMainThread
                    resolved.fulfill()
                },
                reject: { _, _, _ in XCTFail("should not reject") }
            )
        }

        wait(for: [initiated, resolved], timeout: 2)
        XCTAssertEqual(fakeInitiator.initiateOnMainThread, [true])
        XCTAssertEqual(resolvedOnMain, true)
    }

    func test_initiatePrequalification_calledOnMain_doesNotInitiateSynchronously() {
        let resolved = expectation(description: "resolved")
        fakeInitiator.result = .success(.aborted)

        sut.initiatePrequalification(
            instanceId: instanceId,
            data: ["amount": 9999, "currency": "USD"],
            resolve: { _ in resolved.fulfill() },
            reject: { _, _, _ in XCTFail("should not reject") }
        )

        XCTAssertEqual(fakeInitiator.initiateCallCount, 0, "the SDK call must be dispatched, not run inline")
        wait(for: [resolved], timeout: 2)
        XCTAssertEqual(fakeInitiator.initiateCallCount, 1)
    }

    // MARK: - dispose

    func test_dispose_resolvesWithNil() {
        var resolved = false
        var resolvedValue: Any?
        sut.dispose(
            instanceId: instanceId,
            resolve: { value in
                resolved = true
                resolvedValue = value
            },
            reject: { _, _, _ in XCTFail("should not reject") }
        )
        XCTAssertTrue(resolved)
        XCTAssertNil(resolvedValue)
    }

    // MARK: - klarnaMessagingPrequalificationData(from:)

    func test_parse_validData_returnsData() throws {
        let result = klarnaMessagingPrequalificationData(from: ["amount": 9999, "currency": "USD"])
        let data = try result.get()
        XCTAssertEqual(data.amount, 9999)
        XCTAssertEqual(data.currency, "USD")
    }

    func test_parse_largeAmount_preservesInt64() throws {
        let result = klarnaMessagingPrequalificationData(
            from: ["amount": NSNumber(value: Int64(9_007_199_254_740_991)), "currency": "SEK"]
        )
        XCTAssertEqual(try result.get().amount, 9_007_199_254_740_991)
    }

    func test_parse_wholeDoubleAmount_isAccepted() throws {
        // JS numbers arrive as doubles over the bridge.
        let result = klarnaMessagingPrequalificationData(from: ["amount": NSNumber(value: 12500.0), "currency": "EUR"])
        XCTAssertEqual(try result.get().amount, 12500)
    }

    func test_parse_missingAmount_failsWithAmountRequired() {
        assertParseFails(["currency": "USD"], .amountRequired)
    }

    func test_parse_stringAmount_failsWithAmountRequired() {
        assertParseFails(["amount": "9999", "currency": "USD"], .amountRequired)
    }

    func test_parse_booleanAmount_failsWithAmountRequired() {
        assertParseFails(["amount": NSNumber(value: true), "currency": "USD"], .amountRequired)
    }

    func test_parse_fractionalAmount_failsWithAmountRequired() {
        assertParseFails(["amount": NSNumber(value: 99.99), "currency": "USD"], .amountRequired)
    }

    func test_parse_nonFiniteAmount_failsWithAmountRequired() {
        assertParseFails(["amount": NSNumber(value: Double.nan), "currency": "USD"], .amountRequired)
        assertParseFails(["amount": NSNumber(value: Double.infinity), "currency": "USD"], .amountRequired)
    }

    func test_parse_outOfInt64RangeAmount_failsWithAmountRequired() {
        assertParseFails(["amount": NSNumber(value: 1e30), "currency": "USD"], .amountRequired)
    }

    func test_parse_int64Boundary_rejectsFirstValueAboveMaxAndAcceptsMin() throws {
        assertParseFails(["amount": NSNumber(value: 9_223_372_036_854_775_808.0), "currency": "USD"], .amountRequired)

        let result = klarnaMessagingPrequalificationData(
            from: ["amount": NSNumber(value: -9_223_372_036_854_775_808.0), "currency": "USD"]
        )
        XCTAssertEqual(try result.get().amount, Int64.min)
    }

    func test_parse_missingCurrency_failsWithCurrencyRequired() {
        assertParseFails(["amount": 100], .currencyRequired)
    }

    func test_parse_emptyCurrency_failsWithCurrencyRequired() {
        assertParseFails(["amount": 100, "currency": ""], .currencyRequired)
    }

    func test_parse_nonStringCurrency_failsWithCurrencyRequired() {
        assertParseFails(["amount": 100, "currency": 42], .currencyRequired)
    }

    func test_parse_missingAmountAndCurrency_reportsAmountFirst() {
        assertParseFails(NSDictionary(), .amountRequired)
    }

    // MARK: - KlarnaMessagingPrequalificationResult.toDictionary()

    func test_toDictionary_approved_containsStatusAndIsoExpiresAt() throws {
        let expiresAt = Date(timeIntervalSince1970: 1_790_000_000.123)

        let dictionary = KlarnaMessagingPrequalificationResult.approved(expiresAt: expiresAt).toDictionary()

        XCTAssertEqual(dictionary["status"] as? String, "APPROVED")
        XCTAssertEqual(Set(dictionary.keys), ["status", "expiresAt"])
        let iso = try XCTUnwrap(dictionary["expiresAt"] as? String)
        XCTAssertTrue(iso.hasSuffix("Z"), "expected UTC designator, got \(iso)")
        XCTAssertTrue(iso.contains(".123"), "expected fractional seconds, got \(iso)")

        let parser = ISO8601DateFormatter()
        parser.formatOptions = [.withInternetDateTime, .withFractionalSeconds]
        let parsed = try XCTUnwrap(parser.date(from: iso))
        XCTAssertEqual(parsed.timeIntervalSince1970, expiresAt.timeIntervalSince1970, accuracy: 0.001)
    }

    func test_toDictionary_approvedWholeSecond_stillHasFractionalPart() throws {
        let iso = try XCTUnwrap(
            KlarnaMessagingPrequalificationResult
                .approved(expiresAt: Date(timeIntervalSince1970: 0))
                .toDictionary()["expiresAt"] as? String
        )
        XCTAssertEqual(iso, "1970-01-01T00:00:00.000Z")
    }

    func test_toDictionary_aborted_containsOnlyStatus() {
        let dictionary = KlarnaMessagingPrequalificationResult.aborted.toDictionary()

        XCTAssertEqual(dictionary["status"] as? String, "ABORTED")
        XCTAssertEqual(dictionary.count, 1)
    }

    // MARK: - Helpers

    private func makeSUT(registeredInstanceId: String) -> KlarnaNetworkMessagingModuleImpl {
        let initiator = fakeInitiator!
        return KlarnaNetworkMessagingModuleImpl(initiatorResolver: { [weak self] requestedId in
            self?.resolvedInstanceIds.append(requestedId)
            return requestedId == registeredInstanceId ? initiator : nil
        })
    }

    private func assertRejectsWithoutInitiating(
        data: NSDictionary,
        expectedMessage: String,
        file: StaticString = #filePath,
        line: UInt = #line
    ) {
        var rejectedCode: String?
        var rejectedMessage: String?
        var rejectedError: Error?
        sut.initiatePrequalification(
            instanceId: instanceId,
            data: data,
            resolve: { _ in XCTFail("should not resolve", file: file, line: line) },
            reject: { code, message, error in
                rejectedCode = code
                rejectedMessage = message
                rejectedError = error
            }
        )
        // Validation failures are reported synchronously, before any main-thread hop.
        XCTAssertEqual(rejectedCode, moduleName, file: file, line: line)
        XCTAssertEqual(rejectedMessage, expectedMessage, file: file, line: line)
        XCTAssertNil(rejectedError, file: file, line: line)
        XCTAssertEqual(fakeInitiator.initiateCallCount, 0, file: file, line: line)
        // Drain the main queue to be sure nothing was dispatched to the SDK afterwards.
        let drained = expectation(description: "main queue drained")
        DispatchQueue.main.async { drained.fulfill() }
        wait(for: [drained], timeout: 2)
        XCTAssertEqual(fakeInitiator.initiateCallCount, 0, file: file, line: line)
    }

    private func assertFailure(
        name: String,
        message: String,
        file: StaticString = #filePath,
        line: UInt = #line
    ) {
        let done = expectation(description: "rejected")
        fakeInitiator.result = .failure(KlarnaSDKError(name: name, message: message))
        var rejectedCode: String?
        var rejectedMessage: String?
        var rejectedError: Error?

        sut.initiatePrequalification(
            instanceId: instanceId,
            data: ["amount": 9999, "currency": "USD"],
            resolve: { _ in XCTFail("should not resolve", file: file, line: line) },
            reject: { code, msg, error in
                rejectedCode = code
                rejectedMessage = msg
                rejectedError = error
                done.fulfill()
            }
        )

        wait(for: [done], timeout: 2)
        XCTAssertEqual(rejectedCode, name, file: file, line: line)
        XCTAssertEqual(rejectedMessage, message, file: file, line: line)
        XCTAssertNil(rejectedError, file: file, line: line)
    }

    private func assertParseFails(
        _ data: NSDictionary,
        _ expected: KlarnaMessagingPrequalificationInputError,
        file: StaticString = #filePath,
        line: UInt = #line
    ) {
        switch klarnaMessagingPrequalificationData(from: data) {
        case .success:
            XCTFail("expected failure \(expected)", file: file, line: line)
        case .failure(let error):
            XCTAssertEqual(error, expected, file: file, line: line)
        }
    }
}

// MARK: - FakePrequalificationInitiator

private final class FakePrequalificationInitiator: KlarnaMessagingPrequalificationInitiating {
    var result: KlarnaResult<KlarnaMessagingPrequalificationResult> = .success(.aborted)
    var onInitiate: (() -> Void)?
    private(set) var receivedData: [KlarnaMessagingPrequalificationData] = []
    private(set) var initiateOnMainThread: [Bool] = []

    var initiateCallCount: Int { receivedData.count }

    func initiate(data: KlarnaMessagingPrequalificationData, callback: @escaping KlarnaMessagingPrequalificationCallback) {
        receivedData.append(data)
        initiateOnMainThread.append(Thread.isMainThread)
        onInitiate?()
        callback(result)
    }
}

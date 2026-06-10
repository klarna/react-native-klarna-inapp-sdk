import XCTest
@testable import react_native_klarna_network_payment

// MARK: - KlarnaPaymentPresentationMappingTests
//
// Tests for `klarnaPaymentPresentationData(from:)` in KlarnaPaymentPresentationMapping.swift.
//
// Output mapping (KlarnaPaymentPresentationContent.toDictionary()) is NOT covered here because
// KlarnaPaymentPresentationContent is a closed SDK output type with no public initializer —
// it can only be obtained from a live SDK callback, which is outside the scope of unit tests.

final class KlarnaPaymentPresentationMappingTests: XCTestCase {

    // MARK: - Helpers

    /// Returns the minimal valid dictionary required for a non-nil result.
    private func validDict(
        amount: NSNumber = NSNumber(value: 5000.0),
        currency: String = "USD"
    ) -> NSMutableDictionary {
        let dict = NSMutableDictionary()
        dict["amount"] = amount
        dict["currency"] = currency
        return dict
    }

    // MARK: - Missing required fields

    func test_klarnaPaymentPresentationData_missingAmount_returnsNil() {
        // Arrange
        let dict: NSDictionary = ["currency": "USD"]

        // Act
        let result = klarnaPaymentPresentationData(from: dict)

        // Assert
        XCTAssertNil(result)
    }

    func test_klarnaPaymentPresentationData_missingCurrency_returnsNil() {
        // Arrange
        let dict: NSDictionary = ["amount": NSNumber(value: 5000.0)]

        // Act
        let result = klarnaPaymentPresentationData(from: dict)

        // Assert
        XCTAssertNil(result)
    }

    func test_klarnaPaymentPresentationData_emptyDictionary_returnsNil() {
        // Arrange
        let dict = NSDictionary()

        // Act
        let result = klarnaPaymentPresentationData(from: dict)

        // Assert
        XCTAssertNil(result)
    }

    // MARK: - Required fields mapping

    func test_klarnaPaymentPresentationData_requiredFieldsOnly_mapsAmountAndCurrency() {
        // Arrange
        let dict = validDict(amount: NSNumber(value: 9999.0), currency: "EUR")

        // Act
        let result = klarnaPaymentPresentationData(from: dict)

        // Assert
        XCTAssertNotNil(result)
        XCTAssertEqual(result?.amount, 9999)
        XCTAssertEqual(result?.currency, "EUR")
    }

    func test_klarnaPaymentPresentationData_requiredFieldsOnly_optionalsAreNil() {
        // Arrange
        let dict = validDict()

        // Act
        let result = klarnaPaymentPresentationData(from: dict)

        // Assert
        XCTAssertNotNil(result)
        XCTAssertNil(result?.intent)
        XCTAssertNil(result?.paymentProgramEnablementCodes)
        XCTAssertNil(result?.subscriptionBillingInterval)
        XCTAssertNil(result?.subscriptionBillingIntervalFrequency)
    }

    // MARK: - Amount conversion (Double → Int64)

    func test_klarnaPaymentPresentationData_amountWithDecimalFraction_truncatesToInt64() {
        // Arrange — JS passes all numbers as Double; fractional part must be discarded
        let dict = validDict(amount: NSNumber(value: 1234.99))

        // Act
        let result = klarnaPaymentPresentationData(from: dict)

        // Assert
        XCTAssertEqual(result?.amount, 1234)
    }

    // MARK: - intent mapping

    func test_klarnaPaymentPresentationData_intentPay_mapsToPayCase() {
        // Arrange
        let dict = validDict()
        dict["intent"] = "PAY"

        // Act
        let result = klarnaPaymentPresentationData(from: dict)

        // Assert
        XCTAssertEqual(result?.intent, .pay)
    }

    func test_klarnaPaymentPresentationData_intentSubscribe_mapsToSubscribeCase() {
        // Arrange
        let dict = validDict()
        dict["intent"] = "SUBSCRIBE"

        // Act
        let result = klarnaPaymentPresentationData(from: dict)

        // Assert
        XCTAssertEqual(result?.intent, .subscribe)
    }

    func test_klarnaPaymentPresentationData_intentAddToWallet_mapsToAddToWalletCase() {
        // Arrange
        let dict = validDict()
        dict["intent"] = "ADD_TO_WALLET"

        // Act
        let result = klarnaPaymentPresentationData(from: dict)

        // Assert
        XCTAssertEqual(result?.intent, .addToWallet)
    }

    func test_klarnaPaymentPresentationData_unknownIntentString_yieldsNilIntent() {
        // Arrange
        let dict = validDict()
        dict["intent"] = "unknown_value"

        // Act
        let result = klarnaPaymentPresentationData(from: dict)

        // Assert — unknown raw value must not crash; intent should be nil
        XCTAssertNotNil(result)
        XCTAssertNil(result?.intent)
    }

    func test_klarnaPaymentPresentationData_intentWrongType_yieldsNilIntent() {
        // Arrange — intent is not a String (JS type confusion safety check)
        let dict = validDict()
        dict["intent"] = NSNumber(value: 42)

        // Act
        let result = klarnaPaymentPresentationData(from: dict)

        // Assert
        XCTAssertNotNil(result)
        XCTAssertNil(result?.intent)
    }

    // MARK: - paymentProgramEnablementCodes mapping

    func test_klarnaPaymentPresentationData_withEnablementCodes_mapsArray() {
        // Arrange
        let codes = ["CODE_A", "CODE_B"]
        let dict = validDict()
        dict["paymentProgramEnablementCodes"] = codes

        // Act
        let result = klarnaPaymentPresentationData(from: dict)

        // Assert
        XCTAssertEqual(result?.paymentProgramEnablementCodes, codes)
    }

    func test_klarnaPaymentPresentationData_emptyEnablementCodes_mapsEmptyArray() {
        // Arrange
        let dict = validDict()
        dict["paymentProgramEnablementCodes"] = [String]()

        // Act
        let result = klarnaPaymentPresentationData(from: dict)

        // Assert
        XCTAssertEqual(result?.paymentProgramEnablementCodes, [])
    }

    func test_klarnaPaymentPresentationData_enablementCodesWrongType_yieldsNilCodes() {
        // Arrange — wrong element type; cast to [String] must fail gracefully
        let dict = validDict()
        dict["paymentProgramEnablementCodes"] = "not-an-array"

        // Act
        let result = klarnaPaymentPresentationData(from: dict)

        // Assert
        XCTAssertNotNil(result)
        XCTAssertNil(result?.paymentProgramEnablementCodes)
    }

    // MARK: - subscriptionBillingInterval mapping

    func test_klarnaPaymentPresentationData_billingIntervalDay_mapsToDayCase() {
        // Arrange
        let dict = validDict()
        dict["subscriptionBillingInterval"] = "DAY"

        // Act
        let result = klarnaPaymentPresentationData(from: dict)

        // Assert
        XCTAssertEqual(result?.subscriptionBillingInterval, .day)
    }

    func test_klarnaPaymentPresentationData_billingIntervalWeek_mapsToWeekCase() {
        // Arrange
        let dict = validDict()
        dict["subscriptionBillingInterval"] = "WEEK"

        // Act
        let result = klarnaPaymentPresentationData(from: dict)

        // Assert
        XCTAssertEqual(result?.subscriptionBillingInterval, .week)
    }

    func test_klarnaPaymentPresentationData_billingIntervalMonth_mapsToMonthCase() {
        // Arrange
        let dict = validDict()
        dict["subscriptionBillingInterval"] = "MONTH"

        // Act
        let result = klarnaPaymentPresentationData(from: dict)

        // Assert
        XCTAssertEqual(result?.subscriptionBillingInterval, .month)
    }

    func test_klarnaPaymentPresentationData_billingIntervalYear_mapsToYearCase() {
        // Arrange
        let dict = validDict()
        dict["subscriptionBillingInterval"] = "YEAR"

        // Act
        let result = klarnaPaymentPresentationData(from: dict)

        // Assert
        XCTAssertEqual(result?.subscriptionBillingInterval, .year)
    }

    func test_klarnaPaymentPresentationData_unknownBillingInterval_yieldsNilInterval() {
        // Arrange
        let dict = validDict()
        dict["subscriptionBillingInterval"] = "quarterly"

        // Act
        let result = klarnaPaymentPresentationData(from: dict)

        // Assert
        XCTAssertNotNil(result)
        XCTAssertNil(result?.subscriptionBillingInterval)
    }

    // MARK: - subscriptionBillingIntervalFrequency mapping

    func test_klarnaPaymentPresentationData_billingIntervalFrequency_mapsToInt() {
        // Arrange — JS bridge delivers numbers as Double
        let dict = validDict()
        dict["subscriptionBillingIntervalFrequency"] = NSNumber(value: 3.0)

        // Act
        let result = klarnaPaymentPresentationData(from: dict)

        // Assert
        XCTAssertEqual(result?.subscriptionBillingIntervalFrequency, 3)
    }

    func test_klarnaPaymentPresentationData_billingIntervalFrequencyWithFraction_truncatesToInt() {
        // Arrange
        let dict = validDict()
        dict["subscriptionBillingIntervalFrequency"] = NSNumber(value: 2.9)

        // Act
        let result = klarnaPaymentPresentationData(from: dict)

        // Assert
        XCTAssertEqual(result?.subscriptionBillingIntervalFrequency, 2)
    }

    func test_klarnaPaymentPresentationData_billingIntervalFrequencyWrongType_yieldsNilFrequency() {
        // Arrange
        let dict = validDict()
        dict["subscriptionBillingIntervalFrequency"] = "not-a-number"

        // Act
        let result = klarnaPaymentPresentationData(from: dict)

        // Assert
        XCTAssertNotNil(result)
        XCTAssertNil(result?.subscriptionBillingIntervalFrequency)
    }

    // MARK: - Full payload (all fields present)

    func test_klarnaPaymentPresentationData_allFieldsPresent_mapsCorrectly() {
        // Arrange
        let dict = NSMutableDictionary()
        dict["amount"] = NSNumber(value: 15000.0)
        dict["currency"] = "SEK"
        dict["intent"] = "SUBSCRIBE"
        dict["paymentProgramEnablementCodes"] = ["BNPL", "SLICE"]
        dict["subscriptionBillingInterval"] = "MONTH"
        dict["subscriptionBillingIntervalFrequency"] = NSNumber(value: 1.0)

        // Act
        let result = klarnaPaymentPresentationData(from: dict)

        // Assert
        XCTAssertNotNil(result)
        XCTAssertEqual(result?.amount, 15000)
        XCTAssertEqual(result?.currency, "SEK")
        XCTAssertEqual(result?.intent, .subscribe)
        XCTAssertEqual(result?.paymentProgramEnablementCodes, ["BNPL", "SLICE"])
        XCTAssertEqual(result?.subscriptionBillingInterval, .month)
        XCTAssertEqual(result?.subscriptionBillingIntervalFrequency, 1)
    }
}

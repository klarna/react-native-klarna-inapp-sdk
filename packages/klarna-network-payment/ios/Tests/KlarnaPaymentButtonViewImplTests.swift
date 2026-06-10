import XCTest
import KlarnaCore
import KlarnaNetworkPayment
@testable import react_native_klarna_network_payment

final class KlarnaPaymentButtonViewImplTests: XCTestCase {

    // MARK: - makeButton

    func test_makeButton_withUnknownInstanceId_returnsNil() {
        XCTAssertNil(KlarnaPaymentButtonViewImpl.makeButton(
            instanceId: "unknown-instance",
            state: nil,
            intent: "pay",
            shape: nil,
            buttonStyle: nil,
            theme: nil
        ))
    }

    // MARK: - parseState

    func test_parseState_withNil_returnsDefault() {
        XCTAssertEqual(KlarnaPaymentButtonViewImpl.parseState(nil), .default)
    }

    func test_parseState_withDisabled_returnsDisabled() {
        XCTAssertEqual(KlarnaPaymentButtonViewImpl.parseState("disabled"), .disabled)
    }

    func test_parseState_withLoading_returnsLoading() {
        XCTAssertEqual(KlarnaPaymentButtonViewImpl.parseState("loading"), .loading)
    }

    func test_parseState_withUnknownValue_returnsDefault() {
        XCTAssertEqual(KlarnaPaymentButtonViewImpl.parseState("unknown"), .default)
    }

    // MARK: - parseIntent

    func test_parseIntent_withNil_returnsPay() {
        XCTAssertEqual(KlarnaPaymentButtonViewImpl.parseIntent(nil), .pay)
    }

    func test_parseIntent_withPay_returnsPay() {
        XCTAssertEqual(KlarnaPaymentButtonViewImpl.parseIntent("pay"), .pay)
    }

    func test_parseIntent_withSubscribe_returnsSubscribe() {
        XCTAssertEqual(KlarnaPaymentButtonViewImpl.parseIntent("subscribe"), .subscribe)
    }

    func test_parseIntent_withAddToWallet_returnsAddToWallet() {
        XCTAssertEqual(KlarnaPaymentButtonViewImpl.parseIntent("addToWallet"), .addToWallet)
    }

    func test_parseIntent_withUnknownValue_returnsPay() {
        XCTAssertEqual(KlarnaPaymentButtonViewImpl.parseIntent("unknown"), .pay)
    }

    // MARK: - parseShape

    func test_parseShape_withNil_returnsRoundedRect() {
        XCTAssertEqual(KlarnaPaymentButtonViewImpl.parseShape(nil), .roundedRect)
    }

    func test_parseShape_withRoundedRect_returnsRoundedRect() {
        XCTAssertEqual(KlarnaPaymentButtonViewImpl.parseShape("roundedRect"), .roundedRect)
    }

    func test_parseShape_withPill_returnsPill() {
        XCTAssertEqual(KlarnaPaymentButtonViewImpl.parseShape("pill"), .pill)
    }

    func test_parseShape_withRectangle_returnsRectangle() {
        XCTAssertEqual(KlarnaPaymentButtonViewImpl.parseShape("rectangle"), .rectangle)
    }

    func test_parseShape_withUnknownValue_returnsRoundedRect() {
        XCTAssertEqual(KlarnaPaymentButtonViewImpl.parseShape("unknown"), .roundedRect)
    }

    // MARK: - parseStyle

    func test_parseStyle_withNil_returnsFilled() {
        XCTAssertEqual(KlarnaPaymentButtonViewImpl.parseStyle(nil), .filled)
    }

    func test_parseStyle_withFilled_returnsFilled() {
        XCTAssertEqual(KlarnaPaymentButtonViewImpl.parseStyle("filled"), .filled)
    }

    func test_parseStyle_withOutlined_returnsOutlined() {
        XCTAssertEqual(KlarnaPaymentButtonViewImpl.parseStyle("outlined"), .outlined)
    }

    func test_parseStyle_withUnknownValue_returnsFilled() {
        XCTAssertEqual(KlarnaPaymentButtonViewImpl.parseStyle("unknown"), .filled)
    }

    // MARK: - parseTheme

    func test_parseTheme_withNil_returnsDark() {
        XCTAssertEqual(KlarnaPaymentButtonViewImpl.parseTheme(nil), .dark)
    }

    func test_parseTheme_withLight_returnsLight() {
        XCTAssertEqual(KlarnaPaymentButtonViewImpl.parseTheme("light"), .light)
    }

    func test_parseTheme_withDark_returnsDark() {
        XCTAssertEqual(KlarnaPaymentButtonViewImpl.parseTheme("dark"), .dark)
    }

    func test_parseTheme_withAutomatic_returnsAutomatic() {
        XCTAssertEqual(KlarnaPaymentButtonViewImpl.parseTheme("automatic"), .automatic)
    }

    func test_parseTheme_withUnknownValue_returnsDark() {
        XCTAssertEqual(KlarnaPaymentButtonViewImpl.parseTheme("unknown"), .dark)
    }
}

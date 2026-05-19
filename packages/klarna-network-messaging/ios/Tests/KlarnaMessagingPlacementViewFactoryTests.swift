import XCTest
import KlarnaCore
import KlarnaNetworkMessaging
@testable import react_native_klarna_network_messaging

private let autoSize = PlacementType.autoSize.rawValue
private let badge = PlacementType.badge.rawValue

class KlarnaMessagingPlacementViewFactoryTests: XCTestCase {

    // MARK: - parseTheme

    func testParseTheme_lightMapsToLight() {
        XCTAssertEqual(KlarnaMessagingPlacementViewFactory.parseTheme("light"), .light)
    }

    func testParseTheme_darkMapsToDark() {
        XCTAssertEqual(KlarnaMessagingPlacementViewFactory.parseTheme("dark"), .dark)
    }

    func testParseTheme_automaticMapsToAutomatic() {
        XCTAssertEqual(KlarnaMessagingPlacementViewFactory.parseTheme("automatic"), .automatic)
    }

    func testParseTheme_unknownReturnsNil() {
        XCTAssertNil(KlarnaMessagingPlacementViewFactory.parseTheme("neon"))
    }

    func testParseTheme_emptyReturnsNil() {
        XCTAssertNil(KlarnaMessagingPlacementViewFactory.parseTheme(""))
    }

    func testParseTheme_isCaseSensitive() {
        XCTAssertNil(KlarnaMessagingPlacementViewFactory.parseTheme("LIGHT"))
        XCTAssertNil(KlarnaMessagingPlacementViewFactory.parseTheme("Dark"))
    }

    // MARK: - makeConfiguration

    func testMakeConfiguration_badgeReturnsBadgeVariant() {
        let config = KlarnaMessagingPlacementViewFactory.makeConfiguration(
            placementType: badge,
            theme: .light,
            amount: 1000,
            currency: "USD"
        )
        if case .creditPromotionBadge = config {
            // expected
        } else {
            XCTFail("Expected .creditPromotionBadge case, got \(config)")
        }
    }

    func testMakeConfiguration_autoSizeReturnsAutoSizeVariant() {
        let config = KlarnaMessagingPlacementViewFactory.makeConfiguration(
            placementType: autoSize,
            theme: .dark,
            amount: 2000,
            currency: "EUR"
        )
        if case .creditPromotionAutoSize = config {
            // expected
        } else {
            XCTFail("Expected .creditPromotionAutoSize case, got \(config)")
        }
    }

    func testMakeConfiguration_unknownDefaultsToAutoSize() {
        let config = KlarnaMessagingPlacementViewFactory.makeConfiguration(
            placementType: "SomethingElse",
            theme: .automatic,
            amount: 500,
            currency: "SEK"
        )
        if case .creditPromotionAutoSize = config {
            // expected
        } else {
            XCTFail("Expected .creditPromotionAutoSize fallback, got \(config)")
        }
    }

    func testMakeConfiguration_emptyDefaultsToAutoSize() {
        let config = KlarnaMessagingPlacementViewFactory.makeConfiguration(
            placementType: "",
            theme: .automatic,
            amount: 0,
            currency: "GBP"
        )
        if case .creditPromotionAutoSize = config {
            // expected
        } else {
            XCTFail("Expected .creditPromotionAutoSize fallback for empty string, got \(config)")
        }
    }

    func testMakeConfiguration_nilThemeFlowsThroughToConfiguration() {
        // When theme is nil the factory must not synthesize a default; the
        // configuration must carry nil so the native SDK applies its own.
        let autoSizeConfig = KlarnaMessagingPlacementViewFactory.makeConfiguration(
            placementType: autoSize,
            theme: nil,
            amount: 1000,
            currency: "USD"
        )
        if case .creditPromotionAutoSize(_, _, let theme) = autoSizeConfig {
            XCTAssertNil(theme)
        } else {
            XCTFail("Expected .creditPromotionAutoSize, got \(autoSizeConfig)")
        }

        let badgeConfig = KlarnaMessagingPlacementViewFactory.makeConfiguration(
            placementType: badge,
            theme: nil,
            amount: 1000,
            currency: "USD"
        )
        if case .creditPromotionBadge(_, _, let theme) = badgeConfig {
            XCTAssertNil(theme)
        } else {
            XCTFail("Expected .creditPromotionBadge, got \(badgeConfig)")
        }
    }

    func testMakeConfiguration_explicitThemeFlowsThroughToConfiguration() {
        let config = KlarnaMessagingPlacementViewFactory.makeConfiguration(
            placementType: autoSize,
            theme: .dark,
            amount: 1000,
            currency: "USD"
        )
        if case .creditPromotionAutoSize(_, _, let theme) = config {
            XCTAssertEqual(theme, .dark)
        } else {
            XCTFail("Expected .creditPromotionAutoSize, got \(config)")
        }
    }

    func testMakeConfiguration_caseSensitiveOnBadge() {
        // Only exact "CreditPromotionBadge" returns the badge variant.
        let config = KlarnaMessagingPlacementViewFactory.makeConfiguration(
            placementType: "creditpromotionbadge",
            theme: .automatic,
            amount: 100,
            currency: "NOK"
        )
        if case .creditPromotionAutoSize = config {
            // expected
        } else {
            XCTFail("Expected .creditPromotionAutoSize for lowercase input, got \(config)")
        }
    }

    // MARK: - createPlacementView

    func testCreatePlacementView_returnsNilForInvalidKlarnaObject() {
        // Any non-Klarna object should cause the factory to return nil rather than crash.
        let invalidKlarna = NSObject()
        let view = KlarnaMessagingPlacementViewFactory.createPlacementView(
            klarna: invalidKlarna,
            placementType: autoSize,
            theme: "automatic",
            amount: 1000,
            currency: "USD"
        )
        XCTAssertNil(view)
    }
}

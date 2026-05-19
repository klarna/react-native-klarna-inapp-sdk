import Foundation
import UIKit
import KlarnaNetworkMessaging
import KlarnaNetworkCore
import KlarnaCore
@_spi(RNKlarnaNetworkCore) import react_native_klarna_network_core

enum PlacementType: String {
    case autoSize = "CreditPromotionAutoSize"
    case badge = "CreditPromotionBadge"

    init(rawString: String?) {
        self = PlacementType(rawValue: rawString ?? "") ?? .autoSize
    }
}

@objc(KlarnaMessagingPlacementViewFactory)
public class KlarnaMessagingPlacementViewFactory: NSObject {

    @objc public static let defaultPlacementType = PlacementType.autoSize.rawValue

    @objc public static func resolveKlarna(instanceId: String) -> AnyObject? {
        return KlarnaNetworkCoreModuleImpl.getInstance(instanceId: instanceId)
    }

    static func parseTheme(_ value: String) -> KlarnaTheme? {
        switch value {
        case "light": return .light
        case "dark": return .dark
        case "automatic": return .automatic
        default: return nil
        }
    }

    static func makeConfiguration(
        placementType: String,
        theme: KlarnaTheme?,
        amount: Int64,
        currency: String
    ) -> KlarnaMessagingPlacementConfiguration {
        let intAmount = Int(amount)
        switch PlacementType(rawString: placementType) {
        case .badge:
            return .creditPromotionBadge(amount: intAmount, currency: currency, theme: theme)
        case .autoSize:
            return .creditPromotionAutoSize(amount: intAmount, currency: currency, theme: theme)
        }
    }

    @objc public static func createPlacementView(
        klarna: AnyObject,
        placementType: String,
        theme: String,
        amount: Int64,
        currency: String
    ) -> UIView? {
        guard let klarnaInstance = klarna as? Klarna else {
            return nil
        }

        let parsedTheme = parseTheme(theme)
        let configuration = makeConfiguration(
            placementType: placementType,
            theme: parsedTheme,
            amount: amount,
            currency: currency
        )
        let view = KlarnaMessagingPlacementView(klarna: klarnaInstance, configuration: configuration)
        // Align the trait collection with the explicit theme so the SDK's
        // trait-driven colors don't conflict with theme-driven colors when the
        // JS prop opposes the phone's system mode (e.g. theme=dark on a light phone).
        switch parsedTheme {
        case .light:
            view.overrideUserInterfaceStyle = .light
        case .dark:
            view.overrideUserInterfaceStyle = .dark
        default:
            view.overrideUserInterfaceStyle = .unspecified
        }
        // Match Android: render the SDK placement on a transparent surface so the
        // host app's background shows through. The wrapper re-applies this on every
        // layout pass (see clearBackgrounds:) because the SDK repaints the root and
        // its UITextView with theme-derived colors after construction.
        view.backgroundColor = .clear
        return view
    }

    /// Sets `backgroundColor = .clear` on the SDK root and on any UITextView in its
    /// subtree so the host app's background shows through. Called from the Fabric
    /// wrapper's `layoutSubviews` to survive SDK repaints (the SDK resets the root
    /// to its theme background and the UITextView to `systemBackgroundColor`).
    @objc public static func clearBackgrounds(_ view: UIView) {
        view.backgroundColor = .clear
        for subview in view.subviews {
            if subview is UITextView {
                subview.backgroundColor = .clear
            }
            clearBackgrounds(subview)
        }
    }
}

import Foundation
import KlarnaCore
import KlarnaNetworkCore
import KlarnaNetworkPayment
import KlarnaNetworkPaymentButton
@_spi(RNKlarnaNetworkCore) import react_native_klarna_network_core

@objc(KlarnaPaymentButtonViewImpl)
public class KlarnaPaymentButtonViewImpl: NSObject {

    @objc public static func updateState(_ state: String?, onButton button: UIView?) {
        guard let paymentButton = button as? KlarnaPaymentButton else { return }
        paymentButton.state = parseState(state)
    }

    @objc public static func makeButton(
        instanceId: String,
        state: String?,
        intent: String?,
        shape: String?,
        buttonStyle: String?,
        theme: String?
    ) -> UIView? {
        guard let klarna = KlarnaNetworkCoreModuleImpl.getInstance(instanceId: instanceId) else {
            return nil
        }
        let configuration = KlarnaPaymentButtonConfiguration(
            state: parseState(state),
            intent: parseIntent(intent),
            shape: parseShape(shape),
            style: parseStyle(buttonStyle),
            theme: parseTheme(theme)
        )
        return KlarnaPaymentButton(klarna: klarna, configuration: configuration)
    }

    static func parseState(_ value: String?) -> KlarnaButtonState {
        switch value {
        case "disabled": return .disabled
        case "loading": return .loading
        default: return .default
        }
    }

    static func parseIntent(_ value: String?) -> KlarnaPaymentButtonIntent {
        switch value {
        case "subscribe": return .subscribe
        case "addToWallet": return .addToWallet
        default: return .pay
        }
    }

    static func parseShape(_ value: String?) -> KlarnaButtonShape {
        switch value {
        case "roundedRect": return .roundedRect
        case "pill": return .pill
        case "rectangle": return .rectangle
        default: return .roundedRect
        }
    }

    static func parseStyle(_ value: String?) -> KlarnaButtonStyle {
        switch value {
        case "filled": return .filled
        case "outlined": return .outlined
        default: return .filled
        }
    }

    static func parseTheme(_ value: String?) -> KlarnaTheme {
        switch value {
        case "light": return .light
        case "dark": return .dark
        case "automatic": return .automatic
        default: return .dark
        }
    }
}

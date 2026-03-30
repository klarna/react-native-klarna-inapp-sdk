import Foundation
import KlarnaCore
import KlarnaPayments

/// A Swift class that implements the pure-Swift `KlarnaExpressCheckoutButtonDelegate` protocol
/// and forwards events to closures that can be set from Obj-C++ wrapper code.
@objc public class KlarnaExpressCheckoutButtonDelegateProxy: NSObject, KlarnaExpressCheckoutButtonDelegate {

    @objc public var onAuthorizedHandler: ((_ response: NSDictionary) -> Void)?
    @objc public var onErrorHandler: ((_ name: String, _ message: String, _ isFatal: Bool) -> Void)?

    public func onAuthorized(
        view: KlarnaExpressCheckoutButton,
        response: KlarnaExpressCheckoutButtonAuthorizationResponse
    ) {
        let responseDict: NSDictionary = [
            "showForm": response.showForm,
            "approved": response.approved,
            "finalizedRequired": response.finalizedRequired,
            "clientToken": response.clientToken ?? "",
            "authorizationToken": response.authorizationToken ?? "",
            "sessionId": response.sessionId,
            "collectedShippingAddress": response.collectedShippingAddress ?? "",
            "merchantReference1": response.merchantReference1 ?? "",
            "merchantReference2": response.merchantReference2 ?? "",
        ]
        onAuthorizedHandler?(responseDict)
    }

    public func onError(
        view: KlarnaExpressCheckoutButton,
        error: KlarnaError
    ) {
        onErrorHandler?(error.name, error.message, error.isFatal)
    }
}

// MARK: - Helper to create KEC options from Obj-C++

@objc public class KlarnaExpressCheckoutHelper: NSObject {

    @objc public static func createButton(
        sessionType: String?,
        clientId: String?,
        clientToken: String?,
        locale: String?,
        returnUrl: String,
        delegate: KlarnaExpressCheckoutButtonDelegateProxy,
        theme: String?,
        shape: String?,
        buttonStyle: String?,
        autoFinalize: Bool,
        collectShippingAddress: Bool,
        sessionData: String?,
        environment: String?,
        region: String?
    ) -> KlarnaExpressCheckoutButton? {
        // Build session options based on the explicit sessionType.
        let sessionOptions: KlarnaExpressCheckoutSessionOptions
        if sessionType == "clientToken" {
            sessionOptions = KlarnaExpressCheckoutSessionOptions.ServerSideSession(
                clientToken: clientToken ?? "",
                autoFinalize: autoFinalize,
                collectShippingAddress: collectShippingAddress,
                sessionData: sessionData
            )
        } else {
            sessionOptions = KlarnaExpressCheckoutSessionOptions.ClientSideSession(
                clientId: clientId ?? "",
                sessionData: sessionData ?? "",
                autoFinalize: autoFinalize,
                collectShippingAddress: collectShippingAddress
            )
        }

        // Build style configuration
        var buttonTheme: KlarnaButtonTheme?
        if let theme = theme {
            switch theme {
            case "light": buttonTheme = .light
            case "dark": buttonTheme = .dark
            case "auto": buttonTheme = .auto
            default: break
            }
        }

        var buttonShape: KlarnaButtonShape?
        if let shape = shape {
            switch shape {
            case "roundedRect": buttonShape = .roundedRect
            case "pill": buttonShape = .pill
            case "rectangle": buttonShape = .rectangle
            default: break
            }
        }

        var klarnaButtonStyle: KlarnaButtonStyle?
        if let buttonStyle = buttonStyle {
            switch buttonStyle {
            case "filled": klarnaButtonStyle = .filled
            case "outlined": klarnaButtonStyle = .outlined
            default: break
            }
        }

        let styleConfig = KlarnaExpressCheckoutButtonStyleConfiguration(
            theme: buttonTheme,
            shape: buttonShape,
            style: klarnaButtonStyle
        )

        // Build environment
        var klarnaEnvironment: KlarnaEnvironment?
        if let environment = environment {
            switch environment {
            case "playground": klarnaEnvironment = .playground
            case "production": klarnaEnvironment = .production
            default: break
            }
        }

        // Build region
        var klarnaRegion: KlarnaRegion?
        if let region = region {
            switch region {
            case "eu": klarnaRegion = .eu
            case "na": klarnaRegion = .na
            case "oc": klarnaRegion = .oc
            default: break
            }
        }

        let options = KlarnaExpressCheckoutButtonOptions(
            sessionOptions: sessionOptions,
            returnUrl: returnUrl,
            delegate: delegate,
            locale: locale,
            styleConfiguration: styleConfig,
            environment: klarnaEnvironment,
            region: klarnaRegion
        )

        return KlarnaExpressCheckoutButton(options: options)
    }
}

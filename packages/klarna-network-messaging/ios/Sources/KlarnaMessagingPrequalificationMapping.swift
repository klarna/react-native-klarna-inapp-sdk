import Foundation
import KlarnaNetworkMessaging

// MARK: - Input parsing

/// Local (pre-SDK) validation failures for the prequalification payload coming from JS.
enum KlarnaMessagingPrequalificationInputError: Error, Equatable {
    case amountRequired
    case currencyRequired
}

/// Parses the `{ amount: number, currency: string }` payload sent from JS.
///
/// `amount` must be a whole number representable as `Int64`. Booleans (which bridge as `NSNumber`)
/// and fractional or non-finite values are rejected. Positivity is enforced by the TS layer and the
/// native SDK, not here.
func klarnaMessagingPrequalificationData(
    from data: NSDictionary
) -> Result<KlarnaMessagingPrequalificationData, KlarnaMessagingPrequalificationInputError> {
    guard let number = data["amount"] as? NSNumber,
          CFGetTypeID(number) != CFBooleanGetTypeID(),
          let amount = int64Value(of: number) else {
        return .failure(.amountRequired)
    }
    guard let currency = data["currency"] as? String, !currency.isEmpty else {
        return .failure(.currencyRequired)
    }
    return .success(KlarnaMessagingPrequalificationData(amount: amount, currency: currency))
}

/// `Int64(exactly:)` returns `nil` unless the value is exactly representable, so it also rejects
/// fractional, out-of-range (outside `Int64.min...Int64.max`), NaN and infinite values.
private func int64Value(of number: NSNumber) -> Int64? {
    Int64(exactly: number.doubleValue)
}

// MARK: - Result mapping

extension KlarnaMessagingPrequalificationResult {

    /// Bridge representation: `{ status: 'APPROVED', expiresAt: <ISO-8601 UTC, fractional seconds> }`
    /// or `{ status: 'ABORTED' }`.
    func toDictionary() -> [String: Any] {
        switch self {
        case .approved(let expiresAt):
            return [
                "status": "APPROVED",
                "expiresAt": Self.iso8601String(from: expiresAt)
            ]
        case .aborted:
            return ["status": "ABORTED"]
        @unknown default:
            // A case added by a newer SDK carries no approval, so it is surfaced as not prequalified.
            return ["status": "ABORTED"]
        }
    }

    static func iso8601String(from date: Date) -> String {
        let formatter = ISO8601DateFormatter()
        formatter.timeZone = TimeZone(secondsFromGMT: 0)
        formatter.formatOptions = [.withInternetDateTime, .withFractionalSeconds]
        return formatter.string(from: date)
    }
}

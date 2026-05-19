import Foundation
import UIKit

@objc public protocol KlarnaMessagingHeightDelegate: AnyObject {
    func placementViewDidUpdateHeight(_ height: CGFloat)
}

@objc(KlarnaMessagingPlacementViewHelper)
public class KlarnaMessagingPlacementViewHelper: NSObject {

    // MARK: - Amount Parsing

    /// Strict integer-only parser that mirrors Android's `String.toLongOrNull()`.
    /// Returns `nil` for empty, non-integer, or out-of-range inputs instead of
    /// falling back to `NSString.longLongValue`, which silently coerces `"abc"` /
    /// `"12.5"` to `0`.
    @objc public static func parseAmount(_ value: String?) -> NSNumber? {
        guard let value = value, !value.isEmpty, let parsed = Int64(value) else {
            return nil
        }
        return NSNumber(value: parsed)
    }

    // MARK: - Height Calculation

    /// Determines the natural content height of `view` using a three-tier fallback:
    /// 1. Auto Layout `systemLayoutSizeFitting` constrained to `containerWidth`.
    /// 2. The view's current `frame.size.height` (for views laid out manually).
    /// 3. The bottommost subview's `maxY` (last-resort introspection).
    @objc public static func calculateHeight(for view: UIView, containerWidth: CGFloat) -> CGFloat {
        view.setNeedsLayout()
        view.layoutIfNeeded()

        let fittingWidth = containerWidth > 0 ? containerWidth : UIScreen.main.bounds.width
        let fittingSize = view.systemLayoutSizeFitting(
            CGSize(width: fittingWidth, height: UIView.layoutFittingCompressedSize.height),
            withHorizontalFittingPriority: .required,
            verticalFittingPriority: .fittingSizeLevel
        )
        if fittingSize.height > 0 {
            return fittingSize.height
        }

        if view.frame.size.height > 0 {
            return view.frame.size.height
        }

        var maxBottom: CGFloat = 0
        for subview in view.subviews {
            let bottom = subview.frame.maxY
            if bottom > maxBottom {
                maxBottom = bottom
            }
        }
        return maxBottom
    }

    // MARK: - Subview Setup

    /// Adds `placementView` to `container` pinned to top/leading/trailing edges.
    /// When `pinWidth` is `true` an explicit width constraint equal to
    /// `container.bounds.width` is also added (no-op if the container has zero
    /// width). The Fabric wrapper currently always passes `false` and lets Auto
    /// Layout drive width via the trailing constraint.
    @objc public static func addPlacementSubview(_ placementView: UIView, to container: UIView, pinWidth: Bool) {
        placementView.translatesAutoresizingMaskIntoConstraints = false
        container.addSubview(placementView)

        NSLayoutConstraint.activate([
            placementView.topAnchor.constraint(equalTo: container.topAnchor),
            placementView.leadingAnchor.constraint(equalTo: container.leadingAnchor),
            placementView.trailingAnchor.constraint(equalTo: container.trailingAnchor),
        ])

        if pinWidth {
            let width = container.bounds.size.width
            if width > 0 {
                NSLayoutConstraint.activate([
                    placementView.widthAnchor.constraint(equalToConstant: width),
                ])
            }
        }
    }
}

// MARK: - Height Observer

/// Observes a placement view's `bounds` via KVO and fires staggered delayed checks
/// to catch asynchronous content loads. Reports distinct height changes to its `delegate`.
@objc(KlarnaMessagingHeightObserver)
public class KlarnaMessagingHeightObserver: NSObject {

    @objc public weak var delegate: KlarnaMessagingHeightDelegate?

    /// Held strongly while observation is active so `removeObserver` always sees a
    /// live target (KVO crashes if the observed object deallocates first).
    /// Cleared in `stopObserving` after the observer is removed.
    private var observedView: UIView?
    private var isObserving = false
    private var lastReportedHeight: CGFloat = 0
    private var pendingDelayedChecks: Int = 0
    private let delayIntervals: [Double]

    @objc public init(delayIntervals: [Double]) {
        self.delayIntervals = delayIntervals
        super.init()
    }

    /// Starts observing `view` for height changes using KVO on `bounds` plus
    /// staggered delayed checks (configured via `delayIntervals`).
    @objc public func observe(_ view: UIView) {
        stopObserving()
        observedView = view
        lastReportedHeight = 0
        pendingDelayedChecks = delayIntervals.count
        isObserving = true
        view.addObserver(self, forKeyPath: "bounds", options: .new, context: nil)

        for delay in delayIntervals {
            DispatchQueue.main.asyncAfter(deadline: .now() + delay) { [weak self] in
                guard let self = self else { return }
                self.checkAndReportHeight()
                self.pendingDelayedChecks -= 1
                if self.pendingDelayedChecks <= 0 && self.lastReportedHeight == 0 {
                    self.delegate?.placementViewDidUpdateHeight(0)
                }
            }
        }
    }

    @objc public func stopObserving() {
        if isObserving, let view = observedView {
            view.removeObserver(self, forKeyPath: "bounds")
        }
        isObserving = false
        observedView = nil
    }

    /// Measures the observed view's content height and notifies the delegate if
    /// the value changed since the last report (deduplicates identical heights).
    @objc public func checkAndReportHeight() {
        guard let view = observedView else { return }

        let containerWidth = view.superview?.bounds.size.width ?? 0
        let height = KlarnaMessagingPlacementViewHelper.calculateHeight(for: view, containerWidth: containerWidth)

        if height > 0 && height != lastReportedHeight {
            lastReportedHeight = height
            delegate?.placementViewDidUpdateHeight(height)
        }
    }

    // MARK: - KVO

    public override func observeValue(
        forKeyPath keyPath: String?,
        of object: Any?,
        change: [NSKeyValueChangeKey: Any]?,
        context: UnsafeMutableRawPointer?
    ) {
        if keyPath == "bounds", (object as? UIView) === observedView {
            DispatchQueue.main.async { [weak self] in
                self?.checkAndReportHeight()
            }
        }
    }

    deinit {
        stopObserving()
    }
}

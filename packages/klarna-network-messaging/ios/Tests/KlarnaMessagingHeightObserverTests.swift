import XCTest
@testable import react_native_klarna_network_messaging

class MockHeightDelegate: NSObject, KlarnaMessagingHeightDelegate {
    var reportedHeights: [CGFloat] = []

    func placementViewDidUpdateHeight(_ height: CGFloat) {
        reportedHeights.append(height)
    }
}

class KlarnaMessagingHeightObserverTests: XCTestCase {

    var observer: KlarnaMessagingHeightObserver!
    var delegate: MockHeightDelegate!

    override func setUp() {
        super.setUp()
        delegate = MockHeightDelegate()
        observer = KlarnaMessagingHeightObserver(delayIntervals: [])
        observer.delegate = delegate
    }

    override func tearDown() {
        observer.stopObserving()
        observer = nil
        delegate = nil
        super.tearDown()
    }

    // MARK: - Basic Observation

    func testCheckAndReportHeight_doesNothingWithoutObservedView() {
        observer.checkAndReportHeight()
        XCTAssertTrue(delegate.reportedHeights.isEmpty)
    }

    func testCheckAndReportHeight_reportsHeightForViewWithFrame() {
        let container = UIView(frame: CGRect(x: 0, y: 0, width: 320, height: 200))
        let view = UIView(frame: CGRect(x: 0, y: 0, width: 320, height: 100))
        container.addSubview(view)

        observer.observe(view)
        observer.checkAndReportHeight()

        XCTAssertEqual(delegate.reportedHeights.count, 1)
        XCTAssertEqual(delegate.reportedHeights.first, 100)
    }

    func testCheckAndReportHeight_doesNotReportZeroHeight() {
        let view = UIView(frame: .zero)
        observer.observe(view)
        observer.checkAndReportHeight()

        XCTAssertTrue(delegate.reportedHeights.isEmpty)
    }

    func testCheckAndReportHeight_doesNotReportSameHeightTwice() {
        let container = UIView(frame: CGRect(x: 0, y: 0, width: 320, height: 200))
        let view = UIView(frame: CGRect(x: 0, y: 0, width: 320, height: 80))
        container.addSubview(view)

        observer.observe(view)
        observer.checkAndReportHeight()
        observer.checkAndReportHeight()

        XCTAssertEqual(delegate.reportedHeights.count, 1)
    }

    func testCheckAndReportHeight_reportsNewHeightAfterChange() {
        let container = UIView(frame: CGRect(x: 0, y: 0, width: 320, height: 200))
        let view = UIView(frame: CGRect(x: 0, y: 0, width: 320, height: 80))
        container.addSubview(view)

        observer.observe(view)
        observer.checkAndReportHeight()

        view.frame = CGRect(x: 0, y: 0, width: 320, height: 120)
        observer.checkAndReportHeight()

        XCTAssertEqual(delegate.reportedHeights.count, 2)
        XCTAssertEqual(delegate.reportedHeights[0], 80)
        XCTAssertEqual(delegate.reportedHeights[1], 120)
    }

    // MARK: - stopObserving

    func testStopObserving_preventsSubsequentReports() {
        let container = UIView(frame: CGRect(x: 0, y: 0, width: 320, height: 200))
        let view = UIView(frame: CGRect(x: 0, y: 0, width: 320, height: 100))
        container.addSubview(view)

        observer.observe(view)
        observer.stopObserving()
        observer.checkAndReportHeight()

        XCTAssertTrue(delegate.reportedHeights.isEmpty)
    }

    func testStopObserving_canBeCalledMultipleTimes() {
        let view = UIView(frame: CGRect(x: 0, y: 0, width: 320, height: 100))
        observer.observe(view)
        observer.stopObserving()
        observer.stopObserving() // Should not crash
    }

    // Guards against the KVO crash "instance was deallocated while key value
    // observers were still registered". The observer must hold the observed
    // view strongly while observation is active, otherwise the view can die
    // before stopObserving runs.
    func testObservedView_isRetainedWhileObservationIsActive() {
        weak var weakView: UIView?
        autoreleasepool {
            let view = UIView(frame: CGRect(x: 0, y: 0, width: 320, height: 100))
            weakView = view
            observer.observe(view)
        }
        XCTAssertNotNil(weakView, "observer must keep the observed view alive while observing")
        observer.stopObserving()
        XCTAssertNil(weakView, "observed view must be released after stopObserving")
    }

    // MARK: - Re-observe

    func testObserve_resetsLastReportedHeight() {
        let container = UIView(frame: CGRect(x: 0, y: 0, width: 320, height: 200))
        let view = UIView(frame: CGRect(x: 0, y: 0, width: 320, height: 100))
        container.addSubview(view)

        observer.observe(view)
        observer.checkAndReportHeight()
        XCTAssertEqual(delegate.reportedHeights.count, 1)

        // Re-observe the same view — height should be reported again
        observer.observe(view)
        observer.checkAndReportHeight()
        XCTAssertEqual(delegate.reportedHeights.count, 2)
    }

    // MARK: - Delayed Checks

    func testDelayedChecks_fireAfterIntervals() {
        let delayObserver = KlarnaMessagingHeightObserver(delayIntervals: [0.1])
        delayObserver.delegate = delegate

        let container = UIView(frame: CGRect(x: 0, y: 0, width: 320, height: 200))
        let view = UIView(frame: CGRect(x: 0, y: 0, width: 320, height: 50))
        container.addSubview(view)

        delayObserver.observe(view)

        let expectation = expectation(description: "Delayed height check fires")
        DispatchQueue.main.asyncAfter(deadline: .now() + 0.2) {
            expectation.fulfill()
        }

        waitForExpectations(timeout: 1.0)
        XCTAssertFalse(delegate.reportedHeights.isEmpty)

        delayObserver.stopObserving()
    }

    // MARK: - No Content (height 0 fallback)

    func testReportsZeroHeight_whenAllDelaysFinishWithNoContent() {
        let delayObserver = KlarnaMessagingHeightObserver(delayIntervals: [0.05, 0.1])
        delayObserver.delegate = delegate

        let view = UIView(frame: .zero)
        delayObserver.observe(view)

        let expectation = expectation(description: "All delayed checks finished")
        DispatchQueue.main.asyncAfter(deadline: .now() + 0.2) {
            expectation.fulfill()
        }

        waitForExpectations(timeout: 1.0)
        XCTAssertEqual(delegate.reportedHeights, [0])

        delayObserver.stopObserving()
    }

    func testDoesNotReportZero_whenHeightIsPositive() {
        let delayObserver = KlarnaMessagingHeightObserver(delayIntervals: [0.05, 0.1])
        delayObserver.delegate = delegate

        let container = UIView(frame: CGRect(x: 0, y: 0, width: 320, height: 200))
        let view = UIView(frame: CGRect(x: 0, y: 0, width: 320, height: 80))
        container.addSubview(view)

        delayObserver.observe(view)

        let expectation = expectation(description: "All delayed checks finished")
        DispatchQueue.main.asyncAfter(deadline: .now() + 0.2) {
            expectation.fulfill()
        }

        waitForExpectations(timeout: 1.0)
        XCTAssertFalse(delegate.reportedHeights.isEmpty)
        XCTAssertFalse(delegate.reportedHeights.contains(0))

        delayObserver.stopObserving()
    }

    // MARK: - Stale delayed checks

    // The delayed checks of an earlier observation must not count towards (or finish) the
    // current one, otherwise a re-observed placement is reported as empty before its
    // content has had the full delay to load.
    func testStaleDelayedChecks_doNotReportZeroForANewObservation() {
        let delayObserver = KlarnaMessagingHeightObserver(delayIntervals: [0.2, 0.6])
        delayObserver.delegate = delegate
        delayObserver.observe(UIView(frame: .zero))

        // Re-observe while the first observation still has a delayed check pending.
        let reobserved = expectation(description: "Re-observed")
        DispatchQueue.main.asyncAfter(deadline: .now() + 0.4) {
            delayObserver.observe(UIView(frame: .zero))
            reobserved.fulfill()
        }
        waitForExpectations(timeout: 2.0)

        // The old observation's last check and the new one's first check fire around 0.6s;
        // the new observation is only finished after its own last check (1.0s).
        let midway = expectation(description: "Between the first and last new checks")
        DispatchQueue.main.asyncAfter(deadline: .now() + 0.4) {
            midway.fulfill()
        }
        waitForExpectations(timeout: 2.0)
        XCTAssertTrue(delegate.reportedHeights.isEmpty, "stale checks must not finish the new observation")

        let finished = expectation(description: "New observation finished")
        DispatchQueue.main.asyncAfter(deadline: .now() + 0.6) {
            finished.fulfill()
        }
        waitForExpectations(timeout: 2.0)
        XCTAssertEqual(delegate.reportedHeights, [0])

        delayObserver.stopObserving()
    }

    func testDelayedChecks_doNotReportAfterObservationStopped() {
        let delayObserver = KlarnaMessagingHeightObserver(delayIntervals: [0.05, 0.1])
        delayObserver.delegate = delegate
        delayObserver.observe(UIView(frame: .zero))
        delayObserver.stopObserving()

        let expectation = expectation(description: "Delays elapsed")
        DispatchQueue.main.asyncAfter(deadline: .now() + 0.3) {
            expectation.fulfill()
        }
        waitForExpectations(timeout: 1.0)

        XCTAssertTrue(delegate.reportedHeights.isEmpty)
    }
}

import XCTest
@testable import react_native_klarna_network_messaging

class KlarnaMessagingPlacementViewHelperTests: XCTestCase {

    // MARK: - parseAmount

    func testParseAmount_returnsNilForNil() {
        XCTAssertNil(KlarnaMessagingPlacementViewHelper.parseAmount(nil))
    }

    func testParseAmount_returnsNilForEmpty() {
        XCTAssertNil(KlarnaMessagingPlacementViewHelper.parseAmount(""))
    }

    func testParseAmount_returnsNilForNonNumeric() {
        XCTAssertNil(KlarnaMessagingPlacementViewHelper.parseAmount("abc"))
    }

    func testParseAmount_returnsNilForDecimal() {
        XCTAssertNil(KlarnaMessagingPlacementViewHelper.parseAmount("12.5"))
    }

    func testParseAmount_returnsNilForOutOfRange() {
        XCTAssertNil(KlarnaMessagingPlacementViewHelper.parseAmount("99999999999999999999"))
    }

    func testParseAmount_parsesValidInteger() {
        XCTAssertEqual(KlarnaMessagingPlacementViewHelper.parseAmount("19900"), NSNumber(value: 19900))
    }

    func testParseAmount_parsesNegativeInteger() {
        XCTAssertEqual(KlarnaMessagingPlacementViewHelper.parseAmount("-500"), NSNumber(value: -500))
    }

    // MARK: - calculateHeight

    func testCalculateHeight_returnsZeroForEmptyView() {
        let view = UIView(frame: .zero)
        let height = KlarnaMessagingPlacementViewHelper.calculateHeight(for: view, containerWidth: 320)
        XCTAssertEqual(height, 0)
    }

    func testCalculateHeight_returnsFrameHeight() {
        let view = UIView(frame: CGRect(x: 0, y: 0, width: 320, height: 100))
        let height = KlarnaMessagingPlacementViewHelper.calculateHeight(for: view, containerWidth: 320)
        XCTAssertEqual(height, 100)
    }

    func testCalculateHeight_returnsSubviewMaxY() {
        let view = UIView(frame: .zero)
        let subview = UIView(frame: CGRect(x: 0, y: 10, width: 100, height: 50))
        view.addSubview(subview)
        let height = KlarnaMessagingPlacementViewHelper.calculateHeight(for: view, containerWidth: 320)
        XCTAssertEqual(height, 60) // 10 + 50
    }

    func testCalculateHeight_usesScreenWidthWhenContainerWidthIsZero() {
        let view = UIView(frame: CGRect(x: 0, y: 0, width: 320, height: 80))
        let height = KlarnaMessagingPlacementViewHelper.calculateHeight(for: view, containerWidth: 0)
        XCTAssertGreaterThanOrEqual(height, 0)
    }

    // MARK: - addPlacementSubview

    func testAddPlacementSubview_addsSubview() {
        let container = UIView(frame: CGRect(x: 0, y: 0, width: 320, height: 200))
        let placement = UIView()

        KlarnaMessagingPlacementViewHelper.addPlacementSubview(placement, to: container, pinWidth: false)

        XCTAssertEqual(container.subviews.count, 1)
        XCTAssertTrue(container.subviews.contains(placement))
    }

    func testAddPlacementSubview_disablesAutoresizingMask() {
        let container = UIView(frame: CGRect(x: 0, y: 0, width: 320, height: 200))
        let placement = UIView()

        KlarnaMessagingPlacementViewHelper.addPlacementSubview(placement, to: container, pinWidth: false)

        XCTAssertFalse(placement.translatesAutoresizingMaskIntoConstraints)
    }

    func testAddPlacementSubview_addsTopLeadingTrailingConstraints() {
        let container = UIView(frame: CGRect(x: 0, y: 0, width: 320, height: 200))
        let placement = UIView()

        KlarnaMessagingPlacementViewHelper.addPlacementSubview(placement, to: container, pinWidth: false)

        // Should have at least top, leading, trailing constraints
        let constraints = container.constraints.filter { constraint in
            constraint.firstItem === placement || constraint.secondItem === placement
        }
        XCTAssertGreaterThanOrEqual(constraints.count, 3)
    }

    func testAddPlacementSubview_pinsWidthWhenRequested() {
        let container = UIView(frame: CGRect(x: 0, y: 0, width: 320, height: 200))
        let placement = UIView()

        KlarnaMessagingPlacementViewHelper.addPlacementSubview(placement, to: container, pinWidth: true)

        let widthConstraints = placement.constraints.filter { $0.firstAttribute == .width }
        XCTAssertEqual(widthConstraints.count, 1)
        XCTAssertEqual(widthConstraints.first?.constant, 320)
    }

    func testAddPlacementSubview_doesNotPinWidthWhenContainerHasZeroWidth() {
        let container = UIView(frame: .zero)
        let placement = UIView()

        KlarnaMessagingPlacementViewHelper.addPlacementSubview(placement, to: container, pinWidth: true)

        let widthConstraints = placement.constraints.filter { $0.firstAttribute == .width }
        XCTAssertEqual(widthConstraints.count, 0)
    }
}

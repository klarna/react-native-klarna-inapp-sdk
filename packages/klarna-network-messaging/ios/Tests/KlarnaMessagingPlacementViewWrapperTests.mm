#import <XCTest/XCTest.h>

#if RCT_NEW_ARCH_ENABLED
#import <react/renderer/components/KlarnaNetworkMessagingSpec/Props.h>

#if __has_include(<react_native_klarna_network_messaging/react_native_klarna_network_messaging-Swift.h>)
#import <react_native_klarna_network_messaging/react_native_klarna_network_messaging-Swift.h>
#else
#import "react_native_klarna_network_messaging-Swift.h"
#endif

#import "../Sources/view/KlarnaMessagingPlacementViewWrapper.h"

using namespace facebook::react;

@interface KlarnaMessagingPlacementViewWrapper ()

// Expose private properties for testing
@property (nonatomic, strong, nullable) UIView *placementView;
@property (nonatomic, strong) KlarnaMessagingHeightObserver *heightObserver;

@end

// Records the heights the wrapper's height observer reports.
@interface HeightRecorder : NSObject <KlarnaMessagingHeightDelegate>

@property (nonatomic, strong) NSMutableArray<NSNumber *> *heights;

@end

@implementation HeightRecorder

- (instancetype)init {
    self = [super init];
    if (self) {
        _heights = [NSMutableArray array];
    }
    return self;
}

- (void)placementViewDidUpdateHeight:(CGFloat)height {
    [self.heights addObject:@(height)];
}

@end

@interface KlarnaMessagingPlacementViewWrapperTests : XCTestCase

@property (nonatomic, strong) KlarnaMessagingPlacementViewWrapper *wrapper;
@property (nonatomic, strong) HeightRecorder *recorder;

@end

@implementation KlarnaMessagingPlacementViewWrapperTests

- (void)setUp {
    [super setUp];
    self.wrapper = [[KlarnaMessagingPlacementViewWrapper alloc] initWithFrame:CGRectMake(0, 0, 320, 60)];
    self.recorder = [[HeightRecorder alloc] init];
    self.wrapper.heightObserver.delegate = self.recorder;
}

- (void)tearDown {
    [self.wrapper.heightObserver stopObserving];
    self.wrapper = nil;
    self.recorder = nil;
    [super tearDown];
}

#pragma mark - prepareForRecycle

- (void)test_prepareForRecycle_removesThePlacementView {
    UIView *placementView = [self attachPlacementViewOfHeight:50];

    [self.wrapper prepareForRecycle];

    XCTAssertNil(self.wrapper.placementView);
    XCTAssertNil(placementView.superview);
}

- (void)test_prepareForRecycle_stopsObservingHeight {
    UIView *placementView = [self attachPlacementViewOfHeight:50];
    [self.wrapper.heightObserver observe:placementView];
    [self.wrapper.heightObserver checkAndReportHeight];
    XCTAssertEqual(self.recorder.heights.count, 1u, @"the observer reports while it is observing");

    [self.wrapper prepareForRecycle];
    placementView.frame = CGRectMake(0, 0, 320, 120);
    [self.wrapper.heightObserver checkAndReportHeight];

    XCTAssertEqual(self.recorder.heights.count, 1u, @"a recycled view must not keep reporting");
}

// Fabric reuses the view for a new component instance and keeps its stored props. Without a
// reset the same props would not recreate the placement view, the height observer would dedupe
// against the previous mount and JS would never receive onResized.
- (void)test_afterRecycling_samePropsRecreateThePlacementView {
    auto props = [self validProps];
    [self.wrapper updateProps:props oldProps:self.wrapper.props];

    // A placement view that already exists survives unchanged props.
    UIView *existing = [self attachPlacementViewOfHeight:50];
    [self.wrapper updateProps:props oldProps:self.wrapper.props];
    XCTAssertEqualObjects(self.wrapper.placementView, existing, @"unchanged props must not recreate");

    // Recreating always tears down the current placement view first, whether or not the SDK
    // instance can be resolved, so its removal shows that a recreation ran.
    [self.wrapper prepareForRecycle];
    UIView *afterRecycle = [self attachPlacementViewOfHeight:50];
    [self.wrapper updateProps:props oldProps:self.wrapper.props];

    XCTAssertNil(afterRecycle.superview, @"the same props after recycling must recreate");
    XCTAssertNil(self.wrapper.placementView);
}

#pragma mark - Helpers

- (UIView *)attachPlacementViewOfHeight:(CGFloat)height {
    UIView *placementView = [[UIView alloc] initWithFrame:CGRectMake(0, 0, 320, height)];
    [self.wrapper addSubview:placementView];
    self.wrapper.placementView = placementView;
    return placementView;
}

- (Props::Shared)validProps {
    auto props = std::make_shared<RNKlarnaMessagingPlacementViewProps>();
    props->instanceId = "test-instance";
    props->placementType = "CreditPromotionBadge";
    props->theme = "light";
    props->amount = "1000";
    props->currency = "GBP";
    return props;
}

@end

#endif

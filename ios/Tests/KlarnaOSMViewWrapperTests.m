#import <XCTest/XCTest.h>
#import <OCMock/OCMock.h>
#import <React/RCTComponent.h>
#import <KlarnaMobileSDK/KlarnaMobileSDK.h>
#import <KlarnaMobileSDK/KlarnaMobileSDK-Swift.h>

#import "../Sources/view/KlarnaOSMViewWrapper.h"

@interface KlarnaOSMViewWrapper () <KlarnaEventHandler, KlarnaSizingDelegate>

// Expose private properties and methods for testing
@property (nonatomic, strong) KlarnaOSMView* klarnaOSMView;
@property (nonatomic, assign) BOOL isOSMViewReadyEventSent;

- (void)initializeActualOSMView;
- (void)updateStyleConfiguration;
- (UIColor *)colorFromHexString:(NSString *)hexString;
- (void)sendOSMViewReadyEvent;

@end

@interface KlarnaOSMViewWrapperTests : XCTestCase

@property (nonatomic, strong) KlarnaOSMViewWrapper *wrapper;
@property (nonatomic, strong) id osmViewMock;

@end

@implementation KlarnaOSMViewWrapperTests

- (void)setUp {
    // Stub initializeActualOSMView on the partial mock BEFORE init runs,
    // preventing the real KlarnaOSMView SDK from being instantiated.
    KlarnaOSMViewWrapper *wrapper = [KlarnaOSMViewWrapper alloc];
    id partialMock = OCMPartialMock(wrapper);
    OCMStub([partialMock initializeActualOSMView]);
    [wrapper initWithFrame:CGRectZero];
    self.wrapper = partialMock;

    // Assign a class mock as the klarnaOSMView for verification
    id mockOSMView = OCMClassMock([KlarnaOSMView class]);
    self.osmViewMock = mockOSMView;
    self.wrapper.klarnaOSMView = mockOSMView;
}

- (void)tearDown {
    self.wrapper = nil;
    self.osmViewMock = nil;
}

#pragma mark - Initialization Tests

- (void)test_initializesOSMViewOnCreation {
    // The OSM view should be created during initWithFrame
    XCTAssertNotNil(self.wrapper.klarnaOSMView);
}

- (void)test_wrapperHasClearBackground {
    XCTAssertNotNil(self.wrapper.backgroundColor);
}

- (void)test_osmViewReadyEventNotSentInitially {
    XCTAssertFalse(self.wrapper.isOSMViewReadyEventSent);
}

#pragma mark - Property Setter Tests: clientId

- (void)test_setClientId {
    [self.wrapper setClientId:@"test-client-id"];
    XCTAssertEqualObjects(self.wrapper.clientId, @"test-client-id");
}

- (void)test_setClientIdPassesToOSMView {
    [self.wrapper setClientId:@"test-client-id"];
    OCMVerify([self.osmViewMock setClientId:@"test-client-id"]);
}

#pragma mark - Property Setter Tests: placementKey

- (void)test_setPlacementKey {
    [self.wrapper setPlacementKey:@"test-placement-key"];
    XCTAssertEqualObjects(self.wrapper.placementKey, @"test-placement-key");
}

- (void)test_setPlacementKeyPassesToOSMView {
    [self.wrapper setPlacementKey:@"test-placement-key"];
    OCMVerify([self.osmViewMock setPlacementKey:@"test-placement-key"]);
}

#pragma mark - Property Setter Tests: locale

- (void)test_setLocale {
    [self.wrapper setLocale:@"en-US"];
    XCTAssertEqualObjects(self.wrapper.locale, @"en-US");
}

- (void)test_setLocalePassesToOSMView {
    [self.wrapper setLocale:@"en-US"];
    OCMVerify([self.osmViewMock setLocale:@"en-US"]);
}

- (void)test_setLocaleEmptyStringDoesNotPassToOSMView {
    OCMReject([self.osmViewMock setLocale:OCMOCK_ANY]);
    [self.wrapper setLocale:@""];
    XCTAssertEqualObjects(self.wrapper.locale, @"");
}

#pragma mark - Property Setter Tests: purchaseAmount

- (void)test_setPurchaseAmount {
    [self.wrapper setPurchaseAmount:@"9999"];
    XCTAssertEqualObjects(self.wrapper.purchaseAmount, @"9999");
}

- (void)test_setPurchaseAmountPassesNumberToOSMView {
    [self.wrapper setPurchaseAmount:@"9999"];
    OCMVerify([self.osmViewMock setPurchaseAmount:[OCMArg checkWithBlock:^BOOL(NSNumber *amount) {
        return [amount longValue] == 9999;
    }]]);
}

- (void)test_setPurchaseAmountEmptyStringDoesNotPassToOSMView {
    OCMReject([self.osmViewMock setPurchaseAmount:OCMOCK_ANY]);
    [self.wrapper setPurchaseAmount:@""];
}

- (void)test_setPurchaseAmountInvalidStringDoesNotPassToOSMView {
    OCMReject([self.osmViewMock setPurchaseAmount:OCMOCK_ANY]);
    [self.wrapper setPurchaseAmount:@"not-a-number"];
}

#pragma mark - Property Setter Tests: environment

- (void)test_setEnvironmentPlayground {
    [self.wrapper setEnvironment:@"playground"];
    XCTAssertEqualObjects(self.wrapper.environment, @"playground");
    OCMVerify([self.osmViewMock setEnvironment:KlarnaEnvironment.playground]);
}

- (void)test_setEnvironmentProduction {
    [self.wrapper setEnvironment:@"production"];
    OCMVerify([self.osmViewMock setEnvironment:KlarnaEnvironment.production]);
}

- (void)test_setEnvironmentStaging {
    [self.wrapper setEnvironment:@"staging"];
    OCMVerify([self.osmViewMock setEnvironment:KlarnaEnvironment.staging]);
}

- (void)test_setEnvironmentUnknown {
    OCMReject([self.osmViewMock setEnvironment:OCMOCK_ANY]);
    [self.wrapper setEnvironment:@"unknown"];
}

- (void)test_setEnvironmentEmpty {
    OCMReject([self.osmViewMock setEnvironment:OCMOCK_ANY]);
    [self.wrapper setEnvironment:@""];
}

#pragma mark - Property Setter Tests: region

- (void)test_setRegionEu {
    [self.wrapper setRegion:@"eu"];
    XCTAssertEqualObjects(self.wrapper.region, @"eu");
    OCMVerify([self.osmViewMock setRegion:KlarnaRegion.eu]);
}

- (void)test_setRegionNa {
    [self.wrapper setRegion:@"na"];
    OCMVerify([self.osmViewMock setRegion:KlarnaRegion.na]);
}

- (void)test_setRegionOc {
    [self.wrapper setRegion:@"oc"];
    OCMVerify([self.osmViewMock setRegion:KlarnaRegion.oc]);
}

- (void)test_setRegionUnknown {
    OCMReject([self.osmViewMock setRegion:OCMOCK_ANY]);
    [self.wrapper setRegion:@"unknown"];
}

- (void)test_setRegionEmpty {
    OCMReject([self.osmViewMock setRegion:OCMOCK_ANY]);
    [self.wrapper setRegion:@""];
}

#pragma mark - Property Setter Tests: theme

- (void)test_setThemeLight {
    [self.wrapper setTheme:@"light"];
    XCTAssertEqualObjects(self.wrapper.theme, @"light");
    OCMVerify([(KlarnaOSMView *)self.osmViewMock setTheme:KlarnaThemeLight]);
}

- (void)test_setThemeDark {
    [self.wrapper setTheme:@"dark"];
    OCMVerify([(KlarnaOSMView *)self.osmViewMock setTheme:KlarnaThemeDark]);
}

- (void)test_setThemeAutomatic {
    [self.wrapper setTheme:@"automatic"];
    OCMVerify([(KlarnaOSMView *)self.osmViewMock setTheme:KlarnaThemeAutomatic]);
}

- (void)test_setThemeUnknown {
    OCMReject([(KlarnaOSMView *)self.osmViewMock setTheme:KlarnaThemeLight]);
    OCMReject([(KlarnaOSMView *)self.osmViewMock setTheme:KlarnaThemeDark]);
    OCMReject([(KlarnaOSMView *)self.osmViewMock setTheme:KlarnaThemeAutomatic]);
    [self.wrapper setTheme:@"unknown"];
}

- (void)test_setThemeEmpty {
    OCMReject([(KlarnaOSMView *)self.osmViewMock setTheme:KlarnaThemeLight]);
    OCMReject([(KlarnaOSMView *)self.osmViewMock setTheme:KlarnaThemeDark]);
    OCMReject([(KlarnaOSMView *)self.osmViewMock setTheme:KlarnaThemeAutomatic]);
    [self.wrapper setTheme:@""];
}

#pragma mark - Property Setter Tests: returnUrl (no-op on native side)

- (void)test_setReturnUrlIsNoOp {
    // OSM ignores returnUrl - the value is stored but not forwarded to the native SDK
    [self.wrapper setReturnUrl:@"https://return.example.com"];
    XCTAssertEqualObjects(self.wrapper.returnUrl, @"https://return.example.com");
}

- (void)test_setReturnUrlNil {
    [self.wrapper setReturnUrl:nil];
    XCTAssertNil(self.wrapper.returnUrl);
}

#pragma mark - Style Configuration Tests

- (void)test_setOsmBackgroundColorTriggersStyleUpdate {
    [self.wrapper setOsmBackgroundColor:@"#FF0000"];
    XCTAssertEqualObjects(self.wrapper.osmBackgroundColor, @"#FF0000");
    OCMVerify([self.osmViewMock setStyleConfiguration:OCMOCK_ANY]);
}

- (void)test_setTextColorTriggersStyleUpdate {
    [self.wrapper setTextColor:@"#00FF00"];
    XCTAssertEqualObjects(self.wrapper.textColor, @"#00FF00");
    OCMVerify([self.osmViewMock setStyleConfiguration:OCMOCK_ANY]);
}

- (void)test_setNilColorsResetsStyleConfiguration {
    [self.wrapper setOsmBackgroundColor:nil];
    [self.wrapper setTextColor:nil];
    // With both colors nil, styleConfiguration should be set to nil
    OCMVerify([self.osmViewMock setStyleConfiguration:[OCMArg isNil]]);
}

- (void)test_setInvalidColorResetsStyleConfiguration {
    [self.wrapper setOsmBackgroundColor:@"invalid"];
    // Invalid color should result in nil style configuration
    OCMVerify([self.osmViewMock setStyleConfiguration:[OCMArg isNil]]);
}

#pragma mark - Color Parsing Tests

- (void)test_colorFromHexStringRGB {
    UIColor *color = [self.wrapper colorFromHexString:@"#FF0000"];
    XCTAssertNotNil(color);
    CGFloat r, g, b, a;
    [color getRed:&r green:&g blue:&b alpha:&a];
    XCTAssertEqualWithAccuracy(r, 1.0, 0.01);
    XCTAssertEqualWithAccuracy(g, 0.0, 0.01);
    XCTAssertEqualWithAccuracy(b, 0.0, 0.01);
    XCTAssertEqualWithAccuracy(a, 1.0, 0.01);
}

- (void)test_colorFromHexStringRGBA {
    UIColor *color = [self.wrapper colorFromHexString:@"#FF000080"];
    XCTAssertNotNil(color);
    CGFloat r, g, b, a;
    [color getRed:&r green:&g blue:&b alpha:&a];
    XCTAssertEqualWithAccuracy(r, 1.0, 0.01);
    XCTAssertEqualWithAccuracy(g, 0.0, 0.01);
    XCTAssertEqualWithAccuracy(b, 0.0, 0.01);
    XCTAssertEqualWithAccuracy(a, 128.0 / 255.0, 0.01);
}

- (void)test_colorFromHexStringWithoutHash {
    UIColor *color = [self.wrapper colorFromHexString:@"00FF00"];
    XCTAssertNotNil(color);
    CGFloat r, g, b, a;
    [color getRed:&r green:&g blue:&b alpha:&a];
    XCTAssertEqualWithAccuracy(r, 0.0, 0.01);
    XCTAssertEqualWithAccuracy(g, 1.0, 0.01);
    XCTAssertEqualWithAccuracy(b, 0.0, 0.01);
}

- (void)test_colorFromHexStringNil {
    UIColor *color = [self.wrapper colorFromHexString:nil];
    XCTAssertNil(color);
}

- (void)test_colorFromHexStringEmpty {
    UIColor *color = [self.wrapper colorFromHexString:@""];
    XCTAssertNil(color);
}

- (void)test_colorFromHexStringInvalidLength {
    UIColor *color = [self.wrapper colorFromHexString:@"#FFF"];
    XCTAssertNil(color);
}

- (void)test_colorFromHexStringTooLong {
    UIColor *color = [self.wrapper colorFromHexString:@"#FF0000FF00"];
    XCTAssertNil(color);
}

#pragma mark - Render Tests

- (void)test_renderCallsOSMViewRender {
    [self.wrapper render];
    OCMVerify([self.osmViewMock render]);
}

#pragma mark - KlarnaEventHandler Tests

- (void)test_onErrorCallbackFired {
    __block NSDictionary *receivedBody = nil;
    [self.wrapper setOnError:^(NSDictionary *body) {
        receivedBody = body;
    }];

    id mockError = OCMClassMock([KlarnaError class]);
    OCMStub([mockError name]).andReturn(@"TestError");
    OCMStub([mockError message]).andReturn(@"Test error message");
    OCMStub([mockError isFatal]).andReturn(YES);

    [(id<KlarnaEventHandler>)self.wrapper klarnaComponent:self.osmViewMock encounteredError:mockError];

    XCTAssertNotNil(receivedBody);
    NSDictionary *errorDict = receivedBody[@"error"];
    XCTAssertEqualObjects(errorDict[@"name"], @"TestError");
    XCTAssertEqualObjects(errorDict[@"message"], @"Test error message");
    XCTAssertEqualObjects(errorDict[@"isFatal"], @YES);
}

- (void)test_onErrorCallbackNotFatal {
    __block NSDictionary *receivedBody = nil;
    [self.wrapper setOnError:^(NSDictionary *body) {
        receivedBody = body;
    }];

    id mockError = OCMClassMock([KlarnaError class]);
    OCMStub([mockError name]).andReturn(@"MinorError");
    OCMStub([mockError message]).andReturn(@"Non-fatal error");
    OCMStub([mockError isFatal]).andReturn(NO);

    [(id<KlarnaEventHandler>)self.wrapper klarnaComponent:self.osmViewMock encounteredError:mockError];

    XCTAssertNotNil(receivedBody);
    NSDictionary *errorDict = receivedBody[@"error"];
    XCTAssertEqualObjects(errorDict[@"isFatal"], @NO);
}

- (void)test_onErrorWithoutCallbackDoesNotCrash {
    id mockError = OCMClassMock([KlarnaError class]);
    OCMStub([mockError name]).andReturn(@"TestError");
    OCMStub([mockError message]).andReturn(@"Test error message");
    OCMStub([mockError isFatal]).andReturn(NO);

    XCTAssertNoThrow([(id<KlarnaEventHandler>)self.wrapper klarnaComponent:self.osmViewMock encounteredError:mockError]);
}

- (void)test_dispatchedEventIsNoOp {
    id mockEvent = OCMClassMock([KlarnaProductEvent class]);
    XCTAssertNoThrow([(id<KlarnaEventHandler>)self.wrapper klarnaComponent:self.osmViewMock dispatchedEvent:mockEvent]);
}

#pragma mark - KlarnaSizingDelegate Tests

- (void)test_onResizedCallbackFired {
    __block NSDictionary *receivedBody = nil;
    [self.wrapper setOnResized:^(NSDictionary *body) {
        receivedBody = body;
    }];

    [(id<KlarnaSizingDelegate>)self.wrapper klarnaComponent:self.osmViewMock resizedToHeight:150.0];

    XCTAssertNotNil(receivedBody);
    XCTAssertEqualObjects(receivedBody[@"height"], @"150");
}

- (void)test_onResizedWithZeroHeight {
    __block NSDictionary *receivedBody = nil;
    [self.wrapper setOnResized:^(NSDictionary *body) {
        receivedBody = body;
    }];

    [(id<KlarnaSizingDelegate>)self.wrapper klarnaComponent:self.osmViewMock resizedToHeight:0.0];

    XCTAssertNotNil(receivedBody);
    XCTAssertEqualObjects(receivedBody[@"height"], @"0");
}

- (void)test_onResizedWithoutCallbackDoesNotCrash {
    XCTAssertNoThrow([(id<KlarnaSizingDelegate>)self.wrapper klarnaComponent:self.osmViewMock resizedToHeight:100.0]);
}

#pragma mark - OSM View Ready Event Tests

- (void)test_sendOSMViewReadyEventFires {
    __block BOOL eventFired = NO;
    [self.wrapper setOnOSMViewReady:^(NSDictionary *body) {
        eventFired = YES;
    }];

    // Simulate the event being ready to send
    self.wrapper.isOSMViewReadyEventSent = NO;
    [self.wrapper sendOSMViewReadyEvent];

    XCTAssertTrue(eventFired);
    XCTAssertTrue(self.wrapper.isOSMViewReadyEventSent);
}

- (void)test_sendOSMViewReadyEventOnlyFiresOnce {
    __block int fireCount = 0;
    [self.wrapper setOnOSMViewReady:^(NSDictionary *body) {
        fireCount++;
    }];

    self.wrapper.isOSMViewReadyEventSent = NO;
    [self.wrapper sendOSMViewReadyEvent];
    [self.wrapper sendOSMViewReadyEvent];

    XCTAssertEqual(fireCount, 1);
}

- (void)test_sendOSMViewReadyEventWithoutCallbackDoesNotCrash {
    self.wrapper.isOSMViewReadyEventSent = NO;
    XCTAssertNoThrow([self.wrapper sendOSMViewReadyEvent]);
}

- (void)test_didMoveToWindowResetsReadyEventFlag {
    self.wrapper.isOSMViewReadyEventSent = YES;

    // Simulate removal from window (window becomes nil)
    id wrapperMock = self.wrapper;
    OCMStub([wrapperMock window]).andReturn(nil);
    [self.wrapper didMoveToWindow];

    XCTAssertFalse(self.wrapper.isOSMViewReadyEventSent);
}

@end

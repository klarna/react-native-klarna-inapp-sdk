//
//  KlarnaExpressCheckoutViewWrapperTests.m
//  RNKlarnaMobileSDKTests
//
//  Tests for the old-arch KlarnaExpressCheckoutViewWrapper (UIView-based).
//
//  Coverage:
//    • Initial state after -init
//    • All React-Native prop setters store their values
//    • -didSetProps: calls -resetButton then -createButtonIfNeeded when button
//      already exists, or just -createButtonIfNeeded on first render
//    • -resetButton removes the button subview, nils the reference, clears
//      the isButtonCreated flag
//    • -createButtonIfNeeded exits early (no SDK call) when isButtonCreated==YES
//    • -didMoveToWindow with nil window resets the recreation guard
//    • Delegate-proxy handler closures forward events to the React Native blocks
//

#import <XCTest/XCTest.h>
#import <OCMock/OCMock.h>
#import <KlarnaMobileSDK/KlarnaMobileSDK.h>
#import <KlarnaMobileSDK/KlarnaMobileSDK-Swift.h>

#import "../Sources/view/KlarnaExpressCheckoutViewWrapper.h"

// ---------------------------------------------------------------------------
// Expose private state for white-box testing.
// delegateProxy is typed as `id` to avoid importing the Swift bridge header
// into the test target; handler blocks are accessed via KVC below.
// ---------------------------------------------------------------------------
@interface KlarnaExpressCheckoutViewWrapper ()

@property (nonatomic, strong) KlarnaExpressCheckoutButton *expressCheckoutButton;
@property (nonatomic, strong) id delegateProxy;
@property (nonatomic, assign) BOOL isButtonCreated;

- (void)createButtonIfNeeded;
- (void)resetButton;

@end

// ---------------------------------------------------------------------------

@interface KlarnaExpressCheckoutViewWrapperTests : XCTestCase

@property (nonatomic, strong) KlarnaExpressCheckoutViewWrapper *wrapper;
/// OCMPartialMock wrapping wrapper — used to stub / verify internal method calls.
@property (nonatomic, strong) id wrapperMock;

@end

@implementation KlarnaExpressCheckoutViewWrapperTests

- (void)setUp {
    KlarnaExpressCheckoutViewWrapper *w = [KlarnaExpressCheckoutViewWrapper new];
    self.wrapper = w;
    self.wrapperMock = OCMPartialMock(w);
}

- (void)tearDown {
    [self.wrapperMock stopMocking];
    self.wrapperMock = nil;
    self.wrapper = nil;
}

// ---------------------------------------------------------------------------
#pragma mark - Initial State
// ---------------------------------------------------------------------------

- (void)test_initialState_buttonNotCreated {
    KlarnaExpressCheckoutViewWrapper *w = [KlarnaExpressCheckoutViewWrapper new];
    XCTAssertFalse(w.isButtonCreated);
    XCTAssertNil(w.expressCheckoutButton);
}

- (void)test_initialState_delegateProxyIsConfigured {
    // setupDelegateProxy must run during -init and install both handlers.
    KlarnaExpressCheckoutViewWrapper *w = [KlarnaExpressCheckoutViewWrapper new];
    XCTAssertNotNil(w.delegateProxy);

    void (^authHandler)(NSDictionary *) = [w.delegateProxy valueForKey:@"onAuthorizedHandler"];
    void (^errHandler)(NSString *, NSString *, BOOL) = [w.delegateProxy valueForKey:@"onErrorHandler"];
    XCTAssertNotNil(authHandler, @"onAuthorizedHandler must be wired during -init");
    XCTAssertNotNil(errHandler,  @"onErrorHandler must be wired during -init");
}

- (void)test_initialState_autoFinalizeDefaultsToYES {
    KlarnaExpressCheckoutViewWrapper *w = [KlarnaExpressCheckoutViewWrapper new];
    XCTAssertTrue(w.autoFinalize);
}

- (void)test_initialState_collectShippingAddressDefaultsToNO {
    KlarnaExpressCheckoutViewWrapper *w = [KlarnaExpressCheckoutViewWrapper new];
    XCTAssertFalse(w.collectShippingAddress);
}

// ---------------------------------------------------------------------------
#pragma mark - Prop Storage
// ---------------------------------------------------------------------------

- (void)test_setClientId_storesValue {
    [self.wrapper setClientId:@"client-abc"];
    XCTAssertEqualObjects(self.wrapper.clientId, @"client-abc");
}

- (void)test_setClientToken_storesValue {
    [self.wrapper setClientToken:@"token-xyz"];
    XCTAssertEqualObjects(self.wrapper.clientToken, @"token-xyz");
}

- (void)test_setReturnUrl_storesValue {
    [self.wrapper setReturnUrl:@"testapp://checkout/return"];
    XCTAssertEqualObjects(self.wrapper.returnUrl, @"testapp://checkout/return");
}

- (void)test_setLocale_storesValue {
    [self.wrapper setLocale:@"sv-SE"];
    XCTAssertEqualObjects(self.wrapper.locale, @"sv-SE");
}

- (void)test_setEnvironment_storesValue {
    [self.wrapper setEnvironment:@"playground"];
    XCTAssertEqualObjects(self.wrapper.environment, @"playground");
}

- (void)test_setRegion_storesValue {
    [self.wrapper setRegion:@"na"];
    XCTAssertEqualObjects(self.wrapper.region, @"na");
}

- (void)test_setTheme_storesValue {
    [self.wrapper setTheme:@"dark"];
    XCTAssertEqualObjects(self.wrapper.theme, @"dark");
}

- (void)test_setShape_storesValue {
    [self.wrapper setShape:@"pill"];
    XCTAssertEqualObjects(self.wrapper.shape, @"pill");
}

- (void)test_setButtonStyle_storesValue {
    [self.wrapper setButtonStyle:@"outlined"];
    XCTAssertEqualObjects(self.wrapper.buttonStyle, @"outlined");
}

- (void)test_setAutoFinalize_storesNO {
    [self.wrapper setAutoFinalize:NO];
    XCTAssertFalse(self.wrapper.autoFinalize);
}

- (void)test_setCollectShippingAddress_storesYES {
    [self.wrapper setCollectShippingAddress:YES];
    XCTAssertTrue(self.wrapper.collectShippingAddress);
}

- (void)test_setSessionData_storesValue {
    [self.wrapper setSessionData:@"{\"key\":\"val\"}"];
    XCTAssertEqualObjects(self.wrapper.sessionData, @"{\"key\":\"val\"}");
}

// ---------------------------------------------------------------------------
#pragma mark - didSetProps
// ---------------------------------------------------------------------------

- (void)test_didSetProps_callsCreateButtonIfNeeded_onFirstRender {
    // isButtonCreated starts as NO — only createButtonIfNeeded should be invoked.
    OCMExpect([self.wrapperMock createButtonIfNeeded]);

    [self.wrapper didSetProps:@[]];

    OCMVerifyAll(self.wrapperMock);
}

- (void)test_didSetProps_whenButtonAlreadyCreated_resetsBeforeRecreating {
    // Every prop change while a button exists must remove it before recreating.
    self.wrapper.isButtonCreated = YES;

    // resetButton must run with its real implementation (clearing the flag).
    OCMExpect([self.wrapperMock resetButton]).andForwardToRealObject();
    // createButtonIfNeeded is a no-op here to avoid real SDK calls.
    OCMExpect([self.wrapperMock createButtonIfNeeded]);

    [self.wrapper didSetProps:@[@"clientToken"]];

    OCMVerifyAll(self.wrapperMock);
    // resetButton must have cleared the flag to prevent stale-button leaks.
    XCTAssertFalse(self.wrapper.isButtonCreated);
}

- (void)test_didSetProps_whenButtonNotCreated_skipsReset {
    // resetButton must NOT be called on the initial render pass.
    self.wrapper.isButtonCreated = NO;

    OCMReject([self.wrapperMock resetButton]);
    OCMExpect([self.wrapperMock createButtonIfNeeded]);

    [self.wrapper didSetProps:@[]];

    OCMVerifyAll(self.wrapperMock);
}

// ---------------------------------------------------------------------------
#pragma mark - resetButton
// ---------------------------------------------------------------------------

- (void)test_resetButton_clearsIsButtonCreated {
    KlarnaExpressCheckoutViewWrapper *w = [KlarnaExpressCheckoutViewWrapper new];
    w.isButtonCreated = YES;

    [w resetButton];

    XCTAssertFalse(w.isButtonCreated);
}

- (void)test_resetButton_nilsButtonReference {
    KlarnaExpressCheckoutViewWrapper *w = [KlarnaExpressCheckoutViewWrapper new];
    id mockButton = OCMClassMock([KlarnaExpressCheckoutButton class]);
    w.expressCheckoutButton = mockButton;
    w.isButtonCreated = YES;

    [w resetButton];

    XCTAssertNil(w.expressCheckoutButton);
}

- (void)test_resetButton_callsRemoveFromSuperview {
    KlarnaExpressCheckoutViewWrapper *w = [KlarnaExpressCheckoutViewWrapper new];
    id mockButton = OCMClassMock([KlarnaExpressCheckoutButton class]);
    OCMExpect([mockButton removeFromSuperview]);
    w.expressCheckoutButton = mockButton;
    w.isButtonCreated = YES;

    [w resetButton];

    OCMVerifyAll(mockButton);
}

- (void)test_resetButton_withNoExistingButton_doesNotCrash {
    // Called when isButtonCreated==NO and expressCheckoutButton==nil — must be safe.
    KlarnaExpressCheckoutViewWrapper *w = [KlarnaExpressCheckoutViewWrapper new];
    XCTAssertNoThrow([w resetButton]);
}

// ---------------------------------------------------------------------------
#pragma mark - createButtonIfNeeded guard
// ---------------------------------------------------------------------------

- (void)test_createButtonIfNeeded_whenAlreadyCreated_isNoOp {
    // The early-exit guard must prevent double-creation on rapid successive calls.
    // If it were to fall through it would attempt real SDK initialisation and crash.
    KlarnaExpressCheckoutViewWrapper *w = [KlarnaExpressCheckoutViewWrapper new];
    w.isButtonCreated = YES;

    [w createButtonIfNeeded];

    XCTAssertTrue(w.isButtonCreated,   @"isButtonCreated must stay YES — guard failed");
    XCTAssertNil(w.expressCheckoutButton, @"no button should be created by a no-op path");
}

// ---------------------------------------------------------------------------
#pragma mark - didMoveToWindow Lifecycle
// ---------------------------------------------------------------------------

- (void)test_didMoveToWindow_withNilWindow_resetsIsButtonCreated {
    // Removal from the view hierarchy must clear the recreation guard so that
    // re-insertion triggers a fresh button build.
    KlarnaExpressCheckoutViewWrapper *w = [KlarnaExpressCheckoutViewWrapper new];
    w.isButtonCreated = YES;

    // Wrapper is not attached to any UIWindow → window == nil.
    [w didMoveToWindow];

    XCTAssertFalse(w.isButtonCreated);
}

- (void)test_didMoveToWindow_withNilWindow_clearsButtonReference {
    // resetButton is called when the view is detached, which nils the button
    // reference and clears the recreation guard.
    KlarnaExpressCheckoutViewWrapper *w = [KlarnaExpressCheckoutViewWrapper new];
    id mockButton = OCMClassMock([KlarnaExpressCheckoutButton class]);
    w.expressCheckoutButton = mockButton;
    w.isButtonCreated = YES;

    [w didMoveToWindow];

    XCTAssertNil(w.expressCheckoutButton);
    XCTAssertFalse(w.isButtonCreated);
}

// ---------------------------------------------------------------------------
#pragma mark - Delegate Proxy Event Forwarding
//
// The proxy handler blocks (installed by -setupDelegateProxy) are accessed via
// KVC to avoid importing the Swift bridge header into the test target.
// ---------------------------------------------------------------------------

/// Simulates the SDK calling the authorized delegate method by invoking the
/// closure that -setupDelegateProxy installed on the delegate proxy.
- (void)simulateAuthorizedEvent:(NSDictionary *)response {
    void (^handler)(NSDictionary *) = [self.wrapper.delegateProxy valueForKey:@"onAuthorizedHandler"];
    XCTAssertNotNil(handler, @"onAuthorizedHandler must be wired up by setupDelegateProxy");
    handler(response);
}

/// Simulates the SDK calling the error delegate method.
- (void)simulateErrorEventWithName:(NSString *)name
                           message:(NSString *)message
                           isFatal:(BOOL)isFatal {
    void (^handler)(NSString *, NSString *, BOOL) =
        [self.wrapper.delegateProxy valueForKey:@"onErrorHandler"];
    XCTAssertNotNil(handler, @"onErrorHandler must be wired up by setupDelegateProxy");
    handler(name, message, isFatal);
}

- (void)test_onAuthorized_forwardsFullResponseToReactBlock {
    __block NSDictionary *receivedBody = nil;
    [self.wrapper setOnAuthorized:^(NSDictionary *body) {
        receivedBody = body;
    }];

    NSDictionary *authResponse = @{
        @"showForm":                 @YES,
        @"approved":                 @YES,
        @"finalizedRequired":        @NO,
        @"clientToken":              @"ct-123",
        @"authorizationToken":       @"at-456",
        @"sessionId":                @"sid-789",
        @"collectedShippingAddress": @"",
        @"merchantReference1":       @"ref1",
        @"merchantReference2":       @"ref2",
    };

    [self simulateAuthorizedEvent:authResponse];

    XCTAssertNotNil(receivedBody);
    NSDictionary *result = receivedBody[@"authorizationResponse"];
    XCTAssertEqualObjects(result[@"clientToken"],        @"ct-123");
    XCTAssertEqualObjects(result[@"authorizationToken"], @"at-456");
    XCTAssertEqualObjects(result[@"sessionId"],          @"sid-789");
    XCTAssertEqualObjects(result[@"approved"],           @YES);
    XCTAssertEqualObjects(result[@"showForm"],           @YES);
    XCTAssertEqualObjects(result[@"finalizedRequired"],  @NO);
    XCTAssertEqualObjects(result[@"merchantReference1"], @"ref1");
    XCTAssertEqualObjects(result[@"merchantReference2"], @"ref2");
}

- (void)test_onAuthorized_withNilCallback_doesNotCrash {
    // The handler must guard against nil onAuthorized to prevent a crash when
    // the JS side has not registered a listener yet.
    XCTAssertNoThrow([self simulateAuthorizedEvent:@{}]);
}

- (void)test_onError_forwardsFatalErrorToReactBlock {
    __block NSDictionary *receivedBody = nil;
    [self.wrapper setOnError:^(NSDictionary *body) {
        receivedBody = body;
    }];

    [self simulateErrorEventWithName:@"NetworkError"
                             message:@"Request timed out"
                             isFatal:YES];

    XCTAssertNotNil(receivedBody);
    NSDictionary *error = receivedBody[@"error"];
    XCTAssertEqualObjects(error[@"name"],    @"NetworkError");
    XCTAssertEqualObjects(error[@"message"], @"Request timed out");
    XCTAssertEqualObjects(error[@"isFatal"], @YES);
}

- (void)test_onError_nonFatalError_propagatesIsFatalNO {
    __block NSDictionary *receivedBody = nil;
    [self.wrapper setOnError:^(NSDictionary *body) {
        receivedBody = body;
    }];

    [self simulateErrorEventWithName:@"ValidationError"
                             message:@"Invalid card number"
                             isFatal:NO];

    XCTAssertEqualObjects(receivedBody[@"error"][@"isFatal"], @NO);
}

- (void)test_onError_withNilCallback_doesNotCrash {
    // The handler must guard against nil onError to prevent a crash when the
    // JS side has not registered a listener yet.
    XCTAssertNoThrow([self simulateErrorEventWithName:@"err"
                                              message:@"msg"
                                              isFatal:NO]);
}

@end

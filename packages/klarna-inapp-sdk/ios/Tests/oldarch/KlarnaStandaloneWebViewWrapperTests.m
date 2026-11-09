#import <XCTest/XCTest.h>

#if !RCT_NEW_ARCH_ENABLED
#import <OCMock/OCMock.h>
#import <KlarnaMobileSDK/KlarnaMobileSDK.h>
#import <KlarnaMobileSDK/KlarnaMobileSDK-Swift.h>

#import "../../Sources/view/KlarnaStandaloneWebViewWrapper.h"
#import "../../Sources/view/KlarnaStandaloneWebViewLoadOptions.h"

@interface KlarnaStandaloneWebViewWrapper () <KlarnaStandaloneWebViewDelegate>

// Expose private properties and methods for testing
@property (nonatomic, strong) KlarnaStandaloneWebView* klarnaStandaloneWebView;
@property (nonatomic, strong, nullable) NSObject *pendingLoad;
@property (nonatomic, strong, nullable) KlarnaStandaloneWebViewLoadOptions *firstLoadOptions;
@property (nonatomic, strong, nullable) NSURL *firstLoadURL;
- (void)klarnaStandaloneWebView:(KlarnaStandaloneWebView * _Nonnull)webView decidePolicyFor:(WKNavigationAction * _Nonnull)navigationAction decisionHandler:(void (^ _Nonnull)(WKNavigationActionPolicy))decisionHandler;
- (void) initializeKlarnaStandaloneWebView;
- (KlarnaStandaloneWebView *) createKlarnaStandaloneWebView;

@end

@interface KlarnaStandaloneWebViewWrapperTests : XCTestCase

@property (nonatomic, strong) id wrapper;

@end

@implementation KlarnaStandaloneWebViewWrapperTests

- (void)setUp {
    // Create wrapper with a partial mock, stubbing initializeKlarnaStandaloneWebView
    // to prevent the real SDK from being instantiated (it crashes in test env).
    KlarnaStandaloneWebViewWrapper *wrapper = [KlarnaStandaloneWebViewWrapper alloc];
    id partialMock = OCMPartialMock(wrapper);
    OCMStub([partialMock initializeKlarnaStandaloneWebView]);
    [wrapper init];
    self.wrapper = partialMock;
}

- (void)tearDown {
    self.wrapper = nil;
}

#pragma mark - Deferred Initialization Tests

- (void)test_doesNotInitializeViewOnInit {
    // KlarnaStandaloneWebView requires returnURL in its designated initializer
    // (init(frame:) is unavailable). The wrapper defers creation until returnUrl
    // is delivered as a React Native prop.
    OCMReject([self.wrapper initializeKlarnaStandaloneWebView]);

    KlarnaStandaloneWebViewWrapper *freshWrapper = [KlarnaStandaloneWebViewWrapper alloc];
    id freshMock = OCMPartialMock(freshWrapper);
    OCMStub([freshMock initializeKlarnaStandaloneWebView]);
    [freshWrapper init];

    OCMVerifyAll(freshMock);
    XCTAssertNil(((KlarnaStandaloneWebViewWrapper *)freshMock).klarnaStandaloneWebView);
}

- (void)test_initializesViewWhenReturnUrlIsSet {
    // The native view is created once a valid returnUrl arrives from React Native.
    // WHEN
    [self.wrapper setReturnUrl:@"testapp://return"];

    // THEN
    OCMVerify([self.wrapper initializeKlarnaStandaloneWebView]);
}

- (void)test_doesNotInitializeViewWithEmptyReturnUrl {
    // An empty returnUrl should not trigger initialization — the SDK requires
    // a valid URL in initWithReturnURL:.
    // WHEN
    [self.wrapper setReturnUrl:@""];

    // THEN
    XCTAssertNil(((KlarnaStandaloneWebViewWrapper *)self.wrapper).klarnaStandaloneWebView);
}

- (void)test_doesNotInitializeViewWithNilReturnUrl {
    // A nil returnUrl should not trigger initialization.
    // WHEN
    [self.wrapper setReturnUrl:nil];

    // THEN
    XCTAssertNil(((KlarnaStandaloneWebViewWrapper *)self.wrapper).klarnaStandaloneWebView);
}

#pragma mark - Prop Ordering Tests

- (void)test_bouncesCanBeSetBeforeReturnUrl {
    // React Native does not guarantee prop ordering. Setting bounces before
    // returnUrl should not crash, even though the native view doesn't exist yet.
    // WHEN
    XCTAssertNoThrow([self.wrapper setBounces:NO]);
    [self.wrapper setReturnUrl:@"testapp://return"];

    // THEN
    OCMVerify([self.wrapper initializeKlarnaStandaloneWebView]);
}

- (void)test_navigationCommandsInvalidateAPendingLoad {
    // goBack, goForward and reload supersede a load that is still copying shared cookies.
    KlarnaStandaloneWebViewWrapper *wrapper = self.wrapper;

    wrapper.pendingLoad = [NSObject new];
    [wrapper goBack];
    XCTAssertNil(wrapper.pendingLoad);

    wrapper.pendingLoad = [NSObject new];
    [wrapper goForward];
    XCTAssertNil(wrapper.pendingLoad);

    wrapper.pendingLoad = [NSObject new];
    [wrapper reload];
    XCTAssertNil(wrapper.pendingLoad);
}

- (void)test_returnUrlCanBeSetBeforeBounces {
    // WHEN
    [self.wrapper setReturnUrl:@"testapp://return"];
    XCTAssertNoThrow([self.wrapper setBounces:YES]);

    // THEN
    OCMVerify([self.wrapper initializeKlarnaStandaloneWebView]);
}

- (void)test_crossOriginSubframeRequestIsNotRestartedAsATopLevelLoad {
    // A subframe request can match the load headers by chance (e.g. Accept-Language) and must be left alone.
    KlarnaStandaloneWebViewWrapper *wrapper = self.wrapper;
    wrapper.firstLoadOptions = [KlarnaStandaloneWebViewLoadOptions optionsWithJsonString:@"{\"headers\":{\"X-Store\":\"42\"}}"];
    wrapper.firstLoadURL = [NSURL URLWithString:@"https://www.example.com/checkout"];
    NSMutableURLRequest *request = [NSMutableURLRequest requestWithURL:[NSURL URLWithString:@"https://ads.example.com/frame"]];
    [request setValue:@"42" forHTTPHeaderField:@"X-Store"];
    id frame = OCMClassMock([WKFrameInfo class]);
    OCMStub([frame isMainFrame]).andReturn(NO);
    id action = OCMClassMock([WKNavigationAction class]);
    OCMStub([action request]).andReturn(request);
    OCMStub([action targetFrame]).andReturn(frame);
    id webView = OCMClassMock([KlarnaStandaloneWebView class]);
    OCMReject([webView loadURLRequest:[OCMArg any]]);
    __block WKNavigationActionPolicy policy = WKNavigationActionPolicyCancel;

    [wrapper klarnaStandaloneWebView:webView decidePolicyFor:action decisionHandler:^(WKNavigationActionPolicy p) { policy = p; }];

    XCTAssertEqual(policy, WKNavigationActionPolicyAllow);
    XCTAssertNotNil(wrapper.firstLoadOptions);
    OCMVerifyAll(webView);
}

#pragma mark - Theme Tests

- (void)test_setTheme_storesValue {
    // GIVEN
    KlarnaStandaloneWebViewWrapper *wrapper = self.wrapper;

    // WHEN
    [wrapper setTheme:@"dark"];

    // THEN
    XCTAssertEqualObjects(wrapper.theme, @"dark");
}

- (void)test_setTheme_light_setsLightThemeOnView {
    // GIVEN
    KlarnaStandaloneWebViewWrapper *wrapper = self.wrapper;
    KlarnaStandaloneWebView *webView = [self attachMockWebView];

    // WHEN
    [wrapper setTheme:@"light"];

    // THEN
    OCMVerify([webView setTheme:KlarnaThemeLight]);
}

- (void)test_setTheme_dark_setsDarkThemeOnView {
    // GIVEN
    KlarnaStandaloneWebViewWrapper *wrapper = self.wrapper;
    KlarnaStandaloneWebView *webView = [self attachMockWebView];

    // WHEN
    [wrapper setTheme:@"dark"];

    // THEN
    OCMVerify([webView setTheme:KlarnaThemeDark]);
}

- (void)test_setTheme_automatic_setsAutomaticThemeOnView {
    // GIVEN
    KlarnaStandaloneWebViewWrapper *wrapper = self.wrapper;
    KlarnaStandaloneWebView *webView = [self attachMockWebView];

    // WHEN
    [wrapper setTheme:@"automatic"];

    // THEN
    OCMVerify([webView setTheme:KlarnaThemeAutomatic]);
}

- (void)test_setTheme_empty_keepsDefaultTheme {
    // JS always sends a theme, light when the prop is not set. An empty value is ignored.
    // GIVEN
    KlarnaStandaloneWebViewWrapper *wrapper = self.wrapper;
    KlarnaStandaloneWebView *webView = [self attachMockWebView];
    OCMReject([webView setTheme:KlarnaThemeLight]);
    OCMReject([webView setTheme:KlarnaThemeDark]);
    OCMReject([webView setTheme:KlarnaThemeAutomatic]);

    // WHEN
    [wrapper setTheme:@""];

    // THEN
    OCMVerifyAll((id)webView);
}

- (void)test_setTheme_unknown_keepsDefaultTheme {
    // GIVEN
    KlarnaStandaloneWebViewWrapper *wrapper = self.wrapper;
    KlarnaStandaloneWebView *webView = [self attachMockWebView];
    OCMReject([webView setTheme:KlarnaThemeLight]);
    OCMReject([webView setTheme:KlarnaThemeDark]);
    OCMReject([webView setTheme:KlarnaThemeAutomatic]);

    // WHEN
    [wrapper setTheme:@"unknown"];

    // THEN
    OCMVerifyAll((id)webView);
}

- (void)test_themeSetBeforeReturnUrl_isAppliedWhenViewIsCreated {
    // React Native does not guarantee prop ordering, so the theme can arrive before
    // returnUrl, while there is no native view yet. Only initializeKlarnaStandaloneWebView
    // can then apply it to the view it creates.
    // GIVEN
    KlarnaStandaloneWebView *webView = OCMClassMock([KlarnaStandaloneWebView class]);
    KlarnaStandaloneWebViewWrapper *wrapper = [self wrapperCreatingWebView:webView];
    XCTAssertNoThrow([wrapper setTheme:@"dark"]);

    // WHEN
    [wrapper setReturnUrl:@"testapp://return"];

    // THEN
    OCMVerify([webView setTheme:KlarnaThemeDark]);
}

#pragma mark - onError Tests

- (void)test_didFailProvisionalNavigation_sendsFailingUrlWhenWebViewHasNoUrl {
    // WHEN
    NSDictionary *payload = [self onErrorPayloadForFailure:^(KlarnaStandaloneWebViewWrapper *wrapper, id webView, NSError *error) {
        [wrapper klarnaStandaloneWebView:webView didFailProvisionalNavigation:OCMClassMock([WKNavigation class]) withError:error];
    }];

    // THEN
    [self assertErrorPayload:payload];
}

- (void)test_didFail_sendsErrorDictionaryWhenWebViewHasNoUrl {
    // The native SDK reports failures before anything loads through didFail:.
    // WHEN
    NSDictionary *payload = [self onErrorPayloadForFailure:^(KlarnaStandaloneWebViewWrapper *wrapper, id webView, NSError *error) {
        [wrapper klarnaStandaloneWebView:webView didFail:OCMClassMock([WKNavigation class]) withError:error];
    }];

    // THEN
    [self assertErrorPayload:payload];
}

- (void)test_didFail_ignoresCancelledNavigation {
    // GIVEN
    __block BOOL called = NO;
    KlarnaStandaloneWebViewWrapper *wrapper = self.wrapper;
    wrapper.onError = ^(NSDictionary *body) { called = YES; };
    NSError *error = [NSError errorWithDomain:NSURLErrorDomain code:NSURLErrorCancelled userInfo:nil];

    // WHEN
    [wrapper klarnaStandaloneWebView:OCMClassMock([KlarnaStandaloneWebView class]) didFail:OCMClassMock([WKNavigation class]) withError:error];

    // THEN
    XCTAssertFalse(called);
}

- (void)test_didFailProvisionalNavigation_ignoresCancelledNavigation {
    // GIVEN
    __block BOOL called = NO;
    KlarnaStandaloneWebViewWrapper *wrapper = self.wrapper;
    wrapper.onError = ^(NSDictionary *body) { called = YES; };
    NSError *error = [NSError errorWithDomain:NSURLErrorDomain code:NSURLErrorCancelled userInfo:nil];

    // WHEN
    [wrapper klarnaStandaloneWebView:OCMClassMock([KlarnaStandaloneWebView class]) didFailProvisionalNavigation:OCMClassMock([WKNavigation class]) withError:error];

    // THEN
    XCTAssertFalse(called);
}

- (void)test_didFinish_retiresLoadOptions {
    // GIVEN
    self.wrapper.firstLoadOptions = [KlarnaStandaloneWebViewLoadOptions optionsWithJsonString:@"{\"headers\":{\"X-Store\":\"42\"}}"];

    // WHEN
    [self.wrapper klarnaStandaloneWebView:OCMClassMock([KlarnaStandaloneWebView class]) didFinish:OCMClassMock([WKNavigation class])];

    // THEN
    XCTAssertNil(self.wrapper.firstLoadOptions);
}

- (void)test_didFail_retiresLoadOptions {
    // GIVEN
    self.wrapper.firstLoadOptions = [KlarnaStandaloneWebViewLoadOptions optionsWithJsonString:@"{\"headers\":{\"X-Store\":\"42\"}}"];

    // WHEN
    [self.wrapper klarnaStandaloneWebView:OCMClassMock([KlarnaStandaloneWebView class]) didFail:OCMClassMock([WKNavigation class]) withError:[NSError errorWithDomain:NSURLErrorDomain code:NSURLErrorNotConnectedToInternet userInfo:nil]];

    // THEN
    XCTAssertNil(self.wrapper.firstLoadOptions);
}

- (void)test_didFailProvisionalNavigation_retiresLoadOptions {
    // GIVEN
    self.wrapper.firstLoadOptions = [KlarnaStandaloneWebViewLoadOptions optionsWithJsonString:@"{\"headers\":{\"X-Store\":\"42\"}}"];

    // WHEN
    [self.wrapper klarnaStandaloneWebView:OCMClassMock([KlarnaStandaloneWebView class]) didFailProvisionalNavigation:OCMClassMock([WKNavigation class]) withError:[NSError errorWithDomain:NSURLErrorDomain code:NSURLErrorNotConnectedToInternet userInfo:nil]];

    // THEN
    XCTAssertNil(self.wrapper.firstLoadOptions);
}

- (void)test_cancelledNavigation_keepsLoadOptionsOfTheNewLoad {
    // A cancel of the previous load can arrive after the next load installed its options.
    // GIVEN
    self.wrapper.firstLoadOptions = [KlarnaStandaloneWebViewLoadOptions optionsWithJsonString:@"{\"headers\":{\"X-Store\":\"42\"}}"];
    NSError *cancelled = [NSError errorWithDomain:NSURLErrorDomain code:NSURLErrorCancelled userInfo:nil];

    // WHEN
    [self.wrapper klarnaStandaloneWebView:OCMClassMock([KlarnaStandaloneWebView class]) didFail:OCMClassMock([WKNavigation class]) withError:cancelled];
    [self.wrapper klarnaStandaloneWebView:OCMClassMock([KlarnaStandaloneWebView class]) didFailProvisionalNavigation:OCMClassMock([WKNavigation class]) withError:cancelled];

    // THEN
    XCTAssertNotNil(self.wrapper.firstLoadOptions);
}

#pragma mark - Helpers

- (KlarnaStandaloneWebView *)attachMockWebView {
    // Stubbing the getter rather than assigning the property: storing a mock in the
    // partial mock's property over-releases it when the test's invocations are freed.
    KlarnaStandaloneWebView *webView = OCMClassMock([KlarnaStandaloneWebView class]);
    OCMStub([self.wrapper klarnaStandaloneWebView]).andReturn(webView);
    return webView;
}

- (id)wrapperCreatingWebView:(KlarnaStandaloneWebView *)webView {
    // Runs the real initializer and stubs only what it can't do in tests: creating the SDK
    // view, storing the mock in the property (see attachMockWebView) and adding the mock
    // to the view hierarchy. The getter returns nil until the view is created.
    KlarnaStandaloneWebViewWrapper *wrapper = [KlarnaStandaloneWebViewWrapper alloc];
    id partialMock = OCMPartialMock(wrapper);
    __block KlarnaStandaloneWebView *createdWebView = nil;
    OCMStub([partialMock createKlarnaStandaloneWebView]).andDo(^(NSInvocation *invocation) {
        createdWebView = webView;
        [invocation setReturnValue:&createdWebView];
    });
    OCMStub([partialMock klarnaStandaloneWebView]).andDo(^(NSInvocation *invocation) {
        [invocation setReturnValue:&createdWebView];
    });
    OCMStub([partialMock setKlarnaStandaloneWebView:OCMOCK_ANY]);
    OCMStub([partialMock addSubview:OCMOCK_ANY]);
    [wrapper init];
    return partialMock;
}

- (NSDictionary *)onErrorPayloadForFailure:(void (^)(KlarnaStandaloneWebViewWrapper *wrapper, id webView, NSError *error))failure {
    // A web view whose first load failed has no URL and no title.
    id webView = OCMClassMock([KlarnaStandaloneWebView class]);
    OCMStub([webView url]).andReturn(nil);
    OCMStub([webView title]).andReturn(nil);
    NSError *error = [NSError errorWithDomain:NSURLErrorDomain
                                         code:NSURLErrorCannotFindHost
                                     userInfo:@{
        NSURLErrorFailingURLErrorKey: [NSURL URLWithString:@"https://nonexistent.invalid/"],
        NSLocalizedDescriptionKey: @"A server with the specified hostname could not be found.",
    }];

    __block NSDictionary *payload = nil;
    KlarnaStandaloneWebViewWrapper *wrapper = self.wrapper;
    wrapper.onError = ^(NSDictionary *body) { payload = body[@"error"]; };

    failure(wrapper, webView, error);
    return payload;
}

- (void)assertErrorPayload:(NSDictionary *)payload {
    XCTAssertTrue([payload isKindOfClass:[NSDictionary class]]);
    XCTAssertEqualObjects(payload[@"url"], @"https://nonexistent.invalid/");
    XCTAssertEqualObjects(payload[@"title"], @"");
    XCTAssertEqualObjects(payload[@"code"], @(NSURLErrorCannotFindHost));
    XCTAssertEqualObjects(payload[@"description"], @"A server with the specified hostname could not be found.");
}

@end
#endif // !RCT_NEW_ARCH_ENABLED

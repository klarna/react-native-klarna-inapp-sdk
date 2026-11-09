#import <XCTest/XCTest.h>

#if RCT_NEW_ARCH_ENABLED
#import <OCMock/OCMock.h>
#import <KlarnaMobileSDK/KlarnaMobileSDK.h>
#import <KlarnaMobileSDK/KlarnaMobileSDK-Swift.h>
#import <React/RCTLog.h>
#import <WebKit/WebKit.h>

#import <react/renderer/components/RNKlarnaMobileSDK/EventEmitters.h>
#import <react/renderer/components/RNKlarnaMobileSDK/Props.h>

#import "../../Sources/view/KlarnaStandaloneWebViewLoadOptions.h"
#import "../../Sources/view/KlarnaStandaloneWebViewWrapper.h"

using namespace facebook::react;

@interface KlarnaStandaloneWebViewWrapper () <KlarnaStandaloneWebViewDelegate, KlarnaEventHandler>

// Expose private properties and methods for testing
@property (nonatomic, strong) KlarnaStandaloneWebView *klarnaStandaloneWebView;
@property (nonatomic, strong) NSString *returnUrl;
@property (nonatomic, strong) NSString *theme;
@property (nonatomic, strong, nullable) NSObject *pendingLoad;
@property (nonatomic, strong, nullable) KlarnaStandaloneWebViewLoadOptions *firstLoadOptions;
@property (nonatomic, strong, nullable) NSURL *firstLoadURL;
- (void)klarnaStandaloneWebView:(KlarnaStandaloneWebView * _Nonnull)webView decidePolicyFor:(WKNavigationAction * _Nonnull)navigationAction decisionHandler:(void (^ _Nonnull)(WKNavigationActionPolicy))decisionHandler;
- (KlarnaStandaloneWebView *)createKlarnaStandaloneWebView;
- (void)sendLoadProgressEvent:(int)progress;
- (NSString *)serializeDictionaryToJsonString:(NSDictionary *)dictionary;

@end

// The real SDK view crashes in the test environment. This subclass records each creation
// and hands out a mock view instead, so the rest of the real initializer still runs.
@interface TestableStandaloneWebViewWrapper : KlarnaStandaloneWebViewWrapper

@property (nonatomic, strong, nullable) id webViewToCreate;
@property (nonatomic, strong, nullable) id attachedWebView;
@property (nonatomic, assign) NSInteger createCount;
@property (nonatomic, strong) NSMutableArray<NSNumber *> *sentProgress;
@property (nonatomic, assign) BOOL recordsProgress;

@end

@implementation TestableStandaloneWebViewWrapper

- (instancetype)initWithFrame:(CGRect)frame {
    self = [super initWithFrame:frame];
    if (self) {
        _sentProgress = [NSMutableArray array];
    }
    return self;
}

- (KlarnaStandaloneWebView *)klarnaStandaloneWebView {
    return self.attachedWebView;
}

- (void)setKlarnaStandaloneWebView:(KlarnaStandaloneWebView *)klarnaStandaloneWebView {
    self.attachedWebView = klarnaStandaloneWebView;
}

- (KlarnaStandaloneWebView *)createKlarnaStandaloneWebView {
    self.createCount++;
    return self.webViewToCreate;
}

- (void)addSubview:(UIView *)view {
    // A mock can't join a real view hierarchy.
    if (view != self.webViewToCreate) {
        [super addSubview:view];
    }
}

- (void)sendLoadProgressEvent:(int)progress {
    if (self.recordsProgress) {
        [self.sentProgress addObject:@(progress)];
    } else {
        [super sendLoadProgressEvent:progress];
    }
}

@end

static Props::Shared MakeProps(const std::string &returnUrl, const std::string &theme = "", bool bounces = true) {
    auto props = std::make_shared<RNKlarnaStandaloneWebViewProps>();
    props->returnUrl = returnUrl;
    props->theme = theme;
    props->bounces = bounces;
    return props;
}

@interface KlarnaStandaloneWebViewWrapperNewArchTests : XCTestCase

@property (nonatomic, strong) TestableStandaloneWebViewWrapper *wrapper;
@property (nonatomic, strong) KlarnaStandaloneWebView *webView;
@property (nonatomic, strong) NSMutableArray<NSString *> *logs;
@property (nonatomic, assign) RCTLogLevel previousLogThreshold;
@property (nonatomic, copy) RCTLogFunction previousLogFunction;

@end

@implementation KlarnaStandaloneWebViewWrapperNewArchTests

- (void)setUp {
    self.webView = OCMClassMock([KlarnaStandaloneWebView class]);
    self.wrapper = [[TestableStandaloneWebViewWrapper alloc] initWithFrame:CGRectZero];
    self.wrapper.webViewToCreate = self.webView;

    // A Fabric event emitter can't be observed in a unit test: without an event dispatcher
    // it drops every event. The wrapper logs each event it sends, so the event tests
    // capture the log to see what was sent.
    self.logs = [NSMutableArray array];
    self.previousLogThreshold = RCTGetLogThreshold();
    self.previousLogFunction = RCTGetLogFunction();
    RCTSetLogThreshold(RCTLogLevelInfo);
    __weak __typeof(self) weakSelf = self;
    RCTSetLogFunction(^(RCTLogLevel level, RCTLogSource source, NSString *fileName, NSNumber *lineNumber, NSString *message) {
        [weakSelf.logs addObject:message];
    });
}

- (void)tearDown {
    RCTSetLogFunction(self.previousLogFunction);
    RCTSetLogThreshold(self.previousLogThreshold);
    // dealloc removes the wrapper as an observer of its view. A mock would record that call
    // and retain the wrapper while it's being deallocated, so the view is detached first.
    self.wrapper.attachedWebView = nil;
    self.wrapper = nil;
    self.webView = nil;
}

#pragma mark - returnUrl Tests

- (void)test_createsViewWhenReturnUrlArrives {
    // WHEN
    [self updateProps:MakeProps("testapp://return")];

    // THEN
    XCTAssertEqual(self.wrapper.createCount, 1);
    XCTAssertEqualObjects(self.wrapper.returnUrl, @"testapp://return");
}

- (void)test_doesNotCreateViewWithEmptyReturnUrl {
    // WHEN
    [self updateProps:MakeProps("")];

    // THEN
    XCTAssertEqual(self.wrapper.createCount, 0);
}

- (void)test_doesNotCreateViewAgainWhenOtherPropsChange {
    // GIVEN
    [self updateProps:MakeProps("testapp://return", "", true)];

    // WHEN
    [self updateProps:MakeProps("testapp://return", "", false)];

    // THEN
    XCTAssertEqual(self.wrapper.createCount, 1);
}

- (void)test_updatesReturnUrlOfExistingView {
    // GIVEN
    [self updateProps:MakeProps("testapp://return")];

    // WHEN
    [self updateProps:MakeProps("testapp://other")];

    // THEN
    XCTAssertEqual(self.wrapper.createCount, 1);
    OCMVerify([self.webView setReturnURL:[NSURL URLWithString:@"testapp://other"]]);
}

#pragma mark - bounces Tests

- (void)test_appliesBouncesToTheWebView {
    // GIVEN
    WKWebView *innerWebView = [[WKWebView alloc] initWithFrame:CGRectZero];
    OCMStub([self.webView subviews]).andReturn(@[innerWebView]);
    [self updateProps:MakeProps("testapp://return", "", true)];
    XCTAssertTrue(innerWebView.scrollView.bounces);

    // WHEN
    [self updateProps:MakeProps("testapp://return", "", false)];

    // THEN
    XCTAssertFalse(innerWebView.scrollView.bounces);
}

#pragma mark - theme Tests

- (void)test_themeArrivingWithFirstReturnUrl_isAppliedToTheCreatedView {
    // The theme is handled while there is no view yet, so only
    // initializeKlarnaStandaloneWebView can apply it to the view it creates.
    // WHEN
    [self updateProps:MakeProps("testapp://return", "dark")];

    // THEN
    XCTAssertEqual(self.wrapper.createCount, 1);
    OCMVerify([self.webView setTheme:KlarnaThemeDark]);
}

- (void)test_lightTheme_setsLightThemeOnView {
    // GIVEN
    [self updateProps:MakeProps("testapp://return")];

    // WHEN
    [self updateProps:MakeProps("testapp://return", "light")];

    // THEN
    OCMVerify([self.webView setTheme:KlarnaThemeLight]);
}

- (void)test_darkTheme_setsDarkThemeOnView {
    // GIVEN
    [self updateProps:MakeProps("testapp://return")];

    // WHEN
    [self updateProps:MakeProps("testapp://return", "dark")];

    // THEN
    OCMVerify([self.webView setTheme:KlarnaThemeDark]);
}

- (void)test_automaticTheme_setsAutomaticThemeOnView {
    // GIVEN
    [self updateProps:MakeProps("testapp://return")];

    // WHEN
    [self updateProps:MakeProps("testapp://return", "automatic")];

    // THEN
    OCMVerify([self.webView setTheme:KlarnaThemeAutomatic]);
}

- (void)test_unknownTheme_keepsCurrentTheme {
    // GIVEN
    [self updateProps:MakeProps("testapp://return")];
    [self rejectAnyTheme];

    // WHEN
    [self updateProps:MakeProps("testapp://return", "unknown")];

    // THEN
    OCMVerifyAll((id)self.webView);
}

- (void)test_emptyTheme_keepsCurrentTheme {
    // JS always sends a theme, light when the prop is not set. An empty value is ignored.
    // GIVEN
    [self updateProps:MakeProps("testapp://return", "dark")];
    [self rejectAnyTheme];

    // WHEN
    [self updateProps:MakeProps("testapp://return", "")];

    // THEN
    OCMVerifyAll((id)self.webView);
}

- (void)test_unchangedTheme_isNotSetAgainWhenOtherPropsChange {
    // GIVEN
    [self updateProps:MakeProps("testapp://return", "dark", true)];
    [self rejectAnyTheme];

    // WHEN
    [self updateProps:MakeProps("testapp://return", "dark", false)];

    // THEN
    OCMVerifyAll((id)self.webView);
}

#pragma mark - Command Tests

- (void)test_load_loadsValidUrl {
    // GIVEN
    [self updateProps:MakeProps("testapp://return")];

    // WHEN
    [self.wrapper load:@"https://www.klarna.com" optionsJson:nil];

    // THEN
    OCMVerify([self.webView loadURL:[NSURL URLWithString:@"https://www.klarna.com"]]);
}

- (void)test_load_ignoresUrlWithoutSchemeOrHost {
    // GIVEN
    [self updateProps:MakeProps("testapp://return")];
    OCMReject([self.webView loadURL:OCMOCK_ANY]);

    // WHEN
    [self.wrapper load:@"www.klarna.com" optionsJson:nil];
    [self.wrapper load:@"mailto:someone@klarna.com" optionsJson:nil];
    [self.wrapper load:@"not a url" optionsJson:nil];
    [self.wrapper load:@"" optionsJson:nil];

    // THEN
    OCMVerifyAll((id)self.webView);
}

- (void)test_handleCommand_routesEachCommandToItsMethod {
    // GIVEN
    [self updateProps:MakeProps("testapp://return")];

    // WHEN
    [self.wrapper handleCommand:@"load" args:@[@"https://www.klarna.com", @""]];
    [self.wrapper handleCommand:@"goForward" args:@[]];
    [self.wrapper handleCommand:@"goBack" args:@[]];
    [self.wrapper handleCommand:@"reload" args:@[]];
    [self.wrapper handleCommand:@"stopLoading" args:@[]];

    // THEN
    OCMVerify([self.webView loadURL:[NSURL URLWithString:@"https://www.klarna.com"]]);
    OCMVerify([self.webView goForward]);
    OCMVerify([self.webView goBack]);
    OCMVerify([self.webView reload]);
    OCMVerify([self.webView stopLoading]);
}

#pragma mark - onLoadStart / onLoadEnd Tests

- (void)test_didCommit_sendsLoadStart {
    // GIVEN
    [self attachEventEmitter];

    // WHEN
    [self.wrapper klarnaStandaloneWebView:[self webViewWithoutPage] didCommit:OCMClassMock([WKNavigation class])];

    // THEN
    XCTAssertTrue([self.logs containsObject:@"Sending onLoadStart event"]);
}

- (void)test_didFinish_sendsLoadEnd {
    // GIVEN
    [self attachEventEmitter];

    // WHEN
    [self.wrapper klarnaStandaloneWebView:[self webViewWithoutPage] didFinish:OCMClassMock([WKNavigation class])];

    // THEN
    XCTAssertTrue([self.logs containsObject:@"Sending onLoadEnd event"]);
}

#pragma mark - onError Tests

- (void)test_didFailProvisionalNavigation_sendsErrorWhenWebViewHasNoUrl {
    // A failed first load leaves the web view without a URL or title; converting those
    // nils to C++ strings must not crash.
    // GIVEN
    [self attachEventEmitter];

    // WHEN
    [self.wrapper klarnaStandaloneWebView:[self webViewWithoutPage]
             didFailProvisionalNavigation:OCMClassMock([WKNavigation class])
                                withError:[self cannotFindHostError]];

    // THEN
    XCTAssertTrue([self.logs containsObject:@"Sending onError event"]);
}

- (void)test_didFail_sendsErrorWhenWebViewHasNoUrl {
    // The native SDK reports failures before anything loads through didFail:.
    // GIVEN
    [self attachEventEmitter];

    // WHEN
    [self.wrapper klarnaStandaloneWebView:[self webViewWithoutPage]
                                  didFail:OCMClassMock([WKNavigation class])
                                withError:[self cannotFindHostError]];

    // THEN
    XCTAssertTrue([self.logs containsObject:@"Sending onError event"]);
}

- (void)test_didFailProvisionalNavigation_ignoresCancelledNavigation {
    // GIVEN
    [self attachEventEmitter];
    NSError *error = [NSError errorWithDomain:NSURLErrorDomain code:NSURLErrorCancelled userInfo:nil];

    // WHEN
    [self.wrapper klarnaStandaloneWebView:[self webViewWithoutPage]
             didFailProvisionalNavigation:OCMClassMock([WKNavigation class])
                                withError:error];

    // THEN
    XCTAssertFalse([self.logs containsObject:@"Sending onError event"]);
}

- (void)test_didFail_ignoresCancelledNavigation {
    // GIVEN
    [self attachEventEmitter];
    NSError *error = [NSError errorWithDomain:NSURLErrorDomain code:NSURLErrorCancelled userInfo:nil];

    // WHEN
    [self.wrapper klarnaStandaloneWebView:[self webViewWithoutPage]
                                  didFail:OCMClassMock([WKNavigation class])
                                withError:error];

    // THEN
    XCTAssertFalse([self.logs containsObject:@"Sending onError event"]);
}

- (void)test_didFinish_retiresLoadOptions {
    // GIVEN
    [self attachEventEmitter];
    self.wrapper.firstLoadOptions = [KlarnaStandaloneWebViewLoadOptions optionsWithJsonString:@"{\"headers\":{\"X-Store\":\"42\"}}"];

    // WHEN
    [self.wrapper klarnaStandaloneWebView:[self webViewWithoutPage] didFinish:OCMClassMock([WKNavigation class])];

    // THEN
    XCTAssertNil(self.wrapper.firstLoadOptions);
}

- (void)test_didFail_retiresLoadOptions {
    // GIVEN
    [self attachEventEmitter];
    self.wrapper.firstLoadOptions = [KlarnaStandaloneWebViewLoadOptions optionsWithJsonString:@"{\"headers\":{\"X-Store\":\"42\"}}"];

    // WHEN
    [self.wrapper klarnaStandaloneWebView:[self webViewWithoutPage] didFail:OCMClassMock([WKNavigation class]) withError:[self cannotFindHostError]];

    // THEN
    XCTAssertNil(self.wrapper.firstLoadOptions);
}

- (void)test_didFailProvisionalNavigation_retiresLoadOptions {
    // GIVEN
    [self attachEventEmitter];
    self.wrapper.firstLoadOptions = [KlarnaStandaloneWebViewLoadOptions optionsWithJsonString:@"{\"headers\":{\"X-Store\":\"42\"}}"];

    // WHEN
    [self.wrapper klarnaStandaloneWebView:[self webViewWithoutPage] didFailProvisionalNavigation:OCMClassMock([WKNavigation class]) withError:[self cannotFindHostError]];

    // THEN
    XCTAssertNil(self.wrapper.firstLoadOptions);
}

- (void)test_cancelledNavigation_keepsLoadOptionsOfTheNewLoad {
    // A cancel of the previous load can arrive after the next load installed its options.
    // GIVEN
    [self attachEventEmitter];
    self.wrapper.firstLoadOptions = [KlarnaStandaloneWebViewLoadOptions optionsWithJsonString:@"{\"headers\":{\"X-Store\":\"42\"}}"];
    NSError *cancelled = [NSError errorWithDomain:NSURLErrorDomain code:NSURLErrorCancelled userInfo:nil];

    // WHEN
    [self.wrapper klarnaStandaloneWebView:[self webViewWithoutPage] didFail:OCMClassMock([WKNavigation class]) withError:cancelled];
    [self.wrapper klarnaStandaloneWebView:[self webViewWithoutPage] didFailProvisionalNavigation:OCMClassMock([WKNavigation class]) withError:cancelled];

    // THEN
    XCTAssertNotNil(self.wrapper.firstLoadOptions);
}

#pragma mark - onLoadProgress Tests

- (void)test_progressChange_isSentAsPercentage {
    // GIVEN
    self.wrapper.recordsProgress = YES;

    // WHEN
    [self.wrapper observeValueForKeyPath:@"estimatedProgress" ofObject:self.webView change:@{NSKeyValueChangeNewKey: @0.0} context:nil];
    [self.wrapper observeValueForKeyPath:@"estimatedProgress" ofObject:self.webView change:@{NSKeyValueChangeNewKey: @0.42} context:nil];
    [self.wrapper observeValueForKeyPath:@"estimatedProgress" ofObject:self.webView change:@{NSKeyValueChangeNewKey: @1.0} context:nil];

    // THEN
    NSArray *expected = @[@0, @42, @100];
    XCTAssertEqualObjects(self.wrapper.sentProgress, expected);
}

- (void)test_sendLoadProgressEvent_sendsLoadProgress {
    // GIVEN
    [self updateProps:MakeProps("testapp://return")];
    [self attachEventEmitter];

    // WHEN
    [self.wrapper sendLoadProgressEvent:50];

    // THEN
    XCTAssertTrue([self.logs containsObject:@"Sending onLoadProgress event"]);
}

#pragma mark - onKlarnaMessage Tests

- (void)test_dispatchedEvent_sendsKlarnaMessage {
    // GIVEN
    [self attachEventEmitter];
    id event = OCMClassMock([KlarnaProductEvent class]);
    OCMStub([event action]).andReturn(@"complete");
    OCMStub([event getParams]).andReturn(@{@"orderId": @"abc"});

    // WHEN
    [self.wrapper klarnaComponent:OCMProtocolMock(@protocol(KlarnaComponent)) dispatchedEvent:event];

    // THEN
    XCTAssertTrue([self.logs containsObject:@"Sending onKlarnaMessage event"]);
}

- (void)test_serializeDictionaryToJsonString_serializesParams {
    // WHEN
    NSString *json = [self.wrapper serializeDictionaryToJsonString:@{@"orderId": @"abc", @"amount": @42}];

    // THEN
    NSDictionary *parsed = [NSJSONSerialization JSONObjectWithData:[json dataUsingEncoding:NSUTF8StringEncoding] options:0 error:nil];
    NSDictionary *expected = @{@"orderId": @"abc", @"amount": @42};
    XCTAssertEqualObjects(parsed, expected);
}

- (void)test_serializeDictionaryToJsonString_returnsEmptyObjectForNil {
    XCTAssertEqualObjects([self.wrapper serializeDictionaryToJsonString:nil], @"{}");
}

#pragma mark - No Event Emitter Tests

- (void)test_eventsWithoutEventEmitter_doNotThrow {
    // Fabric drops the event emitter when it recycles the view, while a load the view
    // started can still report back.
    KlarnaStandaloneWebView *webView = [self webViewWithoutPage];
    id event = OCMClassMock([KlarnaProductEvent class]);
    OCMStub([event action]).andReturn(@"complete");

    XCTAssertNoThrow([self.wrapper klarnaStandaloneWebView:webView didCommit:OCMClassMock([WKNavigation class])]);
    XCTAssertNoThrow([self.wrapper klarnaStandaloneWebView:webView didFinish:OCMClassMock([WKNavigation class])]);
    XCTAssertNoThrow([self.wrapper klarnaStandaloneWebView:webView didFail:OCMClassMock([WKNavigation class]) withError:[self cannotFindHostError]]);
    XCTAssertNoThrow([self.wrapper sendLoadProgressEvent:50]);
    XCTAssertNoThrow([self.wrapper klarnaComponent:OCMProtocolMock(@protocol(KlarnaComponent)) dispatchedEvent:event]);
    XCTAssertFalse([self.logs containsObject:@"Sending onError event"]);
}

#pragma mark - Helpers

- (void)updateProps:(Props::Shared)props {
    [self.wrapper updateProps:props oldProps:[self.wrapper props]];
}

- (void)attachEventEmitter {
    [self.wrapper updateEventEmitter:std::make_shared<const RNKlarnaStandaloneWebViewEventEmitter>(nullptr, EventDispatcher::Weak{})];
}

- (void)rejectAnyTheme {
    OCMReject([self.webView setTheme:KlarnaThemeLight]);
    OCMReject([self.webView setTheme:KlarnaThemeDark]);
    OCMReject([self.webView setTheme:KlarnaThemeAutomatic]);
}

- (KlarnaStandaloneWebView *)webViewWithoutPage {
    id webView = OCMClassMock([KlarnaStandaloneWebView class]);
    OCMStub([webView url]).andReturn(nil);
    OCMStub([webView title]).andReturn(nil);
    return webView;
}

- (NSError *)cannotFindHostError {
    return [NSError errorWithDomain:NSURLErrorDomain
                               code:NSURLErrorCannotFindHost
                           userInfo:@{
        NSURLErrorFailingURLErrorKey: [NSURL URLWithString:@"https://nonexistent.invalid/"],
        NSLocalizedDescriptionKey: @"A server with the specified hostname could not be found.",
    }];
}

#pragma mark - Load Tests

- (void)test_navigationCommandsInvalidateAPendingLoad {
    // goBack, goForward and reload supersede a load that is still copying shared cookies.
    self.wrapper.pendingLoad = [NSObject new];
    [self.wrapper goBack];
    XCTAssertNil(self.wrapper.pendingLoad);

    self.wrapper.pendingLoad = [NSObject new];
    [self.wrapper goForward];
    XCTAssertNil(self.wrapper.pendingLoad);

    self.wrapper.pendingLoad = [NSObject new];
    [self.wrapper reload];
    XCTAssertNil(self.wrapper.pendingLoad);
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

@end
#endif // RCT_NEW_ARCH_ENABLED

#import <XCTest/XCTest.h>

#if !RCT_NEW_ARCH_ENABLED
#import <OCMock/OCMock.h>
#import <KlarnaMobileSDK/KlarnaMobileSDK.h>
#import <KlarnaMobileSDK/KlarnaMobileSDK-Swift.h>

#import "../../Sources/view/KlarnaStandaloneWebViewWrapper.h"

@interface KlarnaStandaloneWebViewWrapper () <KlarnaStandaloneWebViewDelegate>

// Expose private properties and methods for testing
@property (nonatomic, strong) KlarnaStandaloneWebView* klarnaStandaloneWebView;
- (void) initializeKlarnaStandaloneWebView;

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

- (void)test_returnUrlCanBeSetBeforeBounces {
    // WHEN
    [self.wrapper setReturnUrl:@"testapp://return"];
    XCTAssertNoThrow([self.wrapper setBounces:YES]);

    // THEN
    OCMVerify([self.wrapper initializeKlarnaStandaloneWebView]);
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

#pragma mark - Helpers

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

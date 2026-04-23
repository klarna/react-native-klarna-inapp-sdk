#import <XCTest/XCTest.h>
#import <OCMock/OCMock.h>
#import <KlarnaMobileSDK/KlarnaMobileSDK.h>
#import <KlarnaMobileSDK/KlarnaMobileSDK-Swift.h>

#import "../Sources/view/KlarnaStandaloneWebViewWrapper.h"

@interface KlarnaStandaloneWebViewWrapper ()

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

@end

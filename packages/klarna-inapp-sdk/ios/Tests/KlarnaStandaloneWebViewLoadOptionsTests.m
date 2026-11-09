#import <XCTest/XCTest.h>

#import "../Sources/view/KlarnaStandaloneWebViewLoadOptions.h"

@interface KlarnaStandaloneWebViewLoadOptionsTests : XCTestCase
@end

@implementation KlarnaStandaloneWebViewLoadOptionsTests

- (void)test_nilJsonHasNoHeaders {
    XCTAssertEqualObjects([KlarnaStandaloneWebViewLoadOptions optionsWithJsonString:nil].headers, @{});
}

- (void)test_emptyJsonHasNoHeaders {
    XCTAssertEqualObjects([KlarnaStandaloneWebViewLoadOptions optionsWithJsonString:@""].headers, @{});
}

- (void)test_invalidJsonHasNoHeaders {
    XCTAssertEqualObjects([KlarnaStandaloneWebViewLoadOptions optionsWithJsonString:@"{not json"].headers, @{});
}

- (void)test_parsesHeaders {
    KlarnaStandaloneWebViewLoadOptions *options = [KlarnaStandaloneWebViewLoadOptions
        optionsWithJsonString:@"{\"headers\":{\"X-Store\":\"42\",\"Accept-Language\":\"en-US\"}}"];

    XCTAssertEqualObjects(options.headers, (@{@"X-Store": @"42", @"Accept-Language": @"en-US"}));
}

- (void)test_ignoresOtherKeys {
    KlarnaStandaloneWebViewLoadOptions *options = [KlarnaStandaloneWebViewLoadOptions
        optionsWithJsonString:@"{\"cookies\":[{\"name\":\"a\",\"value\":\"1\"}]}"];

    XCTAssertEqualObjects(options.headers, @{});
}

- (void)test_nonStringHeaderMakesOptionsInvalid {
    KlarnaStandaloneWebViewLoadOptions *options = [KlarnaStandaloneWebViewLoadOptions
        optionsWithJsonString:@"{\"headers\":{\"X-Store\":42}}"];

    XCTAssertEqualObjects(options.headers, @{});
}

- (void)test_requestCarriesTheHeaders {
    KlarnaStandaloneWebViewLoadOptions *options = [KlarnaStandaloneWebViewLoadOptions
        optionsWithJsonString:@"{\"headers\":{\"X-Store\":\"42\"}}"];
    NSURL *url = [NSURL URLWithString:@"https://www.example.com/checkout"];

    NSURLRequest *request = [options requestWithURL:url];

    XCTAssertEqualObjects(request.URL, url);
    XCTAssertEqualObjects([request valueForHTTPHeaderField:@"X-Store"], @"42");
}

#pragma mark - Redirects

- (KlarnaStandaloneWebViewLoadOptions *)storeHeaderOptions {
    return [KlarnaStandaloneWebViewLoadOptions optionsWithJsonString:@"{\"headers\":{\"X-Store\":\"42\"}}"];
}

- (NSURLRequest *)requestTo:(NSString *)url withStoreHeader:(BOOL)withHeader {
    NSMutableURLRequest *request = [NSMutableURLRequest requestWithURL:[NSURL URLWithString:url]];
    if (withHeader) {
        [request setValue:@"42" forHTTPHeaderField:@"X-Store"];
    }
    return request;
}

- (void)test_headersCarriedToAnotherOriginAreDetected {
    NSURL *loaded = [NSURL URLWithString:@"https://www.example.com/checkout"];

    XCTAssertTrue([[self storeHeaderOptions]
        isCarriedByRequest:[self requestTo:@"https://postman-echo.com/headers" withStoreHeader:YES] toAnotherOriginThan:loaded]);
    XCTAssertTrue([[self storeHeaderOptions]
        isCarriedByRequest:[self requestTo:@"https://checkout.example.com/" withStoreHeader:YES] toAnotherOriginThan:loaded]);
}

- (void)test_headersCarriedToAnotherSchemeOrPortAreDetected {
    NSURL *loaded = [NSURL URLWithString:@"https://www.example.com/checkout"];

    XCTAssertTrue([[self storeHeaderOptions]
        isCarriedByRequest:[self requestTo:@"http://www.example.com/checkout" withStoreHeader:YES] toAnotherOriginThan:loaded]);
    XCTAssertTrue([[self storeHeaderOptions]
        isCarriedByRequest:[self requestTo:@"https://www.example.com:8443/checkout" withStoreHeader:YES] toAnotherOriginThan:loaded]);
}

- (void)test_aDefaultPortWrittenOutIsTheSameOrigin {
    NSURL *loaded = [NSURL URLWithString:@"https://www.example.com/checkout"];

    XCTAssertFalse([[self storeHeaderOptions]
        isCarriedByRequest:[self requestTo:@"https://www.example.com:443/payment" withStoreHeader:YES] toAnotherOriginThan:loaded]);
    XCTAssertFalse([[self storeHeaderOptions]
        isCarriedByRequest:[self requestTo:@"HTTPS://www.example.com/payment" withStoreHeader:YES] toAnotherOriginThan:loaded]);
}

- (void)test_headersOnTheSameHostAreKept {
    NSURL *loaded = [NSURL URLWithString:@"https://www.example.com/checkout"];

    XCTAssertFalse([[self storeHeaderOptions]
        isCarriedByRequest:[self requestTo:@"https://www.example.com/payment" withStoreHeader:YES] toAnotherOriginThan:loaded]);
    XCTAssertFalse([[self storeHeaderOptions]
        isCarriedByRequest:[self requestTo:@"https://WWW.Example.com/payment" withStoreHeader:YES] toAnotherOriginThan:loaded]);
}

- (void)test_requestsWithoutTheHeadersAreIgnored {
    NSURL *loaded = [NSURL URLWithString:@"https://www.example.com/checkout"];

    XCTAssertFalse([[self storeHeaderOptions]
        isCarriedByRequest:[self requestTo:@"https://postman-echo.com/headers" withStoreHeader:NO] toAnotherOriginThan:loaded]);
    XCTAssertFalse([[KlarnaStandaloneWebViewLoadOptions optionsWithJsonString:nil]
        isCarriedByRequest:[self requestTo:@"https://postman-echo.com/headers" withStoreHeader:YES] toAnotherOriginThan:loaded]);
}

- (void)test_removingTheHeadersKeepsTheRestOfTheRedirectRequest {
    NSMutableURLRequest *redirect = [[self requestTo:@"https://postman-echo.com/post" withStoreHeader:YES] mutableCopy];
    redirect.HTTPMethod = @"POST";
    redirect.HTTPBody = [@"a=b" dataUsingEncoding:NSUTF8StringEncoding];
    [redirect setValue:@"text/plain" forHTTPHeaderField:@"Content-Type"];

    NSURLRequest *restarted = [[self storeHeaderOptions] requestByRemovingHeadersFromRequest:redirect];

    XCTAssertNil([restarted valueForHTTPHeaderField:@"X-Store"]);
    XCTAssertEqualObjects(restarted.URL, redirect.URL);
    XCTAssertEqualObjects(restarted.HTTPMethod, @"POST");
    XCTAssertEqualObjects(restarted.HTTPBody, redirect.HTTPBody);
    XCTAssertEqualObjects([restarted valueForHTTPHeaderField:@"Content-Type"], @"text/plain");
}

- (void)test_removingTheHeadersLeavesAHeaderWithAnotherValue {
    NSMutableURLRequest *redirect = [NSMutableURLRequest requestWithURL:[NSURL URLWithString:@"https://postman-echo.com/headers"]];
    [redirect setValue:@"set-by-the-site" forHTTPHeaderField:@"X-Store"];

    NSURLRequest *restarted = [[self storeHeaderOptions] requestByRemovingHeadersFromRequest:redirect];

    XCTAssertEqualObjects([restarted valueForHTTPHeaderField:@"X-Store"], @"set-by-the-site");
}

@end

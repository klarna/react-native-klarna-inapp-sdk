#import <XCTest/XCTest.h>

#import "../../Sources/common/RNMobileSDKUtils.h"

@interface RNMobileSDKUtilsTests : XCTestCase
@end

@implementation RNMobileSDKUtilsTests

#pragma mark - RNMSDKStdString Tests

- (void)test_stdString_returnsEmptyStringForNil {
    // UTF8String returns NULL for nil, and std::string(NULL) crashes.
    XCTAssertTrue(RNMSDKStdString(nil).empty());
}

- (void)test_stdString_returnsEmptyStringForEmptyString {
    XCTAssertTrue(RNMSDKStdString(@"").empty());
}

- (void)test_stdString_convertsAsciiString {
    XCTAssertEqualObjects([NSString stringWithUTF8String:RNMSDKStdString(@"https://www.klarna.com").c_str()], @"https://www.klarna.com");
}

- (void)test_stdString_convertsNonAsciiString {
    XCTAssertEqualObjects([NSString stringWithUTF8String:RNMSDKStdString(@"åäö").c_str()], @"åäö");
}

- (void)test_stdString_returnsEmptyStringForNonStringValue {
    // A bridged dictionary can hold NSNull or a number where a string is expected.
    XCTAssertTrue(RNMSDKStdString([NSNull null]).empty());
    XCTAssertTrue(RNMSDKStdString(@42).empty());
}

#pragma mark - RNMSDKNavigationErrorUtil Tests

- (void)test_urlString_prefersFailingUrlOverWebViewUrl {
    // After page A has loaded, a failed navigation to page B must report B, like Android.
    NSURL *webViewUrl = [NSURL URLWithString:@"https://www.klarna.com"];
    NSError *error = [self errorWithUserInfo:@{
        NSURLErrorFailingURLErrorKey: [NSURL URLWithString:@"https://nonexistent.invalid/"],
    }];

    XCTAssertEqualObjects([RNMSDKNavigationErrorUtil urlStringForWebViewURL:webViewUrl error:error], @"https://nonexistent.invalid/");
}

- (void)test_urlString_usesFailingUrlWhenWebViewHasNoUrl {
    // The web view has no URL when the first load fails before anything loads.
    NSError *error = [self errorWithUserInfo:@{
        NSURLErrorFailingURLErrorKey: [NSURL URLWithString:@"https://nonexistent.invalid/"],
    }];

    XCTAssertEqualObjects([RNMSDKNavigationErrorUtil urlStringForWebViewURL:nil error:error], @"https://nonexistent.invalid/");
}

- (void)test_urlString_fallsBackToFailingUrlString {
    NSError *error = [self errorWithUserInfo:@{
        NSURLErrorFailingURLStringErrorKey: @"https://nonexistent.invalid/",
    }];

    XCTAssertEqualObjects([RNMSDKNavigationErrorUtil urlStringForWebViewURL:nil error:error], @"https://nonexistent.invalid/");
}

- (void)test_urlString_fallsBackToWebViewUrlWhenErrorHasNoUrl {
    NSURL *webViewUrl = [NSURL URLWithString:@"https://www.klarna.com"];
    NSError *error = [self errorWithUserInfo:nil];

    XCTAssertEqualObjects([RNMSDKNavigationErrorUtil urlStringForWebViewURL:webViewUrl error:error], @"https://www.klarna.com");
}

- (void)test_urlString_returnsEmptyStringWhenNoUrlIsKnown {
    NSError *error = [self errorWithUserInfo:nil];

    XCTAssertEqualObjects([RNMSDKNavigationErrorUtil urlStringForWebViewURL:nil error:error], @"");
}

#pragma mark - Helpers

- (NSError *)errorWithUserInfo:(NSDictionary *)userInfo {
    return [NSError errorWithDomain:NSURLErrorDomain code:NSURLErrorCannotFindHost userInfo:userInfo];
}

@end

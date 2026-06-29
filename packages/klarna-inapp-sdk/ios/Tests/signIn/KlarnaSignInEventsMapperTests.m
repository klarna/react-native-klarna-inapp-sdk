#import <XCTest/XCTest.h>
#import "../../Sources/signIn/KlarnaSignInEventsMapper.h"

@interface KlarnaSignInEventsMapperTests : XCTestCase
@end

@implementation KlarnaSignInEventsMapperTests

#pragma mark - mapSignInErrorName

- (void)test_mapSignInErrorName_mapsAllKnownErrors {
    XCTAssertEqualObjects([KlarnaSignInEventsMapper mapSignInErrorName:@"klarnaInvalidReturnURLError"],             @"KlarnaSignInInvalidReturnURL");
    XCTAssertEqualObjects([KlarnaSignInEventsMapper mapSignInErrorName:@"klarnaSignInAlreadyInProgress"],          @"KlarnaSignInAlreadyInProgress");
    XCTAssertEqualObjects([KlarnaSignInEventsMapper mapSignInErrorName:@"klarnaSignInAuthorizationFailed"],        @"KlarnaSignInAuthorizationFailed");
    XCTAssertEqualObjects([KlarnaSignInEventsMapper mapSignInErrorName:@"klarnaSignInInvalidClientID"],            @"KlarnaSignInInvalidClientID");
    XCTAssertEqualObjects([KlarnaSignInEventsMapper mapSignInErrorName:@"klarnaSignInInvalidMarket"],              @"KlarnaSignInInvalidMarket");
    XCTAssertEqualObjects([KlarnaSignInEventsMapper mapSignInErrorName:@"klarnaSignInInvalidPresentationContext"], @"KlarnaSignInInvalidPresentationContext");
    XCTAssertEqualObjects([KlarnaSignInEventsMapper mapSignInErrorName:@"klarnaSignInInvalidScope"],               @"KlarnaSignInInvalidScope");
    XCTAssertEqualObjects([KlarnaSignInEventsMapper mapSignInErrorName:@"klarnaSignInMissingTokenizationDelegate"],@"KlarnaSignInMissingTokenizationId");
}

- (void)test_mapSignInErrorName_unknownError_isPassedThrough {
    NSString *unknown = @"someUnknownError";
    XCTAssertEqualObjects([KlarnaSignInEventsMapper mapSignInErrorName:unknown], unknown);
}

#pragma mark - mapSignInEventName

- (void)test_mapSignInEventName_mapsAllKnownEvents {
    XCTAssertEqualObjects([KlarnaSignInEventsMapper mapSignInEventName:@"klarnaToken"],               @"KlarnaSignInToken");
    XCTAssertEqualObjects([KlarnaSignInEventsMapper mapSignInEventName:@"klarnaSignInUserCancelled"], @"KlarnaSignInUserCancelled");
}

- (void)test_mapSignInEventName_unknownEvent_isPassedThrough {
    NSString *unknown = @"someUnknownEvent";
    XCTAssertEqualObjects([KlarnaSignInEventsMapper mapSignInEventName:unknown], unknown);
}

#pragma mark - mapSignInParamName

- (void)test_mapSignInParamName_mapsKlarnaToken {
    XCTAssertEqualObjects([KlarnaSignInEventsMapper mapSignInParamName:@"klarnaToken"], @"KlarnaSignInToken");
}

- (void)test_mapSignInParamName_unknownParam_isPassedThrough {
    NSString *unknown = @"someUnknownParam";
    XCTAssertEqualObjects([KlarnaSignInEventsMapper mapSignInParamName:unknown], unknown);
}

@end

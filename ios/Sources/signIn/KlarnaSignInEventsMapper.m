//
//  KlarnaSignInErrorsMapper.m
//  react-native-klarna-inapp-sdk
//
//  Created by Jorge Palacio on 2025-05-13.
//

#import "KlarnaSignInEventsMapper.h"

@implementation KlarnaSignInEventsMapper
+(NSString *)mapSignInErrorName:(NSString *)errorName {
    NSDictionary *signInErrorsMap = @{
        @"klarnaInvalidReturnURLError": @"KlarnaSignInInvalidReturnURL",
        @"klarnaSignInAlreadyInProgress": @"KlarnaSignInAlreadyInProgress",
        @"klarnaSignInAuthorizationFailed": @"KlarnaSignInAuthorizationFailed",
        @"klarnaSignInInvalidClientID": @"KlarnaSignInInvalidClientID",
        @"klarnaSignInInvalidMarket": @"KlarnaSignInInvalidMarket",
        @"klarnaSignInInvalidPresentationContext": @"KlarnaSignInInvalidPresentationContext",
        @"klarnaSignInInvalidScope": @"KlarnaSignInInvalidScope",
        @"klarnaSignInMissingTokenizationDelegate": @"KlarnaSignInMissingTokenizationId",
    };
    NSString *mappedError = signInErrorsMap[errorName];
    if (mappedError) {
        return mappedError;
    } else {
        return errorName;
    }
}

+(NSString *)mapSignInEventName:(NSString *)eventName {
    NSDictionary *signInEventsMap = @{
        @"klarnaToken": @"KlarnaSignInToken",
        @"klarnaSignInUserCancelled": @"KlarnaSignInUserCancelled"
    };
    NSString *mappedEvent = signInEventsMap[eventName];
    if (mappedEvent) {
        return mappedEvent;
    } else {
        return eventName;
    }
}

@end

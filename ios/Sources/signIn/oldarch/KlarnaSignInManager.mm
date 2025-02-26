//
//  KlarnaSignInSDK.m
//  RNKlarnaMobileSDK
//
//  Created by Jorge Palacio on 2025-02-20.
//  Copyright © 2025 Facebook. All rights reserved.
//

#if !RCT_NEW_ARCH_ENABLED

#import "KlarnaSignInManager.h"
#import "KlarnaSignInEventsHandler.h"
#import "KlarnaMobileSDK/KlarnaMobileSDK-Swift.h"
#import <React/RCTLog.h>

@interface KlarnaSignInManager()
@property (strong, nonatomic) KlarnaSignInSDK *signInSDK;
@property (strong, nonatomic) KlarnaSignInEventsHandler *handler;
@end

@implementation KlarnaSignInManager

RCT_EXPORT_MODULE(RNKlarnaSignIn);

RCT_EXPORT_METHOD(init: (NSString *)environment region: (NSString *)region  returnUrl: (NSString *) returnUrl) {
    RCTLogInfo(@"KlarnaSignInSDK Native Module: Initialised");
    
    KlarnaEnvironment * env = [KlarnaSignInEventsHandler environmentFrom: environment];
    KlarnaRegion * reg = [KlarnaSignInEventsHandler regionFrom: region];
    NSURL *url = [NSURL URLWithString: returnUrl];

    if (url != nil) {
        _handler = [KlarnaSignInEventsHandler new];
        _signInSDK = [[KlarnaSignInSDK alloc]
                      initWithEnvironment: env
                      region: reg
                      returnUrl: url
                      eventHandler: self.handler];
    }
}

RCT_EXPORT_METHOD(signIn:(NSString *)clientId
         scope:(NSString *)scope
        market:(NSString *)market
        locale:(NSString *)locale
tokenizationId:(NSString *)tokenizationId
      resolver:(RCTPromiseResolveBlock)resolve
      rejecter:(RCTPromiseRejectBlock)reject) {
    RCTLogInfo(@"KlarnaSignInSDK Native Module: SignIn started....");
    self.handler.resolver = resolve;
    self.handler.rejecter = reject;
    [self.signInSDK signInClientId: clientId
                             scope: scope
                            market: market
                            locale: locale
                    tokenizationId: tokenizationId
               presentationContext: self.handler];
}

@end

#endif

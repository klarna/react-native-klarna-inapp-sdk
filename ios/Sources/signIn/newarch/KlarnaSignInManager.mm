//
//  KlarnaSignInSDK.m
//  RNKlarnaMobileSDK
//
//  Created by Jorge Palacio on 2025-02-20.
//  Copyright © 2025 Facebook. All rights reserved.
//
#ifdef RCT_NEW_ARCH_ENABLED

#import "KlarnaSignInManager.h"
#import "KlarnaSignInEventsHandler.h"
#import "KlarnaMobileSDK/KlarnaMobileSDK-Swift.h"
#import <React/RCTLog.h>

@interface KlarnaSignInManager()
@property (strong, nonatomic) KlarnaSignInSDK *signInSDK API_AVAILABLE(ios(13.0));
@property (strong, nonatomic) KlarnaSignInEventsHandler *handler;
@end

@implementation KlarnaSignInManager

RCT_EXPORT_MODULE(RNKlarnaSignIn);

- (std::shared_ptr<facebook::react::TurboModule>)getTurboModule:(const facebook::react::ObjCTurboModule::InitParams &)params {
    return std::make_shared<facebook::react::NativeKlarnaSignInSpecJSI>(params);
}

- (void)init: (NSString *)environment region: (NSString *)region  returnUrl: (NSString *) returnUrl {
    RCTLogInfo(@"KlarnaSignInSDK Native Module: Initialized");
    
    KlarnaEnvironment * env = [KlarnaSignInEventsHandler environmentFrom: environment];
    KlarnaRegion * reg = [KlarnaSignInEventsHandler regionFrom: region];
    NSURL *url = [NSURL URLWithString: returnUrl];
    
    if (url != nil) {
        _handler = [KlarnaSignInEventsHandler new];
        if (@available(iOS 13.0, *)) {
            _signInSDK = [[KlarnaSignInSDK alloc]
                          initWithEnvironment: env
                          region: reg
                          returnUrl: url
                          eventHandler: self.handler];
        }
    }
}

- (void)signIn:(NSString *)clientId
         scope:(NSString *)scope
        market:(NSString *)market
        locale:(NSString *)locale
tokenizationId:(NSString *)tokenizationId
      resolve:(RCTPromiseResolveBlock)resolve
      reject:(RCTPromiseRejectBlock)reject {
    RCTLogInfo(@"KlarnaSignInSDK Native Module: SignIn started....");
    self.handler.resolver = resolve;
    self.handler.rejecter = reject;
    if (@available(iOS 13.0, *)) {
        [self.signInSDK signInClientId: clientId
                                 scope: scope
                                market: market
                                locale: locale
                        tokenizationId: tokenizationId
                   presentationContext: self.handler];
    } else {
        [self.handler rejectWithInvalidiOSVersionSupported];
    }
}

@end

#endif

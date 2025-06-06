//
//  KlarnaSignInSDK.m
//  RNKlarnaMobileSDK
//
//  Created by Jorge Palacio on 2025-02-20.
//  Copyright © 2025 Facebook. All rights reserved.
//

#if !RCT_NEW_ARCH_ENABLED

#import "KlarnaSignInModule.h"
#import "KlarnaSignInModuleImpl.h"

@interface KlarnaSignInModule()

@property (strong, nonatomic) KlarnaSignInModuleImp *signInModule;

@end

@implementation KlarnaSignInModule

RCT_EXPORT_MODULE(RNKlarnaSignIn); // This should match the turbo module specification.

- (id)init {
    self = [super init];
    _signInModule = [KlarnaSignInModuleImp new];
    return self;
}

RCT_EXPORT_METHOD(dispose:(NSString *)instanceId
        resolver:(RCTPromiseResolveBlock)resolve
                  rejecter:(RCTPromiseRejectBlock)reject) {
    [self.signInModule dispose: instanceId
                      resolver: resolve
                      rejecter: reject];
}

RCT_EXPORT_METHOD(init:(NSString *)instanceId
        environment:(NSString *)environment
             region:(NSString *)region
          returnUrl:(NSString *) returnUrl
            resolve:(RCTPromiseResolveBlock)resolve
             reject:(RCTPromiseRejectBlock)reject) {
    [self.signInModule initWith: instanceId
                    environment: environment
                         region: region
                      returnUrl: returnUrl
                       resolver: resolve
                       rejecter: reject];
}

RCT_EXPORT_METHOD(signIn:(NSString *)instanceId
      clientId:(NSString *)clientId
         scope:(NSString *)scope
        market:(NSString *)market
        locale:(NSString *)locale
tokenizationId:(NSString *)tokenizationId
      resolver:(RCTPromiseResolveBlock)resolve
      rejecter:(RCTPromiseRejectBlock)reject) {
    [self.signInModule signInWith: instanceId
                         clientId: clientId
                            scope: scope
                           market: market
                           locale: locale
                   tokenizationId: tokenizationId
                         resolver: resolve
                         rejecter: reject];
}

@end

#endif

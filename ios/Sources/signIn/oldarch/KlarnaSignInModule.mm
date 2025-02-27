//
//  KlarnaSignInSDK.m
//  RNKlarnaMobileSDK
//
//  Created by Jorge Palacio on 2025-02-20.
//  Copyright © 2025 Facebook. All rights reserved.
//

#if !RCT_NEW_ARCH_ENABLED

#import "KlarnaSignInModule.h"
#import "KlarnaSignInModuleImp.h"

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

RCT_EXPORT_METHOD(init: (NSString *)environment region: (NSString *)region  returnUrl: (NSString *) returnUrl) {
    [self.signInModule initWith: environment region: region returnUrl: returnUrl];
}

RCT_EXPORT_METHOD(signIn:(NSString *)clientId
         scope:(NSString *)scope
        market:(NSString *)market
        locale:(NSString *)locale
tokenizationId:(NSString *)tokenizationId
      resolver:(RCTPromiseResolveBlock)resolve
      rejecter:(RCTPromiseRejectBlock)reject) {
    [self.signInModule signInWith:clientId
                            scope:scope
                           market:market
                           locale:locale
                   tokenizationId:tokenizationId
                         resolver:resolve
                         rejecter:reject];
}

@end

#endif

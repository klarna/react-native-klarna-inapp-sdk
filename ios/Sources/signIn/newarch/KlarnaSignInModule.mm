//
//  KlarnaSignInSDK.m
//  RNKlarnaMobileSDK
//
//  Created by Jorge Palacio on 2025-02-20.
//  Copyright © 2025 Facebook. All rights reserved.
//
#ifdef RCT_NEW_ARCH_ENABLED

#import "KlarnaSignInModule.h"
#import "KlarnaSignInModuleImp.h"

@interface KlarnaSignInModule()

@property (strong, nonatomic) KlarnaSignInModuleImp *signInModule;

@end

@implementation KlarnaSignInModule

RCT_EXPORT_MODULE(RNKlarnaSignIn);

- (id)init {
    self = [super init];
    _signInModule = [KlarnaSignInModuleImp new];
    return self;
}

- (std::shared_ptr<facebook::react::TurboModule>)getTurboModule:(const facebook::react::ObjCTurboModule::InitParams &)params {
    return std::make_shared<facebook::react::NativeKlarnaSignInSpecJSI>(params);
}

- (void)init: (NSString *)environment region: (NSString *)region  returnUrl: (NSString *) returnUrl {
    [self.signInModule initWith: environment region: region returnUrl: returnUrl];
}

- (void)signIn:(NSString *)clientId
         scope:(NSString *)scope
        market:(NSString *)market
        locale:(NSString *)locale
tokenizationId:(NSString *)tokenizationId
      resolve:(RCTPromiseResolveBlock)resolve
      reject:(RCTPromiseRejectBlock)reject {
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

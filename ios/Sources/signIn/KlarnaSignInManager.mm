//
//  KlarnaSignInSDK.m
//  RNKlarnaMobileSDK
//
//  Created by Jorge Palacio on 2025-02-20.
//  Copyright © 2025 Facebook. All rights reserved.
//

#import "KlarnaSignInManager.h"
#import <RNKlarnaMobileSDK/RNKlarnaMobileSDK.h>
#import "KlarnaMobileSDK/KlarnaMobileSDK-Swift.h"

@interface KlarnaSignInManager() <KlarnaEventHandler>
@property (strong, nonatomic) KlarnaSignInSDK *signInSDK;
@end

@implementation KlarnaSignInManager

RCT_EXPORT_MODULE()

- (std::shared_ptr<facebook::react::TurboModule>)getTurboModule:(const facebook::react::ObjCTurboModule::InitParams &)params { 
    return std::make_shared<facebook::react::NativeKlarnaSignInSpecJSI>(params);
}

- (void)signIn:(NSString *)clientId
         scope:(NSString *)scope
        market:(NSString *)market
        locale:(NSString *)locale
tokenizationId:(NSString *)tokenizationId {
    _signInSDK = [[KlarnaSignInSDK alloc]
                  initWithEnvironment: KlarnaEnvironment.production
                  region: KlarnaRegion.eu
                  returnUrl: [NSURL URLWithString:@""]
                  eventHandler: self];
    NSLog(@"Sign should trigger with the following values:\n clientId: %@\nscope: %@\nmarket: %@\nlocale: %@\ntokenizationId: %@\n", clientId, scope, market, locale, tokenizationId);
}

// MARK: - KlarnaEventHandler Methods

- (void)klarnaComponent:(id <KlarnaComponent> _Nonnull)klarnaComponent dispatchedEvent:(KlarnaProductEvent * _Nonnull)event {
    
}

- (void)klarnaComponent:(id <KlarnaComponent> _Nonnull)klarnaComponent encounteredError:(KlarnaError * _Nonnull)error {
    
}

@end

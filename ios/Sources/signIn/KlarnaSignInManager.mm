//
//  KlarnaSignInSDK.m
//  RNKlarnaMobileSDK
//
//  Created by Jorge Palacio on 2025-02-20.
//  Copyright © 2025 Facebook. All rights reserved.
//

#import "KlarnaSignInManager.h"
#ifdef RCT_NEW_ARCH_ENABLED
#import <RNKlarnaMobileSDK/RNKlarnaMobileSDK.h>
#endif
#import "KlarnaMobileSDK/KlarnaMobileSDK-Swift.h"
#import <AuthenticationServices/AuthenticationServices.h>
#import <UIKit/UIKit.h>
#import <React/RCTLog.h>

@interface KlarnaSignInManager() <KlarnaEventHandler, ASWebAuthenticationPresentationContextProviding>
@property (strong, nonatomic) KlarnaSignInSDK *signInSDK;
@end

@implementation KlarnaSignInManager

RCT_EXPORT_MODULE(RNKlarnaSignIn)

#ifdef RCT_NEW_ARCH_ENABLED
- (std::shared_ptr<facebook::react::TurboModule>)getTurboModule:(const facebook::react::ObjCTurboModule::InitParams &)params {
    return std::make_shared<facebook::react::NativeKlarnaSignInSpecJSI>(params);
}
#endif

- (void)signIn:(NSString *)clientId
         scope:(NSString *)scope
        market:(NSString *)market
        locale:(NSString *)locale
tokenizationId:(NSString *)tokenizationId {
    RCTLogInfo(@"Sign should trigger with the following values:\n clientId: %@\nscope: %@\nmarket: %@\nlocale: %@\ntokenizationId: %@\n", clientId, scope, market, locale, tokenizationId);
    _signInSDK = [[KlarnaSignInSDK alloc]
                  initWithEnvironment: KlarnaEnvironment.staging
                  region: KlarnaRegion.eu
                  returnUrl: [NSURL URLWithString:@"in-app-test://siwk"]
                  eventHandler: self];
    [_signInSDK signInClientId:clientId scope:scope market:market locale:locale tokenizationId:tokenizationId presentationContext:self];
}

// MARK: - KlarnaEventHandler Methods

- (void)klarnaComponent:(id <KlarnaComponent> _Nonnull)klarnaComponent dispatchedEvent:(KlarnaProductEvent * _Nonnull)event {
    RCTLogInfo(@"KlarnaSignIn Native Module Event: %@", event.debugDescription);
}

- (void)klarnaComponent:(id <KlarnaComponent> _Nonnull)klarnaComponent encounteredError:(KlarnaError * _Nonnull)error {
    RCTLogError(@"KlarnaSignIn Native Module Error: %@", error.debugDescription);
}

// MARK: - ASWebAuthenticationPresentationContextProviding

- (ASPresentationAnchor)presentationAnchorForWebAuthenticationSession:(ASWebAuthenticationSession *)session {
    UIWindowScene *windowScene = (UIWindowScene *)[[[UIApplication sharedApplication].connectedScenes objectsPassingTest:^BOOL(UIScene * _Nonnull obj, BOOL * _Nonnull stop) {
        if(obj.activationState == UISceneActivationStateForegroundActive) {
            *stop = YES; // Stop after finding the first match
            return YES;
        }
        return NO;
    }] allObjects].firstObject;

    if (windowScene != nil) {
        UIWindow *currentTopWindow = windowScene.windows.firstObject;
        if (currentTopWindow != nil) {
            RCTLogInfo(@"KlarnaSignIn Native Module Anchor found top window: %@", currentTopWindow.debugDescription);
            return currentTopWindow;
        }
    }
    
    return ASPresentationAnchor();
}

@end

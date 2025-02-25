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
#import <AuthenticationServices/AuthenticationServices.h>
#import <UIKit/UIKit.h>

@interface KlarnaSignInManager() <KlarnaEventHandler, ASWebAuthenticationPresentationContextProviding>
@property (strong, nonatomic) KlarnaSignInSDK *signInSDK;
@end

@implementation KlarnaSignInManager

RCT_EXPORT_MODULE(RNKlarnaSignIn)

- (std::shared_ptr<facebook::react::TurboModule>)getTurboModule:(const facebook::react::ObjCTurboModule::InitParams &)params { 
    return std::make_shared<facebook::react::NativeKlarnaSignInSpecJSI>(params);
}

- (void)signIn:(NSString *)clientId
         scope:(NSString *)scope
        market:(NSString *)market
        locale:(NSString *)locale
tokenizationId:(NSString *)tokenizationId {
    _signInSDK = [[KlarnaSignInSDK alloc]
                  initWithEnvironment: KlarnaEnvironment.staging
                  region: KlarnaRegion.eu
                  returnUrl: [NSURL URLWithString:@"in-app-test://siwk"]
                  eventHandler: self];
    [_signInSDK signInClientId:clientId scope:scope market:market locale:locale tokenizationId:tokenizationId presentationContext:self];
    NSLog(@"Sign should trigger with the following values:\n clientId: %@\nscope: %@\nmarket: %@\nlocale: %@\ntokenizationId: %@\n", clientId, scope, market, locale, tokenizationId);
}

// MARK: - KlarnaEventHandler Methods

- (void)klarnaComponent:(id <KlarnaComponent> _Nonnull)klarnaComponent dispatchedEvent:(KlarnaProductEvent * _Nonnull)event {
    
}

- (void)klarnaComponent:(id <KlarnaComponent> _Nonnull)klarnaComponent encounteredError:(KlarnaError * _Nonnull)error {
    
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
            return currentTopWindow;
        }
    }
    
    return ASPresentationAnchor();
}

@end

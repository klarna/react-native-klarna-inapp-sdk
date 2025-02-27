//
//  KlarnaSignInEventsHandler.m
//  react-native-klarna-inapp-sdk
//
//  Created by Jorge Palacio on 2025-02-26.
//

#import "KlarnaSignInModuleImp.h"
#import "../common/RNMobileSDKUtils.h"
#import <AuthenticationServices/AuthenticationServices.h>
#import "KlarnaMobileSDK/KlarnaMobileSDK-Swift.h"
#import <React/RCTLog.h>

@interface KlarnaSignInModuleImp()<KlarnaEventHandler, ASWebAuthenticationPresentationContextProviding>

@property (strong, nonatomic) KlarnaSignInSDK *signInSDK API_AVAILABLE(ios(13.0));
@property (copy, nonatomic) RCTPromiseResolveBlock resolver;
@property (copy, nonatomic) RCTPromiseRejectBlock rejecter;

-(KlarnaEnvironment *)environmentFrom: (NSString *)value;
-(KlarnaRegion *)regionFrom: (NSString *)value;
-(void)rejectWithInvalidiOSVersionSupported;

@end

@implementation KlarnaSignInModuleImp

-(KlarnaEnvironment *)environmentFrom: (NSString *)value {
    KlarnaEnvironment *env = nil;
    if ([value isEqualToString:@"playground"]) {
        env = KlarnaEnvironment.playground;
    }
    if ([value isEqualToString:@"staging"]) {
        env = KlarnaEnvironment.staging;
    }
    if ([value isEqualToString:@"production"]) {
        env = KlarnaEnvironment.production;
    }
    
    return env;
}

-(KlarnaRegion *)regionFrom: (NSString *)value {
    KlarnaRegion *reg = nil;
    if ([value isEqualToString:@"eu"]) {
        reg = KlarnaRegion.eu;
    }
    
    if ([value isEqualToString:@"na"]) {
        reg = KlarnaRegion.na;
    }
    
    if ([value isEqualToString:@"oc"]) {
        reg = KlarnaRegion.oc;
    }
    
    return reg;
}

-(void)initWith: (NSString *)environment region: (NSString *)region  returnUrl: (NSString *) returnUrl {
    RCTLogInfo(@"KlarnaSignInSDK Native Module: Initialized");
    
    KlarnaEnvironment * env = [self environmentFrom: environment];
    KlarnaRegion * reg = [self regionFrom: region];
    NSURL *url = [NSURL URLWithString: returnUrl];
    
    if (url != nil) {
        if (@available(iOS 13.0, *)) {
            _signInSDK = [[KlarnaSignInSDK alloc]
                          initWithEnvironment: env
                          region: reg
                          returnUrl: url
                          eventHandler: self];
        }
    }
}

-(void)signInWith:(NSString *)clientId
        scope:(NSString *)scope
       market:(NSString *)market
       locale:(NSString *)locale
tokenizationId:(NSString *)tokenizationId
     resolver:(RCTPromiseResolveBlock)resolve
     rejecter:(RCTPromiseRejectBlock)reject {
    RCTLogInfo(@"KlarnaSignInSDK Native Module: SignIn started....");
    _resolver = resolve;
    _rejecter = reject;
    if (@available(iOS 13.0, *)) {
        [self.signInSDK signInClientId: clientId
                                 scope: scope
                                market: market
                                locale: locale
                        tokenizationId: tokenizationId
                   presentationContext: self];
    } else {
        [self rejectWithInvalidiOSVersionSupported];
    }
}


-(void)rejectWithInvalidiOSVersionSupported {
    NSString *msg = @"KlarnaSignIn is supported from iOS version 13.0";
    NSError *error = [NSError errorWithDomain:@"com.klarnamobilesdk"
                                         code:9999
                                     userInfo:@{ @"error": @{ @"message": msg }
                                              }];
    self.rejecter(@"9999", msg, error);
}

// MARK: - KlarnaEventHandler Methods

- (void)klarnaComponent:(id <KlarnaComponent> _Nonnull)klarnaComponent dispatchedEvent:(KlarnaProductEvent * _Nonnull)event {
    RCTLogInfo(@"KlarnaSignIn Native Module Event: %@", event.debugDescription);
    if (!self.rejecter) {
        RCTLog(@"Missing 'rejecter' callback prop.");
        return;
    }
    NSString *serializedParams = [SerializationUtil serializeDictionaryToJsonString:[event getParams]];
    NSDictionary *resolvedEvent = @{
        @"productEvent": @{
            @"action": event.action,
            @"params": serializedParams,
        }
    };
    self.resolver(resolvedEvent);
}

- (void)klarnaComponent:(id <KlarnaComponent> _Nonnull)klarnaComponent encounteredError:(KlarnaError * _Nonnull)error {
    RCTLogError(@"KlarnaSignIn Native Module Error: %@", error.debugDescription);
    if (!self.rejecter) {
        RCTLog(@"Missing 'rejecter' callback prop.");
        return;
    }
    NSError * errorEvent = [NSError errorWithDomain:@"com.klarnamobilesdk" code: 0001 userInfo: @{
        @"error": @{
            @"name": error.name,
            @"message": error.message,
            @"isFatal": [NSNumber numberWithBool: error.isFatal],
        }
    }];
    self.rejecter(@"", error.message, errorEvent);
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
        if (@available(iOS 15.0, *)) {
            return windowScene.keyWindow;
        } else {
            UIWindow *currentTopWindow = windowScene.windows.firstObject;
            if (currentTopWindow != nil) {
                return currentTopWindow;
            }
        }
        
        return [[UIWindow alloc] initWithWindowScene: windowScene];
    }
    
    // This Shouldn't execute app crashes!!!
    // Windows can't be created in a bg thread. Hence the crash.
    return nil;
}

@end

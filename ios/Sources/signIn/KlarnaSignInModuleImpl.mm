//
//  KlarnaSignInEventsHandler.m
//  react-native-klarna-inapp-sdk
//
//  Created by Jorge Palacio on 2025-02-26.
//

#import "KlarnaSignInModuleImpl.h"
#import "../common/RNMobileSDKUtils.h"
#import <AuthenticationServices/AuthenticationServices.h>
#import "KlarnaMobileSDK/KlarnaMobileSDK-Swift.h"
#import <React/RCTLog.h>
#import "KlarnaSignInData.h"
#import "KlarnaSignInEventsMapper.h"

@interface KlarnaSignInModuleImp()<KlarnaEventHandler, ASWebAuthenticationPresentationContextProviding>

@property (nonatomic, strong) NSMutableDictionary<NSString *, KlarnaSignInData *> *signInSDKs;

- (KlarnaEnvironment *)environmentFrom: (NSString *)value;
- (KlarnaRegion *)regionFrom: (NSString *)value;
- (void)rejectWithInvalidiOSVersionSupported:(RCTPromiseRejectBlock) rejectBlock;
- (KlarnaSignInData *)getInstanceFromComponent: (id <KlarnaComponent> _Nonnull)component;

@end

@implementation KlarnaSignInModuleImp

- (id)init {
    self = [super init];
    _signInSDKs = [NSMutableDictionary new];
    return self;
}

- (KlarnaEnvironment *)environmentFrom: (NSString *)value {
    KlarnaEnvironment *env = KlarnaEnvironment.production;
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

- (KlarnaRegion *)regionFrom: (NSString *)value {
    KlarnaRegion *reg = KlarnaRegion.eu;
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

- (KlarnaSignInData *)getInstanceFromComponent:(id<KlarnaComponent>)component {
    for (NSString *key in _signInSDKs) {
        KlarnaSignInData *data = _signInSDKs[key];
        if (data.sdkInstance == component) {
            return data;
        }
    }
    return nil;
}

// MARK: - Module public Methods

- (void)dispose:(NSString *)instanceId
       resolver:(RCTPromiseResolveBlock)resolve
       rejecter:(RCTPromiseRejectBlock)reject {
    KlarnaSignInData *signInData = _signInSDKs[instanceId];
    if (signInData != nil) {
        [_signInSDKs removeObjectForKey:instanceId];
        resolve(nil);
    }
}

- (void)initWith:(NSString *)instanceId
     environment: (NSString *)environment
          region: (NSString *)region
       returnUrl: (NSString *)returnUrl
        resolver:(RCTPromiseResolveBlock)resolve
        rejecter:(RCTPromiseRejectBlock)reject {
    KlarnaEnvironment * env = [self environmentFrom: environment];
    KlarnaRegion * reg = [self regionFrom: region];
    NSURL *url = [NSURL URLWithString: returnUrl];
    if (url != nil) {
        if (@available(iOS 13.0, *)) {
            KlarnaSignInSDK *signInInstance = [[KlarnaSignInSDK alloc] initWithEnvironment: env
                                                                                    region: reg
                                                                                 returnUrl: url
                                                                              eventHandler: self];
            KlarnaSignInData *data = [[KlarnaSignInData alloc] initWith: instanceId sdkInstance: signInInstance];
            [_signInSDKs setObject: data forKey: instanceId];
            resolve(nil);
        } else {
            [self rejectWithInvalidiOSVersionSupported: reject];
        }
    } else {
        RCTLog(@"Invalid returnUrl provided");
        reject(@"RNKlarnaSignIn", @"Invalid returnUrl provided", nil);
    }
}

- (void)signInWith:(NSString *)instanceId
     clientId:(NSString *)clientId
        scope:(NSString *)scope
       market:(NSString *)market
       locale:(NSString *)locale
tokenizationId:(NSString *)tokenizationId
     resolver:(RCTPromiseResolveBlock)resolve
     rejecter:(RCTPromiseRejectBlock)reject {
    if (@available(iOS 13.0, *)) {
        KlarnaSignInData *signInData = [_signInSDKs objectForKey: instanceId];
        if (signInData == nil) {
            RCTLog(@"No instance found for the given instanceId");
            reject(@"RNKlarnaSignIn", @"No instance found to initiate signIn, Init function must be called prior to this one.", nil);
        } else {
            signInData.resolver = resolve;
            signInData.rejecter = reject;
            [signInData.sdkInstance signInClientId: clientId
                                             scope: scope
                                            market: market
                                            locale: locale
                                    tokenizationId: tokenizationId
                               presentationContext: self];
        }
    } else {
        [self rejectWithInvalidiOSVersionSupported: reject];
    }
}

- (void)rejectWithInvalidiOSVersionSupported:(RCTPromiseRejectBlock) rejectBlock {
    NSString *msg = @"KlarnaSignIn is supported from iOS version 13.0";
    rejectBlock(@"RNKlarnaSignIn", msg, nil);
}

// MARK: - KlarnaEventHandler Methods

- (void)klarnaComponent:(id <KlarnaComponent> _Nonnull)klarnaComponent dispatchedEvent:(KlarnaProductEvent * _Nonnull)event {
    KlarnaSignInData *signInData = [self getInstanceFromComponent: klarnaComponent];
    if (signInData == nil) {
        RCTLog(@"No instance found for the given component");
        return;
    }
    if (signInData.resolver == nil) {
        RCTLog(@"Missing 'resolver' callback prop.");
        return;
    }

    if ([event.action isEqual: @"klarnaSignInUserCancelled"]) {
        NSString *errorMsg = @"User canceled the sign-in process.";
        NSError * error = [NSError errorWithDomain:@"com.klarnamobilesdk" code: 0001 userInfo: @{
            @"name": [KlarnaSignInEventsMapper mapSignInEventName: event.action],
                @"message": errorMsg,
                @"isFatal": @"false",
                @"sessionId": event.sessionId
        }];

        signInData.rejecter(@"", errorMsg, error);
        return;
    }
    
    NSString *params = [SerializationUtil serializeDictionaryToJsonString: [event getParams]];
    NSDictionary *resolvedEvent = @{
        @"productEvent": @{
            @"action": event.action,
            @"sessionId": event.sessionId,
            @"params": params
        }
    };
    signInData.resolver(resolvedEvent);
}

- (void)klarnaComponent:(id <KlarnaComponent> _Nonnull)klarnaComponent encounteredError:(KlarnaError * _Nonnull)error {
    if (!error.isFatal) {
        NSArray *allowedNonFatalErrors = @[@"KlarnaSignInAlreadyInProgress", @"KlarnaSignInAuthorizationFailed"];
        if (![allowedNonFatalErrors containsObject: error.name]) {
            return;
        }
    }
    KlarnaSignInData *signInData = [self getInstanceFromComponent: klarnaComponent];
    if (signInData == nil) {
        RCTLog(@"No instance found for the given component");
    } else {
        if (signInData.rejecter == nil) {
            RCTLog(@"Missing 'rejecter' callback prop.");
        } else {
            NSString *params = [SerializationUtil serializeDictionaryToJsonString: [error getParams]];
            NSError *errorEvent = [NSError errorWithDomain:@"com.klarnamobilesdk" code: 0001 userInfo: @{
                @"name": [KlarnaSignInEventsMapper mapSignInErrorName: error.name],
                @"message": error.message,
                @"isFatal": error.isFatal ? @"true" : @"false",
                @"sessionId": error.sessionId,
                @"params": params
            }];
            signInData.rejecter(@"", error.message, errorEvent);
        }
    }
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

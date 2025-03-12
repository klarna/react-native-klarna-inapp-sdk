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
#import "KlarnaSignInData.h"

@interface KlarnaSignInModuleImp()<KlarnaEventHandler, ASWebAuthenticationPresentationContextProviding>

@property (nonatomic, strong) NSMutableArray *signInSDKs;

- (KlarnaEnvironment *)environmentFrom: (NSString *)value;
- (KlarnaRegion *)regionFrom: (NSString *)value;
- (void)rejectWithInvalidiOSVersionSupported:(RCTPromiseRejectBlock) rejectBlock;
- (KlarnaSignInData *)getInstanceFromId: (NSString *)instanceId;
- (KlarnaSignInData *)getInstanceFromComponent: (id <KlarnaComponent> _Nonnull)component;

@end

@implementation KlarnaSignInModuleImp

- (id)init {
    self = [super init];
    _signInSDKs = [NSMutableArray new];
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

- (KlarnaSignInData *)getInstanceFromId: (NSString *)instanceId {
    NSPredicate *predicate = [NSPredicate predicateWithFormat: @"instanceID == %@", instanceId];
    KlarnaSignInData *signInData = [_signInSDKs filteredArrayUsingPredicate: predicate].firstObject;
    return signInData;
}

- (KlarnaSignInData *)getInstanceFromComponent:(id<KlarnaComponent>)component {
    NSPredicate *predicate = [NSPredicate predicateWithFormat: @"sdkInstance == %@", component];
    KlarnaSignInData *signInData = [_signInSDKs filteredArrayUsingPredicate: predicate].firstObject;
    return signInData;
}

- (void)initWith:(NSString *)instanceId environment: (NSString *)environment region: (NSString *)region  returnUrl: (NSString *) returnUrl {
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
            [self.signInSDKs addObject: data];
        }
    } else {
        RCTLog(@"Invalid returnUrl provided");
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
        KlarnaSignInData *signInData = [self getInstanceFromId: instanceId];
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
    NSError *error = [NSError errorWithDomain:@"com.klarnamobilesdk"
                                         code:9999
                                     userInfo:@{ @"error": @{ @"message": msg }
                                              }];
    rejectBlock(@"RNKlarnaSignIn", msg, error);
}

// MARK: - KlarnaEventHandler Methods

- (void)klarnaComponent:(id <KlarnaComponent> _Nonnull)klarnaComponent dispatchedEvent:(KlarnaProductEvent * _Nonnull)event {
    KlarnaSignInData *signInData = [self getInstanceFromComponent: klarnaComponent];
    if (signInData == nil) {
        RCTLog(@"No instance found for the given component");
    } else {
        if (signInData.resolver == nil) {
            RCTLog(@"Missing 'resolver' callback prop.");
        } else {
            NSDictionary *resolvedEvent = @{
                @"productEvent": @{
                    @"action": event.action,
                    @"params": [event getParams],
                }
            };
            signInData.resolver(resolvedEvent);
            [_signInSDKs removeObject: signInData];
        }
    }
}

- (void)klarnaComponent:(id <KlarnaComponent> _Nonnull)klarnaComponent encounteredError:(KlarnaError * _Nonnull)error {
    // Ignore non fatal errors
    if (!error.isFatal) {
        return;
    }
    KlarnaSignInData *signInData = [self getInstanceFromComponent: klarnaComponent];
    if (signInData == nil) {
        RCTLog(@"No instance found for the given component");
    } else {
        if (signInData.resolver == nil) {
            RCTLog(@"Missing 'rejecter' callback prop.");
        } else {
            NSError * errorEvent = [NSError errorWithDomain:@"com.klarnamobilesdk" code: 0001 userInfo: @{
                @"error": @{
                    @"name": error.name,
                    @"message": error.message,
                    @"isFatal": [NSNumber numberWithBool: error.isFatal],
                }
            }];
            signInData.rejecter(@"", error.message, errorEvent);
            [_signInSDKs removeObject: signInData];
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

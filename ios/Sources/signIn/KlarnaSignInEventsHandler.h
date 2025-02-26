//
//  KlarnaSignInEventsHandler.h
//  react-native-klarna-inapp-sdk
//
//  Created by Jorge Palacio on 2025-02-26.
//

#import <Foundation/Foundation.h>
#import <AuthenticationServices/AuthenticationServices.h>
#import <KlarnaMobileSDK/KlarnaMobileSDK-Swift.h>
#import <React/RCTBridgeModule.h>

NS_ASSUME_NONNULL_BEGIN

@interface KlarnaSignInEventsHandler: NSObject<KlarnaEventHandler, ASWebAuthenticationPresentationContextProviding>

+(KlarnaEnvironment *)environmentFrom: (NSString *)value;
+(KlarnaRegion *)regionFrom: (NSString *)value;

@property (copy, nonatomic) RCTPromiseResolveBlock resolver;
@property (copy, nonatomic) RCTPromiseRejectBlock rejecter;

-(void)rejectWithInvalidiOSVersionSupported;

@end

NS_ASSUME_NONNULL_END

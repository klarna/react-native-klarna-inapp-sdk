//
//  KlarnaSignInEventsHandler.h
//  react-native-klarna-inapp-sdk
//
//  Created by Jorge Palacio on 2025-02-26.
//

#import <Foundation/Foundation.h>
#import <React/RCTBridgeModule.h>

NS_ASSUME_NONNULL_BEGIN

@interface KlarnaSignInModuleImp: NSObject

- (void)initWith: (NSString *)environment region: (NSString *)region  returnUrl: (NSString *) returnUrl;
- (void)signInWith:(NSString *)clientId
             scope:(NSString *)scope
            market:(NSString *)market
            locale:(NSString *)locale
    tokenizationId:(NSString *)tokenizationId
          resolver:(RCTPromiseResolveBlock)resolve
          rejecter:(RCTPromiseRejectBlock)reject;

@end

NS_ASSUME_NONNULL_END

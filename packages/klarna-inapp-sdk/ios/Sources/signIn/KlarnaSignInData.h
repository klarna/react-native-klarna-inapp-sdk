//
//  KlarnaSignInData.h
//  react-native-klarna-inapp-sdk
//
//  Created by Jorge Palacio on 2025-03-12.
//

#import <Foundation/Foundation.h>
#import <KlarnaMobileSDK/KlarnaMobileSDK.h>
#import "KlarnaMobileSDK/KlarnaMobileSDK-Swift.h"
#import <React/RCTBridgeModule.h>

NS_ASSUME_NONNULL_BEGIN

@interface KlarnaSignInData : NSObject

@property (nonatomic, strong) NSString *instanceID;
@property (nonatomic, strong) KlarnaSignInSDK *sdkInstance API_AVAILABLE(ios(13.0));
@property (nonatomic, copy) RCTPromiseResolveBlock resolver;
@property (nonatomic, copy) RCTPromiseRejectBlock rejecter;

-(instancetype)initWith:(NSString *)instanceID sdkInstance:(KlarnaSignInSDK *)sdkInstance;

@end

NS_ASSUME_NONNULL_END

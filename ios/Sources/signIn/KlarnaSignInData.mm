//
//  KlarnaSignInData.m
//  react-native-klarna-inapp-sdk
//
//  Created by Jorge Palacio on 2025-03-12.
//

#import "KlarnaSignInData.h"
#import <KlarnaMobileSDK/KlarnaMobileSDK.h>
#import <KlarnaMobileSDK/KlarnaMobileSDK-Swift.h>

@implementation KlarnaSignInData

-(instancetype)initWith:(NSString *)instanceID sdkInstance:(KlarnaSignInSDK *)sdkInstance {
    self = [super init];
    if (self) {
        _instanceID = instanceID;
        _sdkInstance = sdkInstance;
    }
    return self;
}

@end

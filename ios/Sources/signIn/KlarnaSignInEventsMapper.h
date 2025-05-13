//
//  KlarnaSignInErrorsMapper.h
//  react-native-klarna-inapp-sdk
//
//  Created by Jorge Palacio on 2025-05-13.
//

#import <Foundation/Foundation.h>

NS_ASSUME_NONNULL_BEGIN

@interface KlarnaSignInEventsMapper : NSObject
+(NSString *)mapSignInErrorName:(NSString *)errorName;
+(NSString *)mapSignInEventName:(NSString *)eventName;
@end

NS_ASSUME_NONNULL_END

#import <AVFoundation/AVFoundation.h>
#import <UIKit/UIKit.h>

@interface SerializationUtil : NSObject
+ (NSString *)serializeDictionaryToJsonString:(NSDictionary<NSString *, id<NSCoding>> *)dictionary;
@end

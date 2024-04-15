#import "RNMobileSDKUtils.h"

@implementation SerializationUtil

+ (id)serializeDictionaryToJsonString:(id)dictionary {
    NSError *error;
        NSData *jsonData = [NSJSONSerialization dataWithJSONObject:dictionary options:NSJSONWritingPrettyPrinted error:&error];

        if (!jsonData) {
            return @"{}";
        } else {
            NSString *jsonString = [[NSString alloc] initWithData:jsonData encoding:NSUTF8StringEncoding];
            return jsonString;
        }
}

@end

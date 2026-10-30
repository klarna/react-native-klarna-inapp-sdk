#import "RNMobileSDKUtils.h"

@implementation SerializationUtil

+ (id)serializeDictionaryToJsonString:(id)dictionary {
    if (!dictionary) {
        NSLog(@"Dictionary is nil");
        return @"{}";
    }
     
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

@implementation RNMSDKNavigationErrorUtil

+ (NSString *)urlStringForWebViewURL:(NSURL *)url error:(NSError *)error {
    id failingURL = error.userInfo[NSURLErrorFailingURLErrorKey];
    if ([failingURL isKindOfClass:[NSURL class]] && [failingURL absoluteString].length > 0) {
        return [failingURL absoluteString];
    }
    id failingURLString = error.userInfo[NSURLErrorFailingURLStringErrorKey];
    if ([failingURLString isKindOfClass:[NSString class]] && [failingURLString length] > 0) {
        return failingURLString;
    }
    return url.absoluteString ?: @"";
}

@end

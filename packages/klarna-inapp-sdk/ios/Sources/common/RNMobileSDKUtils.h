#import <AVFoundation/AVFoundation.h>
#import <UIKit/UIKit.h>

#ifdef __cplusplus
#include <string>
#endif

NS_ASSUME_NONNULL_BEGIN

@interface SerializationUtil : NSObject
+ (NSString *)serializeDictionaryToJsonString:(nullable NSDictionary<NSString *, id<NSCoding>> *)dictionary;
@end

@interface RNMSDKNavigationErrorUtil : NSObject
// Prefers the URL that failed, which the error carries, like Android does.
// Falls back to the web view's URL, then to an empty string.
+ (NSString *)urlStringForWebViewURL:(nullable NSURL *)url error:(NSError *)error;
@end

#ifdef __cplusplus
inline std::string RNMSDKStdString(id _Nullable string) {
    if (![string isKindOfClass:[NSString class]]) {
        return std::string();
    }
    return std::string([(NSString *)string UTF8String] ?: "");
}
#endif

NS_ASSUME_NONNULL_END

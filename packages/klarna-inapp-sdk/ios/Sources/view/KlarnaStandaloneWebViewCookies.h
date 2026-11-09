#import <Foundation/Foundation.h>
#import <WebKit/WebKit.h>

NS_ASSUME_NONNULL_BEGIN

/// The part of WKHTTPCookieStore used to store cookies, so tests can pass a fake.
@protocol KlarnaStandaloneWebViewCookieStoring <NSObject>
- (void)setCookie:(NSHTTPCookie *)cookie completionHandler:(nullable void (^)(void))completionHandler;
- (void)getAllCookies:(void (^)(NSArray<NSHTTPCookie *> *cookies))completionHandler;
- (void)deleteCookie:(NSHTTPCookie *)cookie completionHandler:(nullable void (^)(void))completionHandler;
@end

API_AVAILABLE(ios(11.0))
@interface WKHTTPCookieStore (KlarnaStandaloneWebViewCookieStoring) <KlarnaStandaloneWebViewCookieStoring>
@end

/// Cookies for a KlarnaStandaloneWebView, parsed from the JSON built by the JS wrapper.
@interface KlarnaStandaloneWebViewCookies : NSObject

/// Returns nil and sets `error` when the JSON is not a valid list of cookies.
+ (nullable NSArray<NSHTTPCookie *> *)cookiesFromJsonString:(NSString *)json error:(NSString * _Nullable * _Nullable)error;

/// Like `cookiesFromJsonString:error:`, and also returns the cookies marked `replaceConflicting` through `replacing`.
+ (nullable NSArray<NSHTTPCookie *> *)cookiesFromJsonString:(NSString *)json replacing:(NSArray<NSHTTPCookie *> * _Nullable * _Nullable)replacing error:(NSString * _Nullable * _Nullable)error;

/// The cookies of the JSON that are marked `replaceConflicting`; empty when the JSON is invalid.
+ (NSArray<NSHTTPCookie *> *)replacingCookiesFromJsonString:(NSString *)json;

/// Deletes the cookies in the store with the name of a `replacing` cookie that would be sent to its host, then stores `cookies`.
/// Calls `completion` on the main queue.
+ (void)setCookies:(NSArray<NSHTTPCookie *> *)cookies replacing:(NSArray<NSHTTPCookie *> *)replacing inStore:(id<KlarnaStandaloneWebViewCookieStoring>)store completion:(void (^)(void))completion;

/// Returns `cookies` without those that have the name of a `replacing` cookie and would be sent to its host, at any path.
+ (NSArray<NSHTTPCookie *> *)cookies:(NSArray<NSHTTPCookie *> *)cookies notConflictingWith:(NSArray<NSHTTPCookie *> *)replacing;

/// Stores the cookies and calls `completion` on the main queue once all of them are stored.
+ (void)setCookies:(NSArray<NSHTTPCookie *> *)cookies inStore:(id<KlarnaStandaloneWebViewCookieStoring>)store completion:(void (^)(void))completion;

/// Returns `cookies` without those that a cookie in `replacements` would overwrite (same name, domain and path).
+ (NSArray<NSHTTPCookie *> *)cookies:(NSArray<NSHTTPCookie *> *)cookies notReplacedBy:(NSArray<NSHTTPCookie *> *)replacements;

@end

NS_ASSUME_NONNULL_END

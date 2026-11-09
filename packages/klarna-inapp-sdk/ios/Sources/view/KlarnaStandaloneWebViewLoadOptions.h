#import <Foundation/Foundation.h>

NS_ASSUME_NONNULL_BEGIN

/// WebKit error code reported when a navigation is interrupted by a frame load (WebKitErrorFrameLoadInterruptedByPolicyChange).
static const NSInteger KlarnaWebKitErrorFrameLoadInterrupted = 102;

/// Headers sent with a KlarnaStandaloneWebView load, parsed from the JSON built by the JS wrapper.
@interface KlarnaStandaloneWebViewLoadOptions : NSObject

@property (nonatomic, copy, readonly) NSDictionary<NSString *, NSString *> *headers;

/// Empty or invalid JSON gives options without headers.
+ (instancetype)optionsWithJsonString:(nullable NSString *)json;

- (NSURLRequest *)requestWithURL:(NSURL *)url;

/// Whether the request still carries these headers but goes to another origin than the loaded URL.
- (BOOL)isCarriedByRequest:(NSURLRequest *)request toAnotherOriginThan:(NSURL *)url;

/// A copy of the request without these headers, keeping its method and body.
- (NSURLRequest *)requestByRemovingHeadersFromRequest:(NSURLRequest *)request;

@end

NS_ASSUME_NONNULL_END

#import "KlarnaStandaloneWebViewLoadOptions.h"

#import <React/RCTLog.h>

@implementation KlarnaStandaloneWebViewLoadOptions

- (instancetype)initWithHeaders:(NSDictionary<NSString *, NSString *> *)headers {
    self = [super init];
    if (self) {
        _headers = [headers copy];
    }
    return self;
}

+ (instancetype)optionsWithJsonString:(nullable NSString *)json {
    KlarnaStandaloneWebViewLoadOptions *empty = [[self alloc] initWithHeaders:@{}];
    if (json.length == 0) {
        return empty;
    }
    id root = [NSJSONSerialization JSONObjectWithData:[json dataUsingEncoding:NSUTF8StringEncoding] options:0 error:nil];
    NSDictionary<NSString *, NSString *> *headers = [root isKindOfClass:NSDictionary.class] ? [self headersFromJson:root[@"headers"]] : nil;
    if (headers == nil) {
        RCTLogError(@"Ignoring invalid load options");
        return empty;
    }
    return [[self alloc] initWithHeaders:headers];
}

+ (nullable NSDictionary<NSString *, NSString *> *)headersFromJson:(nullable id)json {
    if (json == nil) {
        return @{};
    }
    if (![json isKindOfClass:NSDictionary.class]) {
        return nil;
    }
    for (id name in json) {
        if (![name isKindOfClass:NSString.class] || ![json[name] isKindOfClass:NSString.class]) {
            return nil;
        }
    }
    return json;
}

- (NSURLRequest *)requestWithURL:(NSURL *)url {
    NSMutableURLRequest *request = [NSMutableURLRequest requestWithURL:url];
    [self.headers enumerateKeysAndObjectsUsingBlock:^(NSString *name, NSString *value, BOOL *stop) {
        [request setValue:value forHTTPHeaderField:name];
    }];
    return request;
}

+ (NSInteger)effectivePortOfURL:(NSURL *)url {
    if (url.port != nil) {
        return url.port.integerValue;
    }
    return [url.scheme caseInsensitiveCompare:@"https"] == NSOrderedSame ? 443 : 80;
}

+ (BOOL)isURL:(NSURL *)first sameOriginAs:(NSURL *)second {
    return [first.scheme ?: @"" caseInsensitiveCompare:second.scheme ?: @""] == NSOrderedSame
        && [first.host ?: @"" caseInsensitiveCompare:second.host ?: @""] == NSOrderedSame
        && [self effectivePortOfURL:first] == [self effectivePortOfURL:second];
}

- (BOOL)isCarriedByRequest:(NSURLRequest *)request toAnotherOriginThan:(NSURL *)url {
    if (self.headers.count == 0 || [self.class isURL:request.URL sameOriginAs:url]) {
        return NO;
    }
    for (NSString *name in self.headers) {
        if ([[request valueForHTTPHeaderField:name] isEqualToString:self.headers[name]]) {
            return YES;
        }
    }
    return NO;
}

- (NSURLRequest *)requestByRemovingHeadersFromRequest:(NSURLRequest *)request {
    NSMutableURLRequest *copy = [request mutableCopy];
    for (NSString *name in self.headers) {
        if ([[copy valueForHTTPHeaderField:name] isEqualToString:self.headers[name]]) {
            [copy setValue:nil forHTTPHeaderField:name];
        }
    }
    return copy;
}

@end

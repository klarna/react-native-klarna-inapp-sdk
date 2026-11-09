#import "KlarnaStandaloneWebViewCookies.h"

@implementation WKHTTPCookieStore (KlarnaStandaloneWebViewCookieStoring)
@end

@implementation KlarnaStandaloneWebViewCookies

+ (nullable NSArray<NSHTTPCookie *> *)cookiesFromJsonString:(NSString *)json error:(NSString * _Nullable * _Nullable)error {
    return [self cookiesFromJsonString:json replacing:nil error:error];
}

+ (nullable NSArray<NSHTTPCookie *> *)cookiesFromJsonString:(NSString *)json replacing:(NSArray<NSHTTPCookie *> * _Nullable * _Nullable)replacing error:(NSString * _Nullable * _Nullable)error {
    id root = [NSJSONSerialization JSONObjectWithData:[json dataUsingEncoding:NSUTF8StringEncoding] options:0 error:nil];
    if (![root isKindOfClass:NSArray.class]) {
        if (error) { *error = @"Invalid cookies."; }
        return nil;
    }
    NSMutableArray<NSHTTPCookie *> *cookies = [NSMutableArray new];
    NSMutableArray<NSHTTPCookie *> *replacingCookies = [NSMutableArray new];
    for (id item in root) {
        NSHTTPCookie *cookie = [item isKindOfClass:NSDictionary.class] ? [self cookieFromJson:item] : nil;
        if (cookie == nil) {
            if (error) { *error = @"Invalid cookies."; }
            return nil;
        }
        [cookies addObject:cookie];
        id replaceConflicting = item[@"replaceConflicting"];
        if ([replaceConflicting isKindOfClass:NSNumber.class] && [replaceConflicting boolValue]) {
            [replacingCookies addObject:cookie];
        }
    }
    if (replacing) { *replacing = replacingCookies; }
    return cookies;
}

+ (NSArray<NSHTTPCookie *> *)replacingCookiesFromJsonString:(NSString *)json {
    NSArray<NSHTTPCookie *> *replacing = nil;
    [self cookiesFromJsonString:json replacing:&replacing error:nil];
    return replacing ?: @[];
}

+ (nullable NSHTTPCookie *)cookieFromJson:(NSDictionary *)json {
    NSString *name = json[@"name"];
    NSString *value = json[@"value"];
    NSString *domain = json[@"domain"];
    NSString *path = json[@"path"];
    if (![name isKindOfClass:NSString.class] || ![value isKindOfClass:NSString.class] ||
        ![domain isKindOfClass:NSString.class] || ![path isKindOfClass:NSString.class]) {
        return nil;
    }
    NSMutableDictionary<NSHTTPCookiePropertyKey, id> *properties = [@{
        NSHTTPCookieName: name,
        NSHTTPCookieValue: value,
        // The leading dot makes the cookie apply to subdomains as well; without it the cookie is host-only.
        NSHTTPCookieDomain: [json[@"hostOnly"] boolValue] ? domain : [@"." stringByAppendingString:domain],
        NSHTTPCookiePath: path,
    } mutableCopy];
    if ([json[@"secure"] boolValue]) {
        properties[NSHTTPCookieSecure] = @"TRUE";
    }
    if ([json[@"httpOnly"] boolValue]) {
        properties[@"HttpOnly"] = @"TRUE";
    }
    if ([json[@"expires"] isKindOfClass:NSNumber.class]) {
        properties[NSHTTPCookieExpires] = [NSDate dateWithTimeIntervalSince1970:[json[@"expires"] doubleValue] / 1000.0];
    }
    return [NSHTTPCookie cookieWithProperties:properties];
}

+ (void)setCookies:(NSArray<NSHTTPCookie *> *)cookies inStore:(id<KlarnaStandaloneWebViewCookieStoring>)store completion:(void (^)(void))completion {
    dispatch_group_t group = dispatch_group_create();
    for (NSHTTPCookie *cookie in cookies) {
        dispatch_group_enter(group);
        [store setCookie:cookie completionHandler:^{
            dispatch_group_leave(group);
        }];
    }
    dispatch_group_notify(group, dispatch_get_main_queue(), completion);
}

// The domain without a leading dot, in lower case.
+ (NSString *)bareDomainOfCookie:(NSHTTPCookie *)cookie {
    NSString *domain = cookie.domain.lowercaseString;
    return [domain hasPrefix:@"."] ? [domain substringFromIndex:1] : domain;
}

// Whether `cookie` has the name of `replacing` and would be sent to the host `replacing` was stored for.
// Only a cookie with domain scope (a leading dot) reaches subdomains; a host-only cookie needs the exact host.
+ (BOOL)cookie:(NSHTTPCookie *)cookie conflictsWith:(NSHTTPCookie *)replacing {
    if (![cookie.name isEqualToString:replacing.name]) {
        return NO;
    }
    NSString *host = [self bareDomainOfCookie:replacing];
    NSString *domain = [self bareDomainOfCookie:cookie];
    if ([host isEqualToString:domain]) {
        return YES;
    }
    return [cookie.domain hasPrefix:@"."] && [host hasSuffix:[@"." stringByAppendingString:domain]];
}

+ (BOOL)cookie:(NSHTTPCookie *)cookie conflictsWithAny:(NSArray<NSHTTPCookie *> *)replacing {
    for (NSHTTPCookie *candidate in replacing) {
        if ([self cookie:cookie conflictsWith:candidate]) {
            return YES;
        }
    }
    return NO;
}

+ (NSArray<NSHTTPCookie *> *)cookies:(NSArray<NSHTTPCookie *> *)cookies notConflictingWith:(NSArray<NSHTTPCookie *> *)replacing {
    NSPredicate *notConflicting = [NSPredicate predicateWithBlock:^BOOL(NSHTTPCookie *cookie, NSDictionary *bindings) {
        return ![self cookie:cookie conflictsWithAny:replacing];
    }];
    return [cookies filteredArrayUsingPredicate:notConflicting];
}

+ (void)setCookies:(NSArray<NSHTTPCookie *> *)cookies replacing:(NSArray<NSHTTPCookie *> *)replacing inStore:(id<KlarnaStandaloneWebViewCookieStoring>)store completion:(void (^)(void))completion {
    if (replacing.count == 0) {
        [self setCookies:cookies inStore:store completion:completion];
        return;
    }
    [store getAllCookies:^(NSArray<NSHTTPCookie *> *existing) {
        NSArray<NSHTTPCookie *> *stale = [existing filteredArrayUsingPredicate:[NSPredicate predicateWithBlock:^BOOL(NSHTTPCookie *cookie, NSDictionary *bindings) {
            return [self cookie:cookie conflictsWithAny:replacing];
        }]];
        dispatch_group_t group = dispatch_group_create();
        for (NSHTTPCookie *cookie in stale) {
            dispatch_group_enter(group);
            [store deleteCookie:cookie completionHandler:^{
                dispatch_group_leave(group);
            }];
        }
        // The new cookies are stored only after the old ones are gone, so a delete cannot remove a new cookie.
        dispatch_group_notify(group, dispatch_get_main_queue(), ^{
            [self setCookies:cookies inStore:store completion:completion];
        });
    }];
}

// The key a cookie store replaces cookies by; a leading dot on the domain does not make it a different cookie.
+ (NSString *)keyOfCookie:(NSHTTPCookie *)cookie {
    NSString *domain = cookie.domain.lowercaseString;
    if ([domain hasPrefix:@"."]) {
        domain = [domain substringFromIndex:1];
    }
    return [NSString stringWithFormat:@"%@\n%@\n%@", cookie.name, domain, cookie.path ?: @"/"];
}

+ (NSArray<NSHTTPCookie *> *)cookies:(NSArray<NSHTTPCookie *> *)cookies notReplacedBy:(NSArray<NSHTTPCookie *> *)replacements {
    NSMutableSet<NSString *> *replacedKeys = [NSMutableSet new];
    for (NSHTTPCookie *cookie in replacements) {
        [replacedKeys addObject:[self keyOfCookie:cookie]];
    }
    NSPredicate *notReplaced = [NSPredicate predicateWithBlock:^BOOL(NSHTTPCookie *cookie, NSDictionary *bindings) {
        return ![replacedKeys containsObject:[self keyOfCookie:cookie]];
    }];
    return [cookies filteredArrayUsingPredicate:notReplaced];
}

@end

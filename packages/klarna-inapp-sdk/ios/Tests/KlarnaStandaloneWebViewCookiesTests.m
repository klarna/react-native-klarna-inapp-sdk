#import <XCTest/XCTest.h>

#import "../Sources/view/KlarnaStandaloneWebViewCookies.h"

@interface FakeCookieStore : NSObject <KlarnaStandaloneWebViewCookieStoring>
@property (nonatomic, strong, readonly) NSMutableArray<NSHTTPCookie *> *cookies;
@end

@implementation FakeCookieStore {
    dispatch_queue_t _queue;
}

- (instancetype)init {
    self = [super init];
    if (self) {
        _cookies = [NSMutableArray new];
        _queue = dispatch_queue_create("FakeCookieStore", DISPATCH_QUEUE_SERIAL);
    }
    return self;
}

- (void)setCookie:(NSHTTPCookie *)cookie completionHandler:(nullable void (^)(void))completionHandler {
    dispatch_async(_queue, ^{
        [self.cookies addObject:cookie];
        if (completionHandler) { completionHandler(); }
    });
}

- (void)getAllCookies:(void (^)(NSArray<NSHTTPCookie *> *))completionHandler {
    dispatch_async(_queue, ^{
        completionHandler([self.cookies copy]);
    });
}

- (void)deleteCookie:(NSHTTPCookie *)cookie completionHandler:(nullable void (^)(void))completionHandler {
    dispatch_async(_queue, ^{
        [self.cookies removeObject:cookie];
        if (completionHandler) { completionHandler(); }
    });
}

@end

@interface KlarnaStandaloneWebViewCookiesTests : XCTestCase
@end

@implementation KlarnaStandaloneWebViewCookiesTests

- (void)test_parsesCookie {
    NSString *error = nil;
    NSArray<NSHTTPCookie *> *cookies = [KlarnaStandaloneWebViewCookies
        cookiesFromJsonString:@"[{\"name\":\"session\",\"value\":\"abc\",\"domain\":\"www.example.com\",\"path\":\"/\",\"secure\":true,\"httpOnly\":true}]"
        error:&error];

    XCTAssertNil(error);
    XCTAssertEqual(cookies.count, 1);
    XCTAssertEqualObjects(cookies[0].name, @"session");
    XCTAssertEqualObjects(cookies[0].value, @"abc");
    XCTAssertEqualObjects(cookies[0].domain, @".www.example.com");
    XCTAssertEqualObjects(cookies[0].path, @"/");
    XCTAssertTrue(cookies[0].isSecure);
    XCTAssertTrue(cookies[0].isHTTPOnly);
    XCTAssertTrue(cookies[0].isSessionOnly);
}

- (void)test_parsesHostOnlyCookieWithoutSubdomainScope {
    NSArray<NSHTTPCookie *> *cookies = [KlarnaStandaloneWebViewCookies
        cookiesFromJsonString:@"[{\"name\":\"__Host-session\",\"value\":\"abc\",\"domain\":\"checkout.example.com\",\"path\":\"/\",\"secure\":true,\"hostOnly\":true}]"
        error:nil];

    XCTAssertEqualObjects(cookies[0].domain, @"checkout.example.com");
    XCTAssertTrue(cookies[0].isSecure);
}

- (void)test_parsesExpiryInMilliseconds {
    NSTimeInterval expires = ([NSDate date].timeIntervalSince1970 + 3600) * 1000;
    NSString *json = [NSString stringWithFormat:@"[{\"name\":\"a\",\"value\":\"b\",\"domain\":\"example.com\",\"path\":\"/\",\"expires\":%.0f}]", expires];

    NSArray<NSHTTPCookie *> *cookies = [KlarnaStandaloneWebViewCookies cookiesFromJsonString:json error:nil];

    XCTAssertFalse(cookies[0].isSessionOnly);
    XCTAssertEqualWithAccuracy(cookies[0].expiresDate.timeIntervalSince1970, expires / 1000.0, 1.0);
}

- (void)test_emptyListGivesNoCookies {
    NSArray<NSHTTPCookie *> *cookies = [KlarnaStandaloneWebViewCookies cookiesFromJsonString:@"[]" error:nil];

    XCTAssertEqual(cookies.count, 0);
}

- (void)test_rejectsInvalidJson {
    NSString *error = nil;

    XCTAssertNil([KlarnaStandaloneWebViewCookies cookiesFromJsonString:@"{not json" error:&error]);
    XCTAssertNotNil(error);
}

- (void)test_rejectsCookieMissingDomain {
    NSString *error = nil;

    XCTAssertNil([KlarnaStandaloneWebViewCookies
        cookiesFromJsonString:@"[{\"name\":\"a\",\"value\":\"b\",\"path\":\"/\"}]" error:&error]);
    XCTAssertNotNil(error);
}

- (void)test_rejectsWholeListWhenOneCookieIsInvalid {
    XCTAssertNil([KlarnaStandaloneWebViewCookies
        cookiesFromJsonString:@"[{\"name\":\"a\",\"value\":\"b\",\"domain\":\"example.com\",\"path\":\"/\"},{\"name\":\"c\"}]"
        error:nil]);
}

- (void)test_storesCookiesInTheStore {
    FakeCookieStore *store = [FakeCookieStore new];
    NSArray<NSHTTPCookie *> *cookies = [KlarnaStandaloneWebViewCookies
        cookiesFromJsonString:@"[{\"name\":\"a\",\"value\":\"1\",\"domain\":\"example.com\",\"path\":\"/\"},{\"name\":\"b\",\"value\":\"2\",\"domain\":\"example.com\",\"path\":\"/\"}]"
        error:nil];
    XCTestExpectation *stored = [self expectationWithDescription:@"stored"];

    [KlarnaStandaloneWebViewCookies setCookies:cookies inStore:store completion:^{
        XCTAssertTrue(NSThread.isMainThread);
        XCTAssertEqualObjects([store.cookies valueForKey:@"name"], (@[@"a", @"b"]));
        [stored fulfill];
    }];

    [self waitForExpectations:@[stored] timeout:5];
}

- (void)test_dropsCookiesThatAReplacementWouldOverwrite {
    NSArray<NSHTTPCookie *> *shared = [KlarnaStandaloneWebViewCookies
        cookiesFromJsonString:@"[{\"name\":\"session\",\"value\":\"stale\",\"domain\":\"www.example.com\",\"path\":\"/\"},{\"name\":\"other\",\"value\":\"1\",\"domain\":\"www.example.com\",\"path\":\"/\"},{\"name\":\"session\",\"value\":\"2\",\"domain\":\"www.example.com\",\"path\":\"/cart\"}]"
        error:nil];
    NSHTTPCookie *noDot = [NSHTTPCookie cookieWithProperties:@{
        NSHTTPCookieName: @"session", NSHTTPCookieValue: @"fresh", NSHTTPCookieDomain: @"WWW.example.com", NSHTTPCookiePath: @"/"
    }];

    NSArray<NSHTTPCookie *> *kept = [KlarnaStandaloneWebViewCookies cookies:shared notReplacedBy:@[noDot]];

    XCTAssertEqual(kept.count, 2);
    XCTAssertEqualObjects(kept[0].name, @"other");
    XCTAssertEqualObjects(kept[1].path, @"/cart");
}

- (void)test_replacingCookiesAreOnlyTheOnesMarkedReplaceConflicting {
    NSArray<NSHTTPCookie *> *replacing = [KlarnaStandaloneWebViewCookies
        replacingCookiesFromJsonString:@"[{\"name\":\"session\",\"value\":\"1\",\"domain\":\"www.example.com\",\"path\":\"/\",\"replaceConflicting\":true,\"requestPath\":\"/checkout/\"},{\"name\":\"other\",\"value\":\"2\",\"domain\":\"www.example.com\",\"path\":\"/\"}]"];

    XCTAssertEqual(replacing.count, 1);
    XCTAssertEqualObjects(replacing[0].name, @"session");
}

- (void)test_replacingCookiesOfInvalidJsonAreEmpty {
    XCTAssertEqual([KlarnaStandaloneWebViewCookies replacingCookiesFromJsonString:@"{not json"].count, 0);
}

- (NSHTTPCookie *)cookieNamed:(NSString *)name value:(NSString *)value domain:(NSString *)domain path:(NSString *)path {
    return [NSHTTPCookie cookieWithProperties:@{
        NSHTTPCookieName: name, NSHTTPCookieValue: value, NSHTTPCookieDomain: domain, NSHTTPCookiePath: path
    }];
}

- (void)test_replacingDeletesSameNameCookiesForTheHostAtAnyPathThenStoresTheNewOne {
    FakeCookieStore *store = [FakeCookieStore new];
    [store.cookies addObjectsFromArray:@[
        [self cookieNamed:@"session" value:@"old" domain:@"www.example.com" path:@"/checkout"],
        [self cookieNamed:@"session" value:@"parent" domain:@".example.com" path:@"/"],
        [self cookieNamed:@"session" value:@"hostOnlyParent" domain:@"example.com" path:@"/"],
        [self cookieNamed:@"session" value:@"sibling" domain:@"api.example.com" path:@"/"],
        [self cookieNamed:@"session" value:@"elsewhere" domain:@"other.com" path:@"/"],
        [self cookieNamed:@"keep" value:@"1" domain:@"www.example.com" path:@"/"],
    ]];
    NSHTTPCookie *fresh = [self cookieNamed:@"session" value:@"new" domain:@"www.example.com" path:@"/"];
    XCTestExpectation *stored = [self expectationWithDescription:@"stored"];

    [KlarnaStandaloneWebViewCookies setCookies:@[fresh] replacing:@[fresh] inStore:store completion:^{
        XCTAssertTrue(NSThread.isMainThread);
        NSArray<NSString *> *values = [store.cookies valueForKey:@"value"];
        XCTAssertEqualObjects(values, (@[@"hostOnlyParent", @"sibling", @"elsewhere", @"1", @"new"]));
        [stored fulfill];
    }];

    [self waitForExpectations:@[stored] timeout:5];
}

- (void)test_withoutReplacingExistingCookiesAreLeftAlone {
    FakeCookieStore *store = [FakeCookieStore new];
    [store.cookies addObject:[self cookieNamed:@"session" value:@"old" domain:@"www.example.com" path:@"/checkout"]];
    NSHTTPCookie *fresh = [self cookieNamed:@"session" value:@"new" domain:@"www.example.com" path:@"/"];
    XCTestExpectation *stored = [self expectationWithDescription:@"stored"];

    [KlarnaStandaloneWebViewCookies setCookies:@[fresh] replacing:@[] inStore:store completion:^{
        XCTAssertEqualObjects([store.cookies valueForKey:@"value"], (@[@"old", @"new"]));
        [stored fulfill];
    }];

    [self waitForExpectations:@[stored] timeout:5];
}

- (void)test_sharedCookiesConflictingWithAReplacingCookieAreDropped {
    NSHTTPCookie *fresh = [self cookieNamed:@"session" value:@"new" domain:@"www.example.com" path:@"/"];
    NSArray<NSHTTPCookie *> *shared = @[
        [self cookieNamed:@"session" value:@"stale" domain:@"www.example.com" path:@"/checkout"],
        [self cookieNamed:@"session" value:@"parent" domain:@".example.com" path:@"/"],
        [self cookieNamed:@"other" value:@"1" domain:@"www.example.com" path:@"/"],
    ];

    NSArray<NSHTTPCookie *> *kept = [KlarnaStandaloneWebViewCookies cookies:shared notConflictingWith:@[fresh]];

    XCTAssertEqual(kept.count, 1);
    XCTAssertEqualObjects(kept[0].name, @"other");
}

@end

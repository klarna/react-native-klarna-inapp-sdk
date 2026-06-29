#import <XCTest/XCTest.h>
#import "../../Sources/signIn/KlarnaSignInModuleImpl.h"

@interface KlarnaSignInModuleImplTests : XCTestCase
@property (nonatomic, strong) KlarnaSignInModuleImp *impl;
@end

@implementation KlarnaSignInModuleImplTests

- (void)setUp {
    self.impl = [KlarnaSignInModuleImp new];
}

- (void)tearDown {
    self.impl = nil;
}

#pragma mark - initWith: URL validation

- (void)test_initWith_urlWithNoScheme_callsReject {
    __block BOOL rejectCalled = NO;
    __block NSString *rejectedMessage = nil;

    [self.impl initWith:@"test-id"
           environment:@"production"
                region:@"eu"
             returnUrl:@"not-a-valid-url"
              resolver:^(id result) {}
              rejecter:^(NSString *code, NSString *message, NSError *error) {
                  rejectCalled = YES;
                  rejectedMessage = message;
              }];

    XCTAssertTrue(rejectCalled);
    XCTAssertEqualObjects(rejectedMessage, @"Invalid or null return URL.");
}

- (void)test_initWith_emptyUrl_callsReject {
    __block BOOL rejectCalled = NO;

    [self.impl initWith:@"test-id"
           environment:@"production"
                region:@"eu"
             returnUrl:@""
              resolver:^(id result) {}
              rejecter:^(NSString *code, NSString *message, NSError *error) {
                  rejectCalled = YES;
              }];

    XCTAssertTrue(rejectCalled);
}

- (void)test_initWith_invalidUrl_doesNotCallResolve {
    __block BOOL resolveCalled = NO;

    [self.impl initWith:@"test-id"
           environment:@"production"
                region:@"eu"
             returnUrl:@"not-a-valid-url"
              resolver:^(id result) { resolveCalled = YES; }
              rejecter:^(NSString *code, NSString *message, NSError *error) {}];

    XCTAssertFalse(resolveCalled);
}

#pragma mark - signInWith: instance lookup

- (void)test_signInWith_unknownInstanceId_callsReject {
    __block BOOL rejectCalled = NO;
    __block NSString *rejectedMessage = nil;

    [self.impl signInWith:@"unknown-id"
               clientId:@"client-123"
                  scope:@"openid"
                 market:@"SE"
                 locale:@"en-SE"
         tokenizationId:@"tok-123"
               resolver:^(id result) {}
               rejecter:^(NSString *code, NSString *message, NSError *error) {
                   rejectCalled = YES;
                   rejectedMessage = message;
               }];

    XCTAssertTrue(rejectCalled);
    XCTAssertTrue([rejectedMessage containsString:@"No instance found"]);
}

- (void)test_signInWith_unknownInstanceId_doesNotCallResolve {
    __block BOOL resolveCalled = NO;

    [self.impl signInWith:@"unknown-id"
               clientId:@"client-123"
                  scope:@"openid"
                 market:@"SE"
                 locale:@"en-SE"
         tokenizationId:@"tok-123"
               resolver:^(id result) { resolveCalled = YES; }
               rejecter:^(NSString *code, NSString *message, NSError *error) {}];

    XCTAssertFalse(resolveCalled);
}

@end

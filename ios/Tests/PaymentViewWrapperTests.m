//
//  Tests.m
//  Tests
//
//  Created by Nuri Güner on 2020-11-11.
//  Copyright © 2020 Facebook. All rights reserved.
//

#import <XCTest/XCTest.h>
#import <OCMock/OCMock.h>
#import <React/RCTComponent.h>
#import <KlarnaMobileSDK/KlarnaMobileSDK.h>
#import <KlarnaMobileSDK/KlarnaMobileSDK-Swift.h>
#import "../Sources/KlarnaPaymentViewManager.h"

#import "../Sources/view/PaymentViewWrapper.h"

@interface PaymentViewWrapper ()

// declaring private methods to be able to verify them with OCMock
- (void) evaluateProps;
- (void) initializeActualPaymentView;

@end

@interface PaymentViewWrapperTests : XCTestCase

@property (nonatomic, strong) PaymentViewWrapper* paymentViewWrapper;

@property (nonatomic, strong) KlarnaPaymentView* actualPaymentViewMock;

@end

@implementation PaymentViewWrapperTests

- (void)setUp {
    PaymentViewWrapper *wrapper = [PaymentViewWrapper new];
    self.paymentViewWrapper = OCMPartialMock(wrapper);

    id mockPaymentView = OCMClassMock([KlarnaPaymentView class]);
    OCMStub([mockPaymentView alloc]).andReturn(mockPaymentView);
    OCMStub([mockPaymentView initWithCategory:OCMOCK_ANY eventListener:OCMOCK_ANY])
        .andDo(^(NSInvocation *invocation) {
            [((id<KlarnaPaymentEventListener>)self.paymentViewWrapper) klarnaInitializedWithPaymentView:self.actualPaymentViewMock];
        })
        .andReturn(mockPaymentView);
    self.actualPaymentViewMock = mockPaymentView;
}

- (void)tearDown {
    self.paymentViewWrapper = NULL;
    self.actualPaymentViewMock = NULL;
}

- (void)test_categoryIsSet {
    // GIVEN
    OCMStub([self.paymentViewWrapper initializeActualPaymentView]);
    
    // WHEN
    [self.paymentViewWrapper setCategory: @"testCategory"];
    
    // THEN
    XCTAssertEqual(self.paymentViewWrapper.category, @"testCategory");
    OCMVerify([self.paymentViewWrapper evaluateProps]);
}

- (void)test_evaluatePropsWithoutCategory {
    // GIVEN
    OCMReject([self.paymentViewWrapper initializeActualPaymentView]);
    
    // WHEN
    [self.paymentViewWrapper evaluateProps];
    
    // THEN
}

- (void)test_evaluatePropsWithCategory {
    // GIVEN
    OCMStub([self.paymentViewWrapper initializeActualPaymentView]);
    self.paymentViewWrapper.category = @"test";
    
    // WHEN
    [self.paymentViewWrapper evaluateProps];
    
    // THEN
    OCMVerify([self.paymentViewWrapper initializeActualPaymentView]);
}

- (void)test_initializeActualPaymentView {
    // GIVEN
    OCMStub([((UIView *)self.paymentViewWrapper) addSubview:OCMOCK_ANY]);
    id mockConstraints = OCMClassMock([NSLayoutConstraint class]);
    OCMStub([mockConstraints activateConstraints:OCMOCK_ANY]);
    NSArray *mockPlaceholderArray = OCMClassMock([NSArray class]);
    OCMStub([mockPlaceholderArray initWithObjects:OCMArg.anyObjectRef count:OCMArg.anyPointer]);
    
    // WHEN
    [self.paymentViewWrapper initializeActualPaymentView];
    
    // THEN
    XCTAssertFalse(self.actualPaymentViewMock.translatesAutoresizingMaskIntoConstraints);
    OCMVerify([((UIView *)self.paymentViewWrapper) addSubview:self.actualPaymentViewMock]);
    OCMVerify([mockConstraints activateConstraints:OCMOCK_ANY]);
}

- (void)initializePaymentView {
    OCMStub([((UIView *)self.paymentViewWrapper) addSubview:OCMOCK_ANY]);
    id mockConstraints = OCMClassMock([NSLayoutConstraint class]);
    OCMStub([mockConstraints activateConstraints:OCMOCK_ANY]);
    NSArray *mockPlaceholderArray = OCMClassMock([NSArray class]);
    OCMStub([mockPlaceholderArray initWithObjects:OCMArg.anyObjectRef count:OCMArg.anyPointer]);

    [self.paymentViewWrapper initializeActualPaymentView];
}

- (void)test_initializePaymentViewWithClientToken {
    // GIVEN
    [self initializePaymentView];
    OCMExpect([self.actualPaymentViewMock initializeWithClientToken:@"token" returnUrl: [NSURL URLWithString:@"returnUrl"]]);

    // WHEN
    [self.paymentViewWrapper initializePaymentViewWithClientToken:@"token" withReturnUrl:@"returnUrl"];

    // THEN
    OCMVerifyAll(self.actualPaymentViewMock);
}

- (void)test_loadPaymentViewWithSessionData {
    // GIVEN
    [self initializePaymentView];
    OCMExpect([self.actualPaymentViewMock loadWithJsonData:@"sessionData"]);

    // WHEN
    [self.paymentViewWrapper loadPaymentViewWithSessionData:@"sessionData"];

    // THEN
    OCMVerifyAll(self.actualPaymentViewMock);
}

- (void)test_loadPaymentReview {
    // GIVEN
    [self initializePaymentView];
    OCMExpect([self.actualPaymentViewMock loadPaymentReview]);

    // WHEN
    [self.paymentViewWrapper loadPaymentReview];

    // THEN
    OCMVerifyAll(self.actualPaymentViewMock);
}

- (void)test_authorizePaymentViewWithAutoFinalize {
    // GIVEN
    [self initializePaymentView];
    OCMExpect([self.actualPaymentViewMock authorizeWithAutoFinalize:true jsonData:@"sessionData"]);

    // WHEN
    [self.paymentViewWrapper authorizePaymentViewWithAutoFinalize:true sessionData:@"sessionData"];

    // THEN
    OCMVerifyAll(self.actualPaymentViewMock);
}

- (void)test_reauthorizePaymentViewWithSessionData {
    // GIVEN
    [self initializePaymentView];
    OCMExpect([self.actualPaymentViewMock reauthorizeWithJsonData:@"sessionData"]);

    // WHEN
    [self.paymentViewWrapper reauthorizePaymentViewWithSessionData:@"sessionData"];

    // THEN
    OCMVerifyAll(self.actualPaymentViewMock);
}

- (void)test_finalizePaymentViewWithSessionDataa {
    // GIVEN
    [self initializePaymentView];
    OCMExpect([self.actualPaymentViewMock finaliseWithJsonData:@"sessionData"]);

    // WHEN
    [self.paymentViewWrapper finalizePaymentViewWithSessionData:@"sessionData"];

    // THEN
    OCMVerifyAll(self.actualPaymentViewMock);
}

- (void)test_onInitialized {
    // GIVEN
    [self.paymentViewWrapper setOnInitialized:^(NSDictionary *body) {
        
    }];
    
    // WHEN
    [self initializePaymentView];
    
    // THEN
    OCMVerify([self.paymentViewWrapper onInitialized]);
}

- (void)test_onLoaded {
    // GIVEN
    OCMStub([self.actualPaymentViewMock loadWithJsonData:OCMOCK_ANY])
        ._andDo(^(NSInvocation *invocation) {
            [((id<KlarnaPaymentEventListener>)self.paymentViewWrapper) klarnaLoadedWithPaymentView:self.actualPaymentViewMock];
        });
    [self.paymentViewWrapper setOnLoaded:^(NSDictionary *body) {
        
    }];
    
    // WHEN
    [self initializePaymentView];
    [self.paymentViewWrapper loadPaymentViewWithSessionData:NULL];
    
    // THEN
    OCMVerify([self.paymentViewWrapper onLoaded]);
}

- (void)test_onLoadedPaymentReview {
    // GIVEN
    OCMStub([self.actualPaymentViewMock loadPaymentReview])
        ._andDo(^(NSInvocation *invocation) {
            [((id<KlarnaPaymentEventListener>)self.paymentViewWrapper) klarnaLoadedPaymentReviewWithPaymentView:self.actualPaymentViewMock];
        });
    [self.paymentViewWrapper setOnLoadedPaymentReview:^(NSDictionary *body) {
        
    }];
    
    // WHEN
    [self initializePaymentView];
    [self.paymentViewWrapper loadPaymentReview];
    
    // THEN
    OCMVerify([self.paymentViewWrapper onLoadedPaymentReview]);
}

- (void)test_onAuthorized {
    // GIVEN
    OCMStub([self.actualPaymentViewMock authorizeWithAutoFinalize:false jsonData:NULL])
        ._andDo(^(NSInvocation *invocation) {
            [((id<KlarnaPaymentEventListener>)self.paymentViewWrapper) klarnaAuthorizedWithPaymentView:self.actualPaymentViewMock approved:true authToken:@"authToken" finalizeRequired:true];
        });
    [self.paymentViewWrapper setOnAuthorized:^(NSDictionary *body) {
        
    }];
    
    // WHEN
    [self initializePaymentView];
    [self.paymentViewWrapper authorizePaymentViewWithAutoFinalize:false sessionData:NULL];
    
    // THEN
    OCMVerify([self.paymentViewWrapper onAuthorized]);
}

- (void)test_onReauthorized {
    // GIVEN
    OCMStub([self.actualPaymentViewMock reauthorizeWithJsonData:OCMOCK_ANY])
        ._andDo(^(NSInvocation *invocation) {
            [((id<KlarnaPaymentEventListener>)self.paymentViewWrapper) klarnaReauthorizedWithPaymentView:self.actualPaymentViewMock approved:true authToken:@"authToken"];
        });
    [self.paymentViewWrapper setOnReauthorized:^(NSDictionary *body) {
        
    }];
    
    // WHEN
    [self initializePaymentView];
    [self.paymentViewWrapper reauthorizePaymentViewWithSessionData:NULL];
    
    // THEN
    OCMVerify([self.paymentViewWrapper onReauthorized]);
}

- (void)test_onFinalized {
    // GIVEN
    OCMStub([self.actualPaymentViewMock finaliseWithJsonData:OCMOCK_ANY])
        ._andDo(^(NSInvocation *invocation) {
            [((id<KlarnaPaymentEventListener>)self.paymentViewWrapper) klarnaFinalizedWithPaymentView:self.actualPaymentViewMock approved:true authToken:@"authToken"];
        });
    [self.paymentViewWrapper setOnFinalized:^(NSDictionary *body) {
        
    }];
    
    // WHEN
    [self initializePaymentView];
    [self.paymentViewWrapper finalizePaymentViewWithSessionData:NULL];
    
    // THEN
    OCMVerify([self.paymentViewWrapper onFinalized]);
}

- (void)test_onError {
    // GIVEN
    KlarnaPaymentError *mockError = OCMClassMock([KlarnaPaymentError class]);
    OCMStub([mockError action]).andReturn(@"action");
    OCMStub([mockError invalidFields]).andReturn(@[]);
    OCMStub([mockError isFatal]).andReturn(true);
    OCMStub([mockError message]).andReturn(@"errorMessage");
    OCMStub([mockError name]).andReturn(@"errorName");
    OCMStub([self.actualPaymentViewMock finaliseWithJsonData:OCMOCK_ANY])
        ._andDo(^(NSInvocation *invocation) {
            [((id<KlarnaPaymentEventListener>)self.paymentViewWrapper)
                    klarnaFailedInPaymentView:self.actualPaymentViewMock withError:mockError];
        });
    [self.paymentViewWrapper setOnError:^(NSDictionary *body) {
        
    }];
    
    // WHEN
    [self initializePaymentView];
    [self.paymentViewWrapper finalizePaymentViewWithSessionData:NULL];
    
    // THEN
    OCMVerify([self.paymentViewWrapper onError]);
}

- (void)test_klarnaResizedWithPaymentView {
    // GIVEN
    CGFloat size = CGFLOAT_MAX;
    OCMStub([self.actualPaymentViewMock loadWithJsonData:OCMOCK_ANY])
        ._andDo(^(NSInvocation *invocation) {
            [((id<KlarnaPaymentEventListener>)self.paymentViewWrapper) klarnaResizedWithPaymentView:self.actualPaymentViewMock to:size];
        });

    // WHEN
    [self initializePaymentView];
    [self.paymentViewWrapper loadPaymentViewWithSessionData:NULL];
    
    // THEN
    OCMVerify([self.paymentViewWrapper onResized]);
}

@end

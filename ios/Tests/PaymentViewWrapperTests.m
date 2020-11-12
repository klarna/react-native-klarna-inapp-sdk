//
//  Tests.m
//  Tests
//
//  Created by Nuri Güner on 2020-11-11.
//  Copyright © 2020 Facebook. All rights reserved.
//

#import <XCTest/XCTest.h>
#import <OCMock/OCMock.h>
#import <KlarnaMobileSDK/KlarnaMobileSDK-Swift.h>
#import "PaymentViewWrapper.h"

@interface PaymentViewWrapperTests : XCTestCase

@property (nonatomic, strong) PaymentViewWrapper* paymentViewWrapper;

@property (nonatomic, strong) RCTUIManager* rctUIManagerMock;

@property (nonatomic, strong) KlarnaPaymentView* actualPaymentViewMock;

@end

@implementation PaymentViewWrapperTests

- (void)setUp {
    PaymentViewWrapper *wrapper = [PaymentViewWrapper new];
    self.paymentViewWrapper = OCMPartialMock(wrapper);
    
    self.rctUIManagerMock = OCMClassMock([RCTUIManager class]);
    self.paymentViewWrapper.uiManager = self.rctUIManagerMock;

    id mockPaymentView = OCMClassMock([KlarnaPaymentView class]);
    OCMStub([mockPaymentView alloc]).andReturn(mockPaymentView);
    OCMStub([mockPaymentView initWithCategory:OCMOCK_ANY eventListener:OCMOCK_ANY]).andReturn(mockPaymentView);
    self.actualPaymentViewMock = mockPaymentView;
}

- (void)tearDown {
    self.paymentViewWrapper = NULL;
    self.actualPaymentViewMock = NULL;
    self.rctUIManagerMock = NULL;
    
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

@end

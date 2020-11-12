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

@end

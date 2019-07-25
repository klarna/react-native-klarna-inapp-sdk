//
//  PaymentViewWrapper.m
//  react-native-klarna-payment-view
//
//  Created by Gabriel Banfalvi on 2019-07-24.
//

#import "PaymentViewWrapper.h"

#import <KlarnaMobileSDK/KlarnaMobileSDK-Swift.h>
#import <React/RCTLog.h>

@interface PaymentViewWrapper () <KlarnaPaymentEventListener>

@property (nonatomic, strong) KlarnaPaymentView* actualPaymentView;


@end


@implementation PaymentViewWrapper

#pragma mark - React Native Overrides

- (void) setCategory:(NSString *)category {
    _category = category;
    [self evaluateProps];
}

- (void) evaluateProps {
    if (self.category != nil) {
        [self initializeActualPaymentView];
    }
}

- (void) initializeActualPaymentView {
    self.actualPaymentView = [[KlarnaPaymentView alloc] initWithCategory:self.category eventListener:self];
    self.actualPaymentView.translatesAutoresizingMaskIntoConstraints = NO;
    
    [self addSubview:self.actualPaymentView];
    
    [NSLayoutConstraint activateConstraints:@[
        [self.actualPaymentView.topAnchor constraintEqualToAnchor:self.topAnchor],
        [self.actualPaymentView.bottomAnchor constraintEqualToAnchor:self.bottomAnchor],
        [self.actualPaymentView.leadingAnchor constraintEqualToAnchor:self.leadingAnchor],
        [self.actualPaymentView.trailingAnchor constraintEqualToAnchor:self.trailingAnchor]
    ]];
}

- (void)layoutSubviews {
    [super layoutSubviews];
    self.actualPaymentView.frame = self.bounds;
    [self.actualPaymentView layoutSubviews];
}

#pragma mark - Payment View Methods

- (void)initializePaymentViewWithClientToken:(NSString *)clientToken withReturnUrl:(NSString *)returnUrl{
    NSURL* url = [NSURL URLWithString:returnUrl];
    [self.actualPaymentView initializeWithClientToken:clientToken returnUrl:url];
}

- (void)loadPaymentView {
    [self.actualPaymentView loadWithJsonData:nil];
}

- (void)authorizePaymentViewWithAutoFinalize:(BOOL)autoFinalize {
    [self.actualPaymentView authorizeWithAutoFinalize:autoFinalize jsonData:nil];
}

#pragma mark - Klarna PaymentEventListener


- (void)klarnaInitializedWithPaymentView:(KlarnaPaymentView * _Nonnull)paymentView {
    if (!self.onEvent) {
        RCTLog(@"Missing 'onEvent' callback prop.");
        return;
    }
    
    self.onEvent(@{
       @"name": @"initialized"
    });
}

- (void)klarnaLoadedWithPaymentView:(KlarnaPaymentView * _Nonnull)paymentView {
    if (!self.onEvent) {
        RCTLog(@"Missing 'onEvent' callback prop.");
        return;
    }
    
    self.onEvent(@{
       @"name": @"loaded"
    });
}

- (void)klarnaLoadedPaymentReviewWithPaymentView:(KlarnaPaymentView * _Nonnull)paymentView {
    if (!self.onEvent) {
        RCTLog(@"Missing 'onEvent' callback prop.");
        return;
    }
    
    self.onEvent(@{
        @"name": @"loadedPaymentReview"
    });
}

- (void)klarnaAuthorizedWithPaymentView:(KlarnaPaymentView * _Nonnull)paymentView approved:(BOOL)approved authToken:(NSString * _Nullable)authToken finalizeRequired:(BOOL)finalizeRequired {
    self.onEvent(@{
        @"name": @"authorized",
        @"approved": [NSNumber numberWithBool:approved],
        @"authToken": NSNull.null,
        @"finalizeRequired": [NSNumber numberWithBool:finalizeRequired]
    });

}
- (void)klarnaReauthorizedWithPaymentView:(KlarnaPaymentView * _Nonnull)paymentView approved:(BOOL)approved authToken:(NSString * _Nullable)authToken {
    
}

- (void)klarnaFinalizedWithPaymentView:(KlarnaPaymentView * _Nonnull)paymentView approved:(BOOL)approved authToken:(NSString * _Nullable)authToken {
    
}

- (void)klarnaFailedInPaymentView:(KlarnaPaymentView * _Nonnull)paymentView withError:(KlarnaPaymentError * _Nonnull)error {
   
}


- (void)klarnaResizedWithPaymentView:(KlarnaPaymentView * _Nonnull)paymentView to:(CGFloat)newHeight {
    
}

@end

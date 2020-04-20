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

- (void)loadPaymentViewWithSessionData:(NSString*)sessionData {
    [self.actualPaymentView loadWithJsonData:sessionData];
}

- (void)loadPaymentReview {
    [self.actualPaymentView loadPaymentReview];
}

- (void)authorizePaymentViewWithAutoFinalize:(BOOL)autoFinalize sessionData:(NSString*)sessionData {
    [self.actualPaymentView authorizeWithAutoFinalize:autoFinalize jsonData:sessionData];
}

- (void)reauthorizePaymentViewWithSessionData:(NSString*)sessionData {
    [self.actualPaymentView reauthorizeWithJsonData:sessionData];
}

- (void)finalizePaymentViewWithSessionData:(NSString*)sessionData {
    [self.actualPaymentView finaliseWithJsonData:sessionData];

}

#pragma mark - Klarna PaymentEventListener


- (void)klarnaInitializedWithPaymentView:(KlarnaPaymentView * _Nonnull)paymentView {
    if (!self.onInitialized) {
        RCTLog(@"Missing 'onInitialized' callback prop.");
        return;
    }
    
    self.onInitialized(@{});
}

- (void)klarnaLoadedWithPaymentView:(KlarnaPaymentView * _Nonnull)paymentView {
    if (!self.onLoaded) {
        RCTLog(@"Missing 'onLoaded' callback prop.");
        return;
    }
    
    self.onLoaded(@{});
}

- (void)klarnaLoadedPaymentReviewWithPaymentView:(KlarnaPaymentView * _Nonnull)paymentView {
    if (!self.onLoadedPaymentReview) {
        RCTLog(@"Missing 'onLoadedPaymentReview' callback prop.");
        return;
    }
    
    self.onLoadedPaymentReview(@{});
}

- (void)klarnaAuthorizedWithPaymentView:(KlarnaPaymentView * _Nonnull)paymentView approved:(BOOL)approved authToken:(NSString * _Nullable)authToken finalizeRequired:(BOOL)finalizeRequired {
    if (!self.onAuthorized) {
        RCTLog(@"Missing 'onAuthorized' callback prop.");
        return;
    }
    
    self.onAuthorized(@{
        @"approved": [NSNumber numberWithBool:approved],
        @"authToken": authToken ? authToken : NSNull.null,
        @"finalizeRequired": [NSNumber numberWithBool:finalizeRequired]
    });

}
- (void)klarnaReauthorizedWithPaymentView:(KlarnaPaymentView * _Nonnull)paymentView approved:(BOOL)approved authToken:(NSString * _Nullable)authToken {
    if (!self.onReauthorized) {
        RCTLog(@"Missing 'onReauthorized' callback prop.");
        return;
    }
    
    self.onReauthorized(@{
        @"approved": [NSNumber numberWithBool:approved],
        @"authToken": authToken ? authToken : NSNull.null,
    });
}

- (void)klarnaFinalizedWithPaymentView:(KlarnaPaymentView * _Nonnull)paymentView approved:(BOOL)approved authToken:(NSString * _Nullable)authToken {
    if (!self.onFinalized) {
        RCTLog(@"Missing 'onFinalized' callback prop.");
        return;
    }
    
    self.onFinalized(@{
        @"approved": [NSNumber numberWithBool:approved],
        @"authToken": authToken ? authToken : NSNull.null,
    });
}

- (void)klarnaFailedInPaymentView:(KlarnaPaymentView * _Nonnull)paymentView withError:(KlarnaPaymentError * _Nonnull)error {
       if (!self.onError) {
        RCTLog(@"Missing 'onError' callback prop.");
        return;
    }

    self.onError(@{
        @"error": @{
            @"action": error.action,
            @"isFatal": [NSNumber numberWithBool:error.isFatal],
            @"message": error.message,
            @"name": error.name
        }
    });
}

- (void)klarnaResizedWithPaymentView:(KlarnaPaymentView * _Nonnull)paymentView to:(CGFloat)newHeight {

    [self.uiManager setIntrinsicContentSize:CGSizeMake(UIViewNoIntrinsicMetric, newHeight) forView:self];
}

@end

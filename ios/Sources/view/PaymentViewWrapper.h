//
//  PaymentViewWrapper.h
//  react-native-klarna-payment-view
//
//  Created by Gabriel Banfalvi on 2019-07-24.
//
#import <React/RCTUIManager.h>

#ifdef RCT_NEW_ARCH_ENABLED
#import <React/RCTViewComponentView.h>
#import <UIKit/UIKit.h>
#else
#import <UIKit/UIKit.h>
#import <React/RCTView.h>
#endif

NS_ASSUME_NONNULL_BEGIN

#ifdef RCT_NEW_ARCH_ENABLED
@interface PaymentViewWrapper : RCTViewComponentView
#else
@interface PaymentViewWrapper : UIView

@property (nonatomic, copy) RCTDirectEventBlock onInitialized;
@property (nonatomic, copy) RCTDirectEventBlock onLoaded;
@property (nonatomic, copy) RCTDirectEventBlock onLoadedPaymentReview;
@property (nonatomic, copy) RCTDirectEventBlock onAuthorized;
@property (nonatomic, copy) RCTDirectEventBlock onReauthorized;
@property (nonatomic, copy) RCTDirectEventBlock onFinalized;
@property (nonatomic, copy) RCTDirectEventBlock onError;

#endif

@property (nonatomic, strong) NSString* category;

#pragma mark - React Native Overrides
- (void) setCategory:(NSString *)category;
- (void) evaluateProps;
- (void) initializeActualPaymentView;

@property (nonatomic, weak) RCTUIManager* uiManager;

- (void) initializePaymentViewWithClientToken:(NSString*)clientToken withReturnUrl:(NSString*)returnUrl;

- (void)loadPaymentViewWithSessionData:(nullable NSString*)sessionData;

- (void)loadPaymentReview;

- (void)authorizePaymentViewWithAutoFinalize:(BOOL)autoFinalize sessionData:(nullable NSString*)sessionData;

- (void)reauthorizePaymentViewWithSessionData:(nullable NSString*)sessionData;

- (void)finalizePaymentViewWithSessionData:(nullable NSString*)sessionData;

@end

NS_ASSUME_NONNULL_END

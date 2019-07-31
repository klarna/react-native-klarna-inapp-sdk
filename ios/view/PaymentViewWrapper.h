//
//  PaymentViewWrapper.h
//  react-native-klarna-payment-view
//
//  Created by Gabriel Banfalvi on 2019-07-24.
//

#import <UIKit/UIKit.h>
#import <React/RCTView.h>
#import <REact/RCTUIManager.h>

NS_ASSUME_NONNULL_BEGIN

@interface PaymentViewWrapper : UIView

@property (nonatomic, copy) RCTDirectEventBlock onInitialized;
@property (nonatomic, copy) RCTDirectEventBlock onLoaded;
@property (nonatomic, copy) RCTDirectEventBlock onLoadedPaymentReview;
@property (nonatomic, copy) RCTDirectEventBlock onAuthorized;
@property (nonatomic, copy) RCTDirectEventBlock onReauthorized;
@property (nonatomic, copy) RCTDirectEventBlock onFinalized;
@property (nonatomic, copy) RCTDirectEventBlock onError;

@property (nonatomic, strong) NSString* category;

@property (nonatomic, weak) RCTUIManager* uiManager;

- (void) initializePaymentViewWithClientToken:(NSString*)clientToken withReturnUrl:(NSString*)returnUrl;

- (void)loadPaymentViewWithSessionData:(nullable NSString*)sessionData;

- (void)loadPaymentReview;

- (void)authorizePaymentViewWithAutoFinalize:(BOOL)autoFinalize sessionData:(nullable NSString*)sessionData;

- (void)reauthorizePaymentViewWithSessionData:(nullable NSString*)sessionData;

- (void)finalizePaymentViewWithSessionData:(nullable NSString*)sessionData;

@end

NS_ASSUME_NONNULL_END

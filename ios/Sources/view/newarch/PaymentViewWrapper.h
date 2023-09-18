#if RCT_NEW_ARCH_ENABLED

#import <React/RCTUIManager.h>
#import <React/RCTViewComponentView.h>
#import <UIKit/UIKit.h>

NS_ASSUME_NONNULL_BEGIN

@interface PaymentViewWrapper : RCTViewComponentView

@property (nonatomic, strong) NSString* category;

#pragma mark - React Native Overrides
- (void) setCategory:(NSString *)category;
- (void) evaluateProps;
- (void) initializeActualPaymentView;

@property (nonatomic, weak) RCTUIManager* uiManager;

- (void)initializePaymentViewWithClientToken:(NSString*)clientToken withReturnUrl:(NSString*)returnUrl;

- (void)loadPaymentViewWithSessionData:(nullable NSString*)sessionData;

- (void)loadPaymentReview;

- (void)authorizePaymentViewWithAutoFinalize:(BOOL)autoFinalize sessionData:(nullable NSString*)sessionData;

- (void)reauthorizePaymentViewWithSessionData:(nullable NSString*)sessionData;

- (void)finalizePaymentViewWithSessionData:(nullable NSString*)sessionData;

@end


NS_ASSUME_NONNULL_END

#endif

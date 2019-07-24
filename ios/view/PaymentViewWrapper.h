//
//  PaymentViewWrapper.h
//  react-native-klarna-payment-view
//
//  Created by Gabriel Banfalvi on 2019-07-24.
//

#import <UIKit/UIKit.h>
#import <React/RCTView.h>

NS_ASSUME_NONNULL_BEGIN

@interface PaymentViewWrapper : UIView

@property (nonatomic, copy) RCTDirectEventBlock onEvent;
@property (nonatomic, strong) NSString* category;

- (void) initializePaymentViewWithClientToken:(NSString*)clientToken withReturnUrl:(NSString*)returnUrl;

- (void) loadPaymentView;

- (void) authorizePaymentViewWithAutoFinalize:(BOOL)autoFinalize;


@end

NS_ASSUME_NONNULL_END

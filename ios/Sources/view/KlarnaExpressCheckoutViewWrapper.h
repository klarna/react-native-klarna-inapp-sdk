#import <React/RCTUIManager.h>
#import <UIKit/UIKit.h>

#if RCT_NEW_ARCH_ENABLED
#import <React/RCTViewComponentView.h>
#else
#import <AVFoundation/AVFoundation.h>
#import <React/RCTView.h>
#endif

NS_ASSUME_NONNULL_BEGIN

#if RCT_NEW_ARCH_ENABLED
@interface KlarnaExpressCheckoutViewWrapper : RCTViewComponentView
#else
@interface KlarnaExpressCheckoutViewWrapper : UIView

@property (nonatomic, copy) RCTDirectEventBlock onAuthorized;
@property (nonatomic, copy) RCTDirectEventBlock onError;
@property (nonatomic, copy) RCTDirectEventBlock onResized;

@property (nonatomic, strong) NSString* sessionType;
@property (nonatomic, strong) NSString* clientId;
@property (nonatomic, strong) NSString* clientToken;
@property (nonatomic, strong) NSString* locale;
@property (nonatomic, strong) NSString* environment;
@property (nonatomic, strong) NSString* region;
@property (nonatomic, strong) NSString* returnUrl;
@property (nonatomic, strong) NSString* theme;
@property (nonatomic, strong) NSString* shape;
@property (nonatomic, strong) NSString* buttonStyle;
@property (nonatomic, assign) BOOL autoFinalize;
@property (nonatomic, assign) BOOL collectShippingAddress;
@property (nonatomic, strong) NSString* sessionData;
#endif

@property (nonatomic, weak) RCTUIManager* uiManager;

@end

NS_ASSUME_NONNULL_END

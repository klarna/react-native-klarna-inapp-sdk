#import <React/RCTUIManager.h>
#import <UIKit/UIKit.h>

#if RCT_NEW_ARCH_ENABLED
#import <React/RCTViewComponentView.h>
#endif

#if RCT_NEW_ARCH_ENABLED

@interface KlarnaOSMViewWrapper : RCTViewComponentView

#else

@interface KlarnaOSMViewWrapper : UIView

@property (nonatomic, copy) RCTDirectEventBlock onError;
@property (nonatomic, copy) RCTDirectEventBlock onResized;
@property (nonatomic, copy) RCTDirectEventBlock onOSMViewReady;

@property (nonatomic, strong) NSString* clientId;
@property (nonatomic, strong) NSString* placementKey;
@property (nonatomic, strong) NSString* locale;
@property (nonatomic, strong) NSString* purchaseAmount;
@property (nonatomic, strong) NSString* environment;
@property (nonatomic, strong) NSString* region;
@property (nonatomic, strong) NSString* theme;
@property (nonatomic, strong) NSString* osmBackgroundColor;
@property (nonatomic, strong) NSString* textColor;
@property (nullable, nonatomic, strong) NSString* returnUrl;

- (void)setClientId:(NSString * _Nonnull)clientId;
- (void)setPlacementKey:(NSString * _Nonnull)placementKey;
- (void)setLocale:(NSString * _Nonnull)locale;
- (void)setPurchaseAmount:(NSString * _Nonnull)purchaseAmount;
- (void)setEnvironment:(NSString * _Nonnull)environment;
- (void)setRegion:(NSString * _Nonnull)region;
- (void)setTheme:(NSString * _Nonnull)theme;
- (void)setOsmBackgroundColor:(NSString * _Nonnull)osmBackgroundColor;
- (void)setTextColor:(NSString * _Nonnull)textColor;
- (void)setReturnUrl:(NSString * _Nullable)returnUrl;

#endif

@property (nonatomic, weak) RCTUIManager* uiManager;

- (void)render;

@end

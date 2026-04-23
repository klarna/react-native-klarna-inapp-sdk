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
@interface KlarnaCheckoutViewWrapper : RCTViewComponentView
#else
@interface KlarnaCheckoutViewWrapper : UIView

@property (nonatomic, copy) RCTDirectEventBlock onEvent;
@property (nonatomic, copy) RCTDirectEventBlock onError;
@property (nonatomic, copy) RCTDirectEventBlock onResized;
@property (nonatomic, copy) RCTDirectEventBlock onCheckoutViewReady;

@property (nonatomic, strong) NSString* returnUrl;

- (void) setReturnUrl:(NSString * _Nonnull)returnUrl;
#endif

#pragma mark - React Native Overrides

@property (nonatomic, weak) RCTUIManager* uiManager;

- (void)setSnippet:(NSString*)snippet;

- (void)suspend;

- (void)resume;

@end

NS_ASSUME_NONNULL_END

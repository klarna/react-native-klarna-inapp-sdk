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
@interface KlarnaStandaloneWebViewWrapper : RCTViewComponentView
#else
@interface KlarnaStandaloneWebViewWrapper : UIView

@property (nonatomic, copy) RCTDirectEventBlock onLoadStart;
@property (nonatomic, copy) RCTDirectEventBlock onLoadEnd;
@property (nonatomic, copy) RCTDirectEventBlock onError;
@property (nonatomic, copy) RCTDirectEventBlock onLoadProgress;
@property (nonatomic, copy) RCTDirectEventBlock onKlarnaMessage;

@property (nonatomic, strong) NSString* returnUrl;
#endif

#pragma mark - React Native Overrides

@property (nonatomic, weak) RCTUIManager* uiManager;

- (void)load:(nonnull NSString*)url;

- (void)goForward;

- (void)goBack;

- (void)reload;

@end

NS_ASSUME_NONNULL_END

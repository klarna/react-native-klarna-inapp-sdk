#if !RCT_NEW_ARCH_ENABLED

#import <React/RCTUIManager.h>

#import <AVFoundation/AVFoundation.h>
#import <UIKit/UIKit.h>
#import <React/RCTView.h>
#import <React/RCTUIManager.h>

NS_ASSUME_NONNULL_BEGIN

@interface KlarnaStandaloneWebViewWrapper : UIView

@property (nonatomic, copy) RCTDirectEventBlock onBeforeLoad;
@property (nonatomic, copy) RCTDirectEventBlock onLoad;

@property (nonatomic, strong) NSString* returnUrl;

#pragma mark - React Native Overrides

- (void)initializeKlarnaStandaloneWebView;

@property (nonatomic, weak) RCTUIManager* uiManager;

- (void)load:(nonnull NSString*)url;

- (void)goForward;

- (void)goBack;

- (void)reload;

@end

NS_ASSUME_NONNULL_END

#endif



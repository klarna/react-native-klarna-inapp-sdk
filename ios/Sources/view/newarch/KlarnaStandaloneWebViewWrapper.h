#if RCT_NEW_ARCH_ENABLED

#import <React/RCTUIManager.h>
#import <React/RCTViewComponentView.h>
#import <UIKit/UIKit.h>

NS_ASSUME_NONNULL_BEGIN

@interface KlarnaStandaloneWebViewWrapper : RCTViewComponentView

#pragma mark - React Native Overrides

@property (nonatomic, weak) RCTUIManager* uiManager;

- (void) initializeKlarnaStandaloneWebView:(nonnull NSString*)returnUrl;

- (void)load:(nonnull NSString*)url;

- (void)goForward;

- (void)goBack;

- (void)reload;

@end

NS_ASSUME_NONNULL_END

#endif

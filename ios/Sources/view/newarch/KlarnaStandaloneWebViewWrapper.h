#if RCT_NEW_ARCH_ENABLED

#import <React/RCTUIManager.h>
#import <React/RCTViewComponentView.h>
#import <UIKit/UIKit.h>

NS_ASSUME_NONNULL_BEGIN

@interface KlarnaStandaloneWebViewWrapper : RCTViewComponentView

@property (nonatomic, weak) RCTUIManager* uiManager;

@end

NS_ASSUME_NONNULL_END

#endif


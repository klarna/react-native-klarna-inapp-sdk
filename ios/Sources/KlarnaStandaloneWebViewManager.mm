#import "KlarnaStandaloneWebViewManager.h"

#import <React/RCTUIManager.h>
#import <React/RCTLog.h>

#ifdef RCT_NEW_ARCH_ENABLED
#import "view/newarch/KlarnaStandaloneWebViewWrapper.h"
#else
#import "view/oldarch/KlarnaStandaloneWebViewWrapper.h"
#endif

@implementation KlarnaStandaloneWebViewManager

RCT_EXPORT_MODULE(RNKlarnaStandaloneWebView)

#pragma mark - View

RCT_EXPORT_VIEW_PROPERTY(returnUrl, NSString)

- (UIView *)view
{
    KlarnaStandaloneWebViewWrapper* wrapper = [KlarnaStandaloneWebViewWrapper new];
    wrapper.uiManager = self.bridge.uiManager;
    return wrapper;
}

@end

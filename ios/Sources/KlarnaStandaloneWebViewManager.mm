#import "KlarnaStandaloneWebViewManager.h"

#import <React/RCTUIManager.h>
#import <React/RCTLog.h>


#ifdef RCT_NEW_ARCH_ENABLED
#import "view/newarch/KlarnaStandaloneWebView.h"
#else
#import "view/oldarch/KlarnaStandaloneWebView.h"
#endif

@implementation KlarnaStandaloneWebViewManager

RCT_EXPORT_MODULE(RNKlarnaStandaloneWebView)

#pragma mark - View

RCT_EXPORT_VIEW_PROPERTY(returnUrl, NSString)

@end


#if !RCT_NEW_ARCH_ENABLED

#import "KlarnaStandaloneWebView.h"

#import <KlarnaMobileSDK/KlarnaMobileSDK-Swift.h>
#import <React/RCTLog.h>

@interface KlarnaStandaloneWebView ()

@property (nonatomic, strong) KlarnaStandaloneWebView* klarnaStandaloneWebView;

@end

@implementation KlarnaStandaloneWebView

#pragma mark - React Native Overrides

- (void) setReturnUrl:(NSString *)returnUrl {
    _returnUrl = returnUrl;
    if (returnUrl.length > 0) {
        self.klarnaStandaloneWebView.returnUrl = [NSURL URLWithString:self.returnUrl];
    }
}

@end

#endif


#import <React/RCTUIManager.h>
#import <React/RCTLog.h>
#import "KlarnaExpressCheckoutViewManager.h"
#import "view/KlarnaExpressCheckoutViewWrapper.h"

@implementation KlarnaExpressCheckoutViewManager

RCT_EXPORT_MODULE(RNKlarnaExpressCheckoutView)

#pragma mark - View Properties

RCT_EXPORT_VIEW_PROPERTY(sessionType, NSString)
RCT_EXPORT_VIEW_PROPERTY(clientId, NSString)
RCT_EXPORT_VIEW_PROPERTY(clientToken, NSString)
RCT_EXPORT_VIEW_PROPERTY(locale, NSString)
RCT_EXPORT_VIEW_PROPERTY(environment, NSString)
RCT_EXPORT_VIEW_PROPERTY(region, NSString)
RCT_EXPORT_VIEW_PROPERTY(returnUrl, NSString)
RCT_EXPORT_VIEW_PROPERTY(theme, NSString)
RCT_EXPORT_VIEW_PROPERTY(shape, NSString)
RCT_EXPORT_VIEW_PROPERTY(buttonStyle, NSString)
RCT_EXPORT_VIEW_PROPERTY(autoFinalize, BOOL)
RCT_EXPORT_VIEW_PROPERTY(collectShippingAddress, BOOL)
RCT_EXPORT_VIEW_PROPERTY(sessionData, NSString)

RCT_EXPORT_VIEW_PROPERTY(onAuthorized, RCTDirectEventBlock)
RCT_EXPORT_VIEW_PROPERTY(onError, RCTDirectEventBlock)
RCT_EXPORT_VIEW_PROPERTY(onResized, RCTDirectEventBlock)

#pragma mark - View Creation

- (UIView *)view
{
    KlarnaExpressCheckoutViewWrapper* wrapper = [KlarnaExpressCheckoutViewWrapper new];
    wrapper.uiManager = self.bridge.uiManager;
    return wrapper;
}

@end

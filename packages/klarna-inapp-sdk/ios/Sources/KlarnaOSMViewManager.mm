#import <React/RCTUIManager.h>
#import <React/RCTLog.h>
#import "KlarnaOSMViewManager.h"
#import "view/KlarnaOSMViewWrapper.h"

@implementation KlarnaOSMViewManager

RCT_EXPORT_MODULE(RNKlarnaOSMView)

#pragma mark - View Properties

RCT_EXPORT_VIEW_PROPERTY(clientId, NSString)
RCT_EXPORT_VIEW_PROPERTY(placementKey, NSString)
RCT_EXPORT_VIEW_PROPERTY(locale, NSString)
RCT_EXPORT_VIEW_PROPERTY(purchaseAmount, NSString)
RCT_EXPORT_VIEW_PROPERTY(environment, NSString)
RCT_EXPORT_VIEW_PROPERTY(region, NSString)
RCT_EXPORT_VIEW_PROPERTY(theme, NSString)
RCT_REMAP_VIEW_PROPERTY(backgroundColor, osmBackgroundColor, NSString)
RCT_EXPORT_VIEW_PROPERTY(textColor, NSString)
RCT_EXPORT_VIEW_PROPERTY(returnUrl, NSString)
RCT_EXPORT_VIEW_PROPERTY(onError, RCTDirectEventBlock)
RCT_EXPORT_VIEW_PROPERTY(onResized, RCTDirectEventBlock)
RCT_EXPORT_VIEW_PROPERTY(onOSMViewReady, RCTDirectEventBlock)

#pragma mark - View Creation

- (UIView *)view
{
    KlarnaOSMViewWrapper* wrapper = [KlarnaOSMViewWrapper new];
    wrapper.uiManager = self.bridge.uiManager;
    return wrapper;
}

#pragma mark - Exported Methods to React Native

RCT_EXPORT_METHOD(render:(nonnull NSNumber *)reactTag)
{
    [self.bridge.uiManager addUIBlock:^(RCTUIManager *uiManager, NSDictionary<NSNumber *, UIView *> *viewRegistry) {
        KlarnaOSMViewWrapper *view = [self osmViewWrapperForTag:reactTag fromRegistry:viewRegistry];
        if (view) {
            [view render];
        }
    }];
}

#pragma mark - Private Helper Methods

- (KlarnaOSMViewWrapper *)osmViewWrapperForTag:(NSNumber *)reactTag fromRegistry:(NSDictionary<NSNumber *, UIView *> *)viewRegistry
{
    UIView *view = viewRegistry[reactTag];
    if (!view || ![view isKindOfClass:[KlarnaOSMViewWrapper class]]) {
        RCTLogError(@"Can't find KlarnaOSMViewWrapper with tag #%@", reactTag);
        return nil;
    }
    return (KlarnaOSMViewWrapper *)view;
}

@end

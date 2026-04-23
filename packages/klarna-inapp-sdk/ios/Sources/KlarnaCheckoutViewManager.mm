#import <React/RCTUIManager.h>
#import <React/RCTLog.h>
#import "KlarnaCheckoutViewManager.h"
#import "view/KlarnaCheckoutViewWrapper.h"

@implementation KlarnaCheckoutViewManager

RCT_EXPORT_MODULE(RNKlarnaCheckoutView)

#pragma mark - View Properties

RCT_EXPORT_VIEW_PROPERTY(returnUrl, NSString)
RCT_EXPORT_VIEW_PROPERTY(onEvent, RCTDirectEventBlock)
RCT_EXPORT_VIEW_PROPERTY(onError, RCTDirectEventBlock)
RCT_EXPORT_VIEW_PROPERTY(onResized, RCTDirectEventBlock)
RCT_EXPORT_VIEW_PROPERTY(onCheckoutViewReady, RCTDirectEventBlock)

#pragma mark - View Creation

- (UIView *)view
{
    KlarnaCheckoutViewWrapper* wrapper = [KlarnaCheckoutViewWrapper new];
    wrapper.uiManager = self.bridge.uiManager;
    return wrapper;
}

#pragma mark - Exported Methods to React Native

RCT_EXPORT_METHOD(setSnippet:(nonnull NSNumber *)reactTag snippet:(NSString *)snippet)
{
    [self.bridge.uiManager addUIBlock:^(RCTUIManager *uiManager, NSDictionary<NSNumber *, UIView *> *viewRegistry) {
        KlarnaCheckoutViewWrapper *view = [self checkoutViewWrapperForTag:reactTag fromRegistry:viewRegistry];
        if (view) {
            [view setSnippet:snippet];
        }
    }];
}

RCT_EXPORT_METHOD(suspend:(nonnull NSNumber *)reactTag) {
    [self.bridge.uiManager addUIBlock:^(RCTUIManager *uiManager, NSDictionary<NSNumber *,UIView *> *viewRegistry) {
        KlarnaCheckoutViewWrapper *view = [self checkoutViewWrapperForTag:reactTag fromRegistry:viewRegistry];
        if (view) {
            [view suspend];
        }
    }];
}

RCT_EXPORT_METHOD(resume:(nonnull NSNumber *)reactTag) {
    [self.bridge.uiManager addUIBlock:^(RCTUIManager *uiManager, NSDictionary<NSNumber *,UIView *> *viewRegistry) {
        KlarnaCheckoutViewWrapper *view = [self checkoutViewWrapperForTag:reactTag fromRegistry:viewRegistry];
        if (view) {
            [view resume];
        }
    }];
}

#pragma mark - Private Helper Methods

- (KlarnaCheckoutViewWrapper *)checkoutViewWrapperForTag:(NSNumber *)reactTag fromRegistry:(NSDictionary<NSNumber *, UIView *> *)viewRegistry
{
    UIView *view = viewRegistry[reactTag];
    if (!view || ![view isKindOfClass:[KlarnaCheckoutViewWrapper class]]) {
        RCTLogError(@"Can't find KlarnaCheckoutViewWrapper with tag #%@", reactTag);
        return nil;
    }
    return (KlarnaCheckoutViewWrapper *)view;
}

@end

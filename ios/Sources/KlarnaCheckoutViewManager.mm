#import "KlarnaCheckoutViewManager.h"

#import <React/RCTUIManager.h>
#import <React/RCTLog.h>

#ifdef RCT_NEW_ARCH_ENABLED
#import "view/newarch/KlarnaCheckoutViewWrapper.h"
#else
#import "view/oldarch/KlarnaCheckoutViewWrapper.h"
#endif

@implementation KlarnaCheckoutViewManager

RCT_EXPORT_MODULE(RNKlarnaCheckoutView)

#pragma mark - View

RCT_EXPORT_VIEW_PROPERTY(returnUrl, NSString)
RCT_EXPORT_VIEW_PROPERTY(onEvent, RCTDirectEventBlock)
RCT_EXPORT_VIEW_PROPERTY(onError, RCTDirectEventBlock)
RCT_EXPORT_VIEW_PROPERTY(onResized, RCTDirectEventBlock)

- (UIView *)view
{
    KlarnaCheckoutViewWrapper* wrapper = [KlarnaCheckoutViewWrapper new];
    wrapper.uiManager = self.bridge.uiManager;
    return wrapper;
}

#pragma mark - Exported Methods

RCT_EXPORT_METHOD(setSnippet:(nonnull NSNumber*)reactTag snippet:(NSString*)snippet) {
    [self.bridge.uiManager addUIBlock:^(RCTUIManager *uiManager, NSDictionary<NSNumber *,UIView *> *viewRegistry) {
        KlarnaCheckoutViewWrapper* view = (KlarnaCheckoutViewWrapper*) viewRegistry[reactTag];
        if (!view || ![view isKindOfClass:KlarnaCheckoutViewWrapper.class]) {
            RCTLogError(@"Can't find KlarnaCheckoutViewWrapper with tag #%@", reactTag);
            return;
        }
        [view setSnippet:snippet];
    }];
}

RCT_EXPORT_METHOD(suspend:(nonnull NSNumber*)reactTag) {
    [self.bridge.uiManager addUIBlock:^(RCTUIManager *uiManager, NSDictionary<NSNumber *,UIView *> *viewRegistry) {
        KlarnaCheckoutViewWrapper* view = (KlarnaCheckoutViewWrapper*) viewRegistry[reactTag];
        if (!view || ![view isKindOfClass:KlarnaCheckoutViewWrapper.class]) {
            RCTLogError(@"Can't find KlarnaCheckoutViewWrapper with tag #%@", reactTag);
            return;
        }
        [view suspend];
    }];
}

RCT_EXPORT_METHOD(resume:(nonnull NSNumber*)reactTag) {
    [self.bridge.uiManager addUIBlock:^(RCTUIManager *uiManager, NSDictionary<NSNumber *,UIView *> *viewRegistry) {
        KlarnaCheckoutViewWrapper* view = (KlarnaCheckoutViewWrapper*) viewRegistry[reactTag];
        if (!view || ![view isKindOfClass:KlarnaCheckoutViewWrapper.class]) {
            RCTLogError(@"Can't find KlarnaCheckoutViewWrapper with tag #%@", reactTag);
            return;
        }
        [view resume];
    }];
}

@end

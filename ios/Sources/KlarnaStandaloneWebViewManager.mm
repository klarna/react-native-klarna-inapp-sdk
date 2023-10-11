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
RCT_EXPORT_VIEW_PROPERTY(onBeforeLoad, RCTDirectEventBlock)
RCT_EXPORT_VIEW_PROPERTY(onLoad, RCTDirectEventBlock)
RCT_EXPORT_VIEW_PROPERTY(onLoadError, RCTDirectEventBlock)
RCT_EXPORT_VIEW_PROPERTY(onProgressChange, RCTDirectEventBlock)
RCT_EXPORT_VIEW_PROPERTY(onKlarnaMessage, RCTDirectEventBlock)

- (UIView *)view {
    KlarnaStandaloneWebViewWrapper* wrapper = [KlarnaStandaloneWebViewWrapper new];
    wrapper.uiManager = self.bridge.uiManager;
    return wrapper;
}

#pragma mark - Exported Methods

RCT_EXPORT_METHOD(load:(nonnull NSNumber*)reactTag url:(nonnull NSString*)url) {
    [self.bridge.uiManager addUIBlock:^(RCTUIManager *uiManager, NSDictionary<NSNumber *,UIView *> *viewRegistry) {
        KlarnaStandaloneWebViewWrapper* view = (KlarnaStandaloneWebViewWrapper*) viewRegistry[reactTag];
        if (!view || ![view isKindOfClass:KlarnaStandaloneWebViewWrapper.class]) {
            RCTLogError(@"Can't find KlarnaStandaloneWebViewWrapper with tag #%@", reactTag);
            return;
        }
        [view load:url];
    }];
}

RCT_EXPORT_METHOD(goForward:(nonnull NSNumber*)reactTag) {
    [self.bridge.uiManager addUIBlock:^(RCTUIManager *uiManager, NSDictionary<NSNumber *,UIView *> *viewRegistry) {
        KlarnaStandaloneWebViewWrapper* view = (KlarnaStandaloneWebViewWrapper*) viewRegistry[reactTag];
        if (!view || ![view isKindOfClass:KlarnaStandaloneWebViewWrapper.class]) {
            RCTLogError(@"Can't find KlarnaStandaloneWebViewWrapper with tag #%@", reactTag);
            return;
        }
        [view goForward];
    }];
}

RCT_EXPORT_METHOD(goBack:(nonnull NSNumber*)reactTag) {
    [self.bridge.uiManager addUIBlock:^(RCTUIManager *uiManager, NSDictionary<NSNumber *,UIView *> *viewRegistry) {
        KlarnaStandaloneWebViewWrapper* view = (KlarnaStandaloneWebViewWrapper*) viewRegistry[reactTag];
        if (!view || ![view isKindOfClass:KlarnaStandaloneWebViewWrapper.class]) {
            RCTLogError(@"Can't find KlarnaStandaloneWebViewWrapper with tag #%@", reactTag);
            return;
        }
        [view goBack];
    }];
}

RCT_EXPORT_METHOD(reload:(nonnull NSNumber*)reactTag) {
    [self.bridge.uiManager addUIBlock:^(RCTUIManager *uiManager, NSDictionary<NSNumber *,UIView *> *viewRegistry) {
        KlarnaStandaloneWebViewWrapper* view = (KlarnaStandaloneWebViewWrapper*) viewRegistry[reactTag];
        if (!view || ![view isKindOfClass:KlarnaStandaloneWebViewWrapper.class]) {
            RCTLogError(@"Can't find KlarnaStandaloneWebViewWrapper with tag #%@", reactTag);
            return;
        }
        [view reload];
    }];
}

@end

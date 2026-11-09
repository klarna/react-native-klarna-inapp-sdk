#import "KlarnaStandaloneWebViewManager.h"

#import <React/RCTUIManager.h>
#import <React/RCTLog.h>

#import "view/KlarnaStandaloneWebViewWrapper.h"

@implementation KlarnaStandaloneWebViewManager

RCT_EXPORT_MODULE(RNKlarnaStandaloneWebView)

#pragma mark - View

RCT_EXPORT_VIEW_PROPERTY(returnUrl, NSString)
RCT_EXPORT_VIEW_PROPERTY(theme, NSString)
RCT_EXPORT_VIEW_PROPERTY(bounces, BOOL)
RCT_EXPORT_VIEW_PROPERTY(sharedCookiesEnabled, BOOL)
RCT_EXPORT_VIEW_PROPERTY(onLoadStart, RCTDirectEventBlock)
RCT_EXPORT_VIEW_PROPERTY(onLoadEnd, RCTDirectEventBlock)
RCT_EXPORT_VIEW_PROPERTY(onError, RCTDirectEventBlock)
RCT_EXPORT_VIEW_PROPERTY(onLoadProgress, RCTDirectEventBlock)
RCT_EXPORT_VIEW_PROPERTY(onKlarnaMessage, RCTDirectEventBlock)
RCT_EXPORT_VIEW_PROPERTY(onSetCookiesResult, RCTDirectEventBlock)

- (UIView *)view {
    KlarnaStandaloneWebViewWrapper* wrapper = [KlarnaStandaloneWebViewWrapper new];
    wrapper.uiManager = self.bridge.uiManager;
    return wrapper;
}

#pragma mark - Exported Methods

RCT_EXPORT_METHOD(load:(nonnull NSNumber*)reactTag url:(nonnull NSString*)url optionsJson:(nullable NSString*)optionsJson) {
    [self.bridge.uiManager addUIBlock:^(RCTUIManager *uiManager, NSDictionary<NSNumber *,UIView *> *viewRegistry) {
        KlarnaStandaloneWebViewWrapper* view = (KlarnaStandaloneWebViewWrapper*) viewRegistry[reactTag];
        if (!view || ![view isKindOfClass:KlarnaStandaloneWebViewWrapper.class]) {
            RCTLogError(@"Can't find KlarnaStandaloneWebViewWrapper with tag #%@", reactTag);
            return;
        }
        [view load:url optionsJson:optionsJson];
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

RCT_EXPORT_METHOD(stopLoading:(nonnull NSNumber*)reactTag) {
    [self.bridge.uiManager addUIBlock:^(RCTUIManager *uiManager, NSDictionary<NSNumber *,UIView *> *viewRegistry) {
        KlarnaStandaloneWebViewWrapper* view = (KlarnaStandaloneWebViewWrapper*) viewRegistry[reactTag];
        if (!view || ![view isKindOfClass:KlarnaStandaloneWebViewWrapper.class]) {
            RCTLogError(@"Can't find KlarnaStandaloneWebViewWrapper with tag #%@", reactTag);
            return;
        }
        [view stopLoading];
    }];
}

RCT_EXPORT_METHOD(setCookies:(nonnull NSNumber*)reactTag requestId:(nonnull NSNumber*)requestId cookiesJson:(nonnull NSString*)cookiesJson) {
    [self.bridge.uiManager addUIBlock:^(RCTUIManager *uiManager, NSDictionary<NSNumber *,UIView *> *viewRegistry) {
        KlarnaStandaloneWebViewWrapper* view = (KlarnaStandaloneWebViewWrapper*) viewRegistry[reactTag];
        if (!view || ![view isKindOfClass:KlarnaStandaloneWebViewWrapper.class]) {
            RCTLogError(@"Can't find KlarnaStandaloneWebViewWrapper with tag #%@", reactTag);
            return;
        }
        [view setCookies:requestId.integerValue cookiesJson:cookiesJson];
    }];
}

@end

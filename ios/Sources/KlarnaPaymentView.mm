#import <AVFoundation/AVFoundation.h>
#import "KlarnaPaymentView.h"

#import <React/RCTUIManager.h>
#import <React/RCTLog.h>

#import "view/PaymentViewWrapper.h"


@implementation KlarnaPaymentView

RCT_EXPORT_MODULE(RNKlarnaPaymentView)

#pragma mark - View

RCT_EXPORT_VIEW_PROPERTY(category, NSString)
RCT_EXPORT_VIEW_PROPERTY(onInitialized, RCTDirectEventBlock)
RCT_EXPORT_VIEW_PROPERTY(onLoaded, RCTDirectEventBlock)
RCT_EXPORT_VIEW_PROPERTY(onLoadedPaymentReview, RCTDirectEventBlock)
RCT_EXPORT_VIEW_PROPERTY(onAuthorized, RCTDirectEventBlock)
RCT_EXPORT_VIEW_PROPERTY(onReauthorized, RCTDirectEventBlock)
RCT_EXPORT_VIEW_PROPERTY(onFinalized, RCTDirectEventBlock)
RCT_EXPORT_VIEW_PROPERTY(onError, RCTDirectEventBlock)

- (UIView *)view
{
    PaymentViewWrapper* wrapper = [PaymentViewWrapper new];
    wrapper.uiManager = self.bridge.uiManager;
    return wrapper;
}

#pragma mark - Exported Methods

RCT_EXPORT_METHOD(initialize:(nonnull NSNumber*)reactTag clientToken:(NSString*)clientToken returnUrl:(NSString*)returnUrl) {
    
    [self.bridge.uiManager addUIBlock:^(RCTUIManager *uiManager, NSDictionary<NSNumber *,UIView *> *viewRegistry) {
        PaymentViewWrapper* view = (PaymentViewWrapper*) viewRegistry[reactTag];
        if (!view || ![view isKindOfClass:PaymentViewWrapper.class]) {
            RCTLogError(@"Can't find PaymentViewWrapper with tag #%@", reactTag);
            return;
        }
        [view initializePaymentViewWithClientToken:clientToken withReturnUrl:returnUrl];
    }];
    
    
}

RCT_EXPORT_METHOD(load:(nonnull NSNumber*)reactTag sessionData:(nullable NSString*)sessionData) {
    
    [self.bridge.uiManager addUIBlock:^(RCTUIManager *uiManager, NSDictionary<NSNumber *,UIView *> *viewRegistry) {
        PaymentViewWrapper* view = (PaymentViewWrapper*) viewRegistry[reactTag];
        if (!view || ![view isKindOfClass:PaymentViewWrapper.class]) {
            RCTLogError(@"Can't find PaymentViewWrapper with tag #%@", reactTag);
            return;
        }
        [view loadPaymentViewWithSessionData:sessionData];
    }];
}

RCT_EXPORT_METHOD(loadPaymentReview:(nonnull NSNumber*)reactTag) {
    
    [self.bridge.uiManager addUIBlock:^(RCTUIManager *uiManager, NSDictionary<NSNumber *,UIView *> *viewRegistry) {
        PaymentViewWrapper* view = (PaymentViewWrapper*) viewRegistry[reactTag];
        if (!view || ![view isKindOfClass:PaymentViewWrapper.class]) {
            RCTLogError(@"Can't find PaymentViewWrapper with tag #%@", reactTag);
            return;
        }
        [view loadPaymentReview];
    }];
}

RCT_EXPORT_METHOD(authorize:(nonnull NSNumber*)reactTag autoFinalize:(BOOL)autoFinalize sessionData:(nullable NSString*)sessionData) {
    
    [self.bridge.uiManager addUIBlock:^(RCTUIManager *uiManager, NSDictionary<NSNumber *,UIView *> *viewRegistry) {
        PaymentViewWrapper* view = (PaymentViewWrapper*) viewRegistry[reactTag];
        if (!view || ![view isKindOfClass:PaymentViewWrapper.class]) {
            RCTLogError(@"Can't find PaymentViewWrapper with tag #%@", reactTag);
            return;
        }
        [view authorizePaymentViewWithAutoFinalize: autoFinalize sessionData:sessionData];
    }];
}

RCT_EXPORT_METHOD(reauthorize:(nonnull NSNumber*)reactTag sessionData:(nullable NSString*)sessionData) {
    
    [self.bridge.uiManager addUIBlock:^(RCTUIManager *uiManager, NSDictionary<NSNumber *,UIView *> *viewRegistry) {
        PaymentViewWrapper* view = (PaymentViewWrapper*) viewRegistry[reactTag];
        if (!view || ![view isKindOfClass:PaymentViewWrapper.class]) {
            RCTLogError(@"Can't find PaymentViewWrapper with tag #%@", reactTag);
            return;
        }
        [view reauthorizePaymentViewWithSessionData:sessionData];
    }];
}

RCT_EXPORT_METHOD(finalize:(nonnull NSNumber*)reactTag sessionData:(nullable NSString*)sessionData) {
    
    [self.bridge.uiManager addUIBlock:^(RCTUIManager *uiManager, NSDictionary<NSNumber *,UIView *> *viewRegistry) {
        PaymentViewWrapper* view = (PaymentViewWrapper*) viewRegistry[reactTag];
        if (!view || ![view isKindOfClass:PaymentViewWrapper.class]) {
            RCTLogError(@"Can't find PaymentViewWrapper with tag #%@", reactTag);
            return;
        }
        [view finalizePaymentViewWithSessionData:sessionData];
    }];
    
    
}

@end

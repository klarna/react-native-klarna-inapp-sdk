#import "KlarnaPaymentView.h"

#import <React/RCTUIManager.h>
#import <React/RCTLog.h>

#import "view/PaymentViewWrapper.h"


@implementation KlarnaPaymentView

RCT_EXPORT_MODULE()

#pragma mark - View

RCT_EXPORT_VIEW_PROPERTY(category, NSString)
RCT_EXPORT_VIEW_PROPERTY(onEvent, RCTDirectEventBlock)

- (UIView *)view
{
    PaymentViewWrapper* wrapper = [PaymentViewWrapper new];
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


RCT_EXPORT_METHOD(load:(nonnull NSNumber*)reactTag) {
    
    [self.bridge.uiManager addUIBlock:^(RCTUIManager *uiManager, NSDictionary<NSNumber *,UIView *> *viewRegistry) {
        PaymentViewWrapper* view = (PaymentViewWrapper*) viewRegistry[reactTag];
        if (!view || ![view isKindOfClass:PaymentViewWrapper.class]) {
            RCTLogError(@"Can't find PaymentViewWrapper with tag #%@", reactTag);
            return;
        }
        [view loadPaymentView];
    }];
}


RCT_EXPORT_METHOD(authorize:(nonnull NSNumber*)reactTag withAutoFinalize:(BOOL)autoFinalize) {
    
    [self.bridge.uiManager addUIBlock:^(RCTUIManager *uiManager, NSDictionary<NSNumber *,UIView *> *viewRegistry) {
        PaymentViewWrapper* view = (PaymentViewWrapper*) viewRegistry[reactTag];
        if (!view || ![view isKindOfClass:PaymentViewWrapper.class]) {
            RCTLogError(@"Can't find PaymentViewWrapper with tag #%@", reactTag);
            return;
        }
        [view authorizePaymentViewWithAutoFinalize: autoFinalize];
    }];
}

@end

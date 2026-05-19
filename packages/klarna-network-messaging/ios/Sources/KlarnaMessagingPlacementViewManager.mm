#import <React/RCTUIManager.h>
#import <React/RCTLog.h>
#import "KlarnaMessagingPlacementViewManager.h"
#import "view/KlarnaMessagingPlacementViewWrapper.h"

@implementation KlarnaMessagingPlacementViewManager

+ (void)initialize
{
    if (self != [KlarnaMessagingPlacementViewManager class]) {
        return;
    }
#ifndef RCT_NEW_ARCH_ENABLED
    RCTLogError(@"KlarnaMessagingPlacementViewManager: react-native-klarna-network-messaging does not support the old architecture. Please enable the New Architecture (RCT_NEW_ARCH_ENABLED=1) before running pod install.");
#endif
}

RCT_EXPORT_MODULE(RNKlarnaMessagingPlacementView)

#pragma mark - View Properties

RCT_EXPORT_VIEW_PROPERTY(placementType, NSString)
RCT_EXPORT_VIEW_PROPERTY(theme, NSString)
RCT_EXPORT_VIEW_PROPERTY(amount, NSString)
RCT_EXPORT_VIEW_PROPERTY(currency, NSString)
RCT_EXPORT_VIEW_PROPERTY(onError, RCTDirectEventBlock)
RCT_EXPORT_VIEW_PROPERTY(onResized, RCTDirectEventBlock)

#pragma mark - View Creation

- (UIView *)view
{
    KlarnaMessagingPlacementViewWrapper *wrapper = [KlarnaMessagingPlacementViewWrapper new];
    return wrapper;
}

@end

#import <react/renderer/components/KlarnaNetworkMessagingSpec/ComponentDescriptors.h>
#import <react/renderer/components/KlarnaNetworkMessagingSpec/EventEmitters.h>
#import <react/renderer/components/KlarnaNetworkMessagingSpec/Props.h>
#import <react/renderer/components/KlarnaNetworkMessagingSpec/RCTComponentViewHelpers.h>
#import "KlarnaMessagingPlacementViewWrapper.h"
#import "RCTFabricComponentsPlugins.h"

// Framework form resolves under use_frameworks!; quoted form is the headermap fallback for static builds.
#if __has_include(<react_native_klarna_network_messaging/react_native_klarna_network_messaging-Swift.h>)
#import <react_native_klarna_network_messaging/react_native_klarna_network_messaging-Swift.h>
#else
#import "react_native_klarna_network_messaging-Swift.h"
#endif

using namespace facebook::react;

@interface KlarnaMessagingPlacementViewWrapper () <RCTRNKlarnaMessagingPlacementViewViewProtocol, KlarnaMessagingHeightDelegate>

@property (nonatomic, strong) UIView* placementView;
@property (nonatomic, strong) KlarnaMessagingHeightObserver* heightObserver;

@end

@implementation KlarnaMessagingPlacementViewWrapper

#pragma mark - Initialization

- (instancetype)initWithFrame:(CGRect)frame
{
    self = [super initWithFrame:frame];
    if (self) {
        self.backgroundColor = [UIColor clearColor];
        self.clipsToBounds = NO;
        self.heightObserver = [[KlarnaMessagingHeightObserver alloc] initWithDelayIntervals:@[@0.5, @1.5, @3.0]];
        self.heightObserver.delegate = self;
        static const auto defaultProps = std::make_shared<const RNKlarnaMessagingPlacementViewProps>();
        _props = defaultProps;
    }
    return self;
}

- (void)dealloc {
    [self.heightObserver stopObserving];
}

+ (ComponentDescriptorProvider)componentDescriptorProvider
{
    return concreteComponentDescriptorProvider<RNKlarnaMessagingPlacementViewComponentDescriptor>();
}

Class<RCTComponentViewProtocol> RNKlarnaMessagingPlacementViewCls(void)
{
    return KlarnaMessagingPlacementViewWrapper.class;
}

#pragma mark - Props Update

/// Diffs all placement-related props against their previous values and triggers
/// a full view recreation when any of them change. Props are compared before
/// `[super updateProps:…]` stores the new snapshot.
- (void)updateProps:(const facebook::react::Props::Shared &)props oldProps:(const facebook::react::Props::Shared &)oldProps {
    const auto &oldViewProps = *std::static_pointer_cast<RNKlarnaMessagingPlacementViewProps const>(_props);
    const auto &newViewProps = *std::static_pointer_cast<RNKlarnaMessagingPlacementViewProps const>(props);

    BOOL needsRecreate = oldViewProps.instanceId != newViewProps.instanceId
        || oldViewProps.placementType != newViewProps.placementType
        || oldViewProps.theme != newViewProps.theme
        || oldViewProps.amount != newViewProps.amount
        || oldViewProps.currency != newViewProps.currency;

    [super updateProps:props oldProps:oldProps];

    if (needsRecreate) {
        NSString *instanceId = [[NSString alloc] initWithUTF8String:newViewProps.instanceId.c_str()];
        NSString *placementType = [[NSString alloc] initWithUTF8String:newViewProps.placementType.c_str()];
        NSString *theme = [[NSString alloc] initWithUTF8String:newViewProps.theme.c_str()];
        NSString *amount = [[NSString alloc] initWithUTF8String:newViewProps.amount.c_str()];
        NSString *currency = [[NSString alloc] initWithUTF8String:newViewProps.currency.c_str()];

        [self recreatePlacementViewWithInstanceId:instanceId
                                    placementType:placementType
                                            theme:theme
                                           amount:amount
                                         currency:currency];
    }
}

#pragma mark - Placement View Management

/// Tears down any existing SDK placement view and builds a fresh one from the
/// latest props. Validates required fields (instanceId, currency, amount), resolves
/// the Klarna instance from the core module, then creates and attaches the new
/// view with an active height observer.
- (void)recreatePlacementViewWithInstanceId:(NSString *)instanceId
                              placementType:(NSString *)placementType
                                      theme:(NSString *)theme
                                     amount:(NSString *)amount
                                   currency:(NSString *)currency {
    if (!instanceId || instanceId.length == 0) {
        return;
    }
    if (!currency || currency.length == 0) {
        return;
    }

    NSNumber *parsedAmountNumber = [KlarnaMessagingPlacementViewHelper parseAmount:amount];
    if (!parsedAmountNumber) {
        return;
    }

    [self.heightObserver stopObserving];
    [self.placementView removeFromSuperview];
    self.placementView = nil;

    id klarnaInstance = [KlarnaMessagingPlacementViewFactory resolveKlarnaWithInstanceId:instanceId];
    if (!klarnaInstance) {
        [self emitErrorWithName:@"NotInitialized"
                        message:@"No Klarna instance found for instanceId. Call Klarna.initialize() first."];
        return;
    }

    long long parsedAmount = parsedAmountNumber.longLongValue;
    UIView *view = [KlarnaMessagingPlacementViewFactory
                    createPlacementViewWithKlarna:klarnaInstance
                    placementType:placementType ?: KlarnaMessagingPlacementViewFactory.defaultPlacementType
                    theme:theme ?: @""
                    amount:parsedAmount
                    currency:currency];

    if (!view) {
        [self emitErrorWithName:@"CreateFailed"
                        message:@"Failed to create KlarnaMessagingPlacementView."];
        return;
    }

    self.placementView = view;
    [KlarnaMessagingPlacementViewHelper addPlacementSubview:view to:self pinWidth:NO];
    [self.heightObserver observe:view];
}

#pragma mark - Event Emission

/// Emits an `onError` Fabric event carrying a `{name, message}` error payload.
- (void)emitErrorWithName:(NSString *)name message:(NSString *)message {
    if (_eventEmitter) {
        auto emitter = std::dynamic_pointer_cast<const RNKlarnaMessagingPlacementViewEventEmitter>(_eventEmitter);
        if (emitter) {
            emitter->onError(RNKlarnaMessagingPlacementViewEventEmitter::OnError{
                .error = {
                    .name = std::string([name UTF8String]),
                    .message = std::string([message UTF8String]),
                }
            });
        }
    }
}

#pragma mark - KlarnaMessagingHeightDelegate

/// Called by `KlarnaMessagingHeightObserver` when the placement content height
/// changes. Emits an `onResized` Fabric event so the JS side can adjust layout.
- (void)placementViewDidUpdateHeight:(CGFloat)height {
    if (_eventEmitter) {
        auto emitter = std::dynamic_pointer_cast<const RNKlarnaMessagingPlacementViewEventEmitter>(_eventEmitter);
        if (emitter) {
            emitter->onResized(RNKlarnaMessagingPlacementViewEventEmitter::OnResized{
                .height = std::string([[[NSNumber numberWithFloat:height] stringValue] UTF8String]),
            });
        }
    }
}

#pragma mark - Layout

- (void)layoutSubviews {
    [super layoutSubviews];
    if (self.placementView) {
        [KlarnaMessagingPlacementViewFactory clearBackgrounds:self.placementView];
    }
    [self.heightObserver checkAndReportHeight];
}

@end

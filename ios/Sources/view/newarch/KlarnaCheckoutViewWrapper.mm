#if RCT_NEW_ARCH_ENABLED

#import <AVFoundation/AVFoundation.h>
#import <KlarnaMobileSDK/KlarnaMobileSDK-Swift.h>
#import <React/RCTLog.h>
#import <react/renderer/components/RNKlarnaMobileSDK/ComponentDescriptors.h>
#import <react/renderer/components/RNKlarnaMobileSDK/EventEmitters.h>
#import <react/renderer/components/RNKlarnaMobileSDK/Props.h>
#import <react/renderer/components/RNKlarnaMobileSDK/RCTComponentViewHelpers.h>
#import "../KlarnaCheckoutViewWrapper.h"
#import "../../common/RNMobileSDKUtils.h"
#import "RCTFabricComponentsPlugins.h"

using namespace facebook::react;

@interface KlarnaCheckoutViewWrapper () <KlarnaEventHandler, KlarnaSizingDelegate, RCTRNKlarnaCheckoutViewViewProtocol>

@property (nonatomic, strong) KlarnaCheckoutView* klarnaCheckoutView;
@property (nonatomic, assign) BOOL isCheckoutViewReadyEventSent;

@end

@implementation KlarnaCheckoutViewWrapper

#pragma mark - Initialization

- (instancetype)initWithFrame:(CGRect)frame
{
    self = [super initWithFrame:frame];
    if (self) {
        self.isCheckoutViewReadyEventSent = NO;
        static const auto defaultProps = std::make_shared<const RNKlarnaCheckoutViewProps>();
        _props = defaultProps;
    }
    
    return self;
}

+ (ComponentDescriptorProvider)componentDescriptorProvider
{
    return concreteComponentDescriptorProvider<RNKlarnaCheckoutViewComponentDescriptor>();
}

Class<RCTComponentViewProtocol> RNKlarnaCheckoutViewCls(void)
{
    return KlarnaCheckoutViewWrapper.class;
}

- (void)handleCommand:(const NSString *)commandName args:(const NSArray *)args {
    RCTRNKlarnaCheckoutViewHandleCommand(self, commandName, args);
}

#pragma mark - KlarnaCheckoutView Props Update

- (void)updateProps:(const facebook::react::Props::Shared &)props oldProps:(const facebook::react::Props::Shared &)oldProps {
    const auto &oldViewProps = *std::static_pointer_cast<RNKlarnaCheckoutViewProps const>(_props);
    const auto &newViewProps = *std::static_pointer_cast<RNKlarnaCheckoutViewProps const>(props);
    
    if (oldViewProps.returnUrl != newViewProps.returnUrl) {
        NSString * newReturnUrl = [[NSString alloc] initWithUTF8String: newViewProps.returnUrl.c_str()];
        if (self.klarnaCheckoutView != nil) {
            self.klarnaCheckoutView.returnURL = [NSURL URLWithString:newReturnUrl];
        } else {
            [self initializeKlarnaCheckoutView:newReturnUrl];
        }
    }
    
    [super updateProps:props oldProps:oldProps];
}

- (void)updateEventEmitter:(const facebook::react::EventEmitter::Shared &)eventEmitter
{
    [super updateEventEmitter:eventEmitter];

    if (!self.isCheckoutViewReadyEventSent && self.klarnaCheckoutView != nil && _eventEmitter) {
        RCTLogInfo(@"Sending onCheckoutViewReady event.");
        std::dynamic_pointer_cast<const RNKlarnaCheckoutViewEventEmitter>(_eventEmitter)
            ->onCheckoutViewReady({});
        self.isCheckoutViewReadyEventSent = YES;
    } else {
        RCTLogInfo(@"Could not send onCheckoutViewReady event.");
    }
}

#pragma mark - KlarnaCheckoutView Setup

- (void)initializeKlarnaCheckoutView:(NSString*)returnUrl {
    self.isCheckoutViewReadyEventSent = NO;
    self.klarnaCheckoutView = [[KlarnaCheckoutView alloc] initWithReturnURL:[NSURL URLWithString:returnUrl] eventHandler:self];
    self.klarnaCheckoutView.sizingDelegate = self;
    
    self.klarnaCheckoutView.translatesAutoresizingMaskIntoConstraints = NO;
    
    [self addSubview:self.klarnaCheckoutView];

    [NSLayoutConstraint activateConstraints:[[NSArray alloc] initWithObjects:
                                             [self.klarnaCheckoutView.topAnchor constraintEqualToAnchor:self.topAnchor],
                                             [self.klarnaCheckoutView.bottomAnchor constraintEqualToAnchor:self.bottomAnchor],
                                             [self.klarnaCheckoutView.leadingAnchor constraintEqualToAnchor:self.leadingAnchor],
                                             [self.klarnaCheckoutView.trailingAnchor constraintEqualToAnchor:self.trailingAnchor], nil
                                            ]];
}

#pragma mark - Methods Exposed to React Native

- (void)setSnippet:(NSString *)snippet {
    [self.klarnaCheckoutView setSnippet:snippet];
}

- (void)suspend {
    [self.klarnaCheckoutView suspend];
}

- (void)resume {
    [self.klarnaCheckoutView resume];
}

#pragma mark - UIView Lifecycle

- (void)didMoveToWindow {
    [super didMoveToWindow];
    if (!self.window) {
        self.isCheckoutViewReadyEventSent = NO;
    }
}

#pragma mark - KlarnaEventHandler

- (void)klarnaComponent:(id<KlarnaComponent>)klarnaComponent dispatchedEvent:(KlarnaProductEvent *)event {
    if (_eventEmitter) {
        RCTLogInfo(@"Sending onEvent event");
        NSString *serializedParams = [SerializationUtil serializeDictionaryToJsonString:[event getParams]];
        std::dynamic_pointer_cast<const RNKlarnaCheckoutViewEventEmitter>(_eventEmitter)
        ->onEvent(RNKlarnaCheckoutViewEventEmitter::OnEvent{
            .productEvent = {
                .action = std::string([event.action UTF8String]),
                .params = std::string([serializedParams UTF8String]),
            }
        });
    } else {
        RCTLogInfo(@"Could not send onEvent event. _eventEmitter is nil!");
    }
}

- (void)klarnaComponent:(id<KlarnaComponent>)klarnaComponent encounteredError:(KlarnaError *)error {
    if (_eventEmitter) {
        RCTLogInfo(@"Sending onError event");
        std::dynamic_pointer_cast<const RNKlarnaCheckoutViewEventEmitter>(_eventEmitter)
        ->onError(RNKlarnaCheckoutViewEventEmitter::OnError{
            .error = {
                .name = std::string([error.name UTF8String]),
                .message = std::string([error.message UTF8String]),
                .isFatal = error.isFatal,
            }
        });
    } else {
        RCTLogInfo(@"Could not send onError event. _eventEmitter is nil!");
    }
}

#pragma mark - KlarnaSizingDelegate

- (void)klarnaComponent:(id<KlarnaComponent>)klarnaComponent resizedToHeight:(CGFloat)height {
    if (_eventEmitter) {
        RCTLogInfo(@"Sending onResized event");
        std::dynamic_pointer_cast<const RNKlarnaCheckoutViewEventEmitter>(_eventEmitter)
        ->onResized(RNKlarnaCheckoutViewEventEmitter::OnResized{
            .height = std::string([[[NSNumber numberWithFloat:height] stringValue] UTF8String]),
        });
    } else {
        RCTLogInfo(@"Could not send onResized event. _eventEmitter is nil!");
    }
}

@end

#endif

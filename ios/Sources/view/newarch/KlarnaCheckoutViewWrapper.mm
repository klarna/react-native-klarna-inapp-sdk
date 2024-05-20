#if RCT_NEW_ARCH_ENABLED

#import "KlarnaCheckoutViewWrapper.h"
#import "../../common/RNMobileSDKUtils.h"

#import <AVFoundation/AVFoundation.h>
#import <KlarnaMobileSDK/KlarnaMobileSDK-Swift.h>
#import <React/RCTLog.h>

#import <react/renderer/components/RNKlarnaMobileSDK/ComponentDescriptors.h>
#import <react/renderer/components/RNKlarnaMobileSDK/EventEmitters.h>
#import <react/renderer/components/RNKlarnaMobileSDK/Props.h>
#import <react/renderer/components/RNKlarnaMobileSDK/RCTComponentViewHelpers.h>

#import "RCTFabricComponentsPlugins.h"

using namespace facebook::react;

@interface KlarnaCheckoutViewWrapper () <KlarnaEventHandler, KlarnaSizingDelegate, RCTRNKlarnaCheckoutViewViewProtocol>

@property (nonatomic, strong) KlarnaCheckoutView* actualCheckoutView;

@end

@implementation KlarnaCheckoutViewWrapper

#pragma mark - RN Klarna Checout View

- (instancetype)initWithFrame:(CGRect)frame
{
    self = [super initWithFrame:frame];
    if (self) {
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

#pragma mark - RN Klarna Checout View Props

- (void)updateProps:(const facebook::react::Props::Shared &)props oldProps:(const facebook::react::Props::Shared &)oldProps {
    const auto &oldViewProps = *std::static_pointer_cast<RNKlarnaCheckoutViewProps const>(_props);
    const auto &newViewProps = *std::static_pointer_cast<RNKlarnaCheckoutViewProps const>(props);
    
    if (oldViewProps.returnUrl != newViewProps.returnUrl) {
        NSString * newReturnUrl = [[NSString alloc] initWithUTF8String: newViewProps.returnUrl.c_str()];
        if (self.actualCheckoutView != nil) {
            self.actualCheckoutView.returnURL = [NSURL URLWithString:newReturnUrl];
        } else {
            
        }
    }
    
    [super updateProps:props oldProps:oldProps];
}


#pragma mark - RN Klarna Checout View Methods

- (void)initializeActualCheckoutView:(NSString*)returnUrl {
    self.actualCheckoutView = [[KlarnaCheckoutView alloc] initWithReturnURL:[NSURL URLWithString:returnUrl] eventHandler:self];
    self.actualCheckoutView.sizingDelegate = self;
    
    self.actualCheckoutView.translatesAutoresizingMaskIntoConstraints = NO;
    
    [self addSubview:self.actualCheckoutView];

    [NSLayoutConstraint activateConstraints:[[NSArray alloc] initWithObjects:
                                             [self.actualCheckoutView.topAnchor constraintEqualToAnchor:self.topAnchor],
                                             [self.actualCheckoutView.bottomAnchor constraintEqualToAnchor:self.bottomAnchor],
                                             [self.actualCheckoutView.leadingAnchor constraintEqualToAnchor:self.leadingAnchor],
                                             [self.actualCheckoutView.trailingAnchor constraintEqualToAnchor:self.trailingAnchor], nil
                                            ]];
}

- (void)setSnippet:(NSString *)snippet {
    [self.actualCheckoutView setSnippet:snippet];
}

- (void)suspend {
    [self.actualCheckoutView suspend];
}

- (void)resume {
    [self.actualCheckoutView resume];
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
        RCTLogInfo(@"_eventEmitter is nil!");
    }
}

- (void)klarnaComponent:(id<KlarnaComponent>)klarnaComponent encounteredError:(KlarnaError *)error {
    if (_eventEmitter) {
        RCTLogInfo(@"Sending onEvent event");
        std::dynamic_pointer_cast<const RNKlarnaCheckoutViewEventEmitter>(_eventEmitter)
        ->onError(RNKlarnaCheckoutViewEventEmitter::OnError{
            .error = {
                .name = std::string([error.name UTF8String]),
                .message = std::string([error.message UTF8String]),
                .isFatal = error.isFatal,
            }
        });
    } else {
        RCTLogInfo(@"_eventEmitter is nil!");
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
        RCTLogInfo(@"_eventEmitter is nil!");
    }
}

@end

#endif

#if RCT_NEW_ARCH_ENABLED

#import <AVFoundation/AVFoundation.h>
#import <react/renderer/components/RNKlarnaMobileSDK/ComponentDescriptors.h>
#import <react/renderer/components/RNKlarnaMobileSDK/EventEmitters.h>
#import <react/renderer/components/RNKlarnaMobileSDK/Props.h>
#import <react/renderer/components/RNKlarnaMobileSDK/RCTComponentViewHelpers.h>
#import "../KlarnaOSMViewWrapper.h"
#import "../../common/RNMobileSDKUtils.h"
#import "RCTFabricComponentsPlugins.h"
#import <KlarnaMobileSDK/KlarnaMobileSDK.h>
#import <KlarnaMobileSDK/KlarnaMobileSDK-Swift.h>

using namespace facebook::react;

@interface KlarnaOSMViewWrapper () <KlarnaEventHandler, KlarnaSizingDelegate, RCTRNKlarnaOSMViewViewProtocol>

@property (nonatomic, strong) KlarnaOSMView* klarnaOSMView;
@property (nonatomic, assign) BOOL isOSMViewReadyEventSent;

@end

@implementation KlarnaOSMViewWrapper

#pragma mark - Initialization

- (instancetype)initWithFrame:(CGRect)frame
{
    self = [super initWithFrame:frame];
    if (self) {
        self.isOSMViewReadyEventSent = NO;
        self.backgroundColor = [UIColor clearColor];
        static const auto defaultProps = std::make_shared<const RNKlarnaOSMViewProps>();
        _props = defaultProps;
        [self initializeKlarnaOSMView];
    }

    return self;
}

+ (ComponentDescriptorProvider)componentDescriptorProvider
{
    return concreteComponentDescriptorProvider<RNKlarnaOSMViewComponentDescriptor>();
}

Class<RCTComponentViewProtocol> RNKlarnaOSMViewCls(void)
{
    return KlarnaOSMViewWrapper.class;
}

- (void)handleCommand:(const NSString *)commandName args:(const NSArray *)args {
    RCTRNKlarnaOSMViewHandleCommand(self, commandName, args);
}

#pragma mark - KlarnaOSMView Props Update

- (void)updateProps:(const facebook::react::Props::Shared &)props oldProps:(const facebook::react::Props::Shared &)oldProps {
    const auto &oldViewProps = *std::static_pointer_cast<RNKlarnaOSMViewProps const>(_props);
    const auto &newViewProps = *std::static_pointer_cast<RNKlarnaOSMViewProps const>(props);

    if (oldViewProps.clientId != newViewProps.clientId) {
        NSString *value = [[NSString alloc] initWithUTF8String:newViewProps.clientId.c_str()];
        self.klarnaOSMView.clientId = value;
    }

    if (oldViewProps.placementKey != newViewProps.placementKey) {
        NSString *value = [[NSString alloc] initWithUTF8String:newViewProps.placementKey.c_str()];
        self.klarnaOSMView.placementKey = value;
    }

    if (oldViewProps.locale != newViewProps.locale) {
        NSString *value = [[NSString alloc] initWithUTF8String:newViewProps.locale.c_str()];
        if (value.length > 0) {
            self.klarnaOSMView.locale = value;
        }
    }

    if (oldViewProps.purchaseAmount != newViewProps.purchaseAmount) {
        NSString *value = [[NSString alloc] initWithUTF8String:newViewProps.purchaseAmount.c_str()];
        if (value.length > 0) {
            NSNumberFormatter *formatter = [[NSNumberFormatter alloc] init];
            formatter.numberStyle = NSNumberFormatterDecimalStyle;
            NSNumber *amount = [formatter numberFromString:value];
            if (amount != nil) {
                [self.klarnaOSMView setPurchaseAmount:amount];
            }
        }
    }

    if (oldViewProps.environment != newViewProps.environment) {
        NSString *value = [[NSString alloc] initWithUTF8String:newViewProps.environment.c_str()];
        if (value.length > 0) {
            if ([value isEqualToString:@"playground"]) {
                self.klarnaOSMView.environment = KlarnaEnvironment.playground;
            } else if ([value isEqualToString:@"production"]) {
                self.klarnaOSMView.environment = KlarnaEnvironment.production;
            } else if ([value isEqualToString:@"staging"]) {
                self.klarnaOSMView.environment = KlarnaEnvironment.staging;
            }
        }
    }

    if (oldViewProps.region != newViewProps.region) {
        NSString *value = [[NSString alloc] initWithUTF8String:newViewProps.region.c_str()];
        if (value.length > 0) {
            if ([value isEqualToString:@"eu"]) {
                self.klarnaOSMView.region = KlarnaRegion.eu;
            } else if ([value isEqualToString:@"na"]) {
                self.klarnaOSMView.region = KlarnaRegion.na;
            } else if ([value isEqualToString:@"oc"]) {
                self.klarnaOSMView.region = KlarnaRegion.oc;
            }
        }
    }

    if (oldViewProps.theme != newViewProps.theme) {
        NSString *value = [[NSString alloc] initWithUTF8String:newViewProps.theme.c_str()];
        if (value.length > 0) {
            if ([value isEqualToString:@"light"]) {
                self.klarnaOSMView.theme = KlarnaThemeLight;
            } else if ([value isEqualToString:@"dark"]) {
                self.klarnaOSMView.theme = KlarnaThemeDark;
            } else if ([value isEqualToString:@"automatic"]) {
                self.klarnaOSMView.theme = KlarnaThemeAutomatic;
            }
        }
    }

    BOOL styleChanged = NO;
    if (oldViewProps.backgroundColor != newViewProps.backgroundColor) {
        styleChanged = YES;
    }
    if (oldViewProps.textColor != newViewProps.textColor) {
        styleChanged = YES;
    }
    if (styleChanged) {
        NSString *bgColorStr = [[NSString alloc] initWithUTF8String:newViewProps.backgroundColor.c_str()];
        NSString *txtColorStr = [[NSString alloc] initWithUTF8String:newViewProps.textColor.c_str()];
        [self updateStyleConfigurationWithBackgroundColor:bgColorStr textColor:txtColorStr];
    }

    [super updateProps:props oldProps:oldProps];
}

- (void)updateEventEmitter:(const facebook::react::EventEmitter::Shared &)eventEmitter
{
    [super updateEventEmitter:eventEmitter];

    if (!self.isOSMViewReadyEventSent && self.klarnaOSMView != nil && _eventEmitter) {
        auto emitter = std::dynamic_pointer_cast<const RNKlarnaOSMViewEventEmitter>(_eventEmitter);
        if (emitter) {
            emitter->onOSMViewReady({});
            self.isOSMViewReadyEventSent = YES;
        }
    }
}

#pragma mark - KlarnaOSMView Setup

- (void)initializeKlarnaOSMView {
    self.isOSMViewReadyEventSent = NO;
    self.klarnaOSMView = [[KlarnaOSMView alloc] init];
    self.klarnaOSMView.eventHandler = self;
    self.klarnaOSMView.sizingDelegate = self;
    self.klarnaOSMView.backgroundColor = [UIColor clearColor];

    self.klarnaOSMView.translatesAutoresizingMaskIntoConstraints = NO;

    [self addSubview:self.klarnaOSMView];

    [NSLayoutConstraint activateConstraints:[[NSArray alloc] initWithObjects:
                                             [self.klarnaOSMView.topAnchor constraintEqualToAnchor:self.topAnchor],
                                             [self.klarnaOSMView.bottomAnchor constraintEqualToAnchor:self.bottomAnchor],
                                             [self.klarnaOSMView.leadingAnchor constraintEqualToAnchor:self.leadingAnchor],
                                             [self.klarnaOSMView.trailingAnchor constraintEqualToAnchor:self.trailingAnchor], nil
                                            ]];
}

#pragma mark - Style Configuration

- (void)updateStyleConfigurationWithBackgroundColor:(NSString *)bgColorStr textColor:(NSString *)txtColorStr {
    UIColor *bgColor = [self colorFromHexString:bgColorStr];
    UIColor *txtColor = [self colorFromHexString:txtColorStr];

    if (bgColor || txtColor) {
        KlarnaOSMStyleBuilder *builder = [[KlarnaOSMStyleBuilder alloc] init];
        if (bgColor) {
            [builder setBackgroundColor:bgColor];
        }
        if (txtColor) {
            KlarnaTextStyleBuilder *textStyleBuilder = [[KlarnaTextStyleBuilder alloc] init];
            [textStyleBuilder setTextColor:txtColor];
            [builder setTextStyleConfiguration:[textStyleBuilder build]];
        }
        self.klarnaOSMView.styleConfiguration = [builder build];
    } else {
        self.klarnaOSMView.styleConfiguration = nil;
    }
}

- (UIColor *)colorFromHexString:(NSString *)hexString {
    if (!hexString || hexString.length == 0) {
        return nil;
    }

    NSString *cleanString = [hexString stringByReplacingOccurrencesOfString:@"#" withString:@""];
    if (cleanString.length != 6 && cleanString.length != 8) {
        return nil;
    }

    unsigned int rgbValue = 0;
    NSScanner *scanner = [NSScanner scannerWithString:cleanString];
    [scanner scanHexInt:&rgbValue];

    if (cleanString.length == 8) {
        return [UIColor colorWithRed:((rgbValue & 0xFF000000) >> 24) / 255.0
                               green:((rgbValue & 0x00FF0000) >> 16) / 255.0
                                blue:((rgbValue & 0x0000FF00) >> 8) / 255.0
                               alpha:(rgbValue & 0x000000FF) / 255.0];
    }

    return [UIColor colorWithRed:((rgbValue & 0xFF0000) >> 16) / 255.0
                           green:((rgbValue & 0x00FF00) >> 8) / 255.0
                            blue:(rgbValue & 0x0000FF) / 255.0
                           alpha:1.0];
}

#pragma mark - Methods Exposed to React Native

- (void)render {
    UIViewController *hostVC = [self reactViewController];
    if (hostVC) {
        self.klarnaOSMView.hostViewController = hostVC;
    }
    [self.klarnaOSMView render];
}

- (UIViewController *)reactViewController {
    UIResponder *responder = self;
    while (responder) {
        if ([responder isKindOfClass:[UIViewController class]]) {
            return (UIViewController *)responder;
        }
        responder = [responder nextResponder];
    }
    return nil;
}

#pragma mark - UIView Lifecycle

- (void)didMoveToWindow {
    [super didMoveToWindow];
    if (!self.window) {
        self.isOSMViewReadyEventSent = NO;
    }
}

#pragma mark - KlarnaEventHandler

- (void)klarnaComponent:(id<KlarnaComponent>)klarnaComponent dispatchedEvent:(KlarnaProductEvent *)event {
    // OSM view does not emit product events
}

- (void)klarnaComponent:(id<KlarnaComponent>)klarnaComponent encounteredError:(KlarnaError *)error {
    if (_eventEmitter) {
        auto emitter = std::dynamic_pointer_cast<const RNKlarnaOSMViewEventEmitter>(_eventEmitter);
        if (emitter) {
            emitter->onError(RNKlarnaOSMViewEventEmitter::OnError{
                .error = {
                    .name = std::string([error.name UTF8String]),
                    .message = std::string([error.message UTF8String]),
                    .isFatal = error.isFatal,
                }
            });
        }
    }
}

#pragma mark - KlarnaSizingDelegate

- (void)klarnaComponent:(id<KlarnaComponent>)klarnaComponent resizedToHeight:(CGFloat)height {
    if (_eventEmitter) {
        auto emitter = std::dynamic_pointer_cast<const RNKlarnaOSMViewEventEmitter>(_eventEmitter);
        if (emitter) {
            emitter->onResized(RNKlarnaOSMViewEventEmitter::OnResized{
                .height = std::string([[[NSNumber numberWithFloat:height] stringValue] UTF8String]),
            });
        }
    }
}

@end

#endif

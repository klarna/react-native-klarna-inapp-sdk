#if !RCT_NEW_ARCH_ENABLED

#import <React/RCTLog.h>
#import "../KlarnaOSMViewWrapper.h"
#import "../../common/RNMobileSDKUtils.h"
#import <KlarnaMobileSDK/KlarnaMobileSDK.h>
#import <KlarnaMobileSDK/KlarnaMobileSDK-Swift.h>

@interface KlarnaOSMViewWrapper () <KlarnaEventHandler, KlarnaSizingDelegate>

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
        [self initializeActualOSMView];
    }
    return self;
}

#pragma mark - React Native Property Setters

- (void)setClientId:(NSString *)clientId {
    _clientId = clientId;
    self.klarnaOSMView.clientId = clientId;
}

- (void)setPlacementKey:(NSString *)placementKey {
    _placementKey = placementKey;
    self.klarnaOSMView.placementKey = placementKey;
}

- (void)setLocale:(NSString *)locale {
    _locale = locale;
    if (locale.length > 0) {
        self.klarnaOSMView.locale = locale;
    }
}

- (void)setPurchaseAmount:(NSString *)purchaseAmount {
    _purchaseAmount = purchaseAmount;
    if (purchaseAmount.length > 0) {
        NSNumberFormatter *formatter = [[NSNumberFormatter alloc] init];
        formatter.numberStyle = NSNumberFormatterDecimalStyle;
        NSNumber *amount = [formatter numberFromString:purchaseAmount];
        if (amount != nil) {
            [self.klarnaOSMView setPurchaseAmount:amount];
        }
    }
}

- (void)setEnvironment:(NSString *)environment {
    _environment = environment;
    if (environment.length > 0) {
        if ([environment isEqualToString:@"playground"]) {
            self.klarnaOSMView.environment = KlarnaEnvironment.playground;
        } else if ([environment isEqualToString:@"production"]) {
            self.klarnaOSMView.environment = KlarnaEnvironment.production;
        } else if ([environment isEqualToString:@"staging"]) {
            self.klarnaOSMView.environment = KlarnaEnvironment.staging;
        }
    }
}

- (void)setRegion:(NSString *)region {
    _region = region;
    if (region.length > 0) {
        if ([region isEqualToString:@"eu"]) {
            self.klarnaOSMView.region = KlarnaRegion.eu;
        } else if ([region isEqualToString:@"na"]) {
            self.klarnaOSMView.region = KlarnaRegion.na;
        } else if ([region isEqualToString:@"oc"]) {
            self.klarnaOSMView.region = KlarnaRegion.oc;
        }
    }
}

- (void)setTheme:(NSString *)theme {
    _theme = theme;
    if (theme.length > 0) {
        if ([theme isEqualToString:@"light"]) {
            self.klarnaOSMView.theme = KlarnaThemeLight;
        } else if ([theme isEqualToString:@"dark"]) {
            self.klarnaOSMView.theme = KlarnaThemeDark;
        } else if ([theme isEqualToString:@"automatic"]) {
            self.klarnaOSMView.theme = KlarnaThemeAutomatic;
        }
    }
}

- (void)setOsmBackgroundColor:(NSString *)osmBackgroundColor {
    _osmBackgroundColor = osmBackgroundColor;
    [self updateStyleConfiguration];
}

- (void)setTextColor:(NSString *)textColor {
    _textColor = textColor;
    [self updateStyleConfiguration];
}

- (void)setReturnUrl:(NSString *)returnUrl {
    // KlarnaOSMView ignores the return URL set by merchant and uses an internally created return URL.
    _returnUrl = returnUrl;
}

#pragma mark - Style Configuration

- (void)updateStyleConfiguration {
    UIColor *bgColor = [self colorFromHexString:self.osmBackgroundColor];
    UIColor *txtColor = [self colorFromHexString:self.textColor];

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

#pragma mark - KlarnaOSMView Setup

- (void)initializeActualOSMView {
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

#pragma mark - Layout

- (void)layoutSubviews {
    [super layoutSubviews];
    self.klarnaOSMView.frame = self.bounds;
}

#pragma mark - UIView Lifecycle

- (void)didMoveToWindow {
    [super didMoveToWindow];
    if (!self.window) {
        self.isOSMViewReadyEventSent = NO;
    }
}

#pragma mark - React Native Lifecycle

- (void)didSetProps:(NSArray<NSString *> *)changedProps {
    [super didSetProps:changedProps];
    [self sendOSMViewReadyEvent];
}

- (void)sendOSMViewReadyEvent {
    if (self.klarnaOSMView && self.onOSMViewReady && !self.isOSMViewReadyEventSent) {
        self.isOSMViewReadyEventSent = YES;
        self.onOSMViewReady(@{});
    }
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

#pragma mark - KlarnaEventHandler

- (void)klarnaComponent:(id<KlarnaComponent>)klarnaComponent dispatchedEvent:(KlarnaProductEvent *)event {
    // OSM view does not emit product events, but handle for completeness
}

- (void)klarnaComponent:(id<KlarnaComponent>)klarnaComponent encounteredError:(KlarnaError *)error {
    if (!self.onError) {
        RCTLog(@"Missing 'onError' callback prop.");
        return;
    }

    self.onError(@{
        @"error": @{
            @"name": error.name,
            @"message": error.message,
            @"isFatal": [NSNumber numberWithBool:error.isFatal],
        }
    });
}

#pragma mark - KlarnaSizingDelegate

- (void)klarnaComponent:(id<KlarnaComponent>)klarnaComponent resizedToHeight:(CGFloat)height {
    if (!self.onResized) {
        RCTLog(@"Missing 'onResized' callback prop.");
        return;
    }

    self.onResized(@{
        @"height": [[NSNumber numberWithFloat:height] stringValue]
    });
}

@end

#endif

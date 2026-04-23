#if !RCT_NEW_ARCH_ENABLED

#import <React/RCTLog.h>
#import "../KlarnaExpressCheckoutViewWrapper.h"
#import <KlarnaMobileSDK/KlarnaMobileSDK.h>
#import <KlarnaMobileSDK/KlarnaMobileSDK-Swift.h>

#if __has_include("react_native_klarna_inapp_sdk-Swift.h")
#import "react_native_klarna_inapp_sdk-Swift.h"
#elif __has_include("RNKlarnaMobileSDK-Swift.h")
#import "RNKlarnaMobileSDK-Swift.h"
#else
#import <RNKlarnaMobileSDK/RNKlarnaMobileSDK-Swift.h>
#endif

@interface KlarnaExpressCheckoutViewWrapper ()

@property (nonatomic, strong) KlarnaExpressCheckoutButton* expressCheckoutButton;
@property (nonatomic, strong) KlarnaExpressCheckoutButtonDelegateProxy* delegateProxy;
@property (nonatomic, assign) BOOL isButtonCreated;

@end

@implementation KlarnaExpressCheckoutViewWrapper

#pragma mark - Initialization

- (instancetype)initWithFrame:(CGRect)frame
{
    self = [super initWithFrame:frame];
    if (self) {
        self.backgroundColor = [UIColor clearColor];
        self.isButtonCreated = NO;
        self.autoFinalize = YES;
        self.collectShippingAddress = NO;
        [self setupDelegateProxy];
    }
    return self;
}

- (void)setupDelegateProxy {
    self.delegateProxy = [[KlarnaExpressCheckoutButtonDelegateProxy alloc] init];

    __weak __typeof(self) weakSelf = self;

    self.delegateProxy.onAuthorizedHandler = ^(NSDictionary * _Nonnull response) {
        __strong __typeof(weakSelf) strongSelf = weakSelf;
        if (!strongSelf || !strongSelf.onAuthorized) {
            return;
        }
        strongSelf.onAuthorized(@{
            @"authorizationResponse": response
        });
    };

    self.delegateProxy.onErrorHandler = ^(NSString * _Nonnull name, NSString * _Nonnull message, BOOL isFatal) {
        __strong __typeof(weakSelf) strongSelf = weakSelf;
        if (!strongSelf || !strongSelf.onError) {
            return;
        }
        strongSelf.onError(@{
            @"error": @{
                @"name": name,
                @"message": message,
                @"isFatal": @(isFatal),
            }
        });
    };
}

#pragma mark - React Native Property Setters

- (void)setSessionType:(NSString *)sessionType {
    _sessionType = sessionType;
}

- (void)setClientId:(NSString *)clientId {
    _clientId = clientId;
}

- (void)setClientToken:(NSString *)clientToken {
    _clientToken = clientToken;
}

- (void)setLocale:(NSString *)locale {
    _locale = locale;
}

- (void)setEnvironment:(NSString *)environment {
    _environment = environment;
}

- (void)setRegion:(NSString *)region {
    _region = region;
}

- (void)setReturnUrl:(NSString *)returnUrl {
    _returnUrl = returnUrl;
}

- (void)setTheme:(NSString *)theme {
    _theme = theme;
}

- (void)setShape:(NSString *)shape {
    _shape = shape;
}

- (void)setButtonStyle:(NSString *)buttonStyle {
    _buttonStyle = buttonStyle;
}

- (void)setAutoFinalize:(BOOL)autoFinalize {
    _autoFinalize = autoFinalize;
}

- (void)setCollectShippingAddress:(BOOL)collectShippingAddress {
    _collectShippingAddress = collectShippingAddress;
}

- (void)setSessionData:(NSString *)sessionData {
    _sessionData = sessionData;
}

#pragma mark - React Native Lifecycle

/**
 * Called by React Native after a batch of prop updates is applied.
 *
 * The SDK button is configured entirely through its constructor, so any prop
 * change recreates the button. The delegate proxy is reused across instances.
 */
- (void)didSetProps:(NSArray<NSString *> *)changedProps {
    [super didSetProps:changedProps];
    if (self.isButtonCreated) {
        [self resetButton];
    }
    [self createButtonIfNeeded];
}

#pragma mark - Button Creation

- (void)resetButton {
    if (self.expressCheckoutButton) {
        [self.expressCheckoutButton removeFromSuperview];
        self.expressCheckoutButton = nil;
    }
    self.isButtonCreated = NO;
}

- (void)createButtonIfNeeded {
    if (self.isButtonCreated) {
        return;
    }

    KlarnaExpressCheckoutButton *button = [KlarnaExpressCheckoutHelper
        createButtonWithSessionType:self.sessionType ?: @""
        clientId:self.clientId ?: @""
        clientToken:self.clientToken ?: @""
        locale:self.locale
        returnUrl:self.returnUrl ?: @""
        delegate:self.delegateProxy
        theme:self.theme
        shape:self.shape
        buttonStyle:self.buttonStyle
        autoFinalize:self.autoFinalize
        collectShippingAddress:self.collectShippingAddress
        sessionData:self.sessionData
        environment:self.environment
        region:self.region];

    if (button) {
        self.expressCheckoutButton = button;
        self.expressCheckoutButton.translatesAutoresizingMaskIntoConstraints = NO;
        [self addSubview:self.expressCheckoutButton];

        [NSLayoutConstraint activateConstraints:@[
            [self.expressCheckoutButton.topAnchor constraintEqualToAnchor:self.topAnchor],
            [self.expressCheckoutButton.bottomAnchor constraintEqualToAnchor:self.bottomAnchor],
            [self.expressCheckoutButton.leadingAnchor constraintEqualToAnchor:self.leadingAnchor],
            [self.expressCheckoutButton.trailingAnchor constraintEqualToAnchor:self.trailingAnchor],
        ]];

        self.isButtonCreated = YES;

        // Send resize event after layout
        __weak __typeof(self) weakResizeSelf = self;
        dispatch_async(dispatch_get_main_queue(), ^{
            __strong __typeof(weakResizeSelf) strongSelf = weakResizeSelf;
            if (strongSelf && strongSelf.onResized && strongSelf.expressCheckoutButton) {
                CGFloat height = strongSelf.expressCheckoutButton.intrinsicContentSize.height;
                if (height > 0) {
                    strongSelf.onResized(@{
                        @"height": [[NSNumber numberWithFloat:height] stringValue]
                    });
                }
            }
        });
    }
}

#pragma mark - Layout

- (void)layoutSubviews {
    [super layoutSubviews];
    if (self.expressCheckoutButton) {
        self.expressCheckoutButton.frame = self.bounds;
    }
}

#pragma mark - UIView Lifecycle

- (void)didMoveToWindow {
    [super didMoveToWindow];
    if (!self.window) {
        [self resetButton];
    }
}

@end

#endif

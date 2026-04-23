#if RCT_NEW_ARCH_ENABLED

#import <AVFoundation/AVFoundation.h>
#import <React/RCTLog.h>
#import <react/renderer/components/RNKlarnaMobileSDK/ComponentDescriptors.h>
#import <react/renderer/components/RNKlarnaMobileSDK/EventEmitters.h>
#import <react/renderer/components/RNKlarnaMobileSDK/Props.h>
#import <react/renderer/components/RNKlarnaMobileSDK/RCTComponentViewHelpers.h>
#import "../KlarnaExpressCheckoutViewWrapper.h"
#import "RCTFabricComponentsPlugins.h"
#import <KlarnaMobileSDK/KlarnaMobileSDK.h>
#import <KlarnaMobileSDK/KlarnaMobileSDK-Swift.h>

#if __has_include("react_native_klarna_inapp_sdk-Swift.h")
#import "react_native_klarna_inapp_sdk-Swift.h"
#elif __has_include("RNKlarnaMobileSDK-Swift.h")
#import "RNKlarnaMobileSDK-Swift.h"
#else
#import <RNKlarnaMobileSDK/RNKlarnaMobileSDK-Swift.h>
#endif

using namespace facebook::react;

@interface KlarnaExpressCheckoutViewWrapper () <RCTRNKlarnaExpressCheckoutViewViewProtocol>

@property (nonatomic, strong) KlarnaExpressCheckoutButton* expressCheckoutButton;
@property (nonatomic, strong) KlarnaExpressCheckoutButtonDelegateProxy* delegateProxy;
@property (nonatomic, assign) BOOL isButtonCreated;

// Store props for button creation
@property (nonatomic, strong) NSString* storedSessionType;
@property (nonatomic, strong) NSString* storedClientId;
@property (nonatomic, strong) NSString* storedClientToken;
@property (nonatomic, strong) NSString* storedLocale;
@property (nonatomic, strong) NSString* storedEnvironment;
@property (nonatomic, strong) NSString* storedRegion;
@property (nonatomic, strong) NSString* storedReturnUrl;
@property (nonatomic, strong) NSString* storedTheme;
@property (nonatomic, strong) NSString* storedShape;
@property (nonatomic, strong) NSString* storedButtonStyle;
@property (nonatomic, assign) BOOL storedAutoFinalize;
@property (nonatomic, assign) BOOL storedCollectShippingAddress;
@property (nonatomic, strong) NSString* storedSessionData;

@end

@implementation KlarnaExpressCheckoutViewWrapper

#pragma mark - Initialization

- (instancetype)initWithFrame:(CGRect)frame
{
    self = [super initWithFrame:frame];
    if (self) {
        self.backgroundColor = [UIColor clearColor];
        self.isButtonCreated = NO;
        self.storedAutoFinalize = YES;
        self.storedCollectShippingAddress = NO;
        static const auto defaultProps = std::make_shared<const RNKlarnaExpressCheckoutViewProps>();
        _props = defaultProps;
        [self setupDelegateProxy];
    }

    return self;
}

+ (ComponentDescriptorProvider)componentDescriptorProvider
{
    return concreteComponentDescriptorProvider<RNKlarnaExpressCheckoutViewComponentDescriptor>();
}

Class<RCTComponentViewProtocol> RNKlarnaExpressCheckoutViewCls(void)
{
    return KlarnaExpressCheckoutViewWrapper.class;
}

- (void)setupDelegateProxy {
    self.delegateProxy = [[KlarnaExpressCheckoutButtonDelegateProxy alloc] init];

    __weak __typeof(self) weakSelf = self;

    self.delegateProxy.onAuthorizedHandler = ^(NSDictionary * _Nonnull response) {
        __strong __typeof(weakSelf) strongSelf = weakSelf;
        if (!strongSelf || !strongSelf->_eventEmitter) {
            return;
        }

        auto emitter = std::dynamic_pointer_cast<const RNKlarnaExpressCheckoutViewEventEmitter>(strongSelf->_eventEmitter);
        if (emitter) {
            emitter->onAuthorized(RNKlarnaExpressCheckoutViewEventEmitter::OnAuthorized{
                .authorizationResponse = {
                    .showForm = [[response objectForKey:@"showForm"] boolValue],
                    .approved = [[response objectForKey:@"approved"] boolValue],
                    .finalizedRequired = [[response objectForKey:@"finalizedRequired"] boolValue],
                    .clientToken = std::string([([response objectForKey:@"clientToken"] ?: @"") UTF8String]),
                    .authorizationToken = std::string([([response objectForKey:@"authorizationToken"] ?: @"") UTF8String]),
                    .sessionId = std::string([([response objectForKey:@"sessionId"] ?: @"") UTF8String]),
                    .collectedShippingAddress = std::string([([response objectForKey:@"collectedShippingAddress"] ?: @"") UTF8String]),
                    .merchantReference1 = std::string([([response objectForKey:@"merchantReference1"] ?: @"") UTF8String]),
                    .merchantReference2 = std::string([([response objectForKey:@"merchantReference2"] ?: @"") UTF8String]),
                }
            });
        }
    };

    self.delegateProxy.onErrorHandler = ^(NSString * _Nonnull name, NSString * _Nonnull message, BOOL isFatal) {
        __strong __typeof(weakSelf) strongSelf = weakSelf;
        if (!strongSelf || !strongSelf->_eventEmitter) {
            return;
        }

        auto emitter = std::dynamic_pointer_cast<const RNKlarnaExpressCheckoutViewEventEmitter>(strongSelf->_eventEmitter);
        if (emitter) {
            emitter->onError(RNKlarnaExpressCheckoutViewEventEmitter::OnError{
                .error = {
                    .name = std::string([name UTF8String]),
                    .message = std::string([message UTF8String]),
                    .isFatal = isFatal,
                }
            });
        }
    };
}

#pragma mark - Props Update

- (void)updateProps:(const facebook::react::Props::Shared &)props oldProps:(const facebook::react::Props::Shared &)oldProps {
    const auto &newViewProps = *std::static_pointer_cast<RNKlarnaExpressCheckoutViewProps const>(props);

    self.storedSessionType = [[NSString alloc] initWithUTF8String:newViewProps.sessionType.c_str()];
    self.storedClientId = [[NSString alloc] initWithUTF8String:newViewProps.clientId.c_str()];
    self.storedClientToken = [[NSString alloc] initWithUTF8String:newViewProps.clientToken.c_str()];
    self.storedLocale = [[NSString alloc] initWithUTF8String:newViewProps.locale.c_str()];
    self.storedEnvironment = [[NSString alloc] initWithUTF8String:newViewProps.environment.c_str()];
    self.storedRegion = [[NSString alloc] initWithUTF8String:newViewProps.region.c_str()];
    self.storedReturnUrl = [[NSString alloc] initWithUTF8String:newViewProps.returnUrl.c_str()];
    self.storedTheme = [[NSString alloc] initWithUTF8String:newViewProps.theme.c_str()];
    self.storedShape = [[NSString alloc] initWithUTF8String:newViewProps.shape.c_str()];
    self.storedButtonStyle = [[NSString alloc] initWithUTF8String:newViewProps.buttonStyle.c_str()];
    self.storedAutoFinalize = newViewProps.autoFinalize;
    self.storedCollectShippingAddress = newViewProps.collectShippingAddress;
    self.storedSessionData = [[NSString alloc] initWithUTF8String:newViewProps.sessionData.c_str()];

    if (self.isButtonCreated) {
        [self resetButton];
    }
    [self createButtonIfNeeded];

    [super updateProps:props oldProps:oldProps];
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
        createButtonWithSessionType:self.storedSessionType ?: @""
        clientId:self.storedClientId ?: @""
        clientToken:self.storedClientToken ?: @""
        locale:self.storedLocale
        returnUrl:self.storedReturnUrl ?: @""
        delegate:self.delegateProxy
        theme:self.storedTheme
        shape:self.storedShape
        buttonStyle:self.storedButtonStyle
        autoFinalize:self.storedAutoFinalize
        collectShippingAddress:self.storedCollectShippingAddress
        sessionData:self.storedSessionData
        environment:self.storedEnvironment
        region:self.storedRegion];

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
        __weak __typeof(self) weakSelf = self;
        dispatch_async(dispatch_get_main_queue(), ^{
            __strong __typeof(weakSelf) strongSelf = weakSelf;
            if (!strongSelf || !strongSelf->_eventEmitter || !strongSelf.expressCheckoutButton) {
                return;
            }
            CGFloat height = strongSelf.expressCheckoutButton.intrinsicContentSize.height;
            if (height > 0) {
                auto emitter = std::dynamic_pointer_cast<const RNKlarnaExpressCheckoutViewEventEmitter>(strongSelf->_eventEmitter);
                if (emitter) {
                    emitter->onResized(RNKlarnaExpressCheckoutViewEventEmitter::OnResized{
                        .height = std::string([[[NSNumber numberWithFloat:height] stringValue] UTF8String]),
                    });
                }
            }
        });
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

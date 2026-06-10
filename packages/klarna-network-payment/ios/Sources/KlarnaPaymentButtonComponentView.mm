#import <React/RCTViewComponentView.h>
#import <UIKit/UIKit.h>
#import <react/renderer/components/KlarnaNetworkPaymentSpec/ComponentDescriptors.h>
#import <react/renderer/components/KlarnaNetworkPaymentSpec/EventEmitters.h>
#import <react/renderer/components/KlarnaNetworkPaymentSpec/Props.h>
#import <react/renderer/components/KlarnaNetworkPaymentSpec/RCTComponentViewHelpers.h>
#import "RCTFabricComponentsPlugins.h"
#import "RCTConversions.h"
// Framework form resolves under use_frameworks!; quoted form is the headermap fallback for static builds.
#if __has_include(<react_native_klarna_network_payment/react_native_klarna_network_payment-Swift.h>)
#import <react_native_klarna_network_payment/react_native_klarna_network_payment-Swift.h>
#else
#import "react_native_klarna_network_payment-Swift.h"
#endif

using namespace facebook::react;

@interface KlarnaPaymentButtonComponentView : RCTViewComponentView <UIGestureRecognizerDelegate>
@property (nonatomic, strong) UIView *buttonView;
@end

@implementation KlarnaPaymentButtonComponentView

- (instancetype)initWithFrame:(CGRect)frame
{
    self = [super initWithFrame:frame];
    if (self) {
        self.backgroundColor = [UIColor clearColor];
        static const auto defaultProps = std::make_shared<const KlarnaNetworkPaymentButtonProps>();
        _props = defaultProps;
    }
    return self;
}

+ (ComponentDescriptorProvider)componentDescriptorProvider
{
    return concreteComponentDescriptorProvider<KlarnaNetworkPaymentButtonComponentDescriptor>();
}

Class<RCTComponentViewProtocol> KlarnaNetworkPaymentButtonCls(void)
{
    return KlarnaPaymentButtonComponentView.class;
}

- (void)updateProps:(Props::Shared const &)props oldProps:(Props::Shared const &)oldProps
{
    const auto &newViewProps = *std::static_pointer_cast<KlarnaNetworkPaymentButtonProps const>(props);
    const auto &oldViewProps = *std::static_pointer_cast<KlarnaNetworkPaymentButtonProps const>(_props);

    BOOL propsChanged = (newViewProps.instanceId != oldViewProps.instanceId)
        || (newViewProps.intent != oldViewProps.intent)
        || (newViewProps.shape != oldViewProps.shape)
        || (newViewProps.buttonStyle != oldViewProps.buttonStyle)
        || (newViewProps.theme != oldViewProps.theme);

    if (propsChanged) {
        [self _tearDown];

        NSString *instanceId = [[NSString alloc] initWithUTF8String: newViewProps.instanceId.c_str()];
        NSString *state = newViewProps.state.empty() ? nil : [[NSString alloc] initWithUTF8String: newViewProps.state.c_str()];
        NSString *intent = newViewProps.intent.empty() ? nil : [[NSString alloc] initWithUTF8String: newViewProps.intent.c_str()];
        NSString *shape = newViewProps.shape.empty() ? nil : [[NSString alloc] initWithUTF8String:newViewProps.shape.c_str()];
        NSString *buttonStyle = newViewProps.buttonStyle.empty() ? nil : [[NSString alloc] initWithUTF8String: newViewProps.buttonStyle.c_str()];
        NSString *theme = newViewProps.theme.empty() ? nil : [[NSString alloc] initWithUTF8String: newViewProps.theme.c_str()];

        [self _createButtonWithInstanceId: instanceId
                                    state: state
                                   intent: intent
                                    shape: shape
                              buttonStyle: buttonStyle
                                    theme: theme];
    } else if (newViewProps.state != oldViewProps.state) {
        NSString *state = newViewProps.state.empty() ? nil : [[NSString alloc] initWithUTF8String: newViewProps.state.c_str()];
        [KlarnaPaymentButtonViewImpl updateState: state onButton: _buttonView];
    }

    [super updateProps: props oldProps: oldProps];
}

- (void)updateLayoutMetrics:(const facebook::react::LayoutMetrics &)layoutMetrics oldLayoutMetrics:(const facebook::react::LayoutMetrics &)oldLayoutMetrics {
    [super updateLayoutMetrics: layoutMetrics oldLayoutMetrics: oldLayoutMetrics];
}

- (void)_createButtonWithInstanceId:(NSString *)instanceId
                              state:(NSString *)state
                             intent:(NSString *)intent
                              shape:(NSString *)shape
                        buttonStyle:(NSString *)buttonStyle
                              theme:(NSString *)theme
{
    UIView *button = [KlarnaPaymentButtonViewImpl makeButtonWithInstanceId: instanceId
                                                                     state: state
                                                                    intent: intent
                                                                     shape: shape
                                                               buttonStyle: buttonStyle
                                                                     theme: theme];
    if (!button) {
        return;
    }

    button.translatesAutoresizingMaskIntoConstraints = NO;
    [self addSubview:button];
    [NSLayoutConstraint activateConstraints:@[
        [button.topAnchor constraintEqualToAnchor: self.topAnchor],
        [button.bottomAnchor constraintEqualToAnchor: self.bottomAnchor],
        [button.leadingAnchor constraintEqualToAnchor: self.leadingAnchor],
        [button.trailingAnchor constraintEqualToAnchor: self.trailingAnchor],
    ]];

    UITapGestureRecognizer *tap = [[UITapGestureRecognizer alloc]
                                   initWithTarget: self action: @selector(_onButtonTapped)];
    tap.cancelsTouchesInView = NO;
    tap.delegate = self;
    [button addGestureRecognizer: tap];

    _buttonView = button;
}

- (void)_tearDown
{
    [_buttonView removeFromSuperview];
    _buttonView = nil;
}

- (void)_onButtonTapped
{
    if (_eventEmitter) {
        std::dynamic_pointer_cast<const KlarnaNetworkPaymentButtonEventEmitter>(_eventEmitter)
            ->onButtonPress(KlarnaNetworkPaymentButtonEventEmitter::OnButtonPress{});
    }
}

- (void)layoutSubviews {
    [super layoutSubviews];
    CGRect rect = RCTCGRectFromRect(_layoutMetrics.frame);
    _buttonView.frame.size = rect.size;
}

#pragma mark - UIGestureRecognizerDelegate

- (BOOL)gestureRecognizer:(UIGestureRecognizer *)gestureRecognizer
shouldRecognizeSimultaneouslyWithGestureRecognizer:(UIGestureRecognizer *)otherGestureRecognizer
{
    return YES;
}

@end

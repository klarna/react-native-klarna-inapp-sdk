#if RCT_NEW_ARCH_ENABLED

#import "KlarnaStandaloneWebViewWrapper.h"

#import <AVFoundation/AVFoundation.h>
#import <KlarnaMobileSDK/KlarnaMobileSDK-Swift.h>
#import <React/RCTLog.h>

#import <react/renderer/components/RNKlarnaMobileSDK/ComponentDescriptors.h>
#import <react/renderer/components/RNKlarnaMobileSDK/EventEmitters.h>
#import <react/renderer/components/RNKlarnaMobileSDK/Props.h>
#import <react/renderer/components/RNKlarnaMobileSDK/RCTComponentViewHelpers.h>

#import "RCTFabricComponentsPlugins.h"

using namespace facebook::react;

@interface KlarnaStandaloneWebViewWrapper () <KlarnaStandaloneWebViewDelegate, RCTRNKlarnaStandaloneWebViewViewProtocol>

@property (nonatomic, strong) KlarnaStandaloneWebView* klarnaStandaloneWebView;

@end

@implementation KlarnaStandaloneWebViewWrapper

- (id) init {
    self = [super init];
    // TODO: What should we pass here for 'returnUrl'?
    [self initializeKlarnaStandaloneWebView: nil];
    self.klarnaStandaloneWebView.delegate = self;
    return self;
}

#pragma mark - React Native Overrides

- (void) initializeKlarnaStandaloneWebView:(nullable NSString*)returnUrl {
    if (returnUrl != nil && returnUrl.length > 0) {
        self.klarnaStandaloneWebView = [[KlarnaStandaloneWebView alloc] initWithReturnURL:[NSURL URLWithString:returnUrl]];
    } else {
        // TODO: What should we do here? I mean, can returnUrl be nil?
        self.klarnaStandaloneWebView = [[KlarnaStandaloneWebView alloc] initWithReturnURL:[NSURL URLWithString:@"returnUrl://"]];
    }
    self.klarnaStandaloneWebView.translatesAutoresizingMaskIntoConstraints = NO;
    
    [self addSubview:self.klarnaStandaloneWebView];
    
    [NSLayoutConstraint activateConstraints:[[NSArray alloc] initWithObjects:
                                                 [self.klarnaStandaloneWebView.topAnchor constraintEqualToAnchor:self.topAnchor],
                                             [self.klarnaStandaloneWebView.bottomAnchor constraintEqualToAnchor:self.bottomAnchor],
                                             [self.klarnaStandaloneWebView.leadingAnchor constraintEqualToAnchor:self.leadingAnchor],
                                             [self.klarnaStandaloneWebView.trailingAnchor constraintEqualToAnchor:self.trailingAnchor], nil
                                            ]];
}

- (void) layoutSubviews {
    [super layoutSubviews];
    self.klarnaStandaloneWebView.frame = self.bounds;
    [self.klarnaStandaloneWebView layoutSubviews];
}

#pragma mark - Klarna Standalone Web View Methods

- (instancetype) initWithFrame:(CGRect)frame
{
    self = [super initWithFrame:frame];
    if (self) {
        static const auto defaultProps = std::make_shared<const RNKlarnaStandaloneWebViewProps>();
        _props = defaultProps;
    }
    
    return self;
}

+ (ComponentDescriptorProvider)componentDescriptorProvider
{
    return concreteComponentDescriptorProvider<RNKlarnaStandaloneWebViewComponentDescriptor>();
}

Class<RCTComponentViewProtocol> RNKlarnaStandaloneWebViewCls(void)
{
    return KlarnaStandaloneWebViewWrapper.class;
}

- (void) updateProps:(Props::Shared const &)props oldProps:(Props::Shared const &)oldProps
{
    const auto &oldViewProps = *std::static_pointer_cast<RNKlarnaStandaloneWebViewProps const>(_props);
    const auto &newViewProps = *std::static_pointer_cast<RNKlarnaStandaloneWebViewProps const>(props);
    
    if (oldViewProps.returnUrl != newViewProps.returnUrl) {
        NSString * newReturnUrl = [[NSString alloc] initWithUTF8String: newViewProps.returnUrl.c_str()];
        self.klarnaStandaloneWebView.returnURL = [NSURL URLWithString:newReturnUrl];
    }
    
    [super updateProps:props oldProps:oldProps];
}

- (void)handleCommand:(const NSString *)commandName args:(const NSArray *)args {
    RCTRNKlarnaStandaloneWebViewHandleCommand(self, commandName, args);
}

#pragma mark - KlarnaStandaloneWebViewDelegate methods

- (void)klarnaStandaloneWebView:(KlarnaStandaloneWebView * _Nonnull)webView didCommit:(WKNavigation * _Nonnull)navigation {
    if(_eventEmitter){
        RCTLogInfo(@"Sending onBeforeLoad event");
        std::dynamic_pointer_cast<const RNKlarnaStandaloneWebViewEventEmitter>(_eventEmitter)
        ->onBeforeLoad(RNKlarnaStandaloneWebViewEventEmitter::OnBeforeLoad{});
    } else {
        RCTLogInfo(@"_eventEmitter is nil!");
    }
}

- (void)klarnaStandaloneWebView:(KlarnaStandaloneWebView * _Nonnull)webView didFinish:(WKNavigation * _Nonnull)navigation {
    if(_eventEmitter){
        RCTLogInfo(@"Sending onLoad event");
        std::dynamic_pointer_cast<const RNKlarnaStandaloneWebViewEventEmitter>(_eventEmitter)
        ->onLoad(RNKlarnaStandaloneWebViewEventEmitter::OnLoad{});
    } else {
        RCTLogInfo(@"_eventEmitter is nil!");
    }
}

#pragma mark - RCTRNKlarnaStandaloneWebViewViewProtocol methods

- (void)load:(nonnull NSString *)url {
    [self.klarnaStandaloneWebView loadURL:[NSURL URLWithString:url]];
}

@end

#endif

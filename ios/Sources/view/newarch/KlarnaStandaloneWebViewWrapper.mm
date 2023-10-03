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

@interface KlarnaStandaloneWebViewWrapper

@property (nonatomic, strong) KlarnaStandaloneWebView* klarnaStandaloneWebView;

@end

@implementation KlarnaStandaloneWebViewWrapper

- (id) init {
    self = [super init];
    [self initializeKlarnaStandaloneWebView];
    return self;
}

#pragma mark - React Native Overrides

- (void) initializeKlarnaStandaloneWebView:(NSString*)returnUrl {
    if (returnUrl.length > 0) {
        self.klarnaStandaloneWebView = [[KlarnaStandaloneWebView alloc] initWithReturnURL:[NSURL URLWithString:returnUrl]];
    } else {
        // TODO what should we do here?
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

#pragma mark - Standalone Web View Methods

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

@end

#endif


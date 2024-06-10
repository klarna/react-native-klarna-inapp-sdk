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

@interface KlarnaStandaloneWebViewWrapper () <KlarnaStandaloneWebViewDelegate, KlarnaEventHandler, RCTRNKlarnaStandaloneWebViewViewProtocol>

@property (nonatomic, strong) KlarnaStandaloneWebView *klarnaStandaloneWebView;

@end

@implementation KlarnaStandaloneWebViewWrapper

// The property name in KlarnaStandaloneWebView that we want to observe for changes
NSString *const PROPERTY_NAME_ESTIMATED_PROGRESS = @"estimatedProgress";

- (id)init {
    self = [super init];
    // TODO: What should we pass here for 'returnUrl'?
    [self initializeKlarnaStandaloneWebView: @"returnUrl://"];
    self.klarnaStandaloneWebView.delegate = self;
    self.klarnaStandaloneWebView.eventHandler = self;
    [self.klarnaStandaloneWebView addObserver:self forKeyPath:PROPERTY_NAME_ESTIMATED_PROGRESS options:NSKeyValueObservingOptionNew context:nil];
    return self;
}

-(void)dealloc {
    @try {
        [self.klarnaStandaloneWebView removeObserver:self forKeyPath:PROPERTY_NAME_ESTIMATED_PROGRESS context:nil];
    } @catch(NSException *exception) {
        RCTLog(@"Could not remove the progress observer: %@", exception);
    }
}

- (void)observeValueForKeyPath:(NSString *)keyPath ofObject:(id)object change:(NSDictionary *)change context:(void *)context {
    if ([keyPath isEqualToString:PROPERTY_NAME_ESTIMATED_PROGRESS]) {
        // newProgress is a value in range [0..1].
        NSNumber *newProgress = [change objectForKey:NSKeyValueChangeNewKey];
        // We need to convert it to an int value in range [0..100]
        int progress = [NSNumber numberWithDouble:(newProgress.doubleValue * 100)].intValue;
        [self sendLoadProgressEvent:progress];
    } else {
        [super observeValueForKeyPath:keyPath ofObject:object change:change context:context];
    }
}

- (void)sendLoadProgressEvent:(int)progress {
    if (_eventEmitter) {
        RCTLogInfo(@"Sending onLoadProgress event");
        NSString *url = self.klarnaStandaloneWebView.url == nil ? @"" : self.klarnaStandaloneWebView.url.absoluteString;
        NSString *title = self.klarnaStandaloneWebView.title == nil ? @"" : self.klarnaStandaloneWebView.title;
        std::dynamic_pointer_cast<const RNKlarnaStandaloneWebViewEventEmitter>(_eventEmitter)
        ->onLoadProgress(RNKlarnaStandaloneWebViewEventEmitter::OnLoadProgress{
            .progressEvent = {
                .url = std::string([url UTF8String]),
                .title = std::string([title UTF8String]),
                .loading = self.klarnaStandaloneWebView.isLoading,
                .canGoBack = self.klarnaStandaloneWebView.canGoBack,
                .canGoForward = self.klarnaStandaloneWebView.canGoForward,
                .progress = (double)progress
            }
        });
    } else {
        RCTLogInfo(@"_eventEmitter is nil!");
    }
}

#pragma mark - React Native Overrides

- (void)initializeKlarnaStandaloneWebView:(nonnull NSString*)returnUrl {
    self.klarnaStandaloneWebView = [[KlarnaStandaloneWebView alloc] initWithReturnURL:[NSURL URLWithString:returnUrl]];
    self.klarnaStandaloneWebView.translatesAutoresizingMaskIntoConstraints = NO;
    
    [self addSubview:self.klarnaStandaloneWebView];
    [self setWebViewBackgroundToTransparent];
    
    [NSLayoutConstraint activateConstraints:[[NSArray alloc] initWithObjects:
                                            [self.klarnaStandaloneWebView.topAnchor constraintEqualToAnchor:self.topAnchor],
                                             [self.klarnaStandaloneWebView.bottomAnchor constraintEqualToAnchor:self.bottomAnchor],
                                             [self.klarnaStandaloneWebView.leadingAnchor constraintEqualToAnchor:self.leadingAnchor],
                                             [self.klarnaStandaloneWebView.trailingAnchor constraintEqualToAnchor:self.trailingAnchor], nil
                                            ]];
}

- (void)setWebViewBackgroundToTransparent {
    for (UIView *subview in self.klarnaStandaloneWebView.subviews) {
        if ([subview isKindOfClass:[WKWebView class]]) {
            WKWebView *webView = (WKWebView *) subview;
            webView.backgroundColor = [UIColor clearColor];
            webView.opaque = NO;
            webView.scrollView.backgroundColor = [UIColor clearColor];
        }
    }
}

- (void)layoutSubviews {
    [super layoutSubviews];
    self.klarnaStandaloneWebView.frame = self.bounds;
    [self.klarnaStandaloneWebView layoutSubviews];
}

#pragma mark - Klarna Standalone Web View Methods

- (instancetype)initWithFrame:(CGRect)frame
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

Class<RCTComponentViewProtocol>RNKlarnaStandaloneWebViewCls(void)
{
    return KlarnaStandaloneWebViewWrapper.class;
}

- (void)updateProps:(Props::Shared const &)props oldProps:(Props::Shared const &)oldProps
{
    const auto &oldViewProps = *std::static_pointer_cast<RNKlarnaStandaloneWebViewProps const>(_props);
    const auto &newViewProps = *std::static_pointer_cast<RNKlarnaStandaloneWebViewProps const>(props);
    
    if (oldViewProps.returnUrl != newViewProps.returnUrl) {
        NSString *newReturnUrl = [[NSString alloc] initWithUTF8String: newViewProps.returnUrl.c_str()];
        self.klarnaStandaloneWebView.returnURL = [NSURL URLWithString:newReturnUrl];
    }
    
    [super updateProps:props oldProps:oldProps];
}

- (void)handleCommand:(const NSString *)commandName args:(const NSArray *)args {
    RCTRNKlarnaStandaloneWebViewHandleCommand(self, commandName, args);
}

#pragma mark - KlarnaStandaloneWebViewDelegate methods

- (void)klarnaStandaloneWebView:(KlarnaStandaloneWebView * _Nonnull)webView didCommit:(WKNavigation * _Nonnull)navigation {
    if (_eventEmitter) {
        RCTLogInfo(@"Sending onLoadStart event");
        // 'estimatedProgress' is a double value in range [0..1].
        // We need to convert it to an int value in range [0..100].
        std::dynamic_pointer_cast<const RNKlarnaStandaloneWebViewEventEmitter>(_eventEmitter)
        ->onLoadStart(RNKlarnaStandaloneWebViewEventEmitter::OnLoadStart{
            .navigationEvent = {
                .url = std::string([webView.url.absoluteString UTF8String]),
                .title = std::string([webView.title UTF8String]),
                .loading = self.klarnaStandaloneWebView.isLoading,
                .canGoBack = self.klarnaStandaloneWebView.canGoBack,
                .canGoForward = self.klarnaStandaloneWebView.canGoForward,
            }
        });
    } else {
        RCTLogInfo(@"_eventEmitter is nil!");
    }
}

- (void)klarnaStandaloneWebView:(KlarnaStandaloneWebView * _Nonnull)webView didFinish:(WKNavigation * _Nonnull)navigation {
    if (_eventEmitter) {
        RCTLogInfo(@"Sending onLoadEnd event");
        // 'estimatedProgress' is a double value in range [0..1].
        // We need to convert it to an int value in range [0..100].
        std::dynamic_pointer_cast<const RNKlarnaStandaloneWebViewEventEmitter>(_eventEmitter)
        ->onLoadEnd(RNKlarnaStandaloneWebViewEventEmitter::OnLoadEnd{
            .navigationEvent = {
                .url = std::string([webView.url.absoluteString UTF8String]),
                .title = std::string([webView.title UTF8String]),
                .loading = self.klarnaStandaloneWebView.isLoading,
                .canGoBack = self.klarnaStandaloneWebView.canGoBack,
                .canGoForward = self.klarnaStandaloneWebView.canGoForward,
            }
        });
    } else {
        RCTLogInfo(@"_eventEmitter is nil!");
    }
}

- (void)klarnaStandaloneWebView:(KlarnaStandaloneWebView * _Nonnull)webView didFailProvisionalNavigation:(WKNavigation * _Nonnull)navigation withError:(NSError * _Nonnull)error {
    if (_eventEmitter) {
        RCTLogInfo(@"Sending onError event");
        std::dynamic_pointer_cast<const RNKlarnaStandaloneWebViewEventEmitter>(_eventEmitter)
        ->onError(RNKlarnaStandaloneWebViewEventEmitter::OnError{
            .error = {
                .url = std::string([webView.url.absoluteString UTF8String]),
                .title = std::string([webView.title UTF8String]),
                .loading = self.klarnaStandaloneWebView.isLoading,
                .canGoBack = self.klarnaStandaloneWebView.canGoBack,
                .canGoForward = self.klarnaStandaloneWebView.canGoForward,
                .code = (int)error.code,
                .description = std::string([[error localizedDescription] UTF8String])
            }
        });
    } else {
        RCTLogInfo(@"_eventEmitter is nil!");
    }
}

- (void)klarnaStandaloneWebView:(KlarnaStandaloneWebView * _Nonnull)webView didFail:(WKNavigation * _Nonnull)navigation withError:(NSError * _Nonnull)error {
    if (_eventEmitter) {
        RCTLogInfo(@"Sending onError event");
        std::dynamic_pointer_cast<const RNKlarnaStandaloneWebViewEventEmitter>(_eventEmitter)
        ->onError(RNKlarnaStandaloneWebViewEventEmitter::OnError{
            .error = {
                .url = std::string([webView.url.absoluteString UTF8String]),
                .title = std::string([webView.title UTF8String]),
                .loading = self.klarnaStandaloneWebView.isLoading,
                .canGoBack = self.klarnaStandaloneWebView.canGoBack,
                .canGoForward = self.klarnaStandaloneWebView.canGoForward,
                .code = (int)error.code,
                .description = std::string([[error localizedDescription] UTF8String])
            }
        });
    } else {
        RCTLogInfo(@"_eventEmitter is nil!");
    }
}

#pragma mark - RCTRNKlarnaStandaloneWebViewViewProtocol methods

- (void)klarnaComponent:(id <KlarnaComponent> _Nonnull)klarnaComponent dispatchedEvent:(KlarnaProductEvent * _Nonnull)event {
    if (_eventEmitter) {
        RCTLogInfo(@"Sending onKlarnaMessage event");
        std::dynamic_pointer_cast<const RNKlarnaStandaloneWebViewEventEmitter>(_eventEmitter)
        ->onKlarnaMessage(RNKlarnaStandaloneWebViewEventEmitter::OnKlarnaMessage{
            .klarnaMessageEvent = {
                .action = std::string([[event action] UTF8String]),
                .params = std::string([[self serializeDictionaryToJsonString: [event getParams]] UTF8String])
            }
        });
    } else {
        RCTLogInfo(@"_eventEmitter is nil!");
    }
}

- (void)klarnaComponent:(id <KlarnaComponent> _Nonnull)klarnaComponent encounteredError:(KlarnaError * _Nonnull)error {
    // Not used as of now
}

#pragma mark - RCTRNKlarnaStandaloneWebViewViewProtocol methods

- (void)load:(nonnull NSString *)url {
    NSURL *urlToLoad = [NSURL URLWithString:url];
    if ([urlToLoad scheme] && [urlToLoad host] && urlToLoad) {
      [self.klarnaStandaloneWebView loadURL:urlToLoad];
    } else {
        RCTLogInfo(@"url is not a valid URL");
    }
}

- (void)goForward {
    [self.klarnaStandaloneWebView goForward];
}

- (void)goBack {
    [self.klarnaStandaloneWebView goBack];
}

- (void)reload {
    [self.klarnaStandaloneWebView reload];
}

- (NSString *)serializeDictionaryToJsonString:(NSDictionary<NSString *, id<NSCoding>> *)dictionary {
    if (!dictionary) {
        RCTLog(@"Dictionary is nil");
        return @"{}";
    }
    
    NSError *error;
    NSData *jsonData = [NSJSONSerialization dataWithJSONObject:dictionary options:NSJSONWritingPrettyPrinted error:&error];
    
    if (!jsonData) {
        return @"{}";
    } else {
        NSString *jsonString = [[NSString alloc] initWithData:jsonData encoding:NSUTF8StringEncoding];
        return jsonString;
    }
}

@end

#endif

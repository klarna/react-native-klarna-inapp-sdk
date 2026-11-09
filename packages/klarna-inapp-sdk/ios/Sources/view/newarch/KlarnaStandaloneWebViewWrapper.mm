#if RCT_NEW_ARCH_ENABLED

#import "../KlarnaStandaloneWebViewWrapper.h"
#import "../KlarnaStandaloneWebViewLoadOptions.h"
#import "../KlarnaStandaloneWebViewCookies.h"
#import "../../common/RNMobileSDKUtils.h"

#import <AVFoundation/AVFoundation.h>
#import <KlarnaMobileSDK/KlarnaMobileSDK.h>
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
@property (nonatomic, strong) NSString *returnUrl;
@property (nonatomic, strong, nullable) KlarnaStandaloneWebViewLoadOptions *firstLoadOptions;
@property (nonatomic, strong, nullable) NSURL *firstLoadURL;
@property (nonatomic, assign) BOOL isRestartingWithoutHeaders;
// Set while a load waits on shared cookies; any later load, stop, or history command clears or replaces it.
@property (nonatomic, strong, nullable) NSObject *pendingLoad;
// Cookies stored through setCookies since the last load; that load's shared cookie copy must not overwrite them.
@property (nonatomic, strong) NSArray<NSHTTPCookie *> *storedCookies;
// The stored cookies that replace same-name cookies; the shared cookie copy must not bring those back.
@property (nonatomic, strong) NSArray<NSHTTPCookie *> *replacingCookies;
@property (nonatomic, assign) BOOL sharedCookiesEnabled;
@property (nonatomic, strong) NSString *theme;

typedef void (^WebViewOperation)(WKWebView *);

@end

@implementation KlarnaStandaloneWebViewWrapper

// The property name in KlarnaStandaloneWebView that we want to observe for changes
NSString *const PROPERTY_NAME_ESTIMATED_PROGRESS = @"estimatedProgress";

- (id)init {
    self = [super init];
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
        std::dynamic_pointer_cast<const RNKlarnaStandaloneWebViewEventEmitter>(_eventEmitter)
        ->onLoadProgress(RNKlarnaStandaloneWebViewEventEmitter::OnLoadProgress{
            .progressEvent = {
                .url = RNMSDKStdString(self.klarnaStandaloneWebView.url.absoluteString),
                .title = RNMSDKStdString(self.klarnaStandaloneWebView.title),
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

- (KlarnaStandaloneWebView *)createKlarnaStandaloneWebView {
    return [[KlarnaStandaloneWebView alloc] initWithReturnURL:[NSURL URLWithString:self.returnUrl]];
}

- (void)initializeKlarnaStandaloneWebView {
    self.klarnaStandaloneWebView = [self createKlarnaStandaloneWebView];
    self.klarnaStandaloneWebView.delegate = self;
    self.klarnaStandaloneWebView.eventHandler = self;
    [self.klarnaStandaloneWebView addObserver:self forKeyPath:PROPERTY_NAME_ESTIMATED_PROGRESS options:NSKeyValueObservingOptionNew context:nil];
    [self applyTheme];

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

- (void)applyTheme {
    if ([self.theme isEqualToString:@"light"]) {
        self.klarnaStandaloneWebView.theme = KlarnaThemeLight;
    } else if ([self.theme isEqualToString:@"dark"]) {
        self.klarnaStandaloneWebView.theme = KlarnaThemeDark;
    } else if ([self.theme isEqualToString:@"automatic"]) {
        self.klarnaStandaloneWebView.theme = KlarnaThemeAutomatic;
    }
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

    // Stored before returnUrl is handled, so that a theme arriving together with
    // the first returnUrl is applied when the view is created.
    if (oldViewProps.theme != newViewProps.theme) {
        self.theme = [[NSString alloc] initWithUTF8String: newViewProps.theme.c_str()];
        [self applyTheme];
    }

    if (oldViewProps.returnUrl != newViewProps.returnUrl) {
        NSString *newReturnUrl = [[NSString alloc] initWithUTF8String: newViewProps.returnUrl.c_str()];
        self.returnUrl = newReturnUrl;
        if (newReturnUrl.length > 0) {
            if (self.klarnaStandaloneWebView == nil) {
                [self initializeKlarnaStandaloneWebView];
            } else {
                self.klarnaStandaloneWebView.returnURL = [NSURL URLWithString:newReturnUrl];
            }
        }
    }

    WebViewOperation setBounces = ^(WKWebView *webView) {
        webView.scrollView.bounces = newViewProps.bounces;
    };
    [self applyOperationToWebViews:setBounces];
    self.sharedCookiesEnabled = newViewProps.sharedCookiesEnabled;

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
                .url = RNMSDKStdString(webView.url.absoluteString),
                .title = RNMSDKStdString(webView.title),
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
    // The load headers only apply to the initial navigation, so later navigations are left alone.
    self.firstLoadOptions = nil;
    if (_eventEmitter) {
        RCTLogInfo(@"Sending onLoadEnd event");
        // 'estimatedProgress' is a double value in range [0..1].
        // We need to convert it to an int value in range [0..100].
        std::dynamic_pointer_cast<const RNKlarnaStandaloneWebViewEventEmitter>(_eventEmitter)
        ->onLoadEnd(RNKlarnaStandaloneWebViewEventEmitter::OnLoadEnd{
            .navigationEvent = {
                .url = RNMSDKStdString(webView.url.absoluteString),
                .title = RNMSDKStdString(webView.title),
                .loading = self.klarnaStandaloneWebView.isLoading,
                .canGoBack = self.klarnaStandaloneWebView.canGoBack,
                .canGoForward = self.klarnaStandaloneWebView.canGoForward,
            }
        });
    } else {
        RCTLogInfo(@"_eventEmitter is nil!");
    }
}

- (void)klarnaStandaloneWebView:(KlarnaStandaloneWebView * _Nonnull)webView decidePolicyFor:(WKNavigationAction * _Nonnull)navigationAction decisionHandler:(void (^ _Nonnull)(WKNavigationActionPolicy))decisionHandler {
    // WebKit forwards load headers to main-frame redirects on other origins, so such a redirect is restarted without them.
    if (navigationAction.targetFrame.isMainFrame && [self.firstLoadOptions isCarriedByRequest:navigationAction.request toAnotherOriginThan:self.firstLoadURL]) {
        decisionHandler(WKNavigationActionPolicyCancel);
        NSURLRequest *request = [self.firstLoadOptions requestByRemovingHeadersFromRequest:navigationAction.request];
        self.firstLoadOptions = nil;
        self.isRestartingWithoutHeaders = YES;
        [webView loadURLRequest:request];
        return;
    }
    decisionHandler(WKNavigationActionPolicyAllow);
}

- (void)klarnaStandaloneWebView:(KlarnaStandaloneWebView * _Nonnull)webView didFailProvisionalNavigation:(WKNavigation * _Nonnull)navigation withError:(NSError * _Nonnull)error {
    // Cancelling a redirect to restart it without the load headers is not a failure either.
    if (self.isRestartingWithoutHeaders && [error.domain isEqualToString:@"WebKitErrorDomain"] && error.code == KlarnaWebKitErrorFrameLoadInterrupted) {
        self.isRestartingWithoutHeaders = NO;
        return;
    }
    // A cancelled navigation is not a failure. WebKit reports NSURLErrorCancelled for a
    // deliberate stopLoading, for redirects, and when a new load starts before the previous
    // one finished, so reporting it as onError would send false errors to integrators.
    if ([error.domain isEqualToString:NSURLErrorDomain] && error.code == NSURLErrorCancelled) {
        return;
    }
    self.firstLoadOptions = nil;

    if (_eventEmitter) {
        RCTLogInfo(@"Sending onError event");
        std::dynamic_pointer_cast<const RNKlarnaStandaloneWebViewEventEmitter>(_eventEmitter)
        ->onError(RNKlarnaStandaloneWebViewEventEmitter::OnError{
            .error = {
                .url = RNMSDKStdString([RNMSDKNavigationErrorUtil urlStringForWebViewURL:webView.url error:error]),
                .title = RNMSDKStdString(webView.title),
                .loading = self.klarnaStandaloneWebView.isLoading,
                .canGoBack = self.klarnaStandaloneWebView.canGoBack,
                .canGoForward = self.klarnaStandaloneWebView.canGoForward,
                .code = (int)error.code,
                .description = RNMSDKStdString([error localizedDescription])
            }
        });
    } else {
        RCTLogInfo(@"_eventEmitter is nil!");
    }
}

- (void)klarnaStandaloneWebView:(KlarnaStandaloneWebView * _Nonnull)webView didFail:(WKNavigation * _Nonnull)navigation withError:(NSError * _Nonnull)error {
    // A cancelled navigation is not a failure. WebKit reports NSURLErrorCancelled for a
    // deliberate stopLoading, for redirects, and when a new load starts before the previous
    // one finished, so reporting it as onError would send false errors to integrators.
    if ([error.domain isEqualToString:NSURLErrorDomain] && error.code == NSURLErrorCancelled) {
        return;
    }
    self.firstLoadOptions = nil;

    if (_eventEmitter) {
        RCTLogInfo(@"Sending onError event");
        std::dynamic_pointer_cast<const RNKlarnaStandaloneWebViewEventEmitter>(_eventEmitter)
        ->onError(RNKlarnaStandaloneWebViewEventEmitter::OnError{
            .error = {
                .url = RNMSDKStdString([RNMSDKNavigationErrorUtil urlStringForWebViewURL:webView.url error:error]),
                .title = RNMSDKStdString(webView.title),
                .loading = self.klarnaStandaloneWebView.isLoading,
                .canGoBack = self.klarnaStandaloneWebView.canGoBack,
                .canGoForward = self.klarnaStandaloneWebView.canGoForward,
                .code = (int)error.code,
                .description = RNMSDKStdString([error localizedDescription])
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
                .action = RNMSDKStdString([event action]),
                .params = RNMSDKStdString([self serializeDictionaryToJsonString: [event getParams]])
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

- (void)load:(nonnull NSString *)url optionsJson:(nullable NSString *)optionsJson {
    NSURL *urlToLoad = [NSURL URLWithString:url];
    if (!([urlToLoad scheme] && [urlToLoad host] && urlToLoad)) {
        RCTLogInfo(@"url is not a valid URL");
        return;
    }
    KlarnaStandaloneWebViewLoadOptions *options = [KlarnaStandaloneWebViewLoadOptions optionsWithJsonString:optionsJson];
    NSObject *pendingLoad = [NSObject new];
    self.pendingLoad = pendingLoad;
    // Only the cookies supplied for this load are protected, so later loads pick up newer shared cookies.
    NSArray<NSHTTPCookie *> *cookiesForThisLoad = self.storedCookies ?: @[];
    NSArray<NSHTTPCookie *> *replacingForThisLoad = self.replacingCookies ?: @[];
    self.storedCookies = nil;
    self.replacingCookies = nil;
    if (self.sharedCookiesEnabled) {
        if (@available(iOS 11.0, *)) {
            NSArray<NSHTTPCookie *> *sharedCookies = [KlarnaStandaloneWebViewCookies
                cookies:[KlarnaStandaloneWebViewCookies
                    cookies:NSHTTPCookieStorage.sharedHTTPCookieStorage.cookies ?: @[]
                    notReplacedBy:cookiesForThisLoad]
                notConflictingWith:replacingForThisLoad];
            [KlarnaStandaloneWebViewCookies setCookies:sharedCookies inStore:WKWebsiteDataStore.defaultDataStore.httpCookieStore completion:^{
                if (self.pendingLoad == pendingLoad) {
                    self.pendingLoad = nil;
                    [self startLoadingURL:urlToLoad options:options];
                }
            }];
            return;
        }
    }
    [self startLoadingURL:urlToLoad options:options];
}

// Installs the redirect protection for this load only when it actually starts.
- (void)startLoadingURL:(NSURL *)url options:(KlarnaStandaloneWebViewLoadOptions *)options {
    self.firstLoadOptions = options.headers.count > 0 ? options : nil;
    self.firstLoadURL = url;
    if (options.headers.count == 0) {
        [self.klarnaStandaloneWebView loadURL:url];
    } else {
        [self.klarnaStandaloneWebView loadURLRequest:[options requestWithURL:url]];
    }
}

- (void)goForward {
    self.pendingLoad = nil;
    [self.klarnaStandaloneWebView goForward];
}

- (void)goBack {
    self.pendingLoad = nil;
    [self.klarnaStandaloneWebView goBack];
}

- (void)reload {
    self.pendingLoad = nil;
    [self.klarnaStandaloneWebView reload];
}

- (void)stopLoading {
    self.pendingLoad = nil;
    self.firstLoadOptions = nil;
    [self.klarnaStandaloneWebView stopLoading];
}

- (void)setCookies:(NSInteger)requestId cookiesJson:(nonnull NSString *)cookiesJson {
    NSString *error = nil;
    NSArray<NSHTTPCookie *> *replacing = nil;
    NSArray<NSHTTPCookie *> *cookies = [KlarnaStandaloneWebViewCookies cookiesFromJsonString:cookiesJson replacing:&replacing error:&error];
    if (cookies == nil) {
        [self sendSetCookiesResult:requestId error:error];
        return;
    }
    // WKHTTPCookieStore needs iOS 11, so older versions reject the call instead of crashing.
    if (@available(iOS 11.0, *)) {
        // The web view uses the default data store, since this wrapper never turns cachingEnabled off.
        self.storedCookies = [[KlarnaStandaloneWebViewCookies cookies:self.storedCookies ?: @[] notReplacedBy:cookies] arrayByAddingObjectsFromArray:cookies];
        self.replacingCookies = [(self.replacingCookies ?: @[]) arrayByAddingObjectsFromArray:replacing];
        [KlarnaStandaloneWebViewCookies setCookies:cookies replacing:replacing inStore:WKWebsiteDataStore.defaultDataStore.httpCookieStore completion:^{
            [self sendSetCookiesResult:requestId error:nil];
        }];
    } else {
        [self sendSetCookiesResult:requestId error:@"setCookies requires iOS 11 or later."];
    }
}

- (void)sendSetCookiesResult:(NSInteger)requestId error:(nullable NSString *)error {
    if (_eventEmitter) {
        RCTLogInfo(@"Sending onSetCookiesResult event");
        std::dynamic_pointer_cast<const RNKlarnaStandaloneWebViewEventEmitter>(_eventEmitter)
        ->onSetCookiesResult(RNKlarnaStandaloneWebViewEventEmitter::OnSetCookiesResult{
            .setCookiesResult = {
                .requestId = (int)requestId,
                .error = RNMSDKStdString(error)
            }
        });
    } else {
        RCTLogInfo(@"_eventEmitter is nil!");
    }
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

- (void)applyOperationToWebViews:(WebViewOperation)operation {
    for (UIView *subView in self.klarnaStandaloneWebView.subviews) {
        if ([subView isKindOfClass:[WKWebView class]]) {
            WKWebView *webView = (WKWebView *) subView;
            operation(webView);
        }
    }
}

@end

#endif

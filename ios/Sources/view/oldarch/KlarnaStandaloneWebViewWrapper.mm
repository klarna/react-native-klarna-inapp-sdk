#if !RCT_NEW_ARCH_ENABLED

#import "../KlarnaStandaloneWebViewWrapper.h"

#import <KlarnaMobileSDK/KlarnaMobileSDK-Swift.h>
#import <React/RCTLog.h>

@interface KlarnaStandaloneWebViewWrapper () <KlarnaStandaloneWebViewDelegate, KlarnaEventHandler>

typedef void (^WebViewOperation)(WKWebView *);

@property (nonatomic, strong) KlarnaStandaloneWebView* klarnaStandaloneWebView;

@end

@implementation KlarnaStandaloneWebViewWrapper

// The property name in KlarnaStandaloneWebView that we want to observe for changes
NSString * const PROPERTY_NAME_ESTIMATED_PROGRESS = @"estimatedProgress";

- (id)init {
    self = [super init];
    [self initializeKlarnaStandaloneWebView];
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
        NSNumber * newProgress = [change objectForKey:NSKeyValueChangeNewKey];
        // We need to convert it to an int value in range [0..100]
        int progress = (int) (newProgress.doubleValue * 100);
        [self sendLoadProgressEvent:self.klarnaStandaloneWebView progress:progress];
    } else {
        [super observeValueForKeyPath:keyPath ofObject:object change:change context:context];
    }
}

- (void)sendLoadProgressEvent:(KlarnaStandaloneWebView * _Nonnull)webView progress:(int)progress {
    if (!self.onLoadProgress) {
        RCTLog(@"Missing 'onProgressChange' callback prop.");
        return;
    }
    NSMutableDictionary<NSString *, id> *event = [self webViewDict:webView];
    [event addEntriesFromDictionary:@{
          @"progress": [NSNumber numberWithDouble: progress] // could use self.klarnaStandaloneWebView.estimatedProgress
        }];
    
    self.onLoadProgress(@{@"progressEvent": event});

}

#pragma mark - React Native Overrides

- (void)setReturnUrl:(NSString *)returnUrl {
    _returnUrl = returnUrl;
    if (returnUrl.length > 0) {
        self.klarnaStandaloneWebView.returnURL = [NSURL URLWithString:self.returnUrl];
    }
}

- (void)setBounces:(BOOL)bounces {
    WebViewOperation setBounces = ^(WKWebView *webView) {
        webView.scrollView.bounces = bounces;
    };
    [self applyOperationToWebViews:setBounces];
}

- (void)initializeKlarnaStandaloneWebView {
    if (self.returnUrl != nil && self.returnUrl.length > 0) {
        self.klarnaStandaloneWebView = [[KlarnaStandaloneWebView alloc] initWithReturnURL:[NSURL URLWithString:self.returnUrl]];
    } else {
        // TODO: initWithReturnURL expects a non-null URL. What should we do here if returnUrl is null?
        self.klarnaStandaloneWebView = [[KlarnaStandaloneWebView alloc] initWithReturnURL:[NSURL URLWithString:@"returnUrl://"]];
    }
    
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

#pragma mark - KlarnaStandaloneWebViewDelegate methods

- (void)klarnaStandaloneWebView:(KlarnaStandaloneWebView * _Nonnull)webView didCommit:(WKNavigation * _Nonnull)navigation {
    if (!self.onLoadStart) {
        RCTLog(@"Missing 'onLoadStart' callback prop.");
        return;
    }
    NSMutableDictionary<NSString *, id> *event = [self webViewDict:webView];
    self.onLoadStart(@{@"navigationEvent": event});
}

- (void)klarnaStandaloneWebView:(KlarnaStandaloneWebView * _Nonnull)webView didFinish:(WKNavigation * _Nonnull)navigation {
    if (!self.onLoadEnd) {
        RCTLog(@"Missing 'onLoadEnd' callback prop.");
        return;
    }
    NSMutableDictionary<NSString *, id> *event = [self webViewDict:webView];
    self.onLoadEnd(@{@"navigationEvent": event});
}

- (void)klarnaStandaloneWebView:(KlarnaStandaloneWebView * _Nonnull)webView didFailProvisionalNavigation:(WKNavigation * _Nonnull)navigation withError:(NSError * _Nonnull)error {
    if (!self.onError) {
        RCTLog(@"Missing 'onError' callback prop.");
        return;
    }
    NSMutableDictionary<NSString *, id> *event = [self webViewDict:webView];
    [event addEntriesFromDictionary:@{
            @"code": @(error.code),
            @"description": error.description,
        }];
    self.onError(@{@"error": event});
}

- (void)klarnaStandaloneWebView:(KlarnaStandaloneWebView * _Nonnull)webView didFail:(WKNavigation * _Nonnull)navigation withError:(NSError * _Nonnull)error {
    if (!self.onError) {
        RCTLog(@"Missing 'onError' callback prop.");
        return;
    }
    NSMutableDictionary<NSString *, id> *event = [self webViewDict:webView];
    [event addEntriesFromDictionary:@{
            @"code": @(error.code),
            @"description": error.description,
        }];
    self.onError(@{@"error": error});
}

#pragma mark - KlarnaEventHandler methods

- (void)klarnaComponent:(id <KlarnaComponent> _Nonnull)klarnaComponent dispatchedEvent:(KlarnaProductEvent * _Nonnull)event {
    if (!self.onKlarnaMessage) {
        RCTLog(@"Missing 'onKlarnaMessage' callback prop.");
        return;
    }
    
    self.onKlarnaMessage(@{
        @"klarnaMessageEvent": @{
            @"action": event.action,
            @"params": [self serializeDictionaryToJsonString: [event getParams]]
        }
    });
}

- (void)klarnaComponent:(id <KlarnaComponent> _Nonnull)klarnaComponent encounteredError:(KlarnaError * _Nonnull)error {
    // Not used as of now
}

#pragma mark - Events

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

- (NSMutableDictionary<NSString *, id> *)webViewDict:(KlarnaStandaloneWebView * _Nonnull)webView {
    NSDictionary *event = @{
        @"url": webView.url.absoluteString ?: @"",
        @"title": webView.title ?: @"",
        @"loading" : @(webView.isLoading),
        @"canGoBack": @(webView.canGoBack),
        @"canGoForward" : @(webView.canGoForward)
      };
    return [[NSMutableDictionary alloc] initWithDictionary: event];
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

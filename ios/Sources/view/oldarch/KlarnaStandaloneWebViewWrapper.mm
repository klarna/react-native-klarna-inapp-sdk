#if !RCT_NEW_ARCH_ENABLED

#import "KlarnaStandaloneWebViewWrapper.h"

#import <KlarnaMobileSDK/KlarnaMobileSDK-Swift.h>
#import <React/RCTLog.h>

@interface KlarnaStandaloneWebViewWrapper () <KlarnaStandaloneWebViewDelegate>

@property (nonatomic, strong) KlarnaStandaloneWebView* klarnaStandaloneWebView;

@end

// TODO: Add support for sending onKlarnaMessage event.
// TODO: Double-check that we're sending the correct values for parameters of the events.
@implementation KlarnaStandaloneWebViewWrapper

// The property name in KlarnaStandaloneWebView that we want to observe for changes
NSString * const PROPERTY_NAME_ESTIMATED_PROGRESS = @"estimatedProgress";

- (id)init {
    self = [super init];
    [self initializeKlarnaStandaloneWebView];
    self.klarnaStandaloneWebView.delegate = self;
    [self.klarnaStandaloneWebView addObserver:self forKeyPath:PROPERTY_NAME_ESTIMATED_PROGRESS options:NSKeyValueObservingOptionNew context:nil];
    return self;
}

- (void)observeValueForKeyPath:(NSString *)keyPath ofObject:(id)object change:(NSDictionary *)change context:(void *)context {
    if ([keyPath isEqualToString:PROPERTY_NAME_ESTIMATED_PROGRESS]) {
        // newProgress is a value in range [0..1].
        NSNumber * newProgress = [change objectForKey:NSKeyValueChangeNewKey];
        // We need to convert it to an int value in range [0..100]
        int progress = (int) (newProgress.doubleValue * 100);
        [self sendProgressChangeEvent:progress];
    } else {
        [super observeValueForKeyPath:keyPath ofObject:object change:change context:context];
    }
}

- (void)sendProgressChangeEvent:(int)progress {
    if (!self.onProgressChange) {
        RCTLog(@"Missing 'onProgressChange' callback prop.");
        return;
    }
    self.onProgressChange(@{
            @"progressEvent": @{
                @"webViewState": @{
                    @"url": self.klarnaStandaloneWebView.url == nil ? @"" : self.klarnaStandaloneWebView.url.absoluteString,
                    @"title": self.klarnaStandaloneWebView.title == nil ? @"" : self.klarnaStandaloneWebView.title,
                    @"progress": [[NSNumber numberWithInt:progress] stringValue],
                @"isLoading": @(self.klarnaStandaloneWebView.isLoading)
            }
        }
    });

}

#pragma mark - React Native Overrides

- (void)setReturnUrl:(NSString *)returnUrl {
    _returnUrl = returnUrl;
    if (returnUrl.length > 0) {
        self.klarnaStandaloneWebView.returnURL = [NSURL URLWithString:self.returnUrl];
    }
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
    
    [NSLayoutConstraint activateConstraints:[[NSArray alloc] initWithObjects:
                                             [self.klarnaStandaloneWebView.topAnchor constraintEqualToAnchor:self.topAnchor],
                                             [self.klarnaStandaloneWebView.bottomAnchor constraintEqualToAnchor:self.bottomAnchor],
                                             [self.klarnaStandaloneWebView.leadingAnchor constraintEqualToAnchor:self.leadingAnchor],
                                             [self.klarnaStandaloneWebView.trailingAnchor constraintEqualToAnchor:self.trailingAnchor], nil
                                            ]];
}

- (void)layoutSubviews {
    [super layoutSubviews];
    self.klarnaStandaloneWebView.frame = self.bounds;
    [self.klarnaStandaloneWebView layoutSubviews];
}

#pragma mark - Klarna Standalone Web View Methods

- (void)load:(nonnull NSString *)url {
    [self.klarnaStandaloneWebView loadURL:[NSURL URLWithString:url]];
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
    if (!self.onBeforeLoad) {
        RCTLog(@"Missing 'onBeforeLoad' callback prop.");
        return;
    }
    int progress = (int) (webView.estimatedProgress * 100);
    self.onBeforeLoad(@{
        @"navigationEvent": @{
            @"event": @"loadStarted",
            @"newUrl": webView.url.absoluteString,
            @"webViewState": @{
                @"url": webView.url.absoluteString,
                @"title": webView.title,
                @"progress": [[NSNumber numberWithInt:progress] stringValue],
                @"isLoading": @(webView.isLoading)
            }
        }
    });
}

- (void)klarnaStandaloneWebView:(KlarnaStandaloneWebView * _Nonnull)webView didFinish:(WKNavigation * _Nonnull)navigation {
    if (!self.onLoad) {
        RCTLog(@"Missing 'onLoad' callback prop.");
        return;
    }
    int progress = (int) (webView.estimatedProgress * 100);
    self.onLoad(@{
        @"navigationEvent": @{
            @"event": @"loadEnded",
            @"newUrl": webView.url.absoluteString,
            @"webViewState": @{
                @"url": webView.url.absoluteString,
                @"title": webView.title,
                @"progress": [[NSNumber numberWithInt:progress] stringValue],
                @"isLoading": @(webView.isLoading)
            }
        }
    });
}

- (void)klarnaStandaloneWebView:(KlarnaStandaloneWebView * _Nonnull)webView didFailProvisionalNavigation:(WKNavigation * _Nonnull)navigation withError:(NSError * _Nonnull)error {
    if (!self.onLoadError) {
        RCTLog(@"Missing 'onLoadError' callback prop.");
        return;
    }
    self.onLoadError(@{
        @"navigationError": @{
            @"errorMessage": error.description,
        }
    });
}

- (void)klarnaStandaloneWebView:(KlarnaStandaloneWebView * _Nonnull)webView didFail:(WKNavigation * _Nonnull)navigation withError:(NSError * _Nonnull)error {
    if (!self.onLoadError) {
        RCTLog(@"Missing 'onLoadError' callback prop.");
        return;
    }
    self.onLoadError(@{
        @"navigationError": @{
            @"errorMessage": error.description,
        }
    });
}

@end

#endif

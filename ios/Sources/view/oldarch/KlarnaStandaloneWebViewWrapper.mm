#if !RCT_NEW_ARCH_ENABLED

#import "KlarnaStandaloneWebViewWrapper.h"

#import <KlarnaMobileSDK/KlarnaMobileSDK-Swift.h>
#import <React/RCTLog.h>

@interface KlarnaStandaloneWebViewWrapper () <KlarnaStandaloneWebViewDelegate>

@property (nonatomic, strong) KlarnaStandaloneWebView* klarnaStandaloneWebView;

@end

@implementation KlarnaStandaloneWebViewWrapper

- (id)init {
    self = [super init];
    [self initializeKlarnaStandaloneWebView];
    self.klarnaStandaloneWebView.delegate = self;
    return self;
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
        // TODO What should we do here?
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
    self.onBeforeLoad(@{});
}

- (void)klarnaStandaloneWebView:(KlarnaStandaloneWebView * _Nonnull)webView didFinish:(WKNavigation * _Nonnull)navigation {
    if (!self.onLoad) {
        RCTLog(@"Missing 'onLoad' callback prop.");
        return;
    }
    self.onLoad(@{});
}

@end

#endif

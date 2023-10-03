#if !RCT_NEW_ARCH_ENABLED

#import "KlarnaStandaloneWebViewWrapper.h"

#import <KlarnaMobileSDK/KlarnaMobileSDK-Swift.h>

@interface KlarnaStandaloneWebViewWrapper ()

@property (nonatomic, strong) KlarnaStandaloneWebView* klarnaStandaloneWebView;

@end

@implementation KlarnaStandaloneWebViewWrapper

#pragma mark - React Native Overrides

- (void) setReturnUrl:(NSString *)returnUrl {
    _returnUrl = returnUrl;
    if (returnUrl.length > 0) {
        self.klarnaStandaloneWebView.returnURL = [NSURL URLWithString:self.returnUrl];
    }
}

- (void) initializeKlarnaStandaloneWebView {
    if (self.returnUrl != nil && self.returnUrl.length > 0) {
        self.klarnaStandaloneWebView = [[KlarnaStandaloneWebView alloc] initWithReturnURL:[NSURL URLWithString:self.returnUrl]];
        [self.klarnaStandaloneWebView loadURL:[NSURL URLWithString:@"https://google.com"]];
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

@end

#endif

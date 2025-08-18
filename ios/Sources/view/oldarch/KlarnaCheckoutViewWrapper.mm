#if !RCT_NEW_ARCH_ENABLED

#import <React/RCTLog.h>
#import "../KlarnaCheckoutViewWrapper.h"
#import "../../common/RNMobileSDKUtils.h"
#import <KlarnaMobileSDK/KlarnaMobileSDK.h>
#import <KlarnaMobileSDK/KlarnaMobileSDK-Swift.h>

@interface KlarnaCheckoutViewWrapper () <KlarnaEventHandler, KlarnaSizingDelegate>

@property (nonatomic, strong) KlarnaCheckoutView* klarnaCheckoutView;
@property (nonatomic, assign) BOOL isCheckoutViewReadyEventSent;

@end

@implementation KlarnaCheckoutViewWrapper

#pragma mark - Initialization

- (instancetype)initWithFrame:(CGRect)frame
{
    self = [super initWithFrame:frame];
    if (self) {
        self.isCheckoutViewReadyEventSent = NO;
    }
    return self;
}

#pragma mark - React Native Property Setters

- (void) setReturnUrl:(NSString *)returnUrl {
    _returnUrl = returnUrl;
    if (returnUrl.length > 0) {
        self.klarnaCheckoutView.returnURL = [NSURL URLWithString:self.returnUrl];
    }
    [self evaluateProps];
}

- (void) evaluateProps {
    [self initializeActualCheckoutView];
}

#pragma mark - KlarnaCheckoutView Setup

- (void) initializeActualCheckoutView {
    self.isCheckoutViewReadyEventSent = NO;
    self.klarnaCheckoutView = [[KlarnaCheckoutView alloc] initWithReturnURL:[NSURL URLWithString:self.returnUrl] eventHandler:self];
    self.klarnaCheckoutView.sizingDelegate = self;
    
    self.klarnaCheckoutView.translatesAutoresizingMaskIntoConstraints = NO;
    
    [self addSubview:self.klarnaCheckoutView];

    [NSLayoutConstraint activateConstraints:[[NSArray alloc] initWithObjects:
                                             [self.klarnaCheckoutView.topAnchor constraintEqualToAnchor:self.topAnchor],
                                             [self.klarnaCheckoutView.bottomAnchor constraintEqualToAnchor:self.bottomAnchor],
                                             [self.klarnaCheckoutView.leadingAnchor constraintEqualToAnchor:self.leadingAnchor],
                                             [self.klarnaCheckoutView.trailingAnchor constraintEqualToAnchor:self.trailingAnchor], nil
                                            ]];
}

#pragma mark - Layout

- (void)layoutSubviews {
    [super layoutSubviews];
    self.klarnaCheckoutView.frame = self.bounds;
    [self.klarnaCheckoutView layoutSubviews];
}

#pragma mark - UIView Lifecycle

- (void)didMoveToWindow {
    [super didMoveToWindow];
    if (!self.window) {
        self.isCheckoutViewReadyEventSent = NO;
    }
}

#pragma mark - React Native Lifecycle

- (void)didSetProps:(NSArray<NSString *> *)changedProps {
    [super didSetProps:changedProps];
    [self sendCheckoutViewReadyEvent];
}

- (void)sendCheckoutViewReadyEvent {
    if (self.klarnaCheckoutView && self.onCheckoutViewReady && !self.isCheckoutViewReadyEventSent) {
        self.isCheckoutViewReadyEventSent = YES;
        self.onCheckoutViewReady(@{});
    }
}

#pragma mark - Methods Exposed to React Native

- (void)setSnippet:(NSString *)snippet {
    [self.klarnaCheckoutView setSnippet:snippet];
}

- (void)suspend {
    [self.klarnaCheckoutView suspend];
}

- (void)resume {
    [self.klarnaCheckoutView resume];
}

#pragma mark - KlarnaEventHandler

- (void)klarnaComponent:(id<KlarnaComponent>)klarnaComponent dispatchedEvent:(KlarnaProductEvent *)event {
    if (!self.onEvent) {
        RCTLog(@"Missing 'onEvent' callback prop.");
        return;
    }
    
    NSString *serializedParams = [SerializationUtil serializeDictionaryToJsonString:[event getParams]];
    
    self.onEvent(@{
        @"productEvent": @{
            @"action": event.action,
            @"params": serializedParams,
        }
    });
}

- (void)klarnaComponent:(id<KlarnaComponent>)klarnaComponent encounteredError:(KlarnaError *)error {
    if (!self.onError) {
        RCTLog(@"Missing 'onError' callback prop.");
        return;
    }
    
    self.onError(@{
        @"error": @{
            @"name": error.name,
            @"message": error.message,
            @"isFatal": [NSNumber numberWithBool:error.isFatal],
        }
    });
}

#pragma mark - KlarnaSizingDelegate

- (void)klarnaComponent:(id<KlarnaComponent>)klarnaComponent resizedToHeight:(CGFloat)height {
    if (!self.onResized) {
        RCTLog(@"Missing 'onResized' callback prop.");
        return;
    }
    
    self.onResized(@{
        @"height": [[NSNumber numberWithFloat:height] stringValue]
    });
}

@end

#endif

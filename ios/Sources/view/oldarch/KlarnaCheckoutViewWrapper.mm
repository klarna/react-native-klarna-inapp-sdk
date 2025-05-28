#if !RCT_NEW_ARCH_ENABLED

#import "../KlarnaCheckoutViewWrapper.h"
#import "../../common/RNMobileSDKUtils.h"

#import <KlarnaMobileSDK/KlarnaMobileSDK-Swift.h>
#import <React/RCTLog.h>

@interface KlarnaCheckoutViewWrapper () <KlarnaEventHandler, KlarnaSizingDelegate>

@property (nonatomic, strong) KlarnaCheckoutView* actualCheckoutView;
@property (nonatomic, assign) BOOL isCheckoutViewReadyEventSent;

@end

@implementation KlarnaCheckoutViewWrapper

- (instancetype)initWithFrame:(CGRect)frame
{
    self = [super initWithFrame:frame];
    if (self) {
        self.isCheckoutViewReadyEventSent = NO;
    }
    return self;
}

#pragma mark - React Native Overrides

- (void) setReturnUrl:(NSString *)returnUrl {
    _returnUrl = returnUrl;
    if (returnUrl.length > 0) {
        self.actualCheckoutView.returnURL = [NSURL URLWithString:self.returnUrl];
    }
    [self evaluateProps];
}

- (void) evaluateProps {
    [self initializeActualCheckoutView];
}

- (void) initializeActualCheckoutView {
    self.isCheckoutViewReadyEventSent = NO;
    self.actualCheckoutView = [[KlarnaCheckoutView alloc] initWithReturnURL:[NSURL URLWithString:self.returnUrl] eventHandler:self];
    self.actualCheckoutView.sizingDelegate = self;
    
    self.actualCheckoutView.translatesAutoresizingMaskIntoConstraints = NO;
    
    [self addSubview:self.actualCheckoutView];

    [NSLayoutConstraint activateConstraints:[[NSArray alloc] initWithObjects:
                                             [self.actualCheckoutView.topAnchor constraintEqualToAnchor:self.topAnchor],
                                             [self.actualCheckoutView.bottomAnchor constraintEqualToAnchor:self.bottomAnchor],
                                             [self.actualCheckoutView.leadingAnchor constraintEqualToAnchor:self.leadingAnchor],
                                             [self.actualCheckoutView.trailingAnchor constraintEqualToAnchor:self.trailingAnchor], nil
                                            ]];
}

- (void)layoutSubviews {
    [super layoutSubviews];
    self.actualCheckoutView.frame = self.bounds;
    [self.actualCheckoutView layoutSubviews];
}

- (void)didMoveToWindow {
    [super didMoveToWindow];
    if (!self.window) {
        self.isCheckoutViewReadyEventSent = NO;
    }
}

- (void)didSetProps:(NSArray<NSString *> *)changedProps {
    [super didSetProps:changedProps];
    [self sendCheckoutViewReadyEvent];
}

- (void)sendCheckoutViewReadyEvent {
    if (self.actualCheckoutView && self.onCheckoutViewReady && !self.isCheckoutViewReadyEventSent) {
        self.onCheckoutViewReady(@{});
        self.isCheckoutViewReadyEventSent = YES;
    }
}

#pragma mark - KlarnaCheckoutView Methods

- (void)setSnippet:(NSString *)snippet {
    [self.actualCheckoutView setSnippet:snippet];
}

- (void)suspend {
    [self.actualCheckoutView suspend];
}

- (void)resume {
    [self.actualCheckoutView resume];
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

//
//  PaymentViewWrapper.m
//  react-native-klarna-payment-view
//
//  Created by Gabriel Banfalvi on 2019-07-24.
//

#import "PaymentViewWrapper.h"

#import <AVFoundation/AVFoundation.h>
#import <KlarnaMobileSDK/KlarnaMobileSDK-Swift.h>
#import <React/RCTLog.h>

#ifdef RCT_NEW_ARCH_ENABLED
#import <react/renderer/components/RNKlarnaPaymentViewSpec/ComponentDescriptors.h>
#import <react/renderer/components/RNKlarnaPaymentViewSpec/EventEmitters.h>
#import <react/renderer/components/RNKlarnaPaymentViewSpec/Props.h>
#import <react/renderer/components/RNKlarnaPaymentViewSpec/RCTComponentViewHelpers.h>

#import "RCTFabricComponentsPlugins.h"
#endif

#ifdef RCT_NEW_ARCH_ENABLED
using namespace facebook::react;

@interface PaymentViewWrapper () <KlarnaPaymentEventListener, RCTRNKlarnaPaymentViewViewProtocol>
#else
@interface PaymentViewWrapper () <KlarnaPaymentEventListener>
#endif

@property (nonatomic, strong) KlarnaPaymentView* actualPaymentView;


@end


@implementation PaymentViewWrapper

#pragma mark - React Native Overrides

- (void) setCategory:(NSString *)category {
    _category = category;
    [self evaluateProps];
}

- (void) evaluateProps {
    if (self.category != nil) {
        [self initializeActualPaymentView];
    }
}

- (void) initializeActualPaymentView {
    self.actualPaymentView = [[KlarnaPaymentView alloc] initWithCategory:self.category eventListener:self];
    self.actualPaymentView.translatesAutoresizingMaskIntoConstraints = NO;
        
    
    [self addSubview:self.actualPaymentView];
    
    [NSLayoutConstraint activateConstraints:[[NSArray alloc] initWithObjects:
                                                 [self.actualPaymentView.topAnchor constraintEqualToAnchor:self.topAnchor],
                                             [self.actualPaymentView.bottomAnchor constraintEqualToAnchor:self.bottomAnchor],
                                             [self.actualPaymentView.leadingAnchor constraintEqualToAnchor:self.leadingAnchor],
                                             [self.actualPaymentView.trailingAnchor constraintEqualToAnchor:self.trailingAnchor], nil
                                            ]];
}

- (void)layoutSubviews {
    [super layoutSubviews];
    self.actualPaymentView.frame = self.bounds;
    [self.actualPaymentView layoutSubviews];
}

#pragma mark - Payment View Methods

- (void)initializePaymentViewWithClientToken:(NSString *)clientToken withReturnUrl:(NSString *)returnUrl{
    NSURL* url = [NSURL URLWithString:returnUrl];
    [self.actualPaymentView initializeWithClientToken:clientToken returnUrl:url];
}

- (void)loadPaymentViewWithSessionData:(NSString*)sessionData {
    [self.actualPaymentView loadWithJsonData:sessionData.length > 0 ? sessionData : nil];
}

- (void)loadPaymentReview {
    [self.actualPaymentView loadPaymentReview];
}


- (void)authorizePaymentViewWithAutoFinalize:(BOOL)autoFinalize sessionData:(NSString*)sessionData {
    // TODO: Find out if it's possible to dclare string | null type in codegen
    [self.actualPaymentView authorizeWithAutoFinalize:autoFinalize jsonData:sessionData.length > 0 ? sessionData : nil];
}

- (void)reauthorizePaymentViewWithSessionData:(NSString*)sessionData {
    [self.actualPaymentView reauthorizeWithJsonData:sessionData.length > 0 ? sessionData : nil];
}

- (void)finalizePaymentViewWithSessionData:(NSString*)sessionData {
    [self.actualPaymentView finaliseWithJsonData:sessionData.length > 0 ? sessionData : nil];
    
}

#pragma mark - Klarna PaymentEventListener


- (void)klarnaInitializedWithPaymentView:(KlarnaPaymentView * _Nonnull)paymentView {
#ifdef RCT_NEW_ARCH_ENABLED
    if(_eventEmitter){
        std::dynamic_pointer_cast<const RNKlarnaPaymentViewEventEmitter>(_eventEmitter)
        ->onInitialized(RNKlarnaPaymentViewEventEmitter::OnInitialized{});
    }
#else
    if (!self.onInitialized) {
        RCTLog(@"Missing 'onInitialized' callback prop.");
        return;
    }
    
    self.onInitialized(@{});
#endif
}

- (void)klarnaLoadedWithPaymentView:(KlarnaPaymentView * _Nonnull)paymentView {
#ifdef RCT_NEW_ARCH_ENABLED
    if(_eventEmitter){
        std::dynamic_pointer_cast<const RNKlarnaPaymentViewEventEmitter>(_eventEmitter)
        ->onLoaded(RNKlarnaPaymentViewEventEmitter::OnLoaded{});
    }
#else
    if (!self.onLoaded) {
        RCTLog(@"Missing 'onLoaded' callback prop.");
        return;
    }
    
    self.onLoaded(@{});
#endif
}

- (void)klarnaLoadedPaymentReviewWithPaymentView:(KlarnaPaymentView * _Nonnull)paymentView {
#ifdef RCT_NEW_ARCH_ENABLED
    if(_eventEmitter){
        std::dynamic_pointer_cast<const RNKlarnaPaymentViewEventEmitter>(_eventEmitter)
        ->onLoadedPaymentReview(RNKlarnaPaymentViewEventEmitter::OnLoadedPaymentReview{});
    }
#else
    if (!self.onLoadedPaymentReview) {
        RCTLog(@"Missing 'onLoadedPaymentReview' callback prop.");
        return;
    }
    
    self.onLoadedPaymentReview(@{});
#endif
}

- (void)klarnaAuthorizedWithPaymentView:(KlarnaPaymentView * _Nonnull)paymentView approved:(BOOL)approved authToken:(NSString * _Nullable)authToken finalizeRequired:(BOOL)finalizeRequired {
#ifdef RCT_NEW_ARCH_ENABLED
    if(_eventEmitter){
        std::dynamic_pointer_cast<const RNKlarnaPaymentViewEventEmitter>(_eventEmitter)
        ->onAuthorized(RNKlarnaPaymentViewEventEmitter::OnAuthorized{
            .approved = approved,
            .authToken = authToken == nil ? "" : std::string([authToken UTF8String]),
            .finalizeRequired = finalizeRequired
        });
    }
#else
    if (!self.onAuthorized) {
        RCTLog(@"Missing 'onAuthorized' callback prop.");
        return;
    }
    
    self.onAuthorized(@{
        @"approved": [NSNumber numberWithBool:approved],
        @"authToken": authToken ? authToken : NSNull.null,
        @"finalizeRequired": [NSNumber numberWithBool:finalizeRequired]
    });
#endif
    
}
- (void)klarnaReauthorizedWithPaymentView:(KlarnaPaymentView * _Nonnull)paymentView approved:(BOOL)approved authToken:(NSString * _Nullable)authToken {
#ifdef RCT_NEW_ARCH_ENABLED
    if(_eventEmitter){
        std::dynamic_pointer_cast<const RNKlarnaPaymentViewEventEmitter>(_eventEmitter)
        ->onReauthorized(RNKlarnaPaymentViewEventEmitter::OnReauthorized{
            .approved = approved,
            .authToken = authToken == nil ? "" : std::string([authToken UTF8String]),
        });
    }
#else
    if (!self.onReauthorized) {
        RCTLog(@"Missing 'onReauthorized' callback prop.");
        return;
    }
    
    self.onReauthorized(@{
        @"approved": [NSNumber numberWithBool:approved],
        @"authToken": authToken ? authToken : NSNull.null,
    });
#endif
}

- (void)klarnaFinalizedWithPaymentView:(KlarnaPaymentView * _Nonnull)paymentView approved:(BOOL)approved authToken:(NSString * _Nullable)authToken {
#ifdef RCT_NEW_ARCH_ENABLED
    if(_eventEmitter){
        std::dynamic_pointer_cast<const RNKlarnaPaymentViewEventEmitter>(_eventEmitter)
        ->onFinalized(RNKlarnaPaymentViewEventEmitter::OnFinalized{
            .approved = approved,
            .authToken = authToken == nil ? "" : std::string([authToken UTF8String]),
        });
    }
#else
    if (!self.onFinalized) {
        RCTLog(@"Missing 'onFinalized' callback prop.");
        return;
    }
    
    self.onFinalized(@{
        @"approved": [NSNumber numberWithBool:approved],
        @"authToken": authToken ? authToken : NSNull.null,
    });
#endif
}

- (void)klarnaFailedInPaymentView:(KlarnaPaymentView * _Nonnull)paymentView withError:(KlarnaPaymentError * _Nonnull)error {
#ifdef RCT_NEW_ARCH_ENABLED
    if(_eventEmitter){
        std::dynamic_pointer_cast<const RNKlarnaPaymentViewEventEmitter>(_eventEmitter)
        ->onError(RNKlarnaPaymentViewEventEmitter::OnError{
            .error = {
                .action = error.action == nil ? "" : std::string([error.action UTF8String]),
                .isFatal = error.isFatal,
                .message = error.message == nil ? "" : std::string([error.message UTF8String]),
                .name = error.message == nil ? "" : std::string([error.name UTF8String]),
            }
        });
    }
#else
    if (!self.onError) {
        RCTLog(@"Missing 'onError' callback prop.");
        return;
    }
    
    self.onError(@{
        @"error": @{
            @"action": error.action,
            @"isFatal": [NSNumber numberWithBool:error.isFatal],
            @"message": error.message,
            @"name": error.name
        }
    });
#endif
}

- (void)klarnaResizedWithPaymentView:(KlarnaPaymentView * _Nonnull)paymentView to:(CGFloat)newHeight {
#ifdef RCT_NEW_ARCH_ENABLED
    if(_eventEmitter){
        std::dynamic_pointer_cast<const RNKlarnaPaymentViewEventEmitter>(_eventEmitter)
        ->onWebviewHeightChanged(RNKlarnaPaymentViewEventEmitter::OnWebviewHeightChanged{
            .height = static_cast<int>(newHeight)
        });
    }
#else
    [self.uiManager setIntrinsicContentSize:CGSizeMake(UIViewNoIntrinsicMetric, newHeight) forView:self];
#endif
}

#ifdef RCT_NEW_ARCH_ENABLED
- (instancetype)initWithFrame:(CGRect)frame
{
    self = [super initWithFrame:frame];
    if (self) {
        static const auto defaultProps = std::make_shared<const RNKlarnaPaymentViewProps>();
        _props = defaultProps;
        
    }
    
    return self;
}

+ (ComponentDescriptorProvider)componentDescriptorProvider
{
    return concreteComponentDescriptorProvider<RNKlarnaPaymentViewComponentDescriptor>();
}

Class<RCTComponentViewProtocol> RNKlarnaPaymentViewCls(void)
{
    return PaymentViewWrapper.class;
}

// why we need this func -> https://reactnative.dev/docs/next/the-new-architecture/pillars-fabric-components#write-the-native-ios-code
- (void)updateProps:(Props::Shared const &)props oldProps:(Props::Shared const &)oldProps
{
    const auto &oldViewProps = *std::static_pointer_cast<RNKlarnaPaymentViewProps const>(_props);
    const auto &newViewProps = *std::static_pointer_cast<RNKlarnaPaymentViewProps const>(props);
    
    //Check every prop and update it if needed
    if (oldViewProps.category != newViewProps.category) {
        NSString * categoryConver = [[NSString alloc] initWithUTF8String: newViewProps.category.c_str()];
        [self setCategory: categoryConver];
    }
    
    
    
    [super updateProps:props oldProps:oldProps];
}

- (void)handleCommand:(const NSString *)commandName args:(const NSArray *)args {
    RCTRNKlarnaPaymentViewHandleCommand(self, commandName, args);
}


- (void)authorize:(BOOL)autoFinalize sessionData:(nonnull NSString *)sessionData {
    [self authorizePaymentViewWithAutoFinalize: autoFinalize sessionData:sessionData];
}


- (void)finalize:(nonnull NSString *)sessionData {
    [self finalizePaymentViewWithSessionData:sessionData];
}


- (void)initialize:(nonnull NSString *)clientToken returnUrl:(nonnull NSString *)returnUrl {
    [self initializePaymentViewWithClientToken:clientToken withReturnUrl:returnUrl];
}


- (void)load:(nonnull NSString *)sessionData {
    [self loadPaymentViewWithSessionData:sessionData];
}


- (void)reauthorize:(nonnull NSString *)sessionData {
    [self reauthorizePaymentViewWithSessionData:sessionData];
}

#endif


@end

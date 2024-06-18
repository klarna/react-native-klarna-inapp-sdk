#if RCT_NEW_ARCH_ENABLED

#import "../PaymentViewWrapper.h"

#import <AVFoundation/AVFoundation.h>
#import <KlarnaMobileSDK/KlarnaMobileSDK-Swift.h>
#import <React/RCTLog.h>

#import <react/renderer/components/RNKlarnaMobileSDK/ComponentDescriptors.h>
#import <react/renderer/components/RNKlarnaMobileSDK/EventEmitters.h>
#import <react/renderer/components/RNKlarnaMobileSDK/Props.h>
#import <react/renderer/components/RNKlarnaMobileSDK/RCTComponentViewHelpers.h>

#import "RCTFabricComponentsPlugins.h"

using namespace facebook::react;

@interface PaymentViewWrapper () <KlarnaPaymentEventListener, RCTRNKlarnaPaymentViewViewProtocol>

@property (nonatomic, strong) KlarnaPaymentView* actualPaymentView;

@end

@implementation PaymentViewWrapper

#pragma mark - React Native Overrides

- (void) initializeActualPaymentView:(NSString*)category withReturnUrl:(NSString*)returnUrl {
    if (returnUrl.length > 0) {
        self.actualPaymentView = [[KlarnaPaymentView alloc] initWithCategory:category returnUrl:[NSURL URLWithString:returnUrl]  eventListener:self];
    } else {
        self.actualPaymentView = [[KlarnaPaymentView alloc] initWithCategory:category eventListener:self];
    }
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
    if (returnUrl.length > 0) {
        [self.actualPaymentView initializeWithClientToken:clientToken returnUrl:[NSURL URLWithString:returnUrl]];
    } else {
        [self.actualPaymentView initializeWithClientToken:clientToken];
    }
}

- (void)loadPaymentViewWithSessionData:(NSString*)sessionData {
    [self.actualPaymentView loadWithJsonData:sessionData.length > 0 ? sessionData : nil];
}

- (void)loadPaymentReviewPaymentView {
    [self.actualPaymentView loadPaymentReview];
}

- (void)authorizePaymentViewWithAutoFinalize:(BOOL)autoFinalize sessionData:(NSString*)sessionData {
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
    if(_eventEmitter){
        std::dynamic_pointer_cast<const RNKlarnaPaymentViewEventEmitter>(_eventEmitter)
        ->onInitialized(RNKlarnaPaymentViewEventEmitter::OnInitialized{});
    }
}

- (void)klarnaLoadedWithPaymentView:(KlarnaPaymentView * _Nonnull)paymentView {
    if(_eventEmitter){
        std::dynamic_pointer_cast<const RNKlarnaPaymentViewEventEmitter>(_eventEmitter)
        ->onLoaded(RNKlarnaPaymentViewEventEmitter::OnLoaded{});
    }
}

- (void)klarnaLoadedPaymentReviewWithPaymentView:(KlarnaPaymentView * _Nonnull)paymentView {
    if(_eventEmitter){
        std::dynamic_pointer_cast<const RNKlarnaPaymentViewEventEmitter>(_eventEmitter)
        ->onLoadedPaymentReview(RNKlarnaPaymentViewEventEmitter::OnLoadedPaymentReview{});
    }
}

- (void)klarnaAuthorizedWithPaymentView:(KlarnaPaymentView * _Nonnull)paymentView approved:(BOOL)approved authToken:(NSString * _Nullable)authToken finalizeRequired:(BOOL)finalizeRequired {
    if(_eventEmitter){
        std::dynamic_pointer_cast<const RNKlarnaPaymentViewEventEmitter>(_eventEmitter)
        ->onAuthorized(RNKlarnaPaymentViewEventEmitter::OnAuthorized{
            .approved = approved,
            .authToken = authToken == nil ? "" : std::string([authToken UTF8String]),
            .finalizeRequired = finalizeRequired
        });
    }
}
- (void)klarnaReauthorizedWithPaymentView:(KlarnaPaymentView * _Nonnull)paymentView approved:(BOOL)approved authToken:(NSString * _Nullable)authToken {
    if(_eventEmitter){
        std::dynamic_pointer_cast<const RNKlarnaPaymentViewEventEmitter>(_eventEmitter)
        ->onReauthorized(RNKlarnaPaymentViewEventEmitter::OnReauthorized{
            .approved = approved,
            .authToken = authToken == nil ? "" : std::string([authToken UTF8String]),
        });
    }
}

- (void)klarnaFinalizedWithPaymentView:(KlarnaPaymentView * _Nonnull)paymentView approved:(BOOL)approved authToken:(NSString * _Nullable)authToken {
    if(_eventEmitter){
        std::dynamic_pointer_cast<const RNKlarnaPaymentViewEventEmitter>(_eventEmitter)
        ->onFinalized(RNKlarnaPaymentViewEventEmitter::OnFinalized{
            .approved = approved,
            .authToken = authToken == nil ? "" : std::string([authToken UTF8String]),
        });
    }
}

- (void)klarnaFailedInPaymentView:(KlarnaPaymentView * _Nonnull)paymentView withError:(KlarnaPaymentError * _Nonnull)error {
    if(_eventEmitter){
        std::dynamic_pointer_cast<const RNKlarnaPaymentViewEventEmitter>(_eventEmitter)
        ->onError(RNKlarnaPaymentViewEventEmitter::OnError{
            .error = {
                .action = std::string([error.action UTF8String]),
                .isFatal = error.isFatal,
                .message = std::string([error.message UTF8String]),
                .name = std::string([error.name UTF8String]),
            }
        });
    }
}

- (void)klarnaResizedWithPaymentView:(KlarnaPaymentView * _Nonnull)paymentView to:(CGFloat)newHeight {
    if(_eventEmitter){
        std::dynamic_pointer_cast<const RNKlarnaPaymentViewEventEmitter>(_eventEmitter)
        ->onResized(RNKlarnaPaymentViewEventEmitter::OnResized{
            .height = std::string([[[NSNumber numberWithFloat:newHeight] stringValue] UTF8String])
        });
    }
}

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

- (void)updateProps:(Props::Shared const &)props oldProps:(Props::Shared const &)oldProps
{
    const auto &oldViewProps = *std::static_pointer_cast<RNKlarnaPaymentViewProps const>(_props);
    const auto &newViewProps = *std::static_pointer_cast<RNKlarnaPaymentViewProps const>(props);
    
    if (self.actualPaymentView == nil && newViewProps.category.length() > 0) {
        NSString * categoryConver = [[NSString alloc] initWithUTF8String: newViewProps.category.c_str()];
        NSString * returnUrlConver = [[NSString alloc] initWithUTF8String: newViewProps.returnUrl.c_str()];
        [self initializeActualPaymentView:categoryConver withReturnUrl:returnUrlConver];
    } else {
        if (oldViewProps.category != newViewProps.category) {
                NSString * categoryConver = [[NSString alloc] initWithUTF8String: newViewProps.category.c_str()];
        //        not supported
        //        self.actualPaymentView.category = newViewProps.category
        }
        
        if (oldViewProps.returnUrl != newViewProps.returnUrl) {
            NSString * returnUrlConver = [[NSString alloc] initWithUTF8String: newViewProps.returnUrl.c_str()];
            self.actualPaymentView.returnURL = [NSURL URLWithString:returnUrlConver];
        }
    }
    
    
    [super updateProps:props oldProps:oldProps];
}

- (void)handleCommand:(const NSString *)commandName args:(const NSArray *)args {
    RCTRNKlarnaPaymentViewHandleCommand(self, commandName, args);
}

#pragma mark - RCTRNKlarnaPaymentViewViewProtocol

- (void)initialize:(nonnull NSString *)clientToken returnUrl:(nonnull NSString *)returnUrl {
    [self initializePaymentViewWithClientToken:clientToken withReturnUrl:returnUrl];
}

- (void)load:(nonnull NSString *)sessionData {
    [self loadPaymentViewWithSessionData:sessionData];
}

- (void)loadPaymentReview {
    [self loadPaymentReviewPaymentView];
}

- (void)authorize:(BOOL)autoFinalize sessionData:(nonnull NSString *)sessionData {
    [self authorizePaymentViewWithAutoFinalize: autoFinalize sessionData:sessionData];
}

- (void)finalize:(nonnull NSString *)sessionData {
    [self finalizePaymentViewWithSessionData:sessionData];
}

- (void)reauthorize:(nonnull NSString *)sessionData {
    [self reauthorizePaymentViewWithSessionData:sessionData];
}

@end

#endif

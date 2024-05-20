#if !RCT_NEW_ARCH_ENABLED

#import <React/RCTUIManager.h>

#import <AVFoundation/AVFoundation.h>
#import <UIKit/UIKit.h>
#import <React/RCTView.h>
#import <React/RCTUIManager.h>

NS_ASSUME_NONNULL_BEGIN

@interface KlarnaCheckoutViewWrapper : UIView

@property (nonatomic, copy) RCTDirectEventBlock onEvent;
@property (nonatomic, copy) RCTDirectEventBlock onError;
@property (nonatomic, copy) RCTDirectEventBlock onResized;

@property (nonatomic, strong) NSString* returnUrl;

#pragma mark - React Native Overrides
- (void) setReturnUrl:(NSString * _Nonnull)returnUrl;
- (void) evaluateProps;
- (void) initializeActualCheckoutView;

@property (nonatomic, weak) RCTUIManager* uiManager;

- (void)setSnippet:(NSString*)snippet;

- (void)suspend;

- (void)resume;

@end

NS_ASSUME_NONNULL_END

#endif

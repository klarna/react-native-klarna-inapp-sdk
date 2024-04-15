#if RCT_NEW_ARCH_ENABLED

#import <React/RCTUIManager.h>
#import <React/RCTViewComponentView.h>
#import <UIKit/UIKit.h>

NS_ASSUME_NONNULL_BEGIN

@interface KlarnaCheckoutViewWrapper : RCTViewComponentView

#pragma mark - React Native Overrides

@property (nonatomic, weak) RCTUIManager* uiManager;

- (void)setSnippet:(NSString*)snippet;

- (void)suspend;

- (void)resume;

@end


NS_ASSUME_NONNULL_END

#endif

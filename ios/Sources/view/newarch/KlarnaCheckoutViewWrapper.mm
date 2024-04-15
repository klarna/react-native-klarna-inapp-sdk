#if RCT_NEW_ARCH_ENABLED

#import "KlarnaCheckoutViewWrapper.h"

#import <AVFoundation/AVFoundation.h>
#import <KlarnaMobileSDK/KlarnaMobileSDK-Swift.h>
#import <React/RCTLog.h>

#import <react/renderer/components/RNKlarnaMobileSDK/ComponentDescriptors.h>
#import <react/renderer/components/RNKlarnaMobileSDK/EventEmitters.h>
#import <react/renderer/components/RNKlarnaMobileSDK/Props.h>
#import <react/renderer/components/RNKlarnaMobileSDK/RCTComponentViewHelpers.h>

#import "RCTFabricComponentsPlugins.h"

using namespace facebook::react;

@interface KlarnaCheckoutViewWrapper () <KlarnaEventHandler, RCTRNKlarnaCheckoutViewViewProtocol>

@property (nonatomic, strong) KlarnaCheckoutView* actualCheckoutView;

@end

@implementation KlarnaCheckoutViewWrapper

// TODO

@end

#endif

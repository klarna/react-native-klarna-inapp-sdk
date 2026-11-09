#import "KlarnaNetworkMessaging.h"
// Framework form resolves under use_frameworks!; quoted form is the headermap fallback for static builds.
#if __has_include(<react_native_klarna_network_messaging/react_native_klarna_network_messaging-Swift.h>)
#import <react_native_klarna_network_messaging/react_native_klarna_network_messaging-Swift.h>
#else
#import "react_native_klarna_network_messaging-Swift.h"
#endif

@interface KlarnaNetworkMessaging ()

@property (nonatomic, strong) KlarnaNetworkMessagingModuleImpl *impl;

@end

@implementation KlarnaNetworkMessaging

- (KlarnaNetworkMessagingModuleImpl *)impl {
    if (!_impl) {
        _impl = [KlarnaNetworkMessagingModuleImpl new];
    }
    return _impl;
}

- (std::shared_ptr<facebook::react::TurboModule>)getTurboModule:
    (const facebook::react::ObjCTurboModule::InitParams &)params
{
    return std::make_shared<facebook::react::NativeKlarnaNetworkMessagingSpecJSI>(params);
}

+ (NSString *)moduleName
{
    return @"KlarnaNetworkMessaging";
}

# pragma mark - NativeKlarnaNetworkMessagingSpec

- (void)initiatePrequalification:(nonnull NSString *)instanceId
                            data:(NSDictionary *)data
                         resolve:(nonnull RCTPromiseResolveBlock)resolve
                          reject:(nonnull RCTPromiseRejectBlock)reject
{
    [self.impl initiatePrequalificationWithInstanceId:instanceId
                                                 data:data
                                              resolve:resolve
                                               reject:reject];
}

- (void)dispose:(nonnull NSString *)instanceId
        resolve:(nonnull RCTPromiseResolveBlock)resolve
         reject:(nonnull RCTPromiseRejectBlock)reject
{
    [self.impl disposeWithInstanceId:instanceId resolve:resolve reject:reject];
}

@end

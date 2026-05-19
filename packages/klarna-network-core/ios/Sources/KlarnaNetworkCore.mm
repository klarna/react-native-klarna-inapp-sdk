#import "KlarnaNetworkCore.h"

// Framework form resolves under use_frameworks!; quoted form is the headermap fallback for static builds.
#if __has_include(<react_native_klarna_network_core/react_native_klarna_network_core-Swift.h>)
#import <react_native_klarna_network_core/react_native_klarna_network_core-Swift.h>
#else
#import "react_native_klarna_network_core-Swift.h"
#endif

@interface KlarnaNetworkCore ()

@property (nonatomic, strong) KlarnaNetworkCoreModuleImpl *impl;

@end

@implementation KlarnaNetworkCore

- (KlarnaNetworkCoreModuleImpl *)impl {
    if (!_impl) {
        _impl = [KlarnaNetworkCoreModuleImpl new];
    }
    return _impl;
}

- (std::shared_ptr<facebook::react::TurboModule>)getTurboModule:
    (const facebook::react::ObjCTurboModule::InitParams &)params
{
    return std::make_shared<facebook::react::NativeKlarnaNetworkCoreSpecJSI>(params);
}

+ (NSString *)moduleName
{
  return @"KlarnaNetworkCore";
}

# pragma mark - NativeKlarnaNetworkCoreSpec

- (void)initialize:(nonnull NSString *)instanceId
     configuration:(JS::NativeKlarnaNetworkCore::KlarnaConfigurationSpec &)configuration
           resolve:(nonnull RCTPromiseResolveBlock)resolve
            reject:(nonnull RCTPromiseRejectBlock)reject {
    NSString *clientId = configuration.clientId();
    NSString *appReturnUrl = configuration.appReturnUrl();
    NSString * _Nullable accountId = configuration.accountId();
    NSString * _Nullable locale = configuration.locale();
    NSString * _Nullable klarnaNetworkSessionToken = configuration.klarnaNetworkSessionToken();

    [self.impl initializeWithInstanceId:instanceId
                               clientId:clientId
                           appReturnUrl:appReturnUrl
                              accountId:accountId
                                 locale:locale
              klarnaNetworkSessionToken:klarnaNetworkSessionToken
                                resolve:resolve
                                 reject:reject];
}

- (void)getSessionToken:(nonnull NSString *)instanceId
                resolve:(nonnull RCTPromiseResolveBlock)resolve
                 reject:(nonnull RCTPromiseRejectBlock)reject {
    [self.impl getSessionTokenWithInstanceId:instanceId resolve:resolve reject:reject];
}

- (void)clearSession:(nonnull NSString *)instanceId
             resolve:(nonnull RCTPromiseResolveBlock)resolve
              reject:(nonnull RCTPromiseRejectBlock)reject {
    [self.impl clearSessionWithInstanceId:instanceId resolve:resolve reject:reject];
}

- (void)handleReturnUrl:(nonnull NSString *)url
                resolve:(nonnull RCTPromiseResolveBlock)resolve
                 reject:(nonnull RCTPromiseRejectBlock)reject {
    [self.impl handleReturnUrlWithUrlString:url resolve:resolve reject:reject];
}

- (void)setIntegrationMetadata:(nonnull NSString *)instanceId
                      metadata:(JS::NativeKlarnaNetworkCore::KlarnaIntegrationMetadataSpec &)metadata {
    auto integrator = metadata.integrator();
    NSString *integratorName = integrator.name();
    NSString *integratorSessionReference = integrator.sessionReference();
    NSString * _Nullable integratorModuleName = integrator.moduleName();
    NSString * _Nullable integratorModuleVersion = integrator.moduleVersion();

    NSMutableArray<NSDictionary<NSString *, NSString *> *> *originatorsArray = nil;
    if (metadata.originators().has_value()) {
        originatorsArray = [NSMutableArray new];
        for (const auto &orig : metadata.originators().value()) {
            NSMutableDictionary<NSString *, NSString *> *dict = [NSMutableDictionary new];
            dict[@"name"] = orig.name();
            dict[@"sessionReference"] = orig.sessionReference();
            if (orig.moduleName()) {
                dict[@"moduleName"] = orig.moduleName();
            }
            if (orig.moduleVersion()) {
                dict[@"moduleVersion"] = orig.moduleVersion();
            }
            [originatorsArray addObject:dict];
        }
    }

    [self.impl setIntegrationMetadataWithInstanceId:instanceId
                                    integratorName:integratorName
                            integratorSessionReference:integratorSessionReference
                                 integratorModuleName:integratorModuleName
                                integratorModuleVersion:integratorModuleVersion
                                           originators:originatorsArray];
}

- (void)dispose:(nonnull NSString *)instanceId
        resolve:(nonnull RCTPromiseResolveBlock)resolve
         reject:(nonnull RCTPromiseRejectBlock)reject {
    [self.impl disposeWithInstanceId:instanceId resolve:resolve reject:reject];
}

@end

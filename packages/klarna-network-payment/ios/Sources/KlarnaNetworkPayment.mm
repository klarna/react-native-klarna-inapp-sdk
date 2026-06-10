#import "KlarnaNetworkPayment.h"
// Framework form resolves under use_frameworks!; quoted form is the headermap fallback for static builds.
#if __has_include(<react_native_klarna_network_payment/react_native_klarna_network_payment-Swift.h>)
#import <react_native_klarna_network_payment/react_native_klarna_network_payment-Swift.h>
#else
#import "react_native_klarna_network_payment-Swift.h"
#endif

@interface KlarnaNetworkPayment ()

@property (nonatomic, strong) KlarnaNetworkPaymentModuleImpl *impl;

@end

@implementation KlarnaNetworkPayment

- (KlarnaNetworkPaymentModuleImpl *)impl {
    if (!_impl) {
        _impl = [KlarnaNetworkPaymentModuleImpl new];
    }
    return _impl;
}

- (std::shared_ptr<facebook::react::TurboModule>)getTurboModule:
    (const facebook::react::ObjCTurboModule::InitParams &)params
{
    return std::make_shared<facebook::react::NativeKlarnaNetworkPaymentSpecJSI>(params);
}

+ (NSString *)moduleName
{
    return @"KlarnaNetworkPayment";
}

# pragma mark - NativeKlarnaNetworkPaymentSpec

- (void)initiateWithId:(nonnull NSString *)instanceId
      paymentRequestId:(nonnull NSString *)paymentRequestId
               resolve:(nonnull RCTPromiseResolveBlock)resolve
                reject:(nonnull RCTPromiseRejectBlock)reject
{
    [self.impl initiateWithIdWithInstanceId:instanceId
                           paymentRequestId:paymentRequestId
                                    resolve:resolve
                                     reject:reject];
}

- (void)initiateWithData:(nonnull NSString *)instanceId
                    data:(NSDictionary *)data
                 resolve:(nonnull RCTPromiseResolveBlock)resolve
                  reject:(nonnull RCTPromiseRejectBlock)reject
{
    [self.impl initiateWithDataWithInstanceId:instanceId
                                         data:data
                                      resolve:resolve
                                       reject:reject];
}

- (void)fetch:(nonnull NSString *)instanceId
paymentRequestId:(nonnull NSString *)paymentRequestId
      resolve:(nonnull RCTPromiseResolveBlock)resolve
       reject:(nonnull RCTPromiseRejectBlock)reject
{
    [self.impl fetchWithInstanceId:instanceId
                  paymentRequestId:paymentRequestId
                           resolve:resolve
                            reject:reject];
}

- (void)cancel:(nonnull NSString *)instanceId
paymentRequestId:(nonnull NSString *)paymentRequestId
       resolve:(nonnull RCTPromiseResolveBlock)resolve
        reject:(nonnull RCTPromiseRejectBlock)reject
{
    [self.impl cancelWithInstanceId:instanceId
                   paymentRequestId:paymentRequestId
                            resolve:resolve
                             reject:reject];
}

- (void)presentationFetch:(nonnull NSString *)instanceId
                     data:(NSDictionary *)data
                  resolve:(nonnull RCTPromiseResolveBlock)resolve
                   reject:(nonnull RCTPromiseRejectBlock)reject
{
    [self.impl presentationFetchWithInstanceId:instanceId
                                          data:data
                                       resolve:resolve
                                        reject:reject];
}

- (void)presentationHandleLink:(nonnull NSString *)instanceId
                           url:(nonnull NSString *)url
                       resolve:(nonnull RCTPromiseResolveBlock)resolve
                        reject:(nonnull RCTPromiseRejectBlock)reject
{
    [self.impl presentationHandleLinkWithInstanceId:instanceId
                                                url:url
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

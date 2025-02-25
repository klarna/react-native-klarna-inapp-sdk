//
//  KlarnaSignInSDK.h
//  RNKlarnaMobileSDK
//
//  Created by Jorge Palacio on 2025-02-20.
//  Copyright © 2025 Facebook. All rights reserved.
//

#import <Foundation/Foundation.h>

#ifdef RCT_NEW_ARCH_ENABLED
#import <RNKlarnaMobileSDK/RNKlarnaMobileSDK.h>
#endif

NS_ASSUME_NONNULL_BEGIN

#ifdef RCT_NEW_ARCH_ENABLED
@interface KlarnaSignInManager: NSObject<NativeKlarnaSignInSpec>

#else

@interface KlarnaSignInManager: NSObject<RCTBridgeModule>

@property (nonatomic, copy) RCTDirectEventBlock onEvent;
@property (nonatomic, copy) RCTDirectEventBlock onError;

#endif

@end

NS_ASSUME_NONNULL_END

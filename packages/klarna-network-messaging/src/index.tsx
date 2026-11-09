import { Klarna } from '@klarna/react-native-klarna-network-core';
import type { KlarnaMessaging } from './KlarnaMessaging';
import { KlarnaMessagingImpl } from './KlarnaMessagingImpl';
import NativeKlarnaNetworkMessaging from './Specs/NativeKlarnaNetworkMessaging';

// --- Module augmentation (mirrors Swift's `extension Klarna { var messaging: KlarnaMessaging }`) ---
//
// Adds `messaging` directly to Klarna so the call chain matches native:
// sdk.messaging.prequalification.initiate(...)
// The WeakMap key is Klarna itself; instanceId is read from sdk.instanceId.

declare module '@klarna/react-native-klarna-network-core' {
  interface Klarna {
    readonly messaging: KlarnaMessaging;
  }
}

const messagingCache = new WeakMap<Klarna, KlarnaMessaging>();

const _coreDispose = Klarna.prototype.dispose;
Klarna.prototype.dispose = async function (this: Klarna): Promise<void> {
  if (messagingCache.has(this)) {
    messagingCache.delete(this);
    await NativeKlarnaNetworkMessaging.dispose(this.instanceId);
  }
  await _coreDispose.call(this);
};

Object.defineProperty(Klarna.prototype, 'messaging', {
  get(this: Klarna): KlarnaMessaging {
    let instance = messagingCache.get(this);
    if (!instance) {
      instance = new KlarnaMessagingImpl(this.instanceId);
      messagingCache.set(this, instance);
    }
    return instance;
  },
  enumerable: true,
  configurable: true,
});

// --- Public API ---

export {
  KlarnaMessagingPlacementView,
  type KlarnaMessagingPlacementViewProps,
} from './KlarnaMessagingPlacementView';
export { KlarnaMessagingPlacementType } from './types/KlarnaMessagingPlacementType';
export type {
  KlarnaMessagingPlacementConfiguration,
  KlarnaMessagingCreditPromotionAutoSizeConfiguration,
  KlarnaMessagingCreditPromotionBadgeConfiguration,
} from './types/KlarnaMessagingPlacementConfiguration';
export { KlarnaTheme } from '@klarna/react-native-klarna-network-core';
export type { KlarnaMessagingError } from './types/KlarnaMessagingError';
export type {
  KlarnaMessaging,
  KlarnaMessagingPrequalification,
} from './KlarnaMessaging';
export type {
  KlarnaMessagingPrequalificationData,
  KlarnaMessagingPrequalificationResult,
} from './types/KlarnaMessagingPrequalification';

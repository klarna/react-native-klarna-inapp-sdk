import { Klarna } from '@klarna/react-native-klarna-network-core';
import type { KlarnaPayment } from './KlarnaNetworkPayment';
import { KlarnaNetworkPaymentImpl } from './KlarnaNetworkPaymentImpl';
import NativeKlarnaNetworkPayment from './Specs/NativeKlarnaNetworkPayment';

// --- Module augmentation (mirrors Swift's `extension Klarna { var payment: KlarnaPayment }`) ---
//
// Adds `payment` directly to Klarna so the call chain matches native: sdk.payment.initiate(...)
// The WeakMap key is Klarna itself; instanceId is read from sdk.instanceId.

declare module '@klarna/react-native-klarna-network-core' {
  interface Klarna {
    readonly payment: KlarnaPayment;
  }
}

const paymentCache = new WeakMap<Klarna, KlarnaPayment>();

const _coreDispose = Klarna.prototype.dispose;
Klarna.prototype.dispose = async function (this: Klarna): Promise<void> {
  if (paymentCache.has(this)) {
    paymentCache.delete(this);
    await NativeKlarnaNetworkPayment.dispose(this.instanceId);
  }
  await _coreDispose.call(this);
};

Object.defineProperty(Klarna.prototype, 'payment', {
  get(this: Klarna): KlarnaPayment {
    let instance = paymentCache.get(this);
    if (!instance) {
      instance = new KlarnaNetworkPaymentImpl(this.instanceId);
      paymentCache.set(this, instance);
    }
    return instance;
  },
  enumerable: true,
  configurable: true,
});

// --- Public API ---

export type { KlarnaPayment } from './KlarnaNetworkPayment';

// UI Components
export {
  KlarnaPaymentButton,
  type KlarnaPaymentButtonIntent,
  type KlarnaPaymentButtonShape,
  type KlarnaPaymentButtonState,
  type KlarnaPaymentButtonStyle,
  type KlarnaPaymentButtonTheme,
  type KlarnaPaymentButtonProps,
} from './KlarnaPaymentButton';

// Models
export type * from './types/models';

// Request / Response
export type { KlarnaPaymentRequest } from './types/KlarnaPaymentRequest';
export type { KlarnaPaymentRequestState } from './types/KlarnaPaymentRequestState';
export type { KlarnaPaymentRequestStateContext } from './types/KlarnaPaymentRequestStateContext';
export type { KlarnaPaymentRequestStateReason } from './types/KlarnaPaymentRequestStateReason';

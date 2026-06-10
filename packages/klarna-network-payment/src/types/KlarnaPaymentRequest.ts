import type { KlarnaPaymentRequestState } from './KlarnaPaymentRequestState';
import type { KlarnaPaymentRequestStateContext } from './KlarnaPaymentRequestStateContext';
import type { KlarnaPaymentRequestStateReason } from './KlarnaPaymentRequestStateReason';

/** A Payment Request represents a request for a payment. The Payment Request lifecycle is defined in KlarnaPaymentRequestState. */
export type KlarnaPaymentRequest = {
  /** Unique identifier of this payment request. */
  paymentRequestId: string;
  /** Payment request state. */
  state: KlarnaPaymentRequestState;
  /** Previous payment request state. */
  previousState: KlarnaPaymentRequestState | null;
  /** Payment request state context. */
  stateContext: KlarnaPaymentRequestStateContext | null;
  /** Payment request state reason. */
  stateReason: KlarnaPaymentRequestStateReason | null;
  /** Reference to the payment session or equivalent resource created on your side. */
  paymentRequestReference: string | null;
};

import type { KlarnaCustomer } from './models/KlarnaCustomer';
import type { KlarnaShipping } from './models/KlarnaShipping';

/** Context for the state of a payment request. */
export type KlarnaPaymentRequestStateContext = {
  /** A short-lived, network-scoped bearer credential that represents an active Klarna Network session. */
  klarnaNetworkSessionToken: string | null;
  /** The Klarna customer information associated with this payment request. */
  klarnaCustomer?: KlarnaCustomer;
  /** The shipping information associated with this payment request. */
  shipping?: KlarnaShipping;
};

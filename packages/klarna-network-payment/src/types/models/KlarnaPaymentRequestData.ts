import type { KlarnaCollectCustomerProfileType } from './KlarnaCollectCustomerProfileType';
import type { KlarnaRequestCustomerToken } from './KlarnaRequestCustomerToken';
import type { KlarnaShippingConfig } from './KlarnaShippingConfig';
import type { KlarnaSupplementaryPurchaseData } from './KlarnaSupplementaryPurchaseData';

/** Data for creating a new payment request. */
export type KlarnaPaymentRequestData = {
  /** Total payment amount in minor units (e.g. cents). Maps to Swift Int64. */
  amount: number;
  /** ISO 4217 currency code (e.g. "SEK"). */
  currency: string;
  /** Payment option to pre-select in the purchase flow. */
  paymentOptionId?: string;
  /** Partner reference for correlating with payment request webhooks. */
  paymentRequestReference?: string;
  /** Request customer account linking; generated token returned in stateContext on completion. */
  requestCustomerToken?: KlarnaRequestCustomerToken;
  /** Configure whether shipping details should be collected from the customer. */
  shippingConfig?: KlarnaShippingConfig;
  /** Customer profile fields to collect; data available in stateContext after successful flow. */
  collectCustomerProfile?: KlarnaCollectCustomerProfileType[];
  /** Supplementary purchase data (line items, shipping, subscriptions, etc.). */
  supplementaryPurchaseData?: KlarnaSupplementaryPurchaseData;
};

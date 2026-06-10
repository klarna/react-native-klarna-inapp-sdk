import type { KlarnaCustomerProfile } from '@klarna/react-native-klarna-network-core';

/** Klarna customer information associated with a payment request. */
export type KlarnaCustomer = {
  /** Short-lived token that represents the customer's linked Klarna account. */
  customerToken?: string;
  /** Partner reference for the customer token. */
  customerTokenReference?: string;
  /** Customer profile information collected during the purchase flow. */
  customerProfile?: KlarnaCustomerProfile;
};

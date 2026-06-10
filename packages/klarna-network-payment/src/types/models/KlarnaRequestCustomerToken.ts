import type { KlarnaRequestCustomerTokenScope } from './KlarnaRequestCustomerTokenScope';

/** Configuration for requesting a customer token. */
export type KlarnaRequestCustomerToken = {
  /** The scopes to request for the customer token. */
  scopes: KlarnaRequestCustomerTokenScope[];
  /** Partner reference for the customer token. */
  customerTokenReference?: string;
};

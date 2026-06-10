/** Scope for requesting a customer token. */
export type KlarnaRequestCustomerTokenScope =
  /** Allows the customer to log in with Klarna. */
  | 'customer:login'
  /** Allows recurring payments when the customer is not present. */
  | 'payment:customer_not_present'
  /** Allows payments when the customer is present. */
  | 'payment:customer_present';

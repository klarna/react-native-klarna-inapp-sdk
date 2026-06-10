/** Billing interval for subscriptions and on-demand services. */
export type KlarnaInterval =
  /** Daily interval. */
  | 'DAY'
  /** Weekly interval. */
  | 'WEEK'
  /** Monthly interval. */
  | 'MONTH'
  /** Yearly interval. */
  | 'YEAR';

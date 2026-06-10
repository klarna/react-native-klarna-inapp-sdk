import type { KlarnaInterval } from './KlarnaInterval';

export type KlarnaBillingPlan = {
  /** Charge amount in minor currency units. */
  billingAmount: number;
  /** ISO 4217 currency code. */
  currency?: string;
  /** ISO 8601 date string for when the customer will be charged. Must be in the future. */
  from: string;
  /** Billing plan interval. The interval of which the customer is receiving goods or services. */
  interval: KlarnaInterval;
  /** Defines the billing plan interval frequency.
    A weekly billing plan would set `interval` to week and `intervalFrequency` to 1.
    A billing plan every second month would set `interval` to month and `intervalFrequency` to 2.
    A billing plan twice per month would set `interval` to week and `intervalFrequency` to 2. */
  intervalFrequency: number;
};

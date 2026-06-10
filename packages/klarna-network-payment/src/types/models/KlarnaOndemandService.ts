import type { KlarnaInterval } from './KlarnaInterval';

/** On-demand service details for a payment request. */
export type KlarnaOndemandService = {
  /** ISO 4217 currency code. */
  currency?: string;
  /** Average charge amount in minor currency units. */
  averageAmount?: number;
  /** Minimum charge amount in minor currency units. */
  minimumAmount?: number;
  /** Maximum charge amount in minor currency units. */
  maximumAmount?: number;
  /** The interval of which the customer is using or receiving the service. */
  purchaseInterval?: KlarnaInterval;
  /** Defines the purchase interval frequency. */
  purchaseIntervalFrequency?: number;
};

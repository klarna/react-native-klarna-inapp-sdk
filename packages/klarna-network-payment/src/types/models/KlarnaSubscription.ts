import type { KlarnaBillingPlan } from './KlarnaBillingPlan';
import type { KlarnaFreeTrial } from './KlarnaFreeTrial';

/** Subscription details for a payment request. */
export type KlarnaSubscription = {
  /** Reference for the subscription. */
  subscriptionReference: string;
  /** Name of the subscription. */
  name?: string;
  /** Free trial status. */
  freeTrial?: KlarnaFreeTrial;
  /** Billing plans for the subscription. */
  billingPlans?: KlarnaBillingPlan[];
};

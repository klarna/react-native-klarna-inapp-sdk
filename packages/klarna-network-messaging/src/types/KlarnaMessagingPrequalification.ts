/** The input for a prequalification flow. */
export type KlarnaMessagingPrequalificationData = {
  /** The purchase amount in minor units (for example cents). Must be a positive integer. */
  readonly amount: number;
  /** The ISO 4217 currency code (for example `USD`). */
  readonly currency: string;
};

/**
 * The outcome of a prequalification flow.
 *
 * Every customer outcome is a success: `APPROVED` when the customer prequalified,
 * `ABORTED` when the customer was not prequalified or dismissed the flow. Only a
 * flow that could not be started rejects the promise.
 */
export type KlarnaMessagingPrequalificationResult =
  | {
      readonly status: 'APPROVED';
      /** The time at which the prequalification expires. */
      readonly expiresAt: Date;
    }
  | { readonly status: 'ABORTED' };

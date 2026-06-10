/** Represents the lifecycle of a payment request. */
export type KlarnaPaymentRequestState =
  /** Payment request has been submitted to the backend and ready to be initiated. */
  | 'SUBMITTED'
  /** The payment flow is in progress, customer is inside the purchase flow. */
  | 'IN_PROGRESS'
  /** The customer has approved the request, order can be created. */
  | 'COMPLETED'
  /** The payment request has expired. This is a final state. */
  | 'EXPIRED'
  /** The payment request has been canceled. This is a final state. */
  | 'CANCELED'
  /** The payment request has been declined. This is a final state. */
  | 'DECLINED';

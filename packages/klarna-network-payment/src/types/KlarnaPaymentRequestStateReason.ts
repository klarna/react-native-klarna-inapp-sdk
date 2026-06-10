/** Reasons for the current state of the payment request. */
export type KlarnaPaymentRequestStateReason =
  /** The partner has canceled the payment request. Payment request state should be set to CANCELED. */
  | 'PARTNER_CANCELED'
  /** The Payment request has been submitted to Klarna. */
  | 'PAYMENT_REQUEST_SUBMITTED'
  /** The customer dropped out of the purchase flow (e.g. closed the Klarna window or went back to the merchant site). */
  | 'PURCHASE_FLOW_ABORTED'
  /** Klarna failed to start the purchase flow due to internal error. */
  | 'TECHNICAL_ERROR'
  /** The payment was declined by Klarna or an issuing bank. */
  | 'PAYMENT_DECLINED';

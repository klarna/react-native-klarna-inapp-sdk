import type { KlarnaPaymentRequest } from './types/KlarnaPaymentRequest';
import type { KlarnaPaymentRequestData } from './types/models/KlarnaPaymentRequestData';
import type {
  KlarnaPaymentPresentationContent,
  KlarnaPaymentPresentationData,
} from './types/models/KlarnaPaymentPresentation';

/** Provides access to payment presentation content for rendering Klarna UI elements. */
export interface KlarnaPaymentPresentation {
  /** Fetches presentation content for the given amount, currency, and intent. */
  fetch(
    data: KlarnaPaymentPresentationData
  ): Promise<KlarnaPaymentPresentationContent>;
  /** Handles a deep link URL returned from the Klarna flow and returns updated presentation content. */
  handleLink(url: string): Promise<KlarnaPaymentPresentationContent>;
}

/** Entry point for managing the Klarna Network Payment lifecycle. */
export interface KlarnaPayment {
  /** Provides access to payment presentation content. */
  readonly presentation: KlarnaPaymentPresentation;
  /** Initiates a payment request by its existing payment request ID or data for client side payment request creation. */
  initiate(
    paymentRequestIdOrData: string | KlarnaPaymentRequestData
  ): Promise<KlarnaPaymentRequest>;
  /** Fetches the current state of a payment request by its ID. */
  fetch(paymentRequestId: string): Promise<KlarnaPaymentRequest>;
  /** Cancels a payment request by its ID. */
  cancel(paymentRequestId: string): Promise<KlarnaPaymentRequest>;
}

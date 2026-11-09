import type {
  KlarnaMessagingPrequalificationData,
  KlarnaMessagingPrequalificationResult,
} from './types/KlarnaMessagingPrequalification';

/** Prequalification entry point of Klarna messaging. */
export interface KlarnaMessagingPrequalification {
  /**
   * Starts the prequalification flow.
   *
   * Messaging placements are the recommended surface. Use this only when a
   * placement cannot be rendered. On Android a foreground Activity is required.
   *
   * Resolves with the customer outcome (`APPROVED` or `ABORTED`). Rejects when the
   * flow could not be started: native failures reject with an error whose `code`
   * is the native error name (for example `OPERATION_FAILED` or
   * `INITIATE_ALREADY_IN_PROGRESS`) and whose `message` describes the failure.
   * Invalid input (`amount`, `currency`) rejects with a plain `Error` that has
   * no `code`, without calling native code.
   */
  initiate(
    data: KlarnaMessagingPrequalificationData
  ): Promise<KlarnaMessagingPrequalificationResult>;
}

/** Klarna messaging features available on a `Klarna` instance (`sdk.messaging`). */
export interface KlarnaMessaging {
  readonly prequalification: KlarnaMessagingPrequalification;
}

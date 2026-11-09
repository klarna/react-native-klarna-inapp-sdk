import type { KlarnaMessagingPrequalificationResult } from '@klarna/react-native-klarna-network-messaging';

/** Formats a prequalification result for display (and for E2E assertions). */
export function formatPrequalificationResult(
  result: KlarnaMessagingPrequalificationResult
): string {
  switch (result.status) {
    case 'APPROVED':
      return `Approved, expires ${result.expiresAt.toISOString()}`;
    case 'ABORTED':
      return 'Aborted';
  }
}

/**
 * Formats a prequalification failure for display.
 *
 * Native failures reject with an `Error` whose `code` is the native error name
 * (for example `OPERATION_FAILED` or `INITIATE_ALREADY_IN_PROGRESS`) and whose
 * `name` is just `Error`; they render as `code: message`. Local validation
 * failures reject with a plain `Error` without a `code`; they render as
 * `name: message`. Anything else falls back to the message, then `String(error)`.
 */
export function formatPrequalificationError(error: unknown): string {
  if (typeof error === 'object' && error !== null) {
    const { code, name, message } = error as {
      code?: unknown;
      name?: unknown;
      message?: unknown;
    };
    if (typeof message === 'string') {
      if (typeof code === 'string') {
        return `${code}: ${message}`;
      }
      if (typeof name === 'string') {
        return `${name}: ${message}`;
      }
      return message;
    }
  }
  return String(error);
}

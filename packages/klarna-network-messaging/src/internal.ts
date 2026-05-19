import type { KlarnaMessagingError } from './types/KlarnaMessagingError';

interface NextHeightInput {
  readonly rawHeight: string;
  readonly prevHeight: number;
  readonly hasError: boolean;
  readonly isFirstResize?: boolean;
}

/**
 * Derives the next view height from a native `onResized` event. Returns
 * `null` (skip update) when the view is in an error state, the raw value
 * is empty/non-finite/negative, or the height hasn't actually changed.
 *
 * On the first resize, height 0 is accepted so the view can collapse when
 * native reports no content. Subsequent 0→0 duplicates are still skipped.
 */
export function nextHeightOnResize({
  rawHeight,
  prevHeight,
  hasError,
  isFirstResize,
}: NextHeightInput): number | null {
  if (hasError) {
    return null;
  }
  if (rawHeight.trim() === '') {
    return null;
  }
  const parsed = Number(rawHeight);
  if (!Number.isFinite(parsed) || parsed < 0) {
    return null;
  }
  if (parsed === prevHeight && !isFirstResize) {
    return null;
  }
  return parsed;
}

/** Extracts a `KlarnaMessagingError` from the nested native event payload. */
export function errorFromNativeEvent(event: {
  readonly error: { readonly message: string; readonly name: string };
}): KlarnaMessagingError {
  return {
    message: event.error.message,
    name: event.error.name,
  };
}

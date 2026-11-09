import type {
  KlarnaMessagingPrequalificationData,
  KlarnaMessagingPrequalificationResult,
} from './types/KlarnaMessagingPrequalification';

export function mapPrequalificationResult(
  raw: Object
): KlarnaMessagingPrequalificationResult {
  const result = raw as { status?: unknown; expiresAt?: unknown };
  if (result.status === 'ABORTED') {
    return { status: 'ABORTED' };
  }
  if (result.status === 'APPROVED') {
    const expiresAt =
      typeof result.expiresAt === 'string'
        ? new Date(result.expiresAt)
        : undefined;
    if (!expiresAt || Number.isNaN(expiresAt.getTime())) {
      throw new Error(
        `Invalid prequalification result: expiresAt is not a valid ISO-8601 date (${String(
          result.expiresAt
        )}).`
      );
    }
    return { status: 'APPROVED', expiresAt };
  }
  throw new Error(
    `Invalid prequalification result: unknown status (${String(result.status)}).`
  );
}

export function validatePrequalificationData(
  data: KlarnaMessagingPrequalificationData
): void {
  if (!data || typeof data !== 'object') {
    throw new Error('Prequalification data must be an object.');
  }
  if (!Number.isSafeInteger(data.amount) || data.amount <= 0) {
    throw new Error(
      'Prequalification amount must be a positive integer in minor units.'
    );
  }
  if (typeof data.currency !== 'string' || data.currency.trim().length === 0) {
    throw new Error('Prequalification currency must be a non-empty string.');
  }
}

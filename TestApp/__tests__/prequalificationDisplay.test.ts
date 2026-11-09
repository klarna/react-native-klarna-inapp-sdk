import {
  formatPrequalificationError,
  formatPrequalificationResult,
} from '../src/network-integrations/prequalificationDisplay';

describe('formatPrequalificationResult', () => {
  it('formats an approved result with the ISO expiry', () => {
    expect(
      formatPrequalificationResult({
        status: 'APPROVED',
        expiresAt: new Date('2030-01-02T03:04:05.000Z'),
      })
    ).toBe('Approved, expires 2030-01-02T03:04:05.000Z');
  });

  it('formats an aborted result', () => {
    expect(formatPrequalificationResult({ status: 'ABORTED' })).toBe('Aborted');
  });
});

describe('formatPrequalificationError', () => {
  it('formats a native-style error as code: message', () => {
    const nativeError = Object.assign(new Error('Already running'), {
      code: 'INITIATE_ALREADY_IN_PROGRESS',
    });
    expect(formatPrequalificationError(nativeError)).toBe(
      'INITIATE_ALREADY_IN_PROGRESS: Already running'
    );
  });

  it('formats a plain Error (local validation) as name: message', () => {
    expect(formatPrequalificationError(new Error('bad amount'))).toBe(
      'Error: bad amount'
    );
  });

  it('falls back to the message when there is no code or name', () => {
    expect(formatPrequalificationError({ message: 'oops' })).toBe('oops');
  });

  it('stringifies unknown values', () => {
    expect(formatPrequalificationError('boom')).toBe('boom');
    expect(formatPrequalificationError(undefined)).toBe('undefined');
  });
});

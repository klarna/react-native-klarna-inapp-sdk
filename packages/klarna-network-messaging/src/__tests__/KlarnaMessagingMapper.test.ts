import {
  mapPrequalificationResult,
  validatePrequalificationData,
} from '../KlarnaMessagingMapper';

describe('mapPrequalificationResult', () => {
  it('maps an approved result with a valid ISO expiresAt to a Date', () => {
    const result = mapPrequalificationResult({
      status: 'APPROVED',
      expiresAt: '2026-10-23T12:30:45.000Z',
    });
    expect(result.status).toBe('APPROVED');
    if (result.status === 'APPROVED') {
      expect(result.expiresAt).toBeInstanceOf(Date);
      expect(result.expiresAt.toISOString()).toBe('2026-10-23T12:30:45.000Z');
    }
  });

  it('throws when an approved result has no expiresAt', () => {
    expect(() => mapPrequalificationResult({ status: 'APPROVED' })).toThrow(
      /expiresAt is not a valid ISO-8601 date \(undefined\)/
    );
  });

  it.each([
    ['unparseable string', 'not-a-date'],
    ['non-string', 1234567890],
    ['null', null],
  ])('throws when expiresAt is invalid (%s)', (_name, expiresAt) => {
    expect(() =>
      mapPrequalificationResult({ status: 'APPROVED', expiresAt })
    ).toThrow(/expiresAt/);
  });

  it('maps an aborted result and ignores extra fields', () => {
    expect(
      mapPrequalificationResult({ status: 'ABORTED', expiresAt: 'x' })
    ).toEqual({ status: 'ABORTED' });
  });

  it.each([
    ['unknown string', { status: 'weird' }],
    ['lowercase', { status: 'approved' }],
    ['missing', {}],
  ])('throws on an unknown status (%s)', (_name, raw) => {
    expect(() => mapPrequalificationResult(raw)).toThrow(/unknown status/);
  });
});

describe('validatePrequalificationData', () => {
  it('accepts a positive integer amount and a non-empty currency', () => {
    expect(() =>
      validatePrequalificationData({ amount: 9999, currency: 'USD' })
    ).not.toThrow();
  });

  it.each([
    ['null', null],
    ['undefined', undefined],
    ['string', 'data'],
  ])('throws when data is not an object (%s)', (_name, data) => {
    expect(() =>
      validatePrequalificationData(data as unknown as never)
    ).toThrow('Prequalification data must be an object.');
  });

  it.each([
    ['fractional', 10.5],
    ['NaN', NaN],
    ['Infinity', Infinity],
    ['string', '100' as unknown as number],
  ])('throws when amount is not an integer (%s)', (_name, amount) => {
    expect(() =>
      validatePrequalificationData({ amount, currency: 'USD' })
    ).toThrow(/amount/);
  });

  it.each([
    ['zero', 0],
    ['negative', -5],
  ])('throws when amount is not positive (%s)', (_name, amount) => {
    expect(() =>
      validatePrequalificationData({ amount, currency: 'USD' })
    ).toThrow(/amount/);
  });

  it('throws when amount is an unsafe integer', () => {
    expect(() =>
      validatePrequalificationData({
        amount: Number.MAX_SAFE_INTEGER + 1,
        currency: 'USD',
      })
    ).toThrow(/amount/);
  });

  it.each([
    ['empty', ''],
    ['whitespace', '   '],
    ['non-string', 42 as unknown as string],
  ])('throws when currency is invalid (%s)', (_name, currency) => {
    expect(() =>
      validatePrequalificationData({ amount: 100, currency })
    ).toThrow(/currency/);
  });
});

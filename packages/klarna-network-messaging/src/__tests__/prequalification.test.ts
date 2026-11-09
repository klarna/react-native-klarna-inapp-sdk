import NativeKlarnaNetworkMessaging from '../Specs/NativeKlarnaNetworkMessaging';
import { Klarna } from '@klarna/react-native-klarna-network-core';
import '../index';

jest.mock('../Specs/NativeKlarnaNetworkMessaging', () => ({
  __esModule: true,
  default: {
    initiatePrequalification: jest.fn(),
    dispose: jest.fn(),
  },
}));

jest.mock('@klarna/react-native-klarna-network-core', () => ({
  Klarna: class {
    readonly instanceId: string;
    constructor(id: string) {
      this.instanceId = id;
    }
    dispose() {
      return Promise.resolve();
    }
  },
  KlarnaTheme: {},
}));

jest.mock('../KlarnaMessagingPlacementView', () => ({
  KlarnaMessagingPlacementView: () => null,
}));

const mockedNative = jest.mocked(NativeKlarnaNetworkMessaging);

const newKlarna = (id: string): any => new (Klarna as any)(id);

beforeEach(() => {
  jest.clearAllMocks();
  mockedNative.initiatePrequalification.mockResolvedValue({
    status: 'ABORTED',
  });
  mockedNative.dispose.mockResolvedValue(undefined);
});

describe('messaging getter', () => {
  it('returns an object with prequalification for a Klarna instance', () => {
    const klarna = newKlarna('id-1');
    expect(klarna.messaging).toBeDefined();
    expect(klarna.messaging.prequalification).toBeDefined();
  });

  it('returns the same messaging object for the same Klarna instance', () => {
    const klarna = newKlarna('id-2');
    expect(klarna.messaging).toBe(klarna.messaging);
  });

  it('returns different messaging objects for different Klarna instances', () => {
    expect(newKlarna('id-3').messaging).not.toBe(newKlarna('id-4').messaging);
  });
});

describe('prequalification.initiate', () => {
  it('passes the instanceId and payload to native', async () => {
    const klarna = newKlarna('instance-specific-id');
    await klarna.messaging.prequalification.initiate({
      amount: 9999,
      currency: 'USD',
    });
    expect(mockedNative.initiatePrequalification).toHaveBeenCalledWith(
      'instance-specific-id',
      { amount: 9999, currency: 'USD' }
    );
  });

  it('maps an approved result with expiresAt as a Date', async () => {
    mockedNative.initiatePrequalification.mockResolvedValue({
      status: 'APPROVED',
      expiresAt: '2026-10-23T12:30:45.000Z',
    });
    const result = await newKlarna('a').messaging.prequalification.initiate({
      amount: 100,
      currency: 'EUR',
    });
    expect(result.status).toBe('APPROVED');
    if (result.status === 'APPROVED') {
      expect(result.expiresAt).toBeInstanceOf(Date);
      expect(result.expiresAt.toISOString()).toBe('2026-10-23T12:30:45.000Z');
    }
  });

  it('maps an aborted result', async () => {
    mockedNative.initiatePrequalification.mockResolvedValue({
      status: 'ABORTED',
    });
    const result = await newKlarna('b').messaging.prequalification.initiate({
      amount: 100,
      currency: 'EUR',
    });
    expect(result).toEqual({ status: 'ABORTED' });
  });

  it('rejects when expiresAt is not a valid date', async () => {
    mockedNative.initiatePrequalification.mockResolvedValue({
      status: 'APPROVED',
      expiresAt: 'not-a-date',
    });
    await expect(
      newKlarna('c').messaging.prequalification.initiate({
        amount: 100,
        currency: 'EUR',
      })
    ).rejects.toThrow(/expiresAt/);
  });

  it('rejects when expiresAt is missing', async () => {
    mockedNative.initiatePrequalification.mockResolvedValue({
      status: 'APPROVED',
    });
    await expect(
      newKlarna('c2').messaging.prequalification.initiate({
        amount: 100,
        currency: 'EUR',
      })
    ).rejects.toThrow(/expiresAt/);
  });

  it('rejects on an unknown status', async () => {
    mockedNative.initiatePrequalification.mockResolvedValue({
      status: 'weird',
    });
    await expect(
      newKlarna('c3').messaging.prequalification.initiate({
        amount: 100,
        currency: 'EUR',
      })
    ).rejects.toThrow(/status/);
  });

  it.each([
    ['zero', 0],
    ['negative', -5],
    ['fractional', 10.5],
    ['NaN', NaN],
    ['Infinity', Infinity],
    ['unsafe integer', Number.MAX_SAFE_INTEGER + 1],
    ['string', '100' as unknown as number],
  ])(
    'rejects an invalid amount (%s) before calling native',
    async (_n, amount) => {
      await expect(
        newKlarna('d').messaging.prequalification.initiate({
          amount,
          currency: 'USD',
        })
      ).rejects.toThrow(/amount/);
      expect(mockedNative.initiatePrequalification).not.toHaveBeenCalled();
    }
  );

  it.each([
    ['empty', ''],
    ['blank', '   '],
    ['non-string', 42 as unknown as string],
  ])(
    'rejects an invalid currency (%s) before calling native',
    async (_n, currency) => {
      await expect(
        newKlarna('e').messaging.prequalification.initiate({
          amount: 100,
          currency,
        })
      ).rejects.toThrow(/currency/);
      expect(mockedNative.initiatePrequalification).not.toHaveBeenCalled();
    }
  );

  it('propagates native rejections untouched (code and message intact)', async () => {
    // React Native rejects with an Error whose `code` is the native error name
    // and whose `name` stays 'Error'.
    const nativeError = Object.assign(
      new Error('Prequalification operation failed.'),
      { code: 'OPERATION_FAILED' }
    );
    mockedNative.initiatePrequalification.mockRejectedValue(nativeError);
    const rejection = await newKlarna('f')
      .messaging.prequalification.initiate({ amount: 100, currency: 'USD' })
      .catch((e: unknown) => e);
    expect(rejection).toBe(nativeError);
    expect(rejection).toMatchObject({
      code: 'OPERATION_FAILED',
      message: 'Prequalification operation failed.',
    });
  });

  it('rejects local validation failures with a plain Error without a code', async () => {
    const rejection = await newKlarna('g')
      .messaging.prequalification.initiate({ amount: 0, currency: 'USD' })
      .catch((e: unknown) => e);
    expect(rejection).toBeInstanceOf(Error);
    expect(rejection).not.toHaveProperty('code');
  });
});

describe('dispose', () => {
  it('calls native dispose with instanceId when messaging was accessed', async () => {
    const klarna = newKlarna('dispose-id-1');
    klarna.messaging; // populate cache
    await klarna.dispose();
    expect(mockedNative.dispose).toHaveBeenCalledWith('dispose-id-1');
  });

  it('does not call native dispose when messaging was never accessed', async () => {
    await newKlarna('dispose-id-2').dispose();
    expect(mockedNative.dispose).not.toHaveBeenCalled();
  });

  it('clears the cache so a new instance is created on next access', async () => {
    const klarna = newKlarna('dispose-id-3');
    const before = klarna.messaging;
    await klarna.dispose();
    expect(klarna.messaging).not.toBe(before);
  });
});

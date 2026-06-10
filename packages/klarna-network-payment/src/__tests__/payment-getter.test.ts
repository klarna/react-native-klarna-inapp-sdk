import NativeKlarnaNetworkPayment from '../Specs/NativeKlarnaNetworkPayment';
import { Klarna } from '@klarna/react-native-klarna-network-core';
import '../index';

jest.mock('../Specs/NativeKlarnaNetworkPayment', () => ({
  __esModule: true,
  default: {
    initiateWithId: jest.fn(),
    initiateWithData: jest.fn(),
    fetch: jest.fn(),
    cancel: jest.fn(),
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
}));

const mockedNative = jest.mocked(NativeKlarnaNetworkPayment);

beforeEach(() => {
  jest.clearAllMocks();
  mockedNative.initiateWithId.mockResolvedValue({
    paymentRequestId: 'pr-1',
    state: 'SUBMITTED',
    previousState: null,
    stateContext: null,
    stateReason: null,
    paymentRequestReference: null,
  });
  mockedNative.dispose.mockResolvedValue(undefined);
});

describe('payment getter', () => {
  it('returns an object for a Klarna instance', () => {
    const klarna = new (Klarna as any)('id-1');
    expect((klarna as any).payment).toBeDefined();
  });

  it('returns the same payment object for the same Klarna instance', () => {
    const klarna = new (Klarna as any)('id-2');
    const first = (klarna as any).payment;
    const second = (klarna as any).payment;
    expect(first).toBe(second);
  });

  it('returns different payment objects for different Klarna instances', () => {
    const klarna1 = new (Klarna as any)('id-3');
    const klarna2 = new (Klarna as any)('id-4');
    expect((klarna1 as any).payment).not.toBe((klarna2 as any).payment);
  });

  it('uses the instanceId from the Klarna instance when calling a native method', async () => {
    const klarna = new (Klarna as any)('instance-specific-id');
    await (klarna as any).payment.initiate('pr-test');
    expect(mockedNative.initiateWithId).toHaveBeenCalledWith(
      'instance-specific-id',
      'pr-test'
    );
  });
});

describe('dispose', () => {
  it('calls native dispose with instanceId when payment was accessed', async () => {
    const klarna = new (Klarna as any)('dispose-id-1');
    (klarna as any).payment; // populate cache
    await (klarna as any).dispose();
    expect(mockedNative.dispose).toHaveBeenCalledWith('dispose-id-1');
  });

  it('does not call native dispose when payment was never accessed', async () => {
    const klarna = new (Klarna as any)('dispose-id-2');
    await (klarna as any).dispose();
    expect(mockedNative.dispose).not.toHaveBeenCalled();
  });

  it('clears the payment cache so a new instance is created on next access', async () => {
    const klarna = new (Klarna as any)('dispose-id-3');
    const before = (klarna as any).payment;
    await (klarna as any).dispose();
    const after = (klarna as any).payment;
    expect(after).not.toBe(before);
  });
});

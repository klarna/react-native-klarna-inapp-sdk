import NativeKlarnaNetworkPayment from '../Specs/NativeKlarnaNetworkPayment';
import { KlarnaNetworkPaymentImpl } from '../KlarnaNetworkPaymentImpl';
import type { KlarnaPaymentRequestData } from '../types/models/KlarnaPaymentRequestData';

jest.mock('../Specs/NativeKlarnaNetworkPayment', () => ({
  __esModule: true,
  default: {
    initiateWithId: jest.fn(),
    initiateWithData: jest.fn(),
    fetch: jest.fn(),
    cancel: jest.fn(),
  },
}));

const mockedNative = jest.mocked(NativeKlarnaNetworkPayment);

const INSTANCE_ID = 'instance-abc';
const PAYMENT_REQUEST_ID = 'pr-xyz';

const baseNativeResult = {
  paymentRequestId: PAYMENT_REQUEST_ID,
  state: 'SUBMITTED',
  previousState: null,
  stateContext: null,
  stateReason: null,
  paymentRequestReference: null,
};

beforeEach(() => {
  jest.clearAllMocks();
  mockedNative.initiateWithId.mockResolvedValue(baseNativeResult);
  mockedNative.initiateWithData.mockResolvedValue(baseNativeResult);
  mockedNative.fetch.mockResolvedValue(baseNativeResult);
  mockedNative.cancel.mockResolvedValue({
    ...baseNativeResult,
    state: 'CANCELED',
    previousState: 'IN_PROGRESS',
    stateReason: 'PARTNER_CANCELED',
  });
});

describe('KlarnaNetworkPaymentImpl', () => {
  describe('initiate (with ID)', () => {
    it('calls native initiateWithId with instanceId and paymentRequestId', async () => {
      const impl = new KlarnaNetworkPaymentImpl(INSTANCE_ID);
      await impl.initiate(PAYMENT_REQUEST_ID);
      expect(mockedNative.initiateWithId).toHaveBeenCalledWith(
        INSTANCE_ID,
        PAYMENT_REQUEST_ID
      );
    });

    it('returns a mapped KlarnaPaymentRequest with correct fields', async () => {
      mockedNative.initiateWithId.mockResolvedValueOnce({
        paymentRequestId: 'pr-1',
        state: 'IN_PROGRESS',
        previousState: 'SUBMITTED',
        stateContext: {
          klarnaNetworkSessionToken: 'tok_abc',
          klarnaCustomer: null,
          shipping: null,
        },
        stateReason: 'PAYMENT_REQUEST_SUBMITTED',
        paymentRequestReference: 'ref-1',
      });
      const impl = new KlarnaNetworkPaymentImpl(INSTANCE_ID);
      const result = await impl.initiate(PAYMENT_REQUEST_ID);
      expect(result).toEqual({
        paymentRequestId: 'pr-1',
        state: 'IN_PROGRESS',
        previousState: 'SUBMITTED',
        stateContext: {
          klarnaNetworkSessionToken: 'tok_abc',
          klarnaCustomer: null,
          shipping: null,
        },
        stateReason: 'PAYMENT_REQUEST_SUBMITTED',
        paymentRequestReference: 'ref-1',
      });
    });

    it('casts state and previousState to typed union values', async () => {
      mockedNative.initiateWithId.mockResolvedValueOnce({
        ...baseNativeResult,
        state: 'COMPLETED',
        previousState: 'IN_PROGRESS',
      });
      const impl = new KlarnaNetworkPaymentImpl(INSTANCE_ID);
      const result = await impl.initiate(PAYMENT_REQUEST_ID);
      expect(result.state).toBe('COMPLETED');
      expect(result.previousState).toBe('IN_PROGRESS');
    });

    it('casts stateReason to typed union value', async () => {
      mockedNative.initiateWithId.mockResolvedValueOnce({
        ...baseNativeResult,
        stateReason: 'TECHNICAL_ERROR',
      });
      const impl = new KlarnaNetworkPaymentImpl(INSTANCE_ID);
      const result = await impl.initiate(PAYMENT_REQUEST_ID);
      expect(result.stateReason).toBe('TECHNICAL_ERROR');
    });

    it('propagates native rejection', async () => {
      const error = new Error('native initiate failed');
      mockedNative.initiateWithId.mockRejectedValueOnce(error);
      const impl = new KlarnaNetworkPaymentImpl(INSTANCE_ID);
      await expect(impl.initiate(PAYMENT_REQUEST_ID)).rejects.toThrow(
        'native initiate failed'
      );
    });
  });

  describe('initiate (with data)', () => {
    it('calls native initiateWithData with instanceId and mapped data object', async () => {
      const data: KlarnaPaymentRequestData = {
        amount: 10000,
        currency: 'SEK',
      };
      const impl = new KlarnaNetworkPaymentImpl(INSTANCE_ID);
      await impl.initiate(data);
      expect(mockedNative.initiateWithData).toHaveBeenCalledWith(
        INSTANCE_ID,
        expect.objectContaining({ amount: 10000, currency: 'SEK' })
      );
    });

    it('maps amount and currency through unchanged', async () => {
      const data: KlarnaPaymentRequestData = { amount: 5050, currency: 'EUR' };
      const impl = new KlarnaNetworkPaymentImpl(INSTANCE_ID);
      await impl.initiate(data);
      const [, spec] = mockedNative.initiateWithData.mock.calls[0]! as [
        string,
        Record<string, any>,
      ];
      expect(spec.amount).toBe(5050);
      expect(spec.currency).toBe('EUR');
    });

    it('maps absent optional fields to null', async () => {
      const data: KlarnaPaymentRequestData = { amount: 100, currency: 'USD' };
      const impl = new KlarnaNetworkPaymentImpl(INSTANCE_ID);
      await impl.initiate(data);
      const [, spec] = mockedNative.initiateWithData.mock.calls[0]! as [
        string,
        Record<string, any>,
      ];
      expect(spec.paymentOptionId).toBeNull();
      expect(spec.paymentRequestReference).toBeNull();
      expect(spec.requestCustomerToken).toBeNull();
      expect(spec.shippingConfig).toBeNull();
      expect(spec.collectCustomerProfile).toBeNull();
      expect(spec.supplementaryPurchaseData).toBeNull();
    });

    it('maps requestCustomerToken when provided', async () => {
      const data: KlarnaPaymentRequestData = {
        amount: 100,
        currency: 'USD',
        requestCustomerToken: {
          scopes: ['customer:login'],
          customerTokenReference: 'ctr-ref',
        },
      };
      const impl = new KlarnaNetworkPaymentImpl(INSTANCE_ID);
      await impl.initiate(data);
      const [, spec] = mockedNative.initiateWithData.mock.calls[0]! as [
        string,
        Record<string, any>,
      ];
      expect(spec.requestCustomerToken).toEqual({
        scopes: ['customer:login'],
        customerTokenReference: 'ctr-ref',
      });
    });

    it('maps customerTokenReference to null when absent', async () => {
      const data: KlarnaPaymentRequestData = {
        amount: 100,
        currency: 'USD',
        requestCustomerToken: { scopes: ['customer:login'] },
      };
      const impl = new KlarnaNetworkPaymentImpl(INSTANCE_ID);
      await impl.initiate(data);
      const [, spec] = mockedNative.initiateWithData.mock.calls[0]! as [
        string,
        Record<string, any>,
      ];
      expect(spec.requestCustomerToken?.customerTokenReference).toBeNull();
    });

    it('maps shippingConfig when provided', async () => {
      const data: KlarnaPaymentRequestData = {
        amount: 100,
        currency: 'USD',
        shippingConfig: { mode: 'EDITABLE', supportedCountries: ['SE', 'DE'] },
      };
      const impl = new KlarnaNetworkPaymentImpl(INSTANCE_ID);
      await impl.initiate(data);
      const [, spec] = mockedNative.initiateWithData.mock.calls[0]! as [
        string,
        Record<string, any>,
      ];
      expect(spec.shippingConfig).toEqual({
        mode: 'EDITABLE',
        supportedCountries: ['SE', 'DE'],
      });
    });

    it('maps shippingConfig supportedCountries to null when absent', async () => {
      const data: KlarnaPaymentRequestData = {
        amount: 100,
        currency: 'USD',
        shippingConfig: { mode: 'EDITABLE' },
      };
      const impl = new KlarnaNetworkPaymentImpl(INSTANCE_ID);
      await impl.initiate(data);
      const [, spec] = mockedNative.initiateWithData.mock.calls[0]! as [
        string,
        Record<string, any>,
      ];
      expect(spec.shippingConfig?.supportedCountries).toBeNull();
    });

    it('maps collectCustomerProfile array through', async () => {
      const data: KlarnaPaymentRequestData = {
        amount: 100,
        currency: 'USD',
        collectCustomerProfile: ['profile:billing_address', 'profile:email'],
      };
      const impl = new KlarnaNetworkPaymentImpl(INSTANCE_ID);
      await impl.initiate(data);
      const [, spec] = mockedNative.initiateWithData.mock.calls[0]! as [
        string,
        Record<string, any>,
      ];
      expect(spec.collectCustomerProfile).toEqual([
        'profile:billing_address',
        'profile:email',
      ]);
    });

    it('maps supplementaryPurchaseData lineItems with all optional fields null when absent', async () => {
      const data: KlarnaPaymentRequestData = {
        amount: 100,
        currency: 'USD',
        supplementaryPurchaseData: {
          lineItems: [{ name: 'Widget', quantity: 2, totalAmount: 200 }],
        },
      };
      const impl = new KlarnaNetworkPaymentImpl(INSTANCE_ID);
      await impl.initiate(data);
      const [, spec] = mockedNative.initiateWithData.mock.calls[0]! as [
        string,
        Record<string, any>,
      ];
      const item = spec.supplementaryPurchaseData!.lineItems![0]!;
      expect(item.name).toBe('Widget');
      expect(item.quantity).toBe(2);
      expect(item.totalAmount).toBe(200);
      expect(item.currency).toBeNull();
      expect(item.imageUrl).toBeNull();
      expect(item.productIdentifier).toBeNull();
      expect(item.productUrl).toBeNull();
      expect(item.lineItemReference).toBeNull();
      expect(item.shippingReference).toBeNull();
      expect(item.subscriptionReference).toBeNull();
      expect(item.totalTaxAmount).toBeNull();
      expect(item.unitPrice).toBeNull();
    });

    it('maps supplementaryPurchaseData shipping with nested address, recipient, and shippingOption', async () => {
      const data: KlarnaPaymentRequestData = {
        amount: 100,
        currency: 'USD',
        supplementaryPurchaseData: {
          shipping: [
            {
              address: {
                streetAddress: '123 Main St',
                city: 'Stockholm',
                country: 'SE',
              },
              recipient: {
                familyName: 'Doe',
                givenName: 'Jane',
                email: 'jane@example.com',
              },
              shippingOption: {
                shippingType: 'TO_DOOR',
                shippingCarrier: 'DHL',
                shippingTypeAttributes: ['EXPRESS'],
              },
              shippingReference: 'ship-ref-1',
            },
          ],
        },
      };
      const impl = new KlarnaNetworkPaymentImpl(INSTANCE_ID);
      await impl.initiate(data);
      const [, spec] = mockedNative.initiateWithData.mock.calls[0]! as [
        string,
        Record<string, any>,
      ];
      const shipping = spec.supplementaryPurchaseData!.shipping![0]!;
      expect(shipping.address).toEqual({
        streetAddress: '123 Main St',
        streetAddress2: null,
        city: 'Stockholm',
        region: null,
        postalCode: null,
        country: 'SE',
      });
      expect(shipping.recipient).toEqual({
        attention: null,
        email: 'jane@example.com',
        familyName: 'Doe',
        givenName: 'Jane',
        phone: null,
      });
      expect(shipping.shippingOption).toEqual({
        shippingCarrier: 'DHL',
        shippingType: 'TO_DOOR',
        shippingTypeAttributes: ['EXPRESS'],
      });
      expect(shipping.shippingReference).toBe('ship-ref-1');
    });

    it('maps supplementaryPurchaseData subscriptions with nested billingPlans', async () => {
      const data: KlarnaPaymentRequestData = {
        amount: 100,
        currency: 'USD',
        supplementaryPurchaseData: {
          subscriptions: [
            {
              subscriptionReference: 'sub-ref-1',
              name: 'Monthly Plan',
              freeTrial: 'ACTIVE',
              billingPlans: [
                {
                  billingAmount: 999,
                  currency: 'USD',
                  from: '2025-01-01',
                  interval: 'MONTH',
                  intervalFrequency: 1,
                },
              ],
            },
          ],
        },
      };
      const impl = new KlarnaNetworkPaymentImpl(INSTANCE_ID);
      await impl.initiate(data);
      const [, spec] = mockedNative.initiateWithData.mock.calls[0]! as [
        string,
        Record<string, any>,
      ];
      const subscription = spec.supplementaryPurchaseData!.subscriptions![0]!;
      expect(subscription.subscriptionReference).toBe('sub-ref-1');
      expect(subscription.name).toBe('Monthly Plan');
      expect(subscription.freeTrial).toBe('ACTIVE');
      expect(subscription.billingPlans).toEqual([
        {
          billingAmount: 999,
          currency: 'USD',
          from: '2025-01-01',
          interval: 'MONTH',
          intervalFrequency: 1,
        },
      ]);
    });

    it('maps supplementaryPurchaseData subscriptions billingPlans to null when absent', async () => {
      const data: KlarnaPaymentRequestData = {
        amount: 100,
        currency: 'USD',
        supplementaryPurchaseData: {
          subscriptions: [{ subscriptionReference: 'sub-ref-2' }],
        },
      };
      const impl = new KlarnaNetworkPaymentImpl(INSTANCE_ID);
      await impl.initiate(data);
      const [, spec] = mockedNative.initiateWithData.mock.calls[0]! as [
        string,
        Record<string, any>,
      ];
      const subscription = spec.supplementaryPurchaseData!.subscriptions![0]!;
      expect(subscription.name).toBeNull();
      expect(subscription.freeTrial).toBeNull();
      expect(subscription.billingPlans).toBeNull();
    });

    it('maps supplementaryPurchaseData ondemandService including optional fields to null when absent', async () => {
      const data: KlarnaPaymentRequestData = {
        amount: 100,
        currency: 'USD',
        supplementaryPurchaseData: {
          ondemandService: {
            currency: 'SEK',
            averageAmount: 500,
          },
        },
      };
      const impl = new KlarnaNetworkPaymentImpl(INSTANCE_ID);
      await impl.initiate(data);
      const [, spec] = mockedNative.initiateWithData.mock.calls[0]! as [
        string,
        Record<string, any>,
      ];
      const ondemand = spec.supplementaryPurchaseData!.ondemandService!;
      expect(ondemand.currency).toBe('SEK');
      expect(ondemand.averageAmount).toBe(500);
      expect(ondemand.minimumAmount).toBeNull();
      expect(ondemand.maximumAmount).toBeNull();
      expect(ondemand.purchaseInterval).toBeNull();
      expect(ondemand.purchaseIntervalFrequency).toBeNull();
    });

    it('maps supplementaryPurchaseData customer (partnerCustomer) including nested address', async () => {
      const data: KlarnaPaymentRequestData = {
        amount: 100,
        currency: 'USD',
        supplementaryPurchaseData: {
          customer: {
            givenName: 'Jane',
            familyName: 'Doe',
            email: 'jane@example.com',
            address: { city: 'Berlin', country: 'DE' },
          },
        },
      };
      const impl = new KlarnaNetworkPaymentImpl(INSTANCE_ID);
      await impl.initiate(data);
      const [, spec] = mockedNative.initiateWithData.mock.calls[0]! as [
        string,
        Record<string, any>,
      ];
      const customer = spec.supplementaryPurchaseData!.customer!;
      expect(customer.givenName).toBe('Jane');
      expect(customer.familyName).toBe('Doe');
      expect(customer.email).toBe('jane@example.com');
      expect(customer.phone).toBeNull();
      expect(customer.address).toEqual({
        streetAddress: null,
        streetAddress2: null,
        city: 'Berlin',
        region: null,
        postalCode: null,
        country: 'DE',
      });
    });

    it('propagates native rejection', async () => {
      const error = new Error('native initiateWithData failed');
      mockedNative.initiateWithData.mockRejectedValueOnce(error);
      const impl = new KlarnaNetworkPaymentImpl(INSTANCE_ID);
      await expect(
        impl.initiate({ amount: 100, currency: 'SEK' })
      ).rejects.toThrow('native initiateWithData failed');
    });
  });

  describe('fetch', () => {
    it('calls native fetch with instanceId and paymentRequestId', async () => {
      const impl = new KlarnaNetworkPaymentImpl(INSTANCE_ID);
      await impl.fetch(PAYMENT_REQUEST_ID);
      expect(mockedNative.fetch).toHaveBeenCalledWith(
        INSTANCE_ID,
        PAYMENT_REQUEST_ID
      );
    });

    it('returns mapped result', async () => {
      mockedNative.fetch.mockResolvedValueOnce({
        paymentRequestId: 'pr-fetch',
        state: 'COMPLETED',
        previousState: 'IN_PROGRESS',
        stateContext: null,
        stateReason: null,
        paymentRequestReference: 'ref-fetch',
      });
      const impl = new KlarnaNetworkPaymentImpl(INSTANCE_ID);
      const result = await impl.fetch(PAYMENT_REQUEST_ID);
      expect(result.paymentRequestId).toBe('pr-fetch');
      expect(result.state).toBe('COMPLETED');
      expect(result.paymentRequestReference).toBe('ref-fetch');
    });

    it('propagates native rejection', async () => {
      const error = new Error('native fetch failed');
      mockedNative.fetch.mockRejectedValueOnce(error);
      const impl = new KlarnaNetworkPaymentImpl(INSTANCE_ID);
      await expect(impl.fetch(PAYMENT_REQUEST_ID)).rejects.toThrow(
        'native fetch failed'
      );
    });
  });

  describe('cancel', () => {
    it('calls native cancel with instanceId and paymentRequestId', async () => {
      const impl = new KlarnaNetworkPaymentImpl(INSTANCE_ID);
      await impl.cancel(PAYMENT_REQUEST_ID);
      expect(mockedNative.cancel).toHaveBeenCalledWith(
        INSTANCE_ID,
        PAYMENT_REQUEST_ID
      );
    });

    it('returns mapped result with CANCELED state', async () => {
      const impl = new KlarnaNetworkPaymentImpl(INSTANCE_ID);
      const result = await impl.cancel(PAYMENT_REQUEST_ID);
      expect(result.state).toBe('CANCELED');
      expect(result.previousState).toBe('IN_PROGRESS');
      expect(result.stateReason).toBe('PARTNER_CANCELED');
    });

    it('propagates native rejection', async () => {
      const error = new Error('native cancel failed');
      mockedNative.cancel.mockRejectedValueOnce(error);
      const impl = new KlarnaNetworkPaymentImpl(INSTANCE_ID);
      await expect(impl.cancel(PAYMENT_REQUEST_ID)).rejects.toThrow(
        'native cancel failed'
      );
    });
  });
});

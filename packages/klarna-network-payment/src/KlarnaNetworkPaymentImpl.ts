import NativeKlarnaNetworkPayment from './Specs/NativeKlarnaNetworkPayment';
import type {
  KlarnaPayment,
  KlarnaPaymentPresentation,
} from './KlarnaNetworkPayment';
import type { KlarnaPaymentRequest } from './types/KlarnaPaymentRequest';
import type { KlarnaPaymentRequestData } from './types/models/KlarnaPaymentRequestData';
import type {
  KlarnaPaymentPresentationContent,
  KlarnaPaymentPresentationData,
} from './types/models/KlarnaPaymentPresentation';
import {
  mapPaymentRequest,
  mapPaymentRequestData,
  mapPresentationContent,
} from './KlarnaNetworkPaymentMapper';

class KlarnaPaymentPresentationImpl implements KlarnaPaymentPresentation {
  constructor(private readonly instanceId: string) {}

  fetch(
    data: KlarnaPaymentPresentationData
  ): Promise<KlarnaPaymentPresentationContent> {
    return NativeKlarnaNetworkPayment.presentationFetch(
      this.instanceId,
      data as unknown as Object
    ).then(mapPresentationContent);
  }

  handleLink(url: string): Promise<KlarnaPaymentPresentationContent> {
    return NativeKlarnaNetworkPayment.presentationHandleLink(
      this.instanceId,
      url
    ).then(mapPresentationContent);
  }
}

export class KlarnaNetworkPaymentImpl implements KlarnaPayment {
  private readonly instanceId: string;
  readonly presentation: KlarnaPaymentPresentation;

  constructor(instanceId: string) {
    this.instanceId = instanceId;
    this.presentation = new KlarnaPaymentPresentationImpl(instanceId);
  }

  initiate(
    paymentRequestIdOrData: string | KlarnaPaymentRequestData
  ): Promise<KlarnaPaymentRequest> {
    if (typeof paymentRequestIdOrData === 'string') {
      return NativeKlarnaNetworkPayment.initiateWithId(
        this.instanceId,
        paymentRequestIdOrData
      ).then(mapPaymentRequest);
    }
    try {
      return NativeKlarnaNetworkPayment.initiateWithData(
        this.instanceId,
        mapPaymentRequestData(paymentRequestIdOrData)
      ).then(mapPaymentRequest);
    } catch (e) {
      return Promise.reject(e);
    }
  }

  fetch(paymentRequestId: string): Promise<KlarnaPaymentRequest> {
    return NativeKlarnaNetworkPayment.fetch(
      this.instanceId,
      paymentRequestId
    ).then(mapPaymentRequest);
  }

  cancel(paymentRequestId: string): Promise<KlarnaPaymentRequest> {
    return NativeKlarnaNetworkPayment.cancel(
      this.instanceId,
      paymentRequestId
    ).then(mapPaymentRequest);
  }
}

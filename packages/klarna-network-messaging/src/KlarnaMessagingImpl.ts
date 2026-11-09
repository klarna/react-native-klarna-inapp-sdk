import NativeKlarnaNetworkMessaging from './Specs/NativeKlarnaNetworkMessaging';
import type {
  KlarnaMessaging,
  KlarnaMessagingPrequalification,
} from './KlarnaMessaging';
import type {
  KlarnaMessagingPrequalificationData,
  KlarnaMessagingPrequalificationResult,
} from './types/KlarnaMessagingPrequalification';
import {
  mapPrequalificationResult,
  validatePrequalificationData,
} from './KlarnaMessagingMapper';

class KlarnaMessagingPrequalificationImpl implements KlarnaMessagingPrequalification {
  constructor(private readonly instanceId: string) {}

  async initiate(
    data: KlarnaMessagingPrequalificationData
  ): Promise<KlarnaMessagingPrequalificationResult> {
    validatePrequalificationData(data);
    const raw = await NativeKlarnaNetworkMessaging.initiatePrequalification(
      this.instanceId,
      { amount: data.amount, currency: data.currency }
    );
    return mapPrequalificationResult(raw);
  }
}

export class KlarnaMessagingImpl implements KlarnaMessaging {
  readonly prequalification: KlarnaMessagingPrequalification;

  constructor(instanceId: string) {
    this.prequalification = new KlarnaMessagingPrequalificationImpl(instanceId);
  }
}

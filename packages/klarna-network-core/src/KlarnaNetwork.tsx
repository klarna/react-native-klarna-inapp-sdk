import { KlarnaNetworkSession } from './Session/KlarnaNetworkSession';
import { KlarnaNetworkSessionImpl } from './Session/KlarnaNetworkSessionImpl';

export interface KlarnaNetwork {
  readonly session: KlarnaNetworkSession;
}

export class KlarnaNetworkImpl implements KlarnaNetwork {
  session: KlarnaNetworkSession;

  constructor(instanceId: string) {
    this.session = new KlarnaNetworkSessionImpl(instanceId);
  }
}

import NativeKlarnaNetworkCore from '../Specs/NativeKlarnaNetworkCore';
import { KlarnaNetworkSession } from './KlarnaNetworkSession';

export class KlarnaNetworkSessionImpl implements KlarnaNetworkSession {
  private readonly instanceId: string;

  constructor(instanceId: string) {
    this.instanceId = instanceId;
  }

  token(): Promise<string> {
    return NativeKlarnaNetworkCore.getSessionToken(this.instanceId);
  }

  clear(): Promise<void> {
    return NativeKlarnaNetworkCore.clearSession(this.instanceId);
  }
}

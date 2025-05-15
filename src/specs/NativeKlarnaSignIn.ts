import type { TurboModule } from 'react-native';
import { TurboModuleRegistry } from 'react-native';
import type { KlarnaProductEvent } from 'src/types/common/KlarnaProductEvent';

export interface Spec extends TurboModule {
  init(
    instanceId: string,
    environment: string,
    region: string,
    returnUrl: string
  ): Promise<void>;

  dispose(instanceId: string): Promise<void>;

  signIn(
    instanceId: string,
    clientId: string,
    scope: string,
    market: string,
    locale: string,
    tokenizationId: string
  ): Promise<KlarnaProductEvent>;
}

export default TurboModuleRegistry.getEnforcing<Spec>('RNKlarnaSignIn');

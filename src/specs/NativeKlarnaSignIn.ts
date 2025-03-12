import type { TurboModule } from 'react-native';
import { TurboModuleRegistry } from 'react-native';
import type { KlarnaProductEvent } from 'src/types/common/KlarnaProductEvent';

export interface Spec extends TurboModule {
  init(environment: string, region: string, returnUrl: string): void;
  signIn(
    clientId: string,
    scope: string,
    market: string,
    locale: string,
    tokenizationId: string
  ): Promise<KlarnaProductEvent>;
}

export default TurboModuleRegistry.getEnforcing<Spec>('RNKlarnaSignIn');

import type { TurboModule } from 'react-native';
import { TurboModuleRegistry } from 'react-native';

export interface Spec extends TurboModule {
  signIn(
    clientId: string,
    scope: string,
    market: string,
    locale: string,
    tokenizationId: string
  ): void;
}

export default TurboModuleRegistry.getEnforcing<Spec>('KlarnaSignIn');

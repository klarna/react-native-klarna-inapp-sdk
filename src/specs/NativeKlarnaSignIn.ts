import type { TurboModule } from 'react-native';
import { TurboModuleRegistry } from 'react-native';

export interface KlarnaSignInPayload {
  key: string;
  string: string;
}
export interface Spec extends TurboModule {
  init(environment: string, region: string, returnUrl: string): void;
  signIn(
    clientId: string,
    scope: string,
    market: string,
    locale: string,
    tokenizationId: string
  ): Promise<(result: KlarnaSignInPayload, error: KlarnaSignInPayload) => void>;
}

export default TurboModuleRegistry.getEnforcing<Spec>('RNKlarnaSignIn');

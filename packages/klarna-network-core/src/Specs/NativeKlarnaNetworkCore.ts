import { TurboModuleRegistry, type TurboModule } from 'react-native';

type KlarnaConfigurationSpec = Readonly<{
  clientId: string;
  appReturnUrl: string;
  accountId: string | null;
  locale: string | null;
  klarnaNetworkSessionToken: string | null;
}>;

type KlarnaParticipantMetadataSpec = Readonly<{
  name: string;
  sessionReference: string;
  moduleName: string | null;
  moduleVersion: string | null;
}>;

type KlarnaIntegrationMetadataSpec = Readonly<{
  integrator: KlarnaParticipantMetadataSpec;
  originators: ReadonlyArray<KlarnaParticipantMetadataSpec> | null;
}>;

export interface Spec extends TurboModule {
  initialize(
    instanceId: string,
    configuration: KlarnaConfigurationSpec
  ): Promise<void>;

  getSessionToken(instanceId: string): Promise<string>;

  clearSession(instanceId: string): Promise<void>;

  handleReturnUrl(url: string): Promise<boolean>;

  setIntegrationMetadata(
    instanceId: string,
    metadata: KlarnaIntegrationMetadataSpec
  ): void;

  dispose(instanceId: string): Promise<void>;
}

export default TurboModuleRegistry.getEnforcing<Spec>('KlarnaNetworkCore');

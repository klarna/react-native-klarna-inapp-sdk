import { KlarnaNetwork, KlarnaNetworkImpl } from './KlarnaNetwork';
import NativeKlarnaNetworkCore from './Specs/NativeKlarnaNetworkCore';
import type { KlarnaConfiguration } from './types/KlarnaConfiguration';
import type { KlarnaIntegrationMetadata } from './types/KlarnaIntegrationMetadata';

export class Klarna {
  readonly network: KlarnaNetwork;
  private _integrationMetadata: KlarnaIntegrationMetadata | null = null;
  private static _cache = new Map<string, Klarna>();
  readonly instanceId: string;

  private constructor(instanceId: string) {
    this.instanceId = instanceId;
    this.network = new KlarnaNetworkImpl(instanceId);
  }

  private static _cacheKey(configuration: KlarnaConfiguration): string {
    return [
      configuration.clientId,
      configuration.appReturnUrl,
      configuration.accountId ?? '',
      configuration.locale ?? '',
      configuration.klarnaNetworkSessionToken ?? '',
    ].join('|');
  }

  static async initialize(configuration: KlarnaConfiguration): Promise<Klarna> {
    const key = Klarna._cacheKey(configuration);
    const cached = Klarna._cache.get(key);
    if (cached) return cached;

    const instanceId = Math.random().toString(36).substring(2, 15);
    await NativeKlarnaNetworkCore.initialize(instanceId, {
      clientId: configuration.clientId,
      appReturnUrl: configuration.appReturnUrl,
      accountId: configuration.accountId ?? null,
      locale: configuration.locale ?? null,
      klarnaNetworkSessionToken:
        configuration.klarnaNetworkSessionToken ?? null,
    });
    const instance = new Klarna(instanceId);
    Klarna._cache.set(key, instance);
    return instance;
  }

  static handleReturnUrl(url: string): Promise<boolean> {
    return NativeKlarnaNetworkCore.handleReturnUrl(url);
  }

  getIntegrationMetadata(): KlarnaIntegrationMetadata | null {
    return this._integrationMetadata;
  }

  setIntegrationMetadata(metadata: KlarnaIntegrationMetadata): void {
    this._integrationMetadata = metadata;
    NativeKlarnaNetworkCore.setIntegrationMetadata(this.instanceId, {
      integrator: {
        name: metadata.integrator.name,
        sessionReference: metadata.integrator.sessionReference,
        moduleName: metadata.integrator.moduleName ?? null,
        moduleVersion: metadata.integrator.moduleVersion ?? null,
      },
      originators:
        metadata.originators?.map((o) => ({
          name: o.name,
          sessionReference: o.sessionReference,
          moduleName: o.moduleName ?? null,
          moduleVersion: o.moduleVersion ?? null,
        })) ?? null,
    });
  }

  async dispose(): Promise<void> {
    await NativeKlarnaNetworkCore.dispose(this.instanceId);
    Klarna._cache.forEach((v, k) => {
      if (v === this) Klarna._cache.delete(k);
    });
  }
}

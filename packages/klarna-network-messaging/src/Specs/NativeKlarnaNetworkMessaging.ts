import { TurboModuleRegistry, type TurboModule } from 'react-native';

// Object is used for all complex parameters and return types instead of inline typed specs.
// Typed specs would generate C++ structs on iOS that must be immediately converted back to
// NSDictionary for the Swift layer — a round-trip with no benefit. TypeScript in
// KlarnaMessagingImpl.ts is the single type contract for both directions.
export interface Spec extends TurboModule {
  initiatePrequalification(instanceId: string, data: Object): Promise<Object>;
  dispose(instanceId: string): Promise<void>;
}

export default TurboModuleRegistry.getEnforcing<Spec>('KlarnaNetworkMessaging');

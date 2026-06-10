import { TurboModuleRegistry, type TurboModule } from 'react-native';

// Object is used for all complex parameters and return types instead of inline typed specs.
// Typed specs would generate C++ structs on iOS that must be immediately converted back to
// NSDictionary for the Swift layer — a round-trip with no benefit. TypeScript in
// KlarnaNetworkPaymentImpl.ts is the single type contract for both directions.
export interface Spec extends TurboModule {
  initiateWithId(instanceId: string, paymentRequestId: string): Promise<Object>;
  initiateWithData(instanceId: string, data: Object): Promise<Object>;
  fetch(instanceId: string, paymentRequestId: string): Promise<Object>;
  cancel(instanceId: string, paymentRequestId: string): Promise<Object>;
  presentationFetch(instanceId: string, data: Object): Promise<Object>;
  presentationHandleLink(instanceId: string, url: string): Promise<Object>;
  dispose(instanceId: string): Promise<void>;
}

export default TurboModuleRegistry.getEnforcing<Spec>('KlarnaNetworkPayment');

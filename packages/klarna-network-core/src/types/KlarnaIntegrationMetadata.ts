import { KlarnaIntegratorMetadata } from './KlarnaIntegratorMetadata';
import { KlarnaOriginatorMetadata } from './KlarnaOriginatorMetadata';

export interface KlarnaIntegrationMetadata {
  integrator: KlarnaIntegratorMetadata;
  originators?: KlarnaOriginatorMetadata[];
}

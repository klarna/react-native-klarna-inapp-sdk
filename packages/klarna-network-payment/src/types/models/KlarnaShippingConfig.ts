import type { KlarnaShippingConfigMode } from './KlarnaShippingConfigMode';

/** Configuration for collecting shipping information from the customer. */
export type KlarnaShippingConfig = {
  /** List of supported ISO 3166-1 alpha-2 country codes for shipping. */
  supportedCountries?: string[];
  /** Shipping collection mode. */
  mode: KlarnaShippingConfigMode;
};

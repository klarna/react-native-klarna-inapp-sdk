import type { KlarnaShippingType } from './KlarnaShippingType';
import type { KlarnaShippingTypeAttribute } from './KlarnaShippingTypeAttribute';

/** Shipping option details. */
export type KlarnaShippingOption = {
  /** The shipping carrier. */
  shippingCarrier?: string;
  /** The type of shipping. */
  shippingType: KlarnaShippingType;
  /** Additional attributes of the shipping type. */
  shippingTypeAttributes?: KlarnaShippingTypeAttribute[];
};

import type { KlarnaAddress } from '@klarna/react-native-klarna-network-core';
import type { KlarnaShippingOption } from './KlarnaShippingOption';
import type { KlarnaShippingRecipient } from './KlarnaShippingRecipient';

/** Shipping information for a purchase. */
export type KlarnaShipping = {
  /** Shipping address. */
  address?: KlarnaAddress;
  /** Shipping recipient information. */
  recipient?: KlarnaShippingRecipient;
  /** Shipping option. */
  shippingOption?: KlarnaShippingOption;
  /** Reference for the shipping. */
  shippingReference?: string;
};

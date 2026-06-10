import type { KlarnaAddress } from '@klarna/react-native-klarna-network-core';

/** Customer information provided by the partner. */
export type KlarnaPartnerCustomer = {
  /** Customer's address. */
  address?: KlarnaAddress;
  /** Customer's email address. */
  email?: string;
  /** Customer's family name. */
  familyName?: string;
  /** Customer's given name. */
  givenName?: string;
  /** Customer's phone number. */
  phone?: string;
};

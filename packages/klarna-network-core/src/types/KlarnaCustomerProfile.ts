import type { KlarnaAddress } from './KlarnaAddress';

/** Klarna customer profile information. */
export type KlarnaCustomerProfile = {
  /** Customer's billing address. */
  address?: KlarnaAddress;
  /** Klarna's customer identifier. */
  customerId?: string;
  /** ISO 3166-1 alpha-2 country code. */
  country?: string;
  /** Customer's email address. */
  email?: string;
  /** Whether the email address has been verified. */
  emailVerified?: boolean;
  /** Customer's family name. */
  familyName?: string;
  /** Customer's given name. */
  givenName?: string;
  /** Customer's locale. */
  locale?: string;
  /** Customer's phone number. */
  phone?: string;
  /** Whether the phone number has been verified. */
  phoneVerified?: boolean;
};

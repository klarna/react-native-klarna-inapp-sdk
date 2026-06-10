/** Shipping recipient information. */
export type KlarnaShippingRecipient = {
  /** Name to put on the attention line. */
  attention?: string;
  /** Recipient's email address. */
  email?: string;
  /** Recipient's family name. */
  familyName: string;
  /** Recipient's given name. */
  givenName: string;
  /** Recipient's phone number. */
  phone?: string;
};

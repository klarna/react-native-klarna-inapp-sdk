/** A postal address. */
export type KlarnaAddress = {
  /** The first line of the street address. */
  streetAddress?: string;
  /** The second line of the street address. */
  streetAddress2?: string;
  /** The city. */
  city?: string;
  /** The region, state, or province. */
  region?: string;
  /** The postal code. */
  postalCode?: string;
  /** ISO 3166-1 alpha-2 country code. */
  country?: string;
};

/** Types of customer profile information to collect. */
export type KlarnaCollectCustomerProfileType =
  /** Collects the customer profile address. */
  | 'profile:billing_address'
  /** Collects the country. */
  | 'profile:country'
  /** Collects the date of birth of the customer. */
  | 'profile:date_of_birth'
  /** Collects the email and its verification status. */
  | 'profile:email'
  /** Collects the locale. */
  | 'profile:locale'
  /** Collects the family name and given name. */
  | 'profile:name'
  /** Collects the national identification number. */
  | 'profile:national_identification'
  /** Collects the phone number and its verification status. */
  | 'profile:phone';

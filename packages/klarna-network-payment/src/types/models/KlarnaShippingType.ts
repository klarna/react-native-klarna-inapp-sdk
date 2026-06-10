/** Type of shipping for an order. */
export type KlarnaShippingType =
  /** Digital delivery via download. */
  | 'DIGITAL_DOWNLOAD'
  /** Digital delivery via email. */
  | 'DIGITAL_EMAIL'
  /** Other digital delivery method. */
  | 'DIGITAL_OTHER'
  /** Physical delivery by other means. */
  | 'PHYSICAL_OTHER'
  /** Pickup from a box or locker. */
  | 'PICKUP_BOX'
  /** Pickup from a pickup point. */
  | 'PICKUP_POINT'
  /** Pickup from a store. */
  | 'PICKUP_STORE'
  /** Pickup from a warehouse. */
  | 'PICKUP_WAREHOUSE'
  /** Delivery to the curb. */
  | 'TO_CURB'
  /** Delivery to the door. */
  | 'TO_DOOR'
  /** Delivery to the mailbox. */
  | 'TO_MAILBOX';

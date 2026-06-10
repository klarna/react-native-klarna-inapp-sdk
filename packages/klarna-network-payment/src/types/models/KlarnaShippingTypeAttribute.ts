/** Additional attribute for a shipping type. */
export type KlarnaShippingTypeAttribute =
  /** Delivery is contactless. */
  | 'CONTACTLESS_DELIVERY'
  /** Express delivery. */
  | 'EXPRESS'
  /** Identification is required upon delivery. */
  | 'IDENTIFICATION_REQUIRED'
  /** Package may be left at the curb. */
  | 'LEAVE_AT_CURB'
  /** Package may be left at the door. */
  | 'LEAVE_AT_DOOR'
  /** Package may be left with a neighbour. */
  | 'LEAVE_WITH_NEIGHBOUR'
  /** Signature is required upon delivery. */
  | 'SIGNATURE_REQUIRED'
  /** Delivery is tracked. */
  | 'TRACKED'
  /** Delivery is not tracked. */
  | 'UNTRACKED';

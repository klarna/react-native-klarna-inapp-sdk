/** Describes an error reported by a Klarna messaging placement. */
export interface KlarnaMessagingError {
  /** A machine-readable error identifier. */
  readonly name: string;
  /** A human-readable description of the error. */
  readonly message: string;
}

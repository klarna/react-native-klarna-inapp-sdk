export interface KlarnaMobileSDKError {
  readonly isFatal: boolean;
  readonly message: string;
  readonly name: string;
  readonly sessionId: string | undefined;
}

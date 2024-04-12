export interface KlarnaMobileSDKError {
  readonly isFatal: boolean;
  readonly message: string;
  readonly name: string;
  readonly params: { [key: string]: any };
  readonly sessionId: string;
}

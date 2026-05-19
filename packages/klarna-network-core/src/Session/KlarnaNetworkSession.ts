export interface KlarnaNetworkSession {
  token(): Promise<string>;
  clear(): Promise<void>;
}

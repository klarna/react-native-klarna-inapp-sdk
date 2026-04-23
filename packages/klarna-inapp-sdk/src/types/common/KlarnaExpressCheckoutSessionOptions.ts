export namespace KlarnaExpressCheckoutSessionOptions {
  export interface ClientSideSession {
    readonly clientId: string;
  }

  export interface ServerSideSession {
    readonly clientToken: string;
  }
}

export type KlarnaExpressCheckoutSessionOptions =
  | KlarnaExpressCheckoutSessionOptions.ClientSideSession
  | KlarnaExpressCheckoutSessionOptions.ServerSideSession;

import RNKlarnaSignIn from './specs/NativeKlarnaSignIn';
import type { KlarnaEnvironment } from './types/common/KlarnaEnvironment';
import type { KlarnaMobileSDKError } from './types/common/KlarnaMobileSDKError';
import type { KlarnaProductEvent } from './types/common/KlarnaProductEvent';
import type { KlarnaRegion } from './types/common/KlarnaRegion';

export interface KlarnaSignInProps {
  readonly environment: KlarnaEnvironment;
  readonly region: KlarnaRegion;
  readonly returnUrl: string;
}

// declare a class that receive the KlarnaSignInProps in the initializer and expose a method to sign in
export class KlarnaSignIn {
  readonly environment: KlarnaEnvironment;
  readonly region: KlarnaRegion;
  readonly returnUrl: string;
  readonly instanceId: string;

  private constructor(props: KlarnaSignInProps, instanceId: string) {
    this.environment = props.environment;
    this.region = props.region;
    this.returnUrl = props.returnUrl;
    this.instanceId = instanceId;
  }

  static async createInstance(props: KlarnaSignInProps): Promise<KlarnaSignIn> {
    return new Promise((resolve, reject) => {
      let instanceId = Math.random().toString(36).substring(2, 15);
      RNKlarnaSignIn.init(
        instanceId,
        props.environment,
        props.region,
        props.returnUrl
      )
        .then((result) => {
          resolve(new KlarnaSignIn(props, instanceId));
        })
        .catch((error) => {
          reject(error);
        });
    });
  }

  dispose(): Promise<void> {
    return RNKlarnaSignIn.dispose(this.instanceId);
  }

  signIn(
    clientId: string,
    scope: string,
    market: string,
    locale: string,
    tokenizationId: string
  ): Promise<KlarnaProductEvent | KlarnaMobileSDKError> {
    return new Promise((resolve, reject) => {
      RNKlarnaSignIn.signIn(
        this.instanceId,
        clientId,
        scope,
        market,
        locale,
        tokenizationId
      )
        .then((result) => {
          resolve(result);
        })
        .catch((error) => {
          console.error('Sign in failed with error: ', error);
          const errorInfo = error.userInfo;
          const sdkError: KlarnaMobileSDKError = {
            isFatal: errorInfo.isFatal,
            message: errorInfo.message,
            name: errorInfo.action,
            sessionId: errorInfo.sessionId,
          };
          reject(sdkError);
        });
    });
  }
}

import RNKlarnaSignIn from './specs/NativeKlarnaSignIn';

export interface KlarnaSignInProps {
  readonly environment: string;
  readonly region: string;
  readonly returnUrl: string;
}

// declare a class that receive the KlarnaSignInProps in the initializer and expose a method to sign in
export class KlarnaSignIn {
  readonly environment: string;
  readonly region: string;
  readonly returnUrl: string;

  constructor(props: KlarnaSignInProps) {
    this.environment = props.environment;
    this.region = props.region;
    this.returnUrl = props.returnUrl;
    RNKlarnaSignIn.init(this.environment, this.region, this.returnUrl);
  }

  signIn(
    clientId: string,
    scope: string,
    market: string,
    locale: string,
    tokenizationId: string
  ): Promise<(result: any, error: any) => void> {
    return new Promise((resolve, reject) => {
      RNKlarnaSignIn.signIn(clientId, scope, market, locale, tokenizationId)
        .then((result) => {
          console.log('Sign in success with result: ', result);
          resolve(result);
        })
        .catch((error) => {
          console.error('Sign in failed with error: ', error);
          reject(error);
        });
    });
  }
}

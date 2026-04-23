import type { ViewProps } from 'react-native/Libraries/Components/View/ViewPropTypes';
import type { DirectEventHandler } from 'react-native/Libraries/Types/CodegenTypes';
import type { HostComponent } from 'react-native';
import codegenNativeComponent from 'react-native/Libraries/Utilities/codegenNativeComponent';

export interface RNKlarnaExpressCheckoutViewProps extends ViewProps {
  // Session configuration
  readonly sessionType: string;
  readonly clientId: string;
  readonly clientToken: string;

  // Required configuration
  readonly locale: string;
  readonly environment: string;
  readonly region: string;
  readonly returnUrl: string;

  // Style props
  readonly theme: string;
  readonly shape: string;
  readonly buttonStyle: string;

  // Optional configuration
  readonly autoFinalize: boolean;
  readonly collectShippingAddress: boolean;
  readonly sessionData: string;

  // Events
  readonly onAuthorized: DirectEventHandler<
    Readonly<{
      readonly authorizationResponse: Readonly<{
        readonly showForm: boolean;
        readonly approved: boolean;
        readonly finalizedRequired: boolean;
        readonly clientToken: string;
        readonly authorizationToken: string;
        readonly sessionId: string;
        readonly collectedShippingAddress: string;
        readonly merchantReference1: string;
        readonly merchantReference2: string;
      }>;
    }>
  >;
  readonly onError: DirectEventHandler<
    Readonly<{
      readonly error: Readonly<{
        readonly isFatal: boolean;
        readonly message: string;
        readonly name: string;
      }>;
    }>
  >;
  readonly onResized: DirectEventHandler<
    Readonly<{
      // number not supported for events
      readonly height: string;
    }>
  >;
}

type KlarnaExpressCheckoutViewNativeComponentType =
  HostComponent<RNKlarnaExpressCheckoutViewProps>;

export default codegenNativeComponent<RNKlarnaExpressCheckoutViewProps>(
  'RNKlarnaExpressCheckoutView'
) as KlarnaExpressCheckoutViewNativeComponentType;

import type { ViewProps } from 'react-native/Libraries/Components/View/ViewPropTypes';
import type { HostComponent } from 'react-native';
import codegenNativeComponent from 'react-native/Libraries/Utilities/codegenNativeComponent';
import codegenNativeCommands from 'react-native/Libraries/Utilities/codegenNativeCommands';
import type { DirectEventHandler } from 'react-native/Libraries/Types/CodegenTypes';

export interface NativeProps extends ViewProps {
  readonly category: string;
  readonly returnUrl: string;
  readonly onInitialized: DirectEventHandler<null>;
  readonly onLoaded: DirectEventHandler<null>;
  readonly onLoadedPaymentReview: DirectEventHandler<null>;
  readonly onAuthorized: DirectEventHandler<
    Readonly<{
      readonly approved: boolean;
      readonly authToken?: string;
      readonly finalizeRequired: boolean;
    }>
  >;
  readonly onReauthorized: DirectEventHandler<
    Readonly<{
      readonly approved: boolean;
      readonly authToken?: string;
    }>
  >;
  readonly onFinalized: DirectEventHandler<
    Readonly<{
      readonly approved: boolean;
      readonly authToken?: string;
    }>
  >;
  readonly onError: DirectEventHandler<
    Readonly<{
      readonly error: Readonly<{
        readonly action: string;
        readonly isFatal: boolean;
        readonly message: string;
        readonly name: string;
        // not supported yet https://github.com/facebook/react-native/issues/36817#issuecomment-1697107218
        // readonly invalidFields: Array<string>;
        readonly sessionId: string;
      }>;
    }>
  >;
}

type KlarnaPaymentViewNativeComponentType = HostComponent<NativeProps>;

interface NativeCommands {
  initialize: (
    viewRef: React.ElementRef<KlarnaPaymentViewNativeComponentType>,
    clientToken: string,
    returnUrl?: string | undefined
  ) => void;
  load: (
    viewRef: React.ElementRef<KlarnaPaymentViewNativeComponentType>,
    sessionData: string | undefined
  ) => void;
  loadPaymentReview: (
    viewRef: React.ElementRef<KlarnaPaymentViewNativeComponentType>
  ) => void;
  authorize: (
    viewRef: React.ElementRef<KlarnaPaymentViewNativeComponentType>,
    autoFinalize: boolean,
    sessionData: string | undefined
  ) => void;
  reauthorize: (
    viewRef: React.ElementRef<KlarnaPaymentViewNativeComponentType>,
    sessionData: string | undefined
  ) => void;
  finalize: (
    viewRef: React.ElementRef<KlarnaPaymentViewNativeComponentType>,
    sessionData: string | undefined
  ) => void;
}

export const Commands: NativeCommands = codegenNativeCommands<NativeCommands>({
  supportedCommands: [
    'initialize',
    'load',
    'loadPaymentReview',
    'authorize',
    'reauthorize',
    'finalize',
  ],
});

export default codegenNativeComponent<NativeProps>(
  'RNKlarnaPaymentView'
) as HostComponent<NativeProps>;

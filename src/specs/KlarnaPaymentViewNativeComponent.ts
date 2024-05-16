import type { ViewProps } from 'react-native/Libraries/Components/View/ViewPropTypes';
import type { HostComponent } from 'react-native';
import codegenNativeComponent from 'react-native/Libraries/Utilities/codegenNativeComponent';
import codegenNativeCommands from 'react-native/Libraries/Utilities/codegenNativeCommands';
import type { DirectEventHandler } from 'react-native/Libraries/Types/CodegenTypes';
import React from 'react';

export interface RNKlarnaPaymentViewProps extends ViewProps {
  readonly category: string;
  readonly returnUrl: string;
  readonly onInitialized: DirectEventHandler<null>;
  readonly onLoaded: DirectEventHandler<null>;
  readonly onLoadedPaymentReview: DirectEventHandler<null>;
  readonly onAuthorized: DirectEventHandler<
    Readonly<{
      readonly approved: boolean;
      readonly authToken: string | null;
      readonly finalizeRequired: boolean;
    }>
  >;
  readonly onReauthorized: DirectEventHandler<
    Readonly<{
      readonly approved: boolean;
      readonly authToken: string | null;
    }>
  >;
  readonly onFinalized: DirectEventHandler<
    Readonly<{
      readonly approved: boolean;
      readonly authToken: string | null;
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
  readonly onResized: DirectEventHandler<
    Readonly<{
      // number not supported for events
      readonly height: string;
    }>
  >;
}

type KlarnaPaymentViewNativeComponentType =
  HostComponent<RNKlarnaPaymentViewProps>;

interface RNKlarnaPaymentViewNativeCommands {
  initialize: (
    viewRef: React.ElementRef<KlarnaPaymentViewNativeComponentType>,
    clientToken: string,
    returnUrl: string | null
  ) => void;
  load: (
    viewRef: React.ElementRef<KlarnaPaymentViewNativeComponentType>,
    sessionData: string | null
  ) => void;
  loadPaymentReview: (
    viewRef: React.ElementRef<KlarnaPaymentViewNativeComponentType>
  ) => void;
  authorize: (
    viewRef: React.ElementRef<KlarnaPaymentViewNativeComponentType>,
    autoFinalize: boolean,
    sessionData: string | null
  ) => void;
  reauthorize: (
    viewRef: React.ElementRef<KlarnaPaymentViewNativeComponentType>,
    sessionData: string | null
  ) => void;
  finalize: (
    viewRef: React.ElementRef<KlarnaPaymentViewNativeComponentType>,
    sessionData: string | null
  ) => void;
}

export const Commands: RNKlarnaPaymentViewNativeCommands =
  codegenNativeCommands<RNKlarnaPaymentViewNativeCommands>({
    supportedCommands: [
      'initialize',
      'load',
      'loadPaymentReview',
      'authorize',
      'reauthorize',
      'finalize',
    ],
  });

export default codegenNativeComponent<RNKlarnaPaymentViewProps>(
  'RNKlarnaPaymentView'
) as KlarnaPaymentViewNativeComponentType;

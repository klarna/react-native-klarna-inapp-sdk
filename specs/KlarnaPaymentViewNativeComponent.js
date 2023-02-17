// @flow strict-local
import type { ViewProps } from "react-native/Libraries/Components/View/ViewPropTypes";
import type { HostComponent } from "react-native";
import type {
  DirectEventHandler,
  Float,
  WithDefault,
  Double,
  Int32,
} from "react-native/Libraries/Types/CodegenTypes";
import codegenNativeCommands from "react-native/Libraries/Utilities/codegenNativeCommands";
import codegenNativeComponent from "react-native/Libraries/Utilities/codegenNativeComponent";
import React from "react";

type KlarnaPaymentViewNativeComponentType = HostComponent<NativeProps>;

interface NativeCommands {
  +initialize: (
    viewRef: React.ElementRef<KlarnaPaymentViewNativeComponentType>,
    clientToken?: string,
    returnUrl?: string
  ) => void;
  +load: (
    viewRef: React.ElementRef<KlarnaPaymentViewNativeComponentType>,
    sessionData?: string
  ) => void;
  +loadPaymentReview: (
    viewRef: React.ElementRef<KlarnaPaymentViewNativeComponentType>
  ) => void;
  +authorize: (
    viewRef: React.ElementRef<KlarnaPaymentViewNativeComponentType>,
    autoFinalize?: boolean,
    sessionData?: string
  ) => void;
  +reauthorize: (
    viewRef: React.ElementRef<KlarnaPaymentViewNativeComponentType>,
    sessionData?: string
  ) => void;
  +finalize: (
    viewRef: React.ElementRef<KlarnaPaymentViewNativeComponentType>,
    sessionData?: string
  ) => void;
}

type NativeProps = $ReadOnly<{|
  ...ViewProps,
  category?: ?string,
  onInitialized?: ?DirectEventHandler<null>,
  onLoaded: ?DirectEventHandler<null>,
  onLoadedPaymentReview: ?DirectEventHandler<null>,
  onAuthorized: ?DirectEventHandler<
    $ReadOnly<{|
      approved: Int32,
      authToken?: ?string,
      finalizeRequired: Int32,
    |}>
  >,
  onReauthorized: ?DirectEventHandler<
    $ReadOnly<{|
      approved: Int32,
      authToken?: ?string,
    |}>
  >,
  onFinalized: ?DirectEventHandler<
    $ReadOnly<{|
      approved: Int32,
      authToken?: ?string,
    |}>
  >,
  onError: ?DirectEventHandler<
    $ReadOnly<{|
      error: $ReadOnly<{|
        action: string,
        isFatal: Int32,
        message: string,
        name: string,
      |}>,
    |}>
  >,
|}>;

export const Commands: NativeCommands = codegenNativeCommands<NativeCommands>({
  supportedCommands: [
    "initialize",
    "load",
    "loadPaymentReview",
    "authorize",
    "reauthorize",
    "finalize",
  ],
});

export default (codegenNativeComponent<NativeProps>(
  "RNKlarnaPaymentView"
): HostComponent<NativeProps>);

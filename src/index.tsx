import React, { Component, type RefObject } from 'react';
import type {
  NativeMethods,
  NativeSyntheticEvent,
  ViewStyle,
} from 'react-native';
import RNKlarnaPaymentView, {
  Commands,
  type NativeProps,
} from '../specs/KlarnaPaymentViewNativeComponent';

interface KlarnaPaymentViewProps {
  style: ViewStyle;
  category: string;
  returnUrl: string;
  onInitialized: () => void;
  onLoaded: () => void;
  onLoadedPaymentReview: () => void;
  onAuthorized: (
    approved: boolean,
    authToken: string | null,
    finalizeRequired: boolean | null
  ) => void;
  onReauthorized: (approved: boolean, authToken: string | null) => void;
  onFinalized: (approved: boolean, authToken: string | null) => void;
  onError: (error: KlarnaPaymentsSDKError) => void;
}

export class KlarnaPaymentView extends Component<KlarnaPaymentViewProps> {
  paymentViewRef: RefObject<Component<NativeProps> & Readonly<NativeMethods>>;

  constructor(props: KlarnaPaymentViewProps) {
    super(props);
    this.paymentViewRef = React.createRef();
  }

  render() {
    return (
      <RNKlarnaPaymentView
        ref={this.paymentViewRef}
        style={this.props.style}
        category={this.props.category}
        returnUrl={this.props.returnUrl}
        onInitialized={(_event: NativeSyntheticEvent<any>) => {
          this.props.onInitialized();
        }}
        onLoaded={(_event: NativeSyntheticEvent<any>) => {
          this.props.onLoaded();
        }}
        onLoadedPaymentReview={(_event: NativeSyntheticEvent<any>) => {
          this.props.onLoadedPaymentReview();
        }}
        onAuthorized={(
          event: NativeSyntheticEvent<
            Readonly<{
              readonly approved: boolean;
              readonly authToken: string | null;
              readonly finalizeRequired: boolean;
            }>
          >
        ) => {
          this.props.onAuthorized(
            event.nativeEvent.approved,
            event.nativeEvent.authToken,
            event.nativeEvent.finalizeRequired
          );
        }}
        onReauthorized={(
          event: NativeSyntheticEvent<
            Readonly<{
              readonly approved: boolean;
              readonly authToken: string | null;
            }>
          >
        ) => {
          this.props.onReauthorized(
            event.nativeEvent.approved,
            event.nativeEvent.authToken
          );
        }}
        onFinalized={(
          event: NativeSyntheticEvent<
            Readonly<{
              readonly approved: boolean;
              readonly authToken: string | null;
            }>
          >
        ) => {
          this.props.onFinalized(
            event.nativeEvent.approved,
            event.nativeEvent.authToken
          );
        }}
        onError={(
          event: NativeSyntheticEvent<
            Readonly<{
              readonly error: Readonly<{
                readonly action: string;
                readonly isFatal: boolean;
                readonly message: string;
                readonly name: string;
                // readonly invalidFields: Array<string>;
                readonly sessionId: string;
              }>;
            }>
          >
        ) => {
          this.props.onError(event.nativeEvent.error);
        }}
      />
    );
  }

  initialize = (sessionToken: string, returnUrl: string | null) => {
    const view = this.paymentViewRef.current;
    if (view != null) {
      Commands.initialize(view, sessionToken, returnUrl || '');
    }
  };

  load = (sessionData: string | null = null) => {
    const view = this.paymentViewRef.current;
    if (view != null) {
      Commands.load(view, sessionData || '');
    }
  };

  loadPaymentReview = () => {
    const view = this.paymentViewRef.current;
    if (view != null) {
      Commands.loadPaymentReview(view);
    }
  };

  authorize = (
    autoFinalize: boolean | null = true,
    sessionData: string | null = null
  ) => {
    const view = this.paymentViewRef.current;
    if (view != null) {
      Commands.authorize(view, autoFinalize || true, sessionData || '');
    }
  };

  reauthorize = (sessionData: string | null = null) => {
    const view = this.paymentViewRef.current;
    if (view != null) {
      Commands.reauthorize(view, sessionData || '');
    }
  };

  finalize = (sessionData: string | null = null) => {
    const view = this.paymentViewRef.current;
    if (view != null) {
      Commands.finalize(view, sessionData || '');
    }
  };
}

interface KlarnaPaymentsSDKError {
  readonly action: string;
  readonly isFatal: boolean;
  readonly message: string;
  readonly name: string;
  // readonly invalidFields: Array<string>;
  readonly sessionId: string;
}

export default KlarnaPaymentView;

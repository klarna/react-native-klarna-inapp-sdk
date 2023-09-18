import React, { Component, type RefObject } from 'react';
import type {
  NativeMethods,
  NativeSyntheticEvent,
  ViewStyle,
} from 'react-native';
import RNKlarnaPaymentView, {
  RNKlarnaPaymentViewCommands,
  type RNKlarnaPaymentViewProps,
} from './specs/KlarnaPaymentViewNativeComponent';

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

interface KlarnaPaymentViewState {
  nativeViewHeight: number;
}

export class KlarnaPaymentView extends Component<
  KlarnaPaymentViewProps,
  KlarnaPaymentViewState
> {
  paymentViewRef: RefObject<
    Component<RNKlarnaPaymentViewProps> & Readonly<NativeMethods>
  >;

  constructor(props: KlarnaPaymentViewProps) {
    super(props);
    this.state = {
      nativeViewHeight: 0,
    };
    this.paymentViewRef = React.createRef();
  }

  render() {
    return (
      <RNKlarnaPaymentView
        ref={this.paymentViewRef}
        style={{
          width: '100%',
          height: this.state.nativeViewHeight,
        }}
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
        onResized={(
          event: NativeSyntheticEvent<
            Readonly<{
              readonly height: string;
            }>
          >
        ) => {
          const newHeight = Number(event.nativeEvent.height);
          if (newHeight !== this.state.nativeViewHeight) {
            console.log('onResized', newHeight);
            this.setState({ nativeViewHeight: newHeight });
          }
        }}
      />
    );
  }

  initialize = (sessionToken: string, returnUrl: string | null) => {
    const view = this.paymentViewRef.current;
    if (view != null) {
      RNKlarnaPaymentViewCommands.initialize(
        view,
        sessionToken,
        returnUrl || ''
      );
    }
  };

  load = (sessionData: string | null = null) => {
    const view = this.paymentViewRef.current;
    if (view != null) {
      RNKlarnaPaymentViewCommands.load(view, sessionData || '');
    }
  };

  loadPaymentReview = () => {
    const view = this.paymentViewRef.current;
    if (view != null) {
      RNKlarnaPaymentViewCommands.loadPaymentReview(view);
    }
  };

  authorize = (
    autoFinalize: boolean | null = true,
    sessionData: string | null = null
  ) => {
    const view = this.paymentViewRef.current;
    if (view != null) {
      RNKlarnaPaymentViewCommands.authorize(
        view,
        autoFinalize || true,
        sessionData || ''
      );
    }
  };

  reauthorize = (sessionData: string | null = null) => {
    const view = this.paymentViewRef.current;
    if (view != null) {
      RNKlarnaPaymentViewCommands.reauthorize(view, sessionData || '');
    }
  };

  finalize = (sessionData: string | null = null) => {
    const view = this.paymentViewRef.current;
    if (view != null) {
      RNKlarnaPaymentViewCommands.finalize(view, sessionData || '');
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

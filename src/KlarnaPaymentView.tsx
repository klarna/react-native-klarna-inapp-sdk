import React, { Component, type RefObject } from 'react';
import type {
  NativeMethods,
  NativeSyntheticEvent,
  ViewStyle,
} from 'react-native';
import RNKlarnaPaymentView, {
  Commands as RNKlarnaPaymentViewCommands,
  type RNKlarnaPaymentViewProps,
} from './specs/KlarnaPaymentViewNativeComponent';

export interface KlarnaPaymentViewProps {
  style: ViewStyle;
  readonly category: string;
  readonly returnUrl: string;
  readonly onInitialized: () => void;
  readonly onLoaded: () => void;
  readonly onLoadedPaymentReview: () => void;
  readonly onAuthorized: (
    approved: boolean,
    authToken: string | null,
    finalizeRequired: boolean | null
  ) => void;
  readonly onReauthorized: (
    approved: boolean,
    authToken: string | null
  ) => void;
  readonly onFinalized: (approved: boolean, authToken: string | null) => void;
  readonly onError: (error: KlarnaPaymentsSDKError) => void;
}

interface KlarnaReactPaymentViewOldProps {
  style: ViewStyle;
  readonly category: string;
  readonly returnUrl?: string;
  readonly onInitialized?: (event: any) => void;
  readonly onLoaded?: (event: any) => void;
  readonly onLoadedPaymentReview?: (event: any) => void;
  readonly onAuthorized?: (event: any) => void;
  readonly onReauthorized?: (event: any) => void;
  readonly onFinalized?: (event: any) => void;
  readonly onError?: (event: any) => void;
}

type KlarnaPaymentViewPropsAny =
  | KlarnaPaymentViewProps
  | KlarnaReactPaymentViewOldProps;

interface KlarnaPaymentViewState {
  nativeViewHeight: number;
}

export class KlarnaPaymentView extends Component<
  KlarnaPaymentViewPropsAny,
  KlarnaPaymentViewState
> {
  paymentViewRef: RefObject<
    Component<RNKlarnaPaymentViewProps> & Readonly<NativeMethods>
  >;

  constructor(props: KlarnaPaymentViewPropsAny) {
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
        /* eslint-disable-next-line react-native/no-inline-styles */
        style={{
          width: '100%',
          minHeight: this.state.nativeViewHeight,
          flexShrink: 1,
        }}
        category={this.props.category || ''}
        returnUrl={this.props.returnUrl || ''}
        onInitialized={(_event: NativeSyntheticEvent<any>) => {
          if (this.props.onInitialized != null) {
            /**
             * if props is the old spec {@link KlarnaReactPaymentViewOldProps}
             */
            if (
              typeof this.props.onInitialized === 'function' &&
              this.props.onInitialized.length === 1
            ) {
              this.props.onInitialized(_event);
            } else {
              // @ts-ignore
              this.props.onInitialized();
            }
          }
        }}
        onLoaded={(_event: NativeSyntheticEvent<any>) => {
          if (this.props.onLoaded != null) {
            /**
             * if props is the old spec {@link KlarnaReactPaymentViewOldProps}
             */
            if (
              typeof this.props.onLoaded === 'function' &&
              this.props.onLoaded.length === 1
            ) {
              this.props.onLoaded(_event);
            } else {
              // @ts-ignore
              this.props.onLoaded();
            }
          }
        }}
        onLoadedPaymentReview={(_event: NativeSyntheticEvent<any>) => {
          if (this.props.onLoadedPaymentReview != null) {
            /**
             * if props is the old spec {@link KlarnaReactPaymentViewOldProps}
             */
            if (
              typeof this.props.onLoadedPaymentReview === 'function' &&
              this.props.onLoadedPaymentReview.length === 1
            ) {
              this.props.onLoadedPaymentReview(_event);
            } else {
              // @ts-ignore
              this.props.onLoadedPaymentReview();
            }
          }
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
          if (this.props.onAuthorized != null) {
            /**
             * if props is the old spec {@link KlarnaReactPaymentViewOldProps}
             */
            if (
              typeof this.props.onAuthorized === 'function' &&
              this.props.onAuthorized.length === 1
            ) {
              // @ts-ignore
              this.props.onAuthorized(event);
            } else {
              this.props.onAuthorized(
                event.nativeEvent.approved,
                event.nativeEvent.authToken === ''
                  ? null
                  : event.nativeEvent.authToken,
                event.nativeEvent.finalizeRequired
              );
            }
          }
        }}
        onReauthorized={(
          event: NativeSyntheticEvent<
            Readonly<{
              readonly approved: boolean;
              readonly authToken: string | null;
            }>
          >
        ) => {
          if (this.props.onReauthorized != null) {
            /**
             * if props is the old spec {@link KlarnaReactPaymentViewOldProps}
             */
            if (
              typeof this.props.onReauthorized === 'function' &&
              this.props.onReauthorized.length === 1
            ) {
              // @ts-ignore
              this.props.onReauthorized(event);
            } else {
              this.props.onReauthorized(
                event.nativeEvent.approved,
                event.nativeEvent.authToken === ''
                  ? null
                  : event.nativeEvent.authToken
              );
            }
          }
        }}
        onFinalized={(
          event: NativeSyntheticEvent<
            Readonly<{
              readonly approved: boolean;
              readonly authToken: string | null;
            }>
          >
        ) => {
          if (this.props.onFinalized != null) {
            /**
             * if props is the old spec {@link KlarnaReactPaymentViewOldProps}
             */
            if (
              typeof this.props.onFinalized === 'function' &&
              this.props.onFinalized.length === 1
            ) {
              // @ts-ignore
              this.props.onFinalized(event);
            } else {
              this.props.onFinalized(
                event.nativeEvent.approved,
                event.nativeEvent.authToken === ''
                  ? null
                  : event.nativeEvent.authToken
              );
            }
          }
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
          if (this.props.onError != null) {
            /**
             * if props is the old spec {@link KlarnaReactPaymentViewOldProps}
             */
            if (
              typeof this.props.onAuthorized === 'function' &&
              this.props.onAuthorized.length === 1
            ) {
              // @ts-ignore
              this.props.onError(event);
            } else {
              this.props.onError(event.nativeEvent.error);
            }
          }
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

  initialize = (clientToken: string, returnUrl: string | null = null) => {
    const view = this.paymentViewRef.current;
    if (view != null) {
      RNKlarnaPaymentViewCommands.initialize(
        view,
        clientToken,
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

export interface KlarnaPaymentsSDKError {
  readonly action: string;
  readonly isFatal: boolean;
  readonly message: string;
  readonly name: string;
  // readonly invalidFields: Array<string>;
  readonly sessionId: string;
}

export default KlarnaPaymentView;

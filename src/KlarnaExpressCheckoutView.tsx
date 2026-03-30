import React, { Component, type RefObject } from 'react';
import type {
  NativeMethods,
  NativeSyntheticEvent,
  ViewStyle,
} from 'react-native';
import type { KlarnaMobileSDKError } from './types/common/KlarnaMobileSDKError';
import type { KlarnaEnvironment } from './types/common/KlarnaEnvironment';
import type { KlarnaRegion } from './types/common/KlarnaRegion';
import type { KlarnaExpressCheckoutSessionOptions } from './types/common/KlarnaExpressCheckoutSessionOptions';
import type { KlarnaButtonTheme } from './types/button/KlarnaButtonTheme';
import type { KlarnaButtonShape } from './types/button/KlarnaButtonShape';
import type { KlarnaButtonStyle } from './types/button/KlarnaButtonStyle';
import RNKlarnaExpressCheckoutView, {
  type RNKlarnaExpressCheckoutViewProps,
} from './specs/KlarnaExpressCheckoutViewNativeComponent';

export interface KlarnaExpressCheckoutAuthorizationResponse {
  readonly showForm: boolean;
  readonly approved: boolean;
  readonly finalizedRequired: boolean;
  readonly clientToken: string;
  readonly authorizationToken: string;
  readonly sessionId: string;
  readonly collectedShippingAddress: string;
  readonly merchantReference1: string;
  readonly merchantReference2: string;
}

export interface KlarnaExpressCheckoutViewProps {
  style?: ViewStyle;
  // Session
  readonly sessionOptions: KlarnaExpressCheckoutSessionOptions;
  // Required
  readonly locale: string;
  readonly environment: KlarnaEnvironment;
  readonly region: KlarnaRegion;
  readonly returnUrl: string;
  // Style
  readonly theme?: KlarnaButtonTheme;
  readonly shape?: KlarnaButtonShape;
  readonly buttonStyle?: KlarnaButtonStyle;
  // Optional
  readonly autoFinalize?: boolean;
  readonly collectShippingAddress?: boolean;
  readonly sessionData?: string;
  // Callbacks
  readonly onAuthorized?: (
    response: KlarnaExpressCheckoutAuthorizationResponse
  ) => void;
  readonly onError?: (error: KlarnaMobileSDKError) => void;
}

interface KlarnaExpressCheckoutViewState {
  nativeViewHeight: number;
}

const normalizeSessionData = (sessionData?: string): string => {
  if (sessionData?.trim()) {
    return sessionData;
  }
  return '';
};

export class KlarnaExpressCheckoutView extends Component<
  KlarnaExpressCheckoutViewProps,
  KlarnaExpressCheckoutViewState
> {
  viewRef: RefObject<
    Component<RNKlarnaExpressCheckoutViewProps> & Readonly<NativeMethods>
  >;

  constructor(props: KlarnaExpressCheckoutViewProps) {
    super(props);
    this.state = {
      nativeViewHeight: 0,
    };
    this.viewRef = React.createRef();
  }

  render() {
    return (
      <RNKlarnaExpressCheckoutView
        ref={this.viewRef}
        /* eslint-disable-next-line react-native/no-inline-styles */
        style={{
          width: '100%',
          height:
            this.state.nativeViewHeight > 0 ? this.state.nativeViewHeight : 48,
          ...this.props.style,
        }}
        sessionType={
          'clientId' in this.props.sessionOptions ? 'clientId' : 'clientToken'
        }
        clientId={
          'clientId' in this.props.sessionOptions
            ? this.props.sessionOptions.clientId
            : ''
        }
        clientToken={
          'clientToken' in this.props.sessionOptions
            ? this.props.sessionOptions.clientToken
            : ''
        }
        locale={this.props.locale}
        environment={this.props.environment}
        region={this.props.region}
        returnUrl={this.props.returnUrl}
        theme={this.props.theme ?? ''}
        shape={this.props.shape ?? ''}
        buttonStyle={this.props.buttonStyle ?? ''}
        autoFinalize={this.props.autoFinalize ?? true}
        collectShippingAddress={this.props.collectShippingAddress ?? false}
        sessionData={normalizeSessionData(this.props.sessionData)}
        onAuthorized={(
          event: NativeSyntheticEvent<
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
          >
        ) => {
          this.props.onAuthorized?.(event.nativeEvent.authorizationResponse);
        }}
        onError={(
          event: NativeSyntheticEvent<
            Readonly<{
              readonly error: Readonly<{
                readonly isFatal: boolean;
                readonly message: string;
                readonly name: string;
              }>;
            }>
          >
        ) => {
          const mobileSdkError: KlarnaMobileSDKError = {
            isFatal: event.nativeEvent.error.isFatal,
            message: event.nativeEvent.error.message,
            name: event.nativeEvent.error.name,
          };
          this.props.onError?.(mobileSdkError);
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
            this.setState({ nativeViewHeight: newHeight });
          }
        }}
      />
    );
  }
}

export default KlarnaExpressCheckoutView;

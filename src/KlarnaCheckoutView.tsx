import React, { Component, type RefObject } from 'react';
import type {
  NativeMethods,
  NativeSyntheticEvent,
  ViewStyle,
} from 'react-native';
import type { KlarnaProductEvent } from './types/common/KlarnaProductEvent';
import type { KlarnaMobileSDKError } from './types/common/KlarnaMobileSDKError';
import RNKlarnaCheckoutView, {
  RNKlarnaCheckoutViewCommands,
  type RNKlarnaCheckoutViewProps,
} from './specs/KlarnaCheckoutViewNativeComponent';

export interface KlarnaCheckoutViewProps {
  style?: ViewStyle;
  readonly returnUrl: string;
  readonly onEvent?: (klarnaProductEvent: KlarnaProductEvent) => void;
  readonly onError?: (error: KlarnaMobileSDKError) => void;
}

interface KlarnaCheckoutViewState {
  nativeViewHeight: number;
}

export class KlarnaCheckoutView extends Component<
  KlarnaCheckoutViewProps,
  KlarnaCheckoutViewState
> {
  checkoutViewRef: RefObject<
    Component<RNKlarnaCheckoutViewProps> & Readonly<NativeMethods>
  >;

  constructor(props: KlarnaCheckoutViewProps) {
    super(props);
    this.state = {
      nativeViewHeight: 0,
    };
    this.checkoutViewRef = React.createRef();
  }

  render() {
    return (
      <RNKlarnaCheckoutView
        ref={this.checkoutViewRef}
        /* eslint-disable-next-line react-native/no-inline-styles */
        style={{
          width: '100%',
          height: this.state.nativeViewHeight,
        }}
        returnUrl={this.props.returnUrl || ''}
        onEvent={(
          event: NativeSyntheticEvent<
            Readonly<{
              readonly productEvent: Readonly<{
                readonly action: string;
                readonly params: string;
              }>;
            }>
          >
        ) => {
          let params = {};
          try {
            params = JSON.parse(event.nativeEvent.productEvent.params);
          } catch (e) {
            console.error('Failed to parse productEvent.params', e);
          }
          const productEvent: KlarnaProductEvent = {
            action: event.nativeEvent.productEvent.action,
            params: params,
          };
          this.props.onEvent?.(productEvent);
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
            console.log('onResized', newHeight);
            this.setState({ nativeViewHeight: newHeight });
          }
        }}
      />
    );
  }

  setSnippet = (snippet: string) => {
    const view = this.checkoutViewRef.current;
    if (view != null) {
      RNKlarnaCheckoutViewCommands.setSnippet(view, snippet);
    }
  };

  suspend = () => {
    const view = this.checkoutViewRef.current;
    if (view != null) {
      RNKlarnaCheckoutViewCommands.suspend(view);
    }
  };

  resume = () => {
    const view = this.checkoutViewRef.current;
    if (view != null) {
      RNKlarnaCheckoutViewCommands.resume(view);
    }
  };
}

export default KlarnaCheckoutView;

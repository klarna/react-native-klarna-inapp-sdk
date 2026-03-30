import React, { Component, type RefObject } from 'react';
import {
  type NativeMethods,
  type NativeSyntheticEvent,
  StyleSheet,
  type ViewStyle,
} from 'react-native';
import type { KlarnaMobileSDKError } from './types/common/KlarnaMobileSDKError';
import type { KlarnaOSMPlacementKey } from './types/common/KlarnaOSMPlacementKey';
import { KlarnaEnvironment } from './types/common/KlarnaEnvironment';
import { KlarnaRegion } from './types/common/KlarnaRegion';
import { KlarnaTheme } from './types/common/KlarnaTheme';
import RNKlarnaOSMView, {
  Commands as RNKlarnaOSMViewCommands,
  type RNKlarnaOSMViewProps,
} from './specs/KlarnaOSMViewNativeComponent';

export interface KlarnaOSMViewProps {
  style?: ViewStyle;
  readonly clientId: string;
  readonly placementKey: KlarnaOSMPlacementKey;
  readonly locale: string;
  readonly purchaseAmount: string;
  readonly environment: KlarnaEnvironment;
  readonly region: KlarnaRegion;
  readonly theme?: KlarnaTheme;
  readonly backgroundColor?: string;
  readonly textColor?: string;
  readonly returnUrl?: string;
  readonly onError?: (error: KlarnaMobileSDKError) => void;
}

interface KlarnaOSMViewState {
  nativeViewHeight: number;
}

export class KlarnaOSMView extends Component<
  KlarnaOSMViewProps,
  KlarnaOSMViewState
> {
  private osmViewRef: RefObject<
    Component<RNKlarnaOSMViewProps> & Readonly<NativeMethods>
  >;
  private isOSMViewReady = false;
  private hasError = false;

  constructor(props: KlarnaOSMViewProps) {
    super(props);
    this.state = {
      nativeViewHeight: 0,
    };
    this.osmViewRef = React.createRef();
  }

  componentDidUpdate(prevProps: KlarnaOSMViewProps) {
    if (
      this.isOSMViewReady &&
      (prevProps.clientId !== this.props.clientId ||
        prevProps.placementKey !== this.props.placementKey ||
        prevProps.locale !== this.props.locale ||
        prevProps.purchaseAmount !== this.props.purchaseAmount ||
        prevProps.environment !== this.props.environment ||
        prevProps.region !== this.props.region ||
        prevProps.theme !== this.props.theme ||
        prevProps.backgroundColor !== this.props.backgroundColor ||
        prevProps.textColor !== this.props.textColor)
    ) {
      this.renderNative();
    }
  }

  componentWillUnmount() {
    this.isOSMViewReady = false;
  }

  render() {
    return (
      <RNKlarnaOSMView
        ref={this.osmViewRef}
        style={[
          styles.container,
          this.props.style,
          { height: this.state.nativeViewHeight },
        ]}
        clientId={this.props.clientId}
        placementKey={this.props.placementKey}
        locale={this.props.locale}
        purchaseAmount={this.props.purchaseAmount}
        environment={this.props.environment}
        region={this.props.region}
        theme={this.props.theme ?? KlarnaTheme.Automatic}
        backgroundColor={this.props.backgroundColor ?? ''}
        textColor={this.props.textColor ?? ''}
        returnUrl={this.props.returnUrl ?? ''}
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
          this.hasError = true;
          this.setState({ nativeViewHeight: 0 });
          this.props.onError?.(mobileSdkError);
        }}
        onResized={(
          event: NativeSyntheticEvent<
            Readonly<{
              readonly height: string;
            }>
          >
        ) => {
          if (this.hasError) {
            return;
          }
          const newHeight = Number(event.nativeEvent.height);
          if (
            !Number.isNaN(newHeight) &&
            newHeight !== this.state.nativeViewHeight
          ) {
            this.setState({ nativeViewHeight: newHeight });
          }
        }}
        onOSMViewReady={() => {
          this.isOSMViewReady = true;
          setTimeout(() => this.renderNative(), 0);
        }}
      />
    );
  }

  private renderNative = () => {
    this.hasError = false;
    const view = this.osmViewRef.current;
    if (view != null) {
      RNKlarnaOSMViewCommands.render(view);
    }
  };
}

const styles = StyleSheet.create({
  container: {
    width: '100%',
    overflow: 'hidden',
  },
});

export default KlarnaOSMView;

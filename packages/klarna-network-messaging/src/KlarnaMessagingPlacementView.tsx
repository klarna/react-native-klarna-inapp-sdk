import React, { Component } from 'react';
import {
  type NativeSyntheticEvent,
  StyleSheet,
  type ViewStyle,
} from 'react-native';
import type { KlarnaMessagingError } from './types/KlarnaMessagingError';
import type { KlarnaMessagingPlacementConfiguration } from './types/KlarnaMessagingPlacementConfiguration';
import { Klarna } from '@klarna/react-native-klarna-network-core';
import RNKlarnaMessagingPlacementView from './Specs/KlarnaMessagingPlacementViewNativeComponent';
import { errorFromNativeEvent, nextHeightOnResize } from './internal';

/** Props accepted by {@link KlarnaMessagingPlacementView}. */
export interface KlarnaMessagingPlacementViewProps {
  /** Optional custom styles applied to the placement container. */
  style?: ViewStyle;
  /** The Klarna SDK instance obtained from {@link Klarna.initialize}. */
  readonly instance: Klarna;
  /** Placement type, amount, currency and theme for the messaging view. */
  readonly configuration: KlarnaMessagingPlacementConfiguration;
  /** Called when the native placement encounters an error. */
  readonly onError?: (error: KlarnaMessagingError) => void;
}

interface KlarnaMessagingPlacementViewState {
  nativeViewHeight: number;
  hasReceivedResize: boolean;
}

/**
 * Renders a Klarna messaging placement (e.g. credit-promotion banner or badge).
 *
 * The view automatically adjusts its height to match the content reported by the
 * native SDK. Shows a minimum-height fallback while waiting for the first resize
 * event, then collapses to zero when the native side reports height 0 (no content).
 */
export class KlarnaMessagingPlacementView extends Component<
  KlarnaMessagingPlacementViewProps,
  KlarnaMessagingPlacementViewState
> {
  private hasError = false;

  constructor(props: KlarnaMessagingPlacementViewProps) {
    super(props);
    this.state = {
      nativeViewHeight: 0,
      hasReceivedResize: false,
    };
  }

  /**
   * Resets the error latch when any configuration prop changes so the view
   * can accept new height reports from the native side after a recovery.
   */
  componentDidUpdate(prevProps: KlarnaMessagingPlacementViewProps) {
    const prev = prevProps.configuration;
    const next = this.props.configuration;
    if (
      prevProps.instance !== this.props.instance ||
      prev.type !== next.type ||
      prev.theme !== next.theme ||
      prev.amount !== next.amount ||
      prev.currency !== next.currency
    ) {
      this.hasError = false;
      if (this.state.hasReceivedResize) {
        this.setState({ hasReceivedResize: false });
      }
    }
  }

  render() {
    const { configuration } = this.props;
    return (
      <RNKlarnaMessagingPlacementView
        style={[
          styles.container,
          this.props.style,
          this.state.nativeViewHeight > 0
            ? { height: this.state.nativeViewHeight }
            : this.state.hasReceivedResize
              ? styles.collapsed
              : styles.minHeightFallback,
        ]}
        instanceId={this.props.instance.instanceId}
        placementType={configuration.type}
        theme={configuration.theme ?? ''}
        amount={String(configuration.amount)}
        currency={configuration.currency}
        onError={(
          event: NativeSyntheticEvent<
            Readonly<{
              readonly error: Readonly<{
                readonly message: string;
                readonly name: string;
              }>;
            }>
          >
        ) => {
          const error = errorFromNativeEvent(event.nativeEvent);
          this.hasError = true;
          this.setState({ nativeViewHeight: 0, hasReceivedResize: true });
          this.props.onError?.(error);
        }}
        onResized={(
          event: NativeSyntheticEvent<
            Readonly<{
              readonly height: string;
            }>
          >
        ) => {
          const next = nextHeightOnResize({
            rawHeight: event.nativeEvent.height,
            prevHeight: this.state.nativeViewHeight,
            hasError: this.hasError,
            isFirstResize: !this.state.hasReceivedResize,
          });
          if (next !== null) {
            this.setState({ nativeViewHeight: next, hasReceivedResize: true });
          }
        }}
      />
    );
  }
}

const styles = StyleSheet.create({
  container: {
    width: '100%',
    overflow: 'hidden',
  },
  minHeightFallback: {
    minHeight: 60,
  },
  collapsed: {
    height: 0,
    minHeight: 0,
  },
});

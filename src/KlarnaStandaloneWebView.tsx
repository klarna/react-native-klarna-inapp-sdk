import React, { Component, type RefObject } from 'react';
import type {
  NativeMethods,
  NativeSyntheticEvent,
  ViewStyle,
} from 'react-native';
import RNKlarnaStandaloneWebView, {
  RNKlarnaStandaloneWebViewCommands,
  type RNKlarnaStandaloneWebViewProps,
} from './specs/KlarnaStandaloneWebViewNativeComponent';

export interface KlarnaStandaloneWebViewProps {
  style?: ViewStyle;
  readonly returnUrl: string;
  readonly onBeforeLoad?: (event: KlarnaWebViewNavigationEvent) => void;
  readonly onLoad?: (event: KlarnaWebViewNavigationEvent) => void;
}

export class KlarnaStandaloneWebView extends Component<
  KlarnaStandaloneWebViewProps,
  any
> {
  standaloneWebViewRef: RefObject<
    Component<RNKlarnaStandaloneWebViewProps> & Readonly<NativeMethods>
  >;

  constructor(props: KlarnaStandaloneWebViewProps) {
    super(props);
    this.standaloneWebViewRef = React.createRef();
  }

  render() {
    return (
      <RNKlarnaStandaloneWebView
        ref={this.standaloneWebViewRef}
        style={this.props.style}
        returnUrl={this.props.returnUrl || ''}
        onBeforeLoad={(
          event: NativeSyntheticEvent<
            Readonly<{
              readonly event: Readonly<{
                readonly event: 'willLoad' | 'loadStarted' | 'loadEnded';
                readonly newUrl: string;
                readonly webViewState: Readonly<{
                  readonly url: string;
                  readonly title: string;
                  readonly progress: number;
                  readonly isLoading: boolean;
                }>;
              }>;
            }>
          >
        ) => {
          if (this.props.onBeforeLoad != null) {
            this.props.onBeforeLoad(event.nativeEvent.event);
          }
        }}
        onLoad={(
          event: NativeSyntheticEvent<
            Readonly<{
              readonly event: Readonly<{
                readonly event: 'willLoad' | 'loadStarted' | 'loadEnded';
                readonly newUrl: string;
                readonly webViewState: Readonly<{
                  readonly url: string;
                  readonly title: string;
                  readonly progress: number;
                  readonly isLoading: boolean;
                }>;
              }>;
            }>
          >
        ) => {
          if (this.props.onLoad != null) {
            this.props.onLoad(event.nativeEvent.event);
          }
        }}
      />
    );
  }

  load = (url: string) => {
    const view = this.standaloneWebViewRef.current;
    if (view != null) {
      RNKlarnaStandaloneWebViewCommands.load(view, url);
    }
  };

  goForward = () => {
    const view = this.standaloneWebViewRef.current;
    if (view != null) {
      RNKlarnaStandaloneWebViewCommands.goForward(view);
    }
  };

  goBack = () => {
    const view = this.standaloneWebViewRef.current;
    if (view != null) {
      RNKlarnaStandaloneWebViewCommands.goBack(view);
    }
  };

  reload = () => {
    const view = this.standaloneWebViewRef.current;
    if (view != null) {
      RNKlarnaStandaloneWebViewCommands.reload(view);
    }
  };
}

export interface KlarnaWebViewNavigationEvent {
  readonly event: 'willLoad' | 'loadStarted' | 'loadEnded';
  readonly newUrl: string;
  readonly webViewState: Readonly<{
    readonly url: string;
    readonly title: string;
    readonly progress: number;
    readonly isLoading: boolean;
  }>;
}

export default KlarnaStandaloneWebView;

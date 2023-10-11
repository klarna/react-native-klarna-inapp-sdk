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
  readonly onBeforeLoad?: (
    navigationEvent: KlarnaWebViewNavigationEvent
  ) => void;
  readonly onLoad?: (navigationEvent: KlarnaWebViewNavigationEvent) => void;
  readonly onLoadError?: (
    navigationError: KlarnaWebViewNavigationError
  ) => void;
  readonly onProgressChange?: (
    progressEvent: KlarnaWebViewProgressEvent
  ) => void;
  readonly onKlarnaMessage?: (
    klarnaMessageEvent: KlarnaWebViewKlarnaMessageEvent
  ) => void;
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
              readonly navigationEvent: Readonly<{
                readonly event: 'willLoad' | 'loadStarted' | 'loadEnded';
                readonly newUrl: string;
                readonly webViewState: Readonly<{
                  readonly url: string;
                  readonly title: string;
                  readonly progress: string;
                  readonly isLoading: boolean;
                }>;
              }>;
            }>
          >
        ) => {
          if (this.props.onBeforeLoad != null) {
            this.props.onBeforeLoad(event.nativeEvent.navigationEvent);
          }
        }}
        onLoad={(
          event: NativeSyntheticEvent<
            Readonly<{
              readonly navigationEvent: Readonly<{
                readonly event: 'willLoad' | 'loadStarted' | 'loadEnded';
                readonly newUrl: string;
                readonly webViewState: Readonly<{
                  readonly url: string;
                  readonly title: string;
                  readonly progress: string;
                  readonly isLoading: boolean;
                }>;
              }>;
            }>
          >
        ) => {
          if (this.props.onLoad != null) {
            this.props.onLoad(event.nativeEvent.navigationEvent);
          }
        }}
        onLoadError={(
          event: NativeSyntheticEvent<
            Readonly<{
              readonly navigationError: Readonly<{
                readonly errorMessage: string;
              }>;
            }>
          >
        ) => {
          if (this.props.onLoadError != null) {
            this.props.onLoadError(event.nativeEvent.navigationError);
          }
        }}
        onProgressChange={(
          event: NativeSyntheticEvent<
            Readonly<{
              readonly progressEvent: Readonly<{
                readonly webViewState: Readonly<{
                  readonly url: string;
                  readonly title: string;
                  readonly progress: string;
                  readonly isLoading: boolean;
                }>;
              }>;
            }>
          >
        ) => {
          if (this.props.onProgressChange != null) {
            this.props.onProgressChange(event.nativeEvent.progressEvent);
          }
        }}
        onKlarnaMessage={(
          event: NativeSyntheticEvent<
            Readonly<{
              readonly klarnaMessageEvent: Readonly<{
                readonly message: string;
              }>;
            }>
          >
        ) => {
          if (this.props.onKlarnaMessage != null) {
            this.props.onKlarnaMessage(event.nativeEvent.klarnaMessageEvent);
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
    readonly progress: string;
    readonly isLoading: boolean;
  }>;
}

export interface KlarnaWebViewNavigationError {
  readonly errorMessage: string;
}

export interface KlarnaWebViewProgressEvent {
  readonly webViewState: Readonly<{
    readonly url: string;
    readonly title: string;
    readonly progress: string;
    readonly isLoading: boolean;
  }>;
}

export interface KlarnaWebViewKlarnaMessageEvent {
  readonly message: string;
}

export default KlarnaStandaloneWebView;

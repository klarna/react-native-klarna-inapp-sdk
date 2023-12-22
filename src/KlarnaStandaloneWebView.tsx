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

export interface KlarnaWebViewProps {
  style?: ViewStyle;
  readonly returnUrl: string;
  readonly onLoadStart?: (
    navigationEvent: KlarnaWebViewNavigationEvent
  ) => void;
  readonly onLoad?: (navigationEvent: KlarnaWebViewNavigationEvent) => void;
  readonly onError?: (navigationError: KlarnaWebViewNavigationError) => void;
  readonly onLoadProgress?: (progressEvent: KlarnaWebViewProgressEvent) => void;
  readonly onKlarnaMessage?: (
    klarnaMessageEvent: KlarnaWebViewKlarnaMessageEvent
  ) => void;
  /* Android only */
  readonly onRenderProcessGone?: (
    renderProcessGoneEvent: KlarnaWebViewRenderProcessGoneEvent
  ) => void;
  /* End of Android only */
}

export class KlarnaStandaloneWebView extends Component<
  KlarnaWebViewProps,
  any
> {
  standaloneWebViewRef: RefObject<
    Component<RNKlarnaStandaloneWebViewProps> & Readonly<NativeMethods>
  >;

  constructor(props: KlarnaWebViewProps) {
    super(props);
    this.standaloneWebViewRef = React.createRef();
  }

  render() {
    return (
      <RNKlarnaStandaloneWebView
        ref={this.standaloneWebViewRef}
        style={this.props.style}
        returnUrl={this.props.returnUrl || ''}
        onLoadStart={(
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
          if (this.props.onLoadStart != null) {
            this.props.onLoadStart(event.nativeEvent.navigationEvent);
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
        onError={(
          event: NativeSyntheticEvent<
            Readonly<{
              readonly navigationError: Readonly<{
                readonly errorMessage: string;
              }>;
            }>
          >
        ) => {
          if (this.props.onError != null) {
            this.props.onError(event.nativeEvent.navigationError);
          }
        }}
        onLoadProgress={(
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
          if (this.props.onLoadProgress != null) {
            this.props.onLoadProgress(event.nativeEvent.progressEvent);
          }
        }}
        onKlarnaMessage={(
          event: NativeSyntheticEvent<
            Readonly<{
              readonly klarnaMessageEvent: Readonly<{
                readonly action: string;
                // Dictionary is not support for events
                // readonly params: { [key: string]: any };
              }>;
            }>
          >
        ) => {
          if (this.props.onKlarnaMessage != null) {
            this.props.onKlarnaMessage(event.nativeEvent.klarnaMessageEvent);
          }
        }}
        onRenderProcessGone={(
          event: NativeSyntheticEvent<
            Readonly<{
              readonly renderProcessGoneEvent: Readonly<{
                readonly didCrash: boolean;
              }>;
            }>
          >
        ) => {
          if (this.props.onRenderProcessGone != null) {
            this.props.onRenderProcessGone(
              event.nativeEvent.renderProcessGoneEvent
            );
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
  readonly action: string;
  // Dictionary is not supported for events
  // readonly params: { [key: string]: any };
  // TODO What is a KlarnaWebViewComponent?
  // readonly component: KlarnaWebViewComponent;
}

export interface KlarnaWebViewRenderProcessGoneEvent {
  readonly didCrash: boolean;
}

export default KlarnaStandaloneWebView;

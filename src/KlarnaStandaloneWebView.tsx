import React, { Component, type RefObject } from 'react';
import type {
  NativeMethods,
  NativeSyntheticEvent,
  ViewStyle,
} from 'react-native';
import RNKlarnaStandaloneWebView, {
  Commands as RNKlarnaStandaloneWebViewCommands,
  type RNKlarnaStandaloneWebViewProps,
} from './specs/KlarnaStandaloneWebViewNativeComponent';
import type { Double } from 'react-native/Libraries/Types/CodegenTypes';

export interface KlarnaWebViewProps {
  style?: ViewStyle;
  readonly returnUrl: string;
  readonly overScrollMode?: 'always' | 'content' | 'never';
  readonly bounces?: boolean;
  readonly onLoadStart?: (
    navigationEvent: KlarnaWebViewNavigationEvent
  ) => void;
  readonly onLoadEnd?: (navigationEvent: KlarnaWebViewNavigationEvent) => void;
  readonly onError?: (navigationError: KlarnaWebViewError) => void;
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
        overScrollMode={this.props.overScrollMode ?? 'always'}
        bounces={this.props.bounces ?? true}
        onLoadStart={(
          event: NativeSyntheticEvent<
            Readonly<{
              readonly navigationEvent: Readonly<{
                readonly url: string;
                readonly loading: boolean;
                readonly title: string;
                readonly canGoBack: boolean;
                readonly canGoForward: boolean;
              }>;
            }>
          >
        ) => {
          if (this.props.onLoadStart != null) {
            this.props.onLoadStart(event.nativeEvent.navigationEvent);
          }
        }}
        onLoadEnd={(
          event: NativeSyntheticEvent<
            Readonly<{
              readonly navigationEvent: Readonly<{
                readonly url: string;
                readonly loading: boolean;
                readonly title: string;
                readonly canGoBack: boolean;
                readonly canGoForward: boolean;
              }>;
            }>
          >
        ) => {
          if (this.props.onLoadEnd != null) {
            this.props.onLoadEnd(event.nativeEvent.navigationEvent);
          }
        }}
        onError={(
          event: NativeSyntheticEvent<
            Readonly<{
              readonly error: Readonly<{
                readonly url: string;
                readonly loading: boolean;
                readonly title: string;
                readonly canGoBack: boolean;
                readonly canGoForward: boolean;
                readonly code: number;
                readonly description: string;
              }>;
            }>
          >
        ) => {
          if (this.props.onError != null) {
            this.props.onError(event.nativeEvent.error);
          }
        }}
        onLoadProgress={(
          event: NativeSyntheticEvent<
            Readonly<{
              readonly progressEvent: Readonly<{
                readonly url: string;
                readonly loading: boolean;
                readonly title: string;
                readonly canGoBack: boolean;
                readonly canGoForward: boolean;
                readonly progress: Double;
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
                readonly params: string;
              }>;
            }>
          >
        ) => {
          if (this.props.onKlarnaMessage != null) {
            const klarnaMessageEvent = event.nativeEvent.klarnaMessageEvent;
            let params = {};
            try {
              params = JSON.parse(klarnaMessageEvent.params);
            } catch (e) {}
            this.props.onKlarnaMessage({
              action: klarnaMessageEvent.action,
              params: params,
            });
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

interface KlarnaWebViewNativeEvent {
  readonly url: string;
  readonly loading: boolean;
  readonly title: string;
  readonly canGoBack: boolean;
  readonly canGoForward: boolean;
}

export interface KlarnaWebViewNavigationEvent
  extends KlarnaWebViewNativeEvent {}

export interface KlarnaWebViewError extends KlarnaWebViewNativeEvent {
  readonly code: number;
  readonly description: string;
}

export interface KlarnaWebViewProgressEvent extends KlarnaWebViewNativeEvent {
  readonly progress: number;
}

export interface KlarnaWebViewKlarnaMessageEvent {
  readonly action: string;
  readonly params: { [key: string]: any };
  // TODO What is a KlarnaWebViewComponent?
  // readonly component: KlarnaWebViewComponent;
}

export interface KlarnaWebViewRenderProcessGoneEvent {
  readonly didCrash: boolean;
}

export default KlarnaStandaloneWebView;

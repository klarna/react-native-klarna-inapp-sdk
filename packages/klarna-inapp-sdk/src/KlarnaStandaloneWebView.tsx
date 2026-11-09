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
import {
  serializeCookies,
  serializeLoadOptions,
  splitCookieHeader,
  validateSourceURL,
  type KlarnaWebViewCookie,
  type KlarnaWebViewLoadOptions,
} from './KlarnaStandaloneWebViewLoadOptions';

export interface KlarnaWebViewSource {
  /** Needs a scheme and a host, so `about:blank` and `file:///` URLs are reported through onError (code -1). */
  readonly uri: string;
  /** A Cookie header is stored as cookies for the host of `uri` before the page loads. It needs a registrable domain host (no localhost, IP or single-label host). */
  readonly headers?: { readonly [name: string]: string };
}
import { KlarnaTheme } from './types/common/KlarnaTheme';

export interface KlarnaWebViewProps {
  style?: ViewStyle;
  readonly returnUrl: string;
  /** Loads the page on mount and whenever `uri` or `headers` change, like react-native-webview. */
  readonly source?: KlarnaWebViewSource;
  readonly theme?: KlarnaTheme;
  readonly overScrollMode?: 'always' | 'content' | 'never';
  readonly bounces?: boolean;
  /**
   * iOS only: copies all of the app's shared HTTPCookieStorage cookies into the web view before every load.
   * Not filtered by host, and it can overwrite newer cookies in the web view's store.
   */
  readonly sharedCookiesEnabled?: boolean;
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

  private shouldSuppressStopEvent: boolean = false;
  private lastSetCookiesRequestId = 0;
  private sourceRequestId = 0;
  private readonly pendingSetCookies = new Map<
    number,
    { resolve: () => void; reject: (error: Error) => void }
  >();

  constructor(props: KlarnaWebViewProps) {
    super(props);
    this.standaloneWebViewRef = React.createRef();
  }

  componentDidMount() {
    this.loadSource();
  }

  componentDidUpdate(previousProps: KlarnaWebViewProps) {
    if (!isSameSource(previousProps.source, this.props.source)) {
      this.loadSource();
    }
  }

  private loadSource() {
    const source = this.props.source;
    const requestId = ++this.sourceRequestId;
    if (source == null) {
      return;
    }
    const isCurrent = () => requestId === this.sourceRequestId;
    const reportError = (error: unknown) => {
      if (isCurrent()) {
        this.reportSourceError(source.uri, error);
      }
    };
    try {
      validateSourceURL(source.uri);
      const { headers, cookies } = splitCookieHeader(
        source.uri,
        source.headers
      );
      if (cookies.length === 0) {
        this.loadWithOptions(source.uri, { headers });
        return;
      }
      // Rejects invalid headers before any cookie reaches the shared store.
      serializeLoadOptions(source.uri, { headers });
      this.storeCookies(cookies)
        .then(() => {
          if (isCurrent()) {
            this.loadWithOptions(source.uri, { headers });
          }
        })
        .catch(reportError);
    } catch (error) {
      reportError(error);
    }
  }

  private reportSourceError(uri: string, error: unknown) {
    this.props.onError?.({
      url: uri,
      loading: false,
      title: '',
      canGoBack: false,
      canGoForward: false,
      code: -1,
      description: error instanceof Error ? error.message : String(error),
    });
  }

  componentWillUnmount() {
    this.sourceRequestId++;
    this.pendingSetCookies.forEach(({ reject }) =>
      reject(
        new Error(
          'KlarnaStandaloneWebView: the view was unmounted before the cookies were stored.'
        )
      )
    );
    this.pendingSetCookies.clear();
  }

  render() {
    return (
      <RNKlarnaStandaloneWebView
        ref={this.standaloneWebViewRef}
        style={this.props.style}
        returnUrl={this.props.returnUrl || ''}
        theme={this.props.theme ?? KlarnaTheme.Light}
        overScrollMode={this.props.overScrollMode ?? 'always'}
        bounces={this.props.bounces ?? true}
        sharedCookiesEnabled={this.props.sharedCookiesEnabled ?? false}
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
          this.shouldSuppressStopEvent = false;
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
          // On Android, stopping a load triggers the platform's page finished
          // callback, which arrives here as onLoadEnd alongside onLoadProgress
          // updates of 100. All of them are dropped so that stopLoading()
          // emits nothing on either platform.
          if (this.shouldSuppressStopEvent) {
            return;
          }
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
          this.shouldSuppressStopEvent = false;
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
          // A stopped load reports no further progress. Android reports it as
          // a finished page, with progress 100 more than once, and updates that
          // were already in flight can arrive after the stop as well. The flag
          // stays armed until the next load starts, so all of them are dropped.
          if (this.shouldSuppressStopEvent) {
            return;
          }
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
        onSetCookiesResult={(
          event: NativeSyntheticEvent<
            Readonly<{
              readonly setCookiesResult: Readonly<{
                readonly requestId: number;
                readonly error: string;
              }>;
            }>
          >
        ) => {
          const { requestId, error } = event.nativeEvent.setCookiesResult;
          const pending = this.pendingSetCookies.get(requestId);
          if (pending == null) {
            return;
          }
          this.pendingSetCookies.delete(requestId);
          if (error === '') {
            pending.resolve();
          } else {
            pending.reject(new Error(`KlarnaStandaloneWebView: ${error}`));
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
    // A source load still waiting on its cookies must not override this one.
    this.sourceRequestId++;
    this.loadWithOptions(url, undefined);
  };

  /**
   * Loads the URL. Headers go with this request and its same-origin redirects, never to another origin or a Klarna page; both platforms drop them on a cross-origin redirect.
   * Throws if the URL isn't http(s), the host is a Klarna domain or not ASCII, a header is invalid, or a Cookie header is passed.
   */
  private loadWithOptions(url: string, options?: KlarnaWebViewLoadOptions) {
    const optionsJson = serializeLoadOptions(url, options);
    const view = this.standaloneWebViewRef.current;
    if (view != null) {
      RNKlarnaStandaloneWebViewCommands.load(view, url, optionsJson);
    }
  }

  /**
   * Stores the cookies in the app-wide cookie store (Android CookieManager, iOS default data store), shared with other web views; they persist.
   * Resolves once every cookie is stored. Rejects without storing anything if a cookie is invalid, and rejects if the view isn't mounted or unmounts first.
   * On Android it also rejects if the web view refuses a cookie; the cookies stored before it are kept.
   */
  private storeCookies(
    cookies: ReadonlyArray<KlarnaWebViewCookie>
  ): Promise<void> {
    let cookiesJson: string;
    try {
      cookiesJson = serializeCookies(cookies);
    } catch (error) {
      return Promise.reject(error);
    }
    const view = this.standaloneWebViewRef.current;
    if (view == null) {
      return Promise.reject(
        new Error(
          'KlarnaStandaloneWebView: cookies from source.headers cannot be stored while the view is not mounted.'
        )
      );
    }
    if (cookiesJson === '') {
      return Promise.resolve();
    }
    const requestId = ++this.lastSetCookiesRequestId;
    return new Promise((resolve, reject) => {
      this.pendingSetCookies.set(requestId, { resolve, reject });
      RNKlarnaStandaloneWebViewCommands.setCookies(
        view,
        requestId,
        cookiesJson
      );
    });
  }

  goForward = () => {
    // A source load still waiting on its cookies must not override this navigation.
    this.sourceRequestId++;
    const view = this.standaloneWebViewRef.current;
    if (view != null) {
      RNKlarnaStandaloneWebViewCommands.goForward(view);
    }
  };

  goBack = () => {
    this.sourceRequestId++;
    const view = this.standaloneWebViewRef.current;
    if (view != null) {
      RNKlarnaStandaloneWebViewCommands.goBack(view);
    }
  };

  reload = () => {
    this.sourceRequestId++;
    const view = this.standaloneWebViewRef.current;
    if (view != null) {
      RNKlarnaStandaloneWebViewCommands.reload(view);
    }
  };

  stopLoading = () => {
    this.sourceRequestId++;
    const view = this.standaloneWebViewRef.current;
    if (view != null) {
      this.shouldSuppressStopEvent = true;
      RNKlarnaStandaloneWebViewCommands.stopLoading(view);
    }
  };
}

function isSameSource(
  first: KlarnaWebViewSource | undefined,
  second: KlarnaWebViewSource | undefined
): boolean {
  if (first == null || second == null) {
    return first === second;
  }
  const firstHeaders = first.headers ?? {};
  const secondHeaders = second.headers ?? {};
  const names = Object.keys(firstHeaders);
  return (
    first.uri === second.uri &&
    names.length === Object.keys(secondHeaders).length &&
    names.every((name) => firstHeaders[name] === secondHeaders[name])
  );
}

interface KlarnaWebViewNativeEvent {
  readonly url: string;
  readonly loading: boolean;
  readonly title: string;
  readonly canGoBack: boolean;
  readonly canGoForward: boolean;
}

export interface KlarnaWebViewNavigationEvent extends KlarnaWebViewNativeEvent {}

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

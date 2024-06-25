import type { ViewProps } from 'react-native/Libraries/Components/View/ViewPropTypes';
import type { HostComponent } from 'react-native';
import codegenNativeComponent from 'react-native/Libraries/Utilities/codegenNativeComponent';
import codegenNativeCommands from 'react-native/Libraries/Utilities/codegenNativeCommands';
import type { DirectEventHandler } from 'react-native/Libraries/Types/CodegenTypes';
import React from 'react';
import type { Double, Int32 } from 'react-native/Libraries/Types/CodegenTypes';

export interface RNKlarnaStandaloneWebViewProps extends ViewProps {
  returnUrl: string;
  onLoadStart: DirectEventHandler<KlarnaWebViewNavigationEvent>;
  onLoadEnd: DirectEventHandler<KlarnaWebViewNavigationEvent>;
  onError: DirectEventHandler<KlarnaWebViewError>;
  onLoadProgress: DirectEventHandler<KlarnaWebViewProgressEvent>;
  onKlarnaMessage: DirectEventHandler<KlarnaWebViewKlarnaMessageEvent>;

  /* Android only */
  onRenderProcessGone: DirectEventHandler<KlarnaWebViewRenderProcessGoneEvent>;
  overScrollMode: string;
  /* End of Android only */

  /* iOS only */
  bounces: boolean;
  /* End of iOS only */
}

type KlarnaWebViewNavigationEvent = Readonly<{
  navigationEvent: Readonly<{
    url: string;
    loading: boolean;
    title: string;
    canGoBack: boolean;
    canGoForward: boolean;
  }>;
}>;

type KlarnaWebViewError = Readonly<{
  error: Readonly<{
    url: string;
    loading: boolean;
    title: string;
    canGoBack: boolean;
    canGoForward: boolean;
    code: Int32;
    description: string;
  }>;
}>;

type KlarnaWebViewProgressEvent = Readonly<{
  progressEvent: Readonly<{
    url: string;
    loading: boolean;
    title: string;
    canGoBack: boolean;
    canGoForward: boolean;
    progress: Double;
  }>;
}>;

type KlarnaWebViewKlarnaMessageEvent = Readonly<{
  klarnaMessageEvent: Readonly<{
    action: string;
    params: string;
    // TODO What is a KlarnaWebViewComponent?
    // component: KlarnaWebViewComponent;
  }>;
}>;

type KlarnaWebViewRenderProcessGoneEvent = Readonly<{
  renderProcessGoneEvent: Readonly<{
    didCrash: boolean;
  }>;
}>;

type KlarnaStandaloneWebViewNativeComponentType =
  HostComponent<RNKlarnaStandaloneWebViewProps>;

interface RNKlarnaStandaloneWebViewNativeCommands {
  load: (
    viewRef: React.ElementRef<KlarnaStandaloneWebViewNativeComponentType>,
    url: string
  ) => void;
  goBack: (
    viewRef: React.ElementRef<KlarnaStandaloneWebViewNativeComponentType>
  ) => void;
  goForward: (
    viewRef: React.ElementRef<KlarnaStandaloneWebViewNativeComponentType>
  ) => void;
  reload: (
    viewRef: React.ElementRef<KlarnaStandaloneWebViewNativeComponentType>
  ) => void;
}

export const Commands: RNKlarnaStandaloneWebViewNativeCommands =
  codegenNativeCommands<RNKlarnaStandaloneWebViewNativeCommands>({
    supportedCommands: ['load', 'goBack', 'goForward', 'reload'],
  });

export default codegenNativeComponent<RNKlarnaStandaloneWebViewProps>(
  'RNKlarnaStandaloneWebView'
) as KlarnaStandaloneWebViewNativeComponentType;

import type { ViewProps } from 'react-native/Libraries/Components/View/ViewPropTypes';
import type { HostComponent } from 'react-native';
import codegenNativeComponent from 'react-native/Libraries/Utilities/codegenNativeComponent';
import codegenNativeCommands from 'react-native/Libraries/Utilities/codegenNativeCommands';
import type { DirectEventHandler } from 'react-native/Libraries/Types/CodegenTypes';
import React from 'react';

export interface RNKlarnaStandaloneWebViewProps extends ViewProps {
  returnUrl: string;
  onBeforeLoad: DirectEventHandler<KlarnaWebViewNavigationEvent>;
  onLoad: DirectEventHandler<KlarnaWebViewNavigationEvent>;
  onLoadError: DirectEventHandler<KlarnaWebViewNavigationError>;
  onProgressChange: DirectEventHandler<KlarnaWebViewProgressEvent>;
  onKlarnaMessage: DirectEventHandler<KlarnaWebViewKlarnaMessageEvent>;
  /* Android only */
  onRenderProcessGone: DirectEventHandler<KlarnaWebViewRenderProcessGoneEvent>;
  /* End of Android only */
}
type KlarnaWebViewNavigationEvent = Readonly<{
  navigationEvent: Readonly<{
    event: 'willLoad' | 'loadStarted' | 'loadEnded';
    newUrl: string;
    webViewState: Readonly<{
      url: string;
      title: string;
      // Number is not supported for events. So for now we pass 'progress' as a string.
      // 'progress' is a number is range [0..100]
      progress: string;
      isLoading: boolean;
    }>;
  }>;
}>;

// TODO Add the fields when the definition is known. For now we just add an error message
type KlarnaWebViewNavigationError = Readonly<{
  navigationError: Readonly<{
    errorMessage: string;
  }>;
}>;

type KlarnaWebViewProgressEvent = Readonly<{
  progressEvent: Readonly<{
    webViewState: Readonly<{
      url: string;
      title: string;
      // Number is not supported for events
      progress: string;
      isLoading: boolean;
    }>;
  }>;
}>;

type KlarnaWebViewKlarnaMessageEvent = Readonly<{
  klarnaMessageEvent: Readonly<{
    action: string;
    // Dictionary is not support for events
    // params: { [key: string]: any };
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

export const RNKlarnaStandaloneWebViewCommands: RNKlarnaStandaloneWebViewNativeCommands =
  codegenNativeCommands<RNKlarnaStandaloneWebViewNativeCommands>({
    supportedCommands: ['load', 'goBack', 'goForward', 'reload'],
  });

export default codegenNativeComponent<RNKlarnaStandaloneWebViewProps>(
  'RNKlarnaStandaloneWebView'
) as KlarnaStandaloneWebViewNativeComponentType;

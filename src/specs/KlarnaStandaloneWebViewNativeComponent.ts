import type { ViewProps } from 'react-native/Libraries/Components/View/ViewPropTypes';
import type { HostComponent } from 'react-native';
import codegenNativeComponent from 'react-native/Libraries/Utilities/codegenNativeComponent';
import codegenNativeCommands from 'react-native/Libraries/Utilities/codegenNativeCommands';
import type { DirectEventHandler } from 'react-native/Libraries/Types/CodegenTypes';
import React from 'react';

export interface RNKlarnaStandaloneWebViewProps extends ViewProps {
  readonly returnUrl: string;
  readonly onBeforeLoad: DirectEventHandler<KlarnaWebViewNavigationEvent>;
  readonly onLoad: DirectEventHandler<KlarnaWebViewNavigationEvent>;
  readonly onLoadError: DirectEventHandler<KlarnaWebViewNavigationError>;
  readonly onProgressChange: DirectEventHandler<KlarnaWebViewProgressEvent>;
  readonly onKlarnaMessage: DirectEventHandler<KlarnaWebViewKlarnaMessageEvent>;
  /* Android only */
  readonly onRenderProcessGone: DirectEventHandler<KlarnaWebViewRenderProcessGoneEvent>;
  /* End of Android only */
}
type KlarnaWebViewNavigationEvent = Readonly<{
  readonly navigationEvent: Readonly<{
    readonly event: 'willLoad' | 'loadStarted' | 'loadEnded';
    readonly newUrl: string;
    readonly webViewState: Readonly<{
      readonly url: string;
      readonly title: string;
      // Number is not supported for events. So for now we pass 'progress' as a string
      // 'progress' is a number is range [0..100]
      readonly progress: string;
      readonly isLoading: boolean;
    }>;
  }>;
}>;

// TODO Add the fields when the definition is known. For now we just add an error message
type KlarnaWebViewNavigationError = Readonly<{
  readonly navigationError: Readonly<{
    readonly errorMessage: string;
  }>;
}>;

type KlarnaWebViewProgressEvent = Readonly<{
  readonly progressEvent: Readonly<{
    readonly webViewState: Readonly<{
      readonly url: string;
      readonly title: string;
      // Number is not supported for events
      readonly progress: string;
      readonly isLoading: boolean;
    }>;
  }>;
}>;

type KlarnaWebViewKlarnaMessageEvent = Readonly<{
  readonly klarnaMessageEvent: Readonly<{
    readonly action: string;
    // Dictionary is not support for events
    // readonly params: { [key: string]: any };
    // TODO What is a KlarnaWebViewComponent?
    // readonly component: KlarnaWebViewComponent;
  }>;
}>;

type KlarnaWebViewRenderProcessGoneEvent = Readonly<{
  readonly renderProcessGoneEvent: Readonly<{
    readonly didCrash: boolean;
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

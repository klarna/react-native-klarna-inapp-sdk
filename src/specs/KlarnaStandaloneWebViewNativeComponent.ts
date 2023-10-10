import type { ViewProps } from 'react-native/Libraries/Components/View/ViewPropTypes';
import type { HostComponent } from 'react-native';
import codegenNativeComponent from 'react-native/Libraries/Utilities/codegenNativeComponent';
import codegenNativeCommands from 'react-native/Libraries/Utilities/codegenNativeCommands';
import type { DirectEventHandler } from 'react-native/Libraries/Types/CodegenTypes';
import React from 'react';

// TODO add all the props
export interface RNKlarnaStandaloneWebViewProps extends ViewProps {
  readonly returnUrl: string;
  readonly onBeforeLoad: DirectEventHandler<
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
  >;
  readonly onLoad: DirectEventHandler<
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
  >;
}

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

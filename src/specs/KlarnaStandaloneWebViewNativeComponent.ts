import type { ViewProps } from 'react-native/Libraries/Components/View/ViewPropTypes';
import type { HostComponent } from 'react-native';
import codegenNativeComponent from 'react-native/Libraries/Utilities/codegenNativeComponent';
import codegenNativeCommands from 'react-native/Libraries/Utilities/codegenNativeCommands';
import type { DirectEventHandler } from 'react-native/Libraries/Types/CodegenTypes';
import React from 'react';

// TODO add all the props
export interface RNKlarnaStandaloneWebViewProps extends ViewProps {
  readonly returnUrl: string;
  readonly onBeforeLoad: DirectEventHandler<null>;
  readonly onLoad: DirectEventHandler<null>;
}

type KlarnaStandaloneWebViewNativeComponentType =
  HostComponent<RNKlarnaStandaloneWebViewProps>;

interface RNKlarnaStandaloneWebViewNativeCommands {
  load: (
    viewRef: React.ElementRef<KlarnaStandaloneWebViewNativeComponentType>,
    url: string
  ) => void;
}

export const RNKlarnaStandaloneWebViewCommands: RNKlarnaStandaloneWebViewNativeCommands =
  codegenNativeCommands<RNKlarnaStandaloneWebViewNativeCommands>({
    supportedCommands: ['load'],
  });

export default codegenNativeComponent<RNKlarnaStandaloneWebViewProps>(
  'RNKlarnaStandaloneWebView'
) as KlarnaStandaloneWebViewNativeComponentType;

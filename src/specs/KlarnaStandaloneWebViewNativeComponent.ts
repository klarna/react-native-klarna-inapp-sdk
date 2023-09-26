import type { ViewProps } from 'react-native/Libraries/Components/View/ViewPropTypes';
import type { HostComponent } from 'react-native';
import codegenNativeComponent from 'react-native/Libraries/Utilities/codegenNativeComponent';
// import type { DirectEventHandler } from 'react-native/Libraries/Types/CodegenTypes';

// TODO add all the props
export interface RNKlarnaStandaloneWebViewProps extends ViewProps {
  readonly returnUrl: string;
  // readonly progress: number;
  // readonly title: number;
  // readonly url: number;
  // readonly onEvent: DirectEventHandler<null>;
  // readonly onError: DirectEventHandler<null>;
}

export default codegenNativeComponent<RNKlarnaStandaloneWebViewProps>(
  'RNKlarnaStandaloneWebView',
) as HostComponent<RNKlarnaStandaloneWebViewProps>;

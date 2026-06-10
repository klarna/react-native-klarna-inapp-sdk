import type { ViewProps } from 'react-native/Libraries/Components/View/ViewPropTypes';
import type { DirectEventHandler } from 'react-native/Libraries/Types/CodegenTypes';
import type { HostComponent } from 'react-native';
import codegenNativeComponent from 'react-native/Libraries/Utilities/codegenNativeComponent';

export interface KlarnaPaymentButtonProps extends ViewProps {
  readonly instanceId: string;
  readonly state?: string;
  readonly intent?: string;
  readonly shape?: string;
  readonly buttonStyle?: string;
  readonly theme?: string;

  readonly onButtonPress: DirectEventHandler<null>;
}

type KlarnaPaymentButtonNativeComponentType =
  HostComponent<KlarnaPaymentButtonProps>;

export default codegenNativeComponent<KlarnaPaymentButtonProps>(
  'KlarnaNetworkPaymentButton'
) as KlarnaPaymentButtonNativeComponentType;

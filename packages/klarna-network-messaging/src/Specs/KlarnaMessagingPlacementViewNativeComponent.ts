import type { ViewProps } from 'react-native/Libraries/Components/View/ViewPropTypes';
import type { DirectEventHandler } from 'react-native/Libraries/Types/CodegenTypes';
import type { HostComponent } from 'react-native';
import codegenNativeComponent from 'react-native/Libraries/Utilities/codegenNativeComponent';

export interface RNKlarnaMessagingPlacementViewProps extends ViewProps {
  readonly instanceId: string;
  readonly placementType: string;
  readonly theme: string;
  readonly amount: string;
  readonly currency: string;
  readonly onError: DirectEventHandler<
    Readonly<{
      readonly error: Readonly<{
        readonly message: string;
        readonly name: string;
      }>;
    }>
  >;
  readonly onResized: DirectEventHandler<
    Readonly<{
      readonly height: string;
    }>
  >;
}

type KlarnaMessagingPlacementViewNativeComponentType =
  HostComponent<RNKlarnaMessagingPlacementViewProps>;

export default codegenNativeComponent<RNKlarnaMessagingPlacementViewProps>(
  'RNKlarnaMessagingPlacementView'
) as KlarnaMessagingPlacementViewNativeComponentType;

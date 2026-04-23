import type { ViewProps } from 'react-native/Libraries/Components/View/ViewPropTypes';
import type { DirectEventHandler } from 'react-native/Libraries/Types/CodegenTypes';
import type { HostComponent } from 'react-native';
import React from 'react';
import codegenNativeCommands from 'react-native/Libraries/Utilities/codegenNativeCommands';
import codegenNativeComponent from 'react-native/Libraries/Utilities/codegenNativeComponent';

export interface RNKlarnaOSMViewProps extends ViewProps {
  readonly clientId: string;
  readonly placementKey: string;
  readonly locale: string;
  readonly purchaseAmount: string;
  readonly environment: string;
  readonly region: string;
  readonly theme: string;
  readonly backgroundColor: string;
  readonly textColor: string;
  readonly returnUrl?: string;
  readonly onError: DirectEventHandler<
    Readonly<{
      readonly error: Readonly<{
        readonly isFatal: boolean;
        readonly message: string;
        readonly name: string;
      }>;
    }>
  >;
  readonly onResized: DirectEventHandler<
    Readonly<{
      // number not supported for events
      readonly height: string;
    }>
  >;
  readonly onOSMViewReady?: DirectEventHandler<{}>;
}

type KlarnaOSMViewNativeComponentType = HostComponent<RNKlarnaOSMViewProps>;

interface RNKlarnaOSMViewNativeCommands {
  render: (viewRef: React.ElementRef<KlarnaOSMViewNativeComponentType>) => void;
}

export const Commands: RNKlarnaOSMViewNativeCommands =
  codegenNativeCommands<RNKlarnaOSMViewNativeCommands>({
    supportedCommands: ['render'],
  });

export default codegenNativeComponent<RNKlarnaOSMViewProps>(
  'RNKlarnaOSMView'
) as KlarnaOSMViewNativeComponentType;

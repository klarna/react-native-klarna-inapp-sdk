import type { ViewProps } from 'react-native/Libraries/Components/View/ViewPropTypes';
import type { DirectEventHandler } from 'react-native/Libraries/Types/CodegenTypes';
import type { HostComponent } from 'react-native';
import React from 'react';
import codegenNativeCommands from 'react-native/Libraries/Utilities/codegenNativeCommands';
import codegenNativeComponent from 'react-native/Libraries/Utilities/codegenNativeComponent';

export interface RNKlarnaCheckoutViewProps extends ViewProps {
  readonly returnUrl: string;
  readonly checkoutOptions: Readonly<{
    readonly merchantHandlesEPM?: boolean;
    readonly merchantHandlesValidationErrors?: boolean;
  }>;
  readonly onEvent: DirectEventHandler<
    Readonly<{
      readonly productEvent: Readonly<{
        readonly action: string;
        readonly params: string;
      }>;
    }>
  >;
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
}

type KlarnaCheckoutViewNativeComponentType =
  HostComponent<RNKlarnaCheckoutViewProps>;

interface RNKlarnaCheckoutViewNativeCommands {
  setSnippet: (
    viewRef: React.ElementRef<KlarnaCheckoutViewNativeComponentType>,
    snippet: string
  ) => void;
  suspend: (
    viewRef: React.ElementRef<KlarnaCheckoutViewNativeComponentType>
  ) => void;
  resume: (
    viewRef: React.ElementRef<KlarnaCheckoutViewNativeComponentType>
  ) => void;
}

export const RNKlarnaCheckoutViewCommands: RNKlarnaCheckoutViewNativeCommands =
  codegenNativeCommands<RNKlarnaCheckoutViewNativeCommands>({
    supportedCommands: ['setSnippet', 'suspend', 'resume'],
  });

export default codegenNativeComponent<RNKlarnaCheckoutViewProps>(
  'RNKlarnaCheckoutView'
) as KlarnaCheckoutViewNativeComponentType;

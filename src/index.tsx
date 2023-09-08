import React, { Component } from 'react';
import {
  findNodeHandle,
  Platform,
  requireNativeComponent,
  UIManager,
} from 'react-native';
import type { ViewProps } from 'react-native/Libraries/Components/View/ViewPropTypes';

const ComponentName = 'KlarnaPaymentView';

interface KlarnaReactPaymentViewProps extends ViewProps {
  category: string;
  onInitialized: (event: any) => void;
  onLoaded: (event: any) => void;
  onAuthorized: (event: any) => void;
  onReauthorized: (event: any) => void;
  onFinalized: (event: any) => void;
  onError: (event: any) => void;
}

export class KlarnaPaymentView extends Component<KlarnaReactPaymentViewProps> {
  render() {
    return <KlarnaReactPaymentView {...this.props} />;
  }

  _viewManager() {
    return UIManager.getViewManagerConfig(ComponentName);
  }

  initialize = (sessionToken: string, returnUrl: string) => {
    UIManager.dispatchViewManagerCommand(
      findNodeHandle(this),
      this._viewManager().Commands.initialize!!,
      [sessionToken, returnUrl]
    );
  };

  load = (sessionData: string | undefined = undefined) => {
    UIManager.dispatchViewManagerCommand(
      findNodeHandle(this),
      this._viewManager().Commands.load!!,
      [sessionData || null]
    );
  };

  loadPaymentReview = () => {
    UIManager.dispatchViewManagerCommand(
      findNodeHandle(this),
      this._viewManager().Commands.loadPaymentReview!!,
      []
    );
  };

  authorize = (
    autoFinalize: boolean | undefined = undefined,
    sessionData: string | undefined = undefined
  ) => {
    UIManager.dispatchViewManagerCommand(
      findNodeHandle(this),
      this._viewManager().Commands.authorize!!,
      [autoFinalize || true, sessionData || null]
    );
  };

  reauthorize = (sessionData: string | undefined = undefined) => {
    UIManager.dispatchViewManagerCommand(
      findNodeHandle(this),
      this._viewManager().Commands.reauthorize!!,
      [sessionData || null]
    );
  };

  finalize = (sessionData: string | undefined = undefined) => {
    UIManager.dispatchViewManagerCommand(
      findNodeHandle(this),
      this._viewManager().Commands.finalize!!,
      [sessionData || null]
    );
  };
}

const LINKING_ERROR =
  `The package 'react-native-awesome-library' doesn't seem to be linked. Make sure: \n\n` +
  Platform.select({ ios: "- You have run 'pod install'\n", default: '' }) +
  '- You rebuilt the app after installing the package\n' +
  '- You are not using Expo Go\n';

const KlarnaReactPaymentView =
  UIManager.getViewManagerConfig(ComponentName) != null
    ? requireNativeComponent(ComponentName)
    : () => {
        throw new Error(LINKING_ERROR);
      };

export { KlarnaReactPaymentView };
export default KlarnaPaymentView;

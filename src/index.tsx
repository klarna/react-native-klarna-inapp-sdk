import React, { Component } from 'react';
import {
  findNodeHandle,
  Platform,
  requireNativeComponent,
  UIManager,
} from 'react-native';

const ComponentName = 'KlarnaPaymentView';

type KlarnaReactPaymentViewProps = {
  category: string;
  onInitialized: () => void;
  onLoaded: () => void;
  onAuthorized: (
    authorized: boolean,
    authToken: string | undefined,
    finalizeRequired: boolean | undefined
  ) => void;
  onReauthorized: (authorized: boolean, authToken: string | undefined) => void;
  onFinalized: (authorized: boolean, authToken: string | undefined) => void;
  onError: (error: any) => void;
};

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
      this._viewManager().Commands.initialize!!.toString(),
      [sessionToken, returnUrl]
    );
  };

  load = (sessionData: string | undefined = undefined) => {
    UIManager.dispatchViewManagerCommand(
      findNodeHandle(this),
      this._viewManager().Commands.load!!.toString(),
      [sessionData || null]
    );
  };

  loadPaymentReview = () => {
    UIManager.dispatchViewManagerCommand(
      findNodeHandle(this),
      this._viewManager().Commands.loadPaymentReview!!.toString(),
      []
    );
  };

  authorize = (
    autoFinalize: boolean | undefined = undefined,
    sessionData: string | undefined = undefined
  ) => {
    UIManager.dispatchViewManagerCommand(
      findNodeHandle(this),
      this._viewManager().Commands.authorize!!.toString(),
      [autoFinalize || true, sessionData || null]
    );
  };

  reauthorize = (sessionData: string | undefined = undefined) => {
    UIManager.dispatchViewManagerCommand(
      findNodeHandle(this),
      this._viewManager().Commands.reauthorize!!.toString(),
      [sessionData || null]
    );
  };

  finalize = (sessionData: string | undefined = undefined) => {
    UIManager.dispatchViewManagerCommand(
      findNodeHandle(this),
      this._viewManager().Commands.finalize!!.toString(),
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

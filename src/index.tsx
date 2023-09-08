import React, { Component, type SyntheticEvent } from 'react';
import {
  findNodeHandle,
  Platform,
  requireNativeComponent,
  UIManager,
  type ViewStyle,
} from 'react-native';

const ComponentName = 'KlarnaPaymentView';

interface KlarnaPaymentViewProps {
  style: ViewStyle;
  category: string;
  onInitialized: () => void;
  onLoaded: () => void;
  onAuthorized: (
    approved: boolean,
    authToken: string | undefined,
    finalizeRequired: boolean | undefined
  ) => void;
  onReauthorized: (approved: boolean, authToken: string | undefined) => void;
  onFinalized: (approved: boolean, authToken: string | undefined) => void;
  onError: (error: KlarnaPaymentsSDKError) => void;
}

export class KlarnaPaymentView extends Component<KlarnaPaymentViewProps> {
  render() {
    // @ts-ignore
    return (
      <KlarnaReactPaymentView
        style={this.props.style}
        category={this.props.category}
        onInitialized={(_event: SyntheticEvent) => {
          this.props.onInitialized();
        }}
        onLoaded={(_event: SyntheticEvent) => {
          this.props.onLoaded();
        }}
        onAuthorized={(event: SyntheticEvent) => {
          const data = this._getNativeEvent(event);
          this.props.onAuthorized(
            data?.approved,
            data?.authToken,
            data?.finalizeRequired
          );
        }}
        onReauthorized={(event: SyntheticEvent) => {
          const data = this._getNativeEvent(event);
          this.props.onReauthorized(data?.approved, data?.authToken);
        }}
        onFinalized={(event: SyntheticEvent) => {
          const data = this._getNativeEvent(event);
          this.props.onFinalized(data?.approved, data?.authToken);
        }}
        onError={(event: SyntheticEvent) => {
          const data = this._getNativeEvent(event);
          this.props.onError(data?.error);
        }}
      />
    );
  }

  _getNativeEvent(event: SyntheticEvent): any | undefined {
    if (event !== undefined && event !== null && event.nativeEvent) {
      if (__DEV__) {
        console.log('nativeEvent', event, event.nativeEvent);
      }
      return event.nativeEvent;
    }
    return undefined;
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

interface KlarnaPaymentsSDKError {
  action: string;
  isFatal: boolean;
  message: string;
  name: string;
  invalidFields: Array<string>;
  sessionId: string;
}

interface KlarnaReactPaymentViewProps {
  style: ViewStyle;
  category: string;
  onInitialized: (event: SyntheticEvent) => void;
  onLoaded: (event: SyntheticEvent) => void;
  onAuthorized: (event: SyntheticEvent) => void;
  onReauthorized: (event: SyntheticEvent) => void;
  onFinalized: (event: SyntheticEvent) => void;
  onError: (event: SyntheticEvent) => void;
}

const LINKING_ERROR =
  `The package 'react-native-awesome-library' doesn't seem to be linked. Make sure: \n\n` +
  Platform.select({ ios: "- You have run 'pod install'\n", default: '' }) +
  '- You rebuilt the app after installing the package\n' +
  '- You are not using Expo Go\n';

const KlarnaReactPaymentView =
  UIManager.getViewManagerConfig(ComponentName) != null
    ? requireNativeComponent<KlarnaReactPaymentViewProps>(ComponentName)
    : () => {
        throw new Error(LINKING_ERROR);
      };

export default KlarnaPaymentView;

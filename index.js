import React, { Component } from "react";
import PropTypes from "prop-types";
import KlarnaPaymentView, {
  Commands,
} from "./specs/KlarnaPaymentViewNativeComponent";

const returnNullIfEmptyString = (prop) => {
  if (typeof prop === "string" && prop?.length === 0) {
    return null;
  }
  return prop;
};

class KlarnaReactPaymentView extends Component {
  constructor(props) {
    super(props);
    this.state = { height: props?.style?.height };
  }

  render() {
    const nativeProps = {
      ...this.props,
      onAuthorized: this._onAuthorized,
      onReauthorized: this._onReauthorized,
      onFinalized: this._onFinalized,
      onWebviewHeightChanged: this._onWebviewHeightChanged,
      onError: this._onError,
      style: { height: this.state.height, ...this?.props?.style },
    };

    return <KlarnaPaymentView {...nativeProps} ref={this._assignRoot} />;
  }

  _assignRoot = (component) => {
    this._root = component;
  };

  initialize = (sessionToken, returnUrl) => {
    if (this._root != null) {
      Commands.initialize(this._root, sessionToken, returnUrl);
    }
  };

  load = (sessionData) => {
    if (this._root != null) {
      Commands.load(this._root, sessionData || "");
    }
  };

  loadPaymentReview = () => {
    if (this._root != null) {
      Commands.loadPaymentReview(this._root);
    }
  };

  authorize = (autoFinalize, sessionData) => {
    if (this._root != null) {
      Commands.authorize(this._root, autoFinalize || true, sessionData || "");
    }
  };

  reauthorize = (sessionData) => {
    if (this._root != null) {
      Commands.reauthorize(this._root, sessionData || "");
    }
  };

  finalize = (sessionData) => {
    if (this._root != null) {
      Commands.finalize(this._root, sessionData || "");
    }
  };

  _onAuthorized = (event) => {
    if (this.props.onAuthorized) {
      this.props.onAuthorized({
        ...event,
        nativeEvent: {
          ...event.nativeEvent,
          authToken: returnNullIfEmptyString(event.nativeEvent?.authToken),
        },
      });
    }
  };

  _onReauthorized = (event) => {
    if (this.props.onReauthorized) {
      this.props.onReauthorized({
        ...event,
        nativeEvent: {
          ...event.nativeEvent,
          authToken: returnNullIfEmptyString(event.nativeEvent?.authToken),
        },
      });
    }
  };

  _onFinalized = (event) => {
    if (this.props.onFinalized) {
      this.props.onFinalized({
        ...event,
        nativeEvent: {
          ...event.nativeEvent,
          authToken: returnNullIfEmptyString(event.nativeEvent?.authToken),
        },
      });
    }
  };

  _onWebviewHeightChanged = (event) => {
    if (event.nativeEvent.height != null) {
      this.setState({
        height: event.nativeEvent.height,
      });
    }
  };

  _onError = (event) => {
    if (this.props.onError) {
      this.props.onError({
        ...event,
        nativeEvent: {
          ...event.nativeEvent,
          action: returnNullIfEmptyString(event.nativeEvent?.action),
          message: returnNullIfEmptyString(event.nativeEvent?.message),
          name: returnNullIfEmptyString(event.nativeEvent?.name),
        },
      });
    }
  };
}

KlarnaReactPaymentView.propTypes = {
  category: PropTypes.string,
  onInitialized: PropTypes.func,
  onLoaded: PropTypes.func,
  onLoadedPaymentReview: PropTypes.func,
  onAuthorized: PropTypes.func,
  onReauthorized: PropTypes.func,
  onFinalized: PropTypes.func,
  onError: PropTypes.func,
};

export default KlarnaReactPaymentView;

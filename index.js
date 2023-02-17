import React, { Component } from "react";
import PropTypes from "prop-types";
import KlarnaPaymentView, {
  Commands,
} from "./specs/KlarnaPaymentViewNativeComponent";

class KlarnaReactPaymentView extends Component {
  render() {
    return <KlarnaPaymentView {...this.props} ref={this._assignRoot} />;
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

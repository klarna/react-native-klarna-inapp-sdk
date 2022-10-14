import React, {Component}from 'react';
import { requireNativeComponent, UIManager, findNodeHandle } from 'react-native';
import PropTypes from 'prop-types';

// Testing something :)
class KlarnaReactPaymentView extends Component {
// and again
    render() {
        return <KlarnaPaymentView {...this.props} />
    }

    initialize = (sessionToken, returnUrl) => {
        UIManager.dispatchViewManagerCommand(
            findNodeHandle(this),
            UIManager.getViewManagerConfig('KlarnaPaymentView').Commands.initialize,
            [sessionToken, returnUrl]
        )
    }

    load = (sessionData) => {
        UIManager.dispatchViewManagerCommand(
            findNodeHandle(this),
            UIManager.getViewManagerConfig('KlarnaPaymentView').Commands.load,
            [sessionData || null]
        )
    }

    loadPaymentReview = () => {
        UIManager.dispatchViewManagerCommand(
            findNodeHandle(this),
            UIManager.getViewManagerConfig('KlarnaPaymentView').Commands.loadPaymentReview,
            []
        )
    }

    authorize = (autoFinalize, sessionData) => {
        UIManager.dispatchViewManagerCommand(
            findNodeHandle(this),
            UIManager.getViewManagerConfig('KlarnaPaymentView').Commands.authorize,
            [autoFinalize || true, sessionData || null]
        )
    }

    reauthorize = (sessionData) => {
        UIManager.dispatchViewManagerCommand(
            findNodeHandle(this),
            UIManager.getViewManagerConfig('KlarnaPaymentView').Commands.reauthorize,
            [sessionData || null]
        )
    }

    finalize = (sessionData) => {
        UIManager.dispatchViewManagerCommand(
            findNodeHandle(this),
            UIManager.getViewManagerConfig('KlarnaPaymentView').Commands.finalize,
            [sessionData || null]
        )
    }
}

KlarnaReactPaymentView.propTypes = {
    category: PropTypes.string,
    onInitialized: PropTypes.func,
    onLoaded: PropTypes.func,
    onAuthorized: PropTypes.func,
    onReauthorized: PropTypes.func,
    onFinalized: PropTypes.func,
    onError: PropTypes.func,
}

const KlarnaPaymentView = requireNativeComponent('KlarnaPaymentView', KlarnaReactPaymentView);

export default KlarnaReactPaymentView;

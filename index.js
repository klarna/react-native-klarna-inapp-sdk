import React from 'react';
import { requireNativeComponent, UIManager, findNodeHandle } from 'react-native';
import PropTypes from 'prop-types';

class KlarnaReactPaymentView extends React.Component {
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

    load = () => {
        UIManager.dispatchViewManagerCommand(
            findNodeHandle(this),
            UIManager.getViewManagerConfig('KlarnaPaymentView').Commands.load,
            []
        )
    }

    authorize = (autoFinalize) => {
        UIManager.dispatchViewManagerCommand(
            findNodeHandle(this),
            UIManager.getViewManagerConfig('KlarnaPaymentView').Commands.authorize,
            [autoFinalize || true]
        )
    }
}

KlarnaReactPaymentView.propTypes = {
    category: PropTypes.string,
    onEvent: PropTypes.func
}

const KlarnaPaymentView = requireNativeComponent('KlarnaPaymentView', KlarnaReactPaymentView);

export default KlarnaReactPaymentView;

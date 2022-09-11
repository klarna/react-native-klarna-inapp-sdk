import React, {useRef, useState} from 'react';
import {Button, NativeModules, Platform, Text, View} from 'react-native';
import KlarnaPaymentView from 'react-native-klarna-inapp-sdk';
import styles from '../Styles';
import testProps from '../TestProps';

interface PaymentsProps {
  clientToken: string;
  paymentMethodName: string;
}

const PaymentsContainer = (props: PaymentsProps) => {
  const paymentViewRef = useRef();
  const [eventState, setEventState] = useState();

  const onEvent = event => {
    const eventString = JSON.stringify(event.nativeEvent);
    setEventState(eventString);
    window.console.warn(eventString);
  };

  const actionButtons = () => {
    return (
      <View style={styles.buttonsContainer}>
        <Button
          onPress={() => {
            paymentViewRef.current.initialize(
              props.clientToken,
              'returnUrl://',
            );

            //You can skip this line, it's for integration testing purposes by Klarna.
            if (Platform.OS === 'android') {
              NativeModules.DebugWebViewModule.enable();
            }
          }}
          title="Init."
          {...testProps('initButton_' + props.paymentMethodName)}
          style={styles.button}
        />
        <Button
          onPress={() => {
            paymentViewRef.current.load();
          }}
          title="Load"
          {...testProps('loadButton_' + props.paymentMethodName)}
          style={styles.button}
        />
        <Button
          onPress={() => {
            paymentViewRef.current.authorize();
          }}
          title="Authorize"
          {...testProps('authorizeButton_' + props.paymentMethodName)}
          style={styles.button}
        />
        <Button
          onPress={() => {
            paymentViewRef.current.reauthorize();
          }}
          title="Reauthorize"
          {...testProps('reauthorizeButton_' + props.paymentMethodName)}
          style={styles.button}
        />
        <Button
          onPress={() => {
            paymentViewRef.current.finalize();
          }}
          title="Finalize"
          {...testProps('finalizeButton_' + props.paymentMethodName)}
          style={styles.button}
        />
      </View>
    );
  };

  const renderPaymentMethod = () => {
    return (
      <View style={styles.container} key={props.paymentMethodName}>
        <Text style={styles.title}>{props.paymentMethodName}</Text>
        <KlarnaPaymentView
          ref={paymentViewRef}
          style={styles.paymentView}
          category={props.paymentMethodName}
          onInitialized={event => {
            onEvent(event);
          }}
          onLoaded={event => {
            onEvent(event);
          }}
          onAuthorized={event => {
            onEvent(event);
          }}
          onReauthorized={event => {
            onEvent(event);
          }}
          onFinalized={event => {
            onEvent(event);
          }}
          onError={event => {
            onEvent(event);
          }}
        />
        {actionButtons()}
        <Text
          style={{color: 'gray'}}
          {...testProps('state_' + props.paymentMethodName)}>
          {eventState}
        </Text>
      </View>
    );
  };

  return renderPaymentMethod();
};

export default PaymentsContainer;

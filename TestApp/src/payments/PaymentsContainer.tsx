import React, {useRef, useState} from 'react';
import {NativeModules, Platform, Text, View} from 'react-native';
import {KlarnaPaymentView} from 'react-native-klarna-inapp-sdk';
import styles from '../common/ui/Styles';
import testProps from '../common/util/TestProps';
import Button from '../common/ui/view/Button';

interface PaymentsContainerProps {
  clientToken: string;
  paymentMethodName: string;
}

export default function PaymentsContainer(props: PaymentsContainerProps) {
  const paymentViewRef = useRef<KlarnaPaymentView>(null);
  const [eventState, setEventState] = useState<string>();

  const onEvent = (event: any | undefined) => {
    const eventString = JSON.stringify(event.nativeEvent);
    setEventState(eventString);
    console.warn(eventString);
  };

  const actionButtons = () => {
    return (
      <View style={styles.buttonsContainer}>
        <Button
          onPress={() => {
            paymentViewRef.current?.initialize(
              props.clientToken,
              'returnUrl://',
            );

            //You can skip this line, it's for integration testing purposes by Klarna.
            if (
              Platform.OS === 'android' &&
              NativeModules.DebugWebViewModule !== undefined
            ) {
              NativeModules.DebugWebViewModule.enable();
            }
          }}
          title="Init."
          {...testProps('initButton_' + props.paymentMethodName)}
        />
        <Button
          onPress={() => {
            paymentViewRef.current?.load();
          }}
          title="Load"
          {...testProps('loadButton_' + props.paymentMethodName)}
        />
        <Button
          onPress={() => {
            paymentViewRef.current?.authorize();
          }}
          title="Authorize"
          {...testProps('authorizeButton_' + props.paymentMethodName)}
        />
        <Button
          onPress={() => {
            paymentViewRef.current?.reauthorize();
          }}
          title="Reauthorize"
          {...testProps('reauthorizeButton_' + props.paymentMethodName)}
        />
        <Button
          onPress={() => {
            paymentViewRef.current?.finalize();
          }}
          title="Finalize"
          {...testProps('finalizeButton_' + props.paymentMethodName)}
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
          category={props.paymentMethodName}
          onInitialized={() => {
            onEvent(undefined);
          }}
          onLoaded={() => {
            onEvent(undefined);
          }}
          onAuthorized={(authorized, authToken, finalizeRequired) => {
            onEvent(authorized);
          }}
          onReauthorized={(authorized, authToken) => {
            onEvent(authorized);
          }}
          onFinalized={(authorized, authToken) => {
            onEvent(authorized);
          }}
          onError={error => {
            onEvent(error);
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
}

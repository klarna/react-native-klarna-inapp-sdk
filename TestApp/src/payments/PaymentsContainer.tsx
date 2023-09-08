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

  const onEvent = (event: string, data: any | undefined = undefined) => {
    console.log('onEvent', event, data);
    if (data !== undefined && data !== null && data.nativeEvent) {
      console.log('nativeEvent', event, data.nativeEvent);
      const eventString = JSON.stringify(data.nativeEvent);
      setEventState(eventString);
      console.log('eventString', event, eventString);
    }
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
              NativeModules.DebugWebViewModule !== undefined &&
              NativeModules.DebugWebViewModule !== null
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
        <View style={styles.paymentContainer}>
          <KlarnaPaymentView
            ref={paymentViewRef}
            style={styles.paymentView}
            category={props.paymentMethodName}
            onInitialized={event => {
              onEvent('onInitialized', event);
            }}
            onLoaded={event => {
              onEvent('onLoaded', event);
            }}
            onAuthorized={event => {
              onEvent('onAuthorized', event);
            }}
            onReauthorized={event => {
              onEvent('onReauthorized', event);
            }}
            onFinalized={event => {
              onEvent('onFinalized', event);
            }}
            onError={error => {
              onEvent('onError', error);
            }}
          />
        </View>
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

import { useRoute } from '@react-navigation/native';
import type { NativeStackScreenProps } from '@react-navigation/native-stack';
import React, { useState } from 'react';
import { ScrollView, Text, View } from 'react-native';
import type { AppStackParamList } from '../../../../App';
import styles, { useTheme } from '../../../common/ui/Styles';
import Button from '../../../common/ui/view/Button';
import networkStyles from '../../Styles/NetworkIntegrationStyles';
import '@klarna/react-native-klarna-network-payment';

type RouteProp = NativeStackScreenProps<
  AppStackParamList,
  'KlarnaNetworkPaymentInitiateWithId'
>['route'];

function timestamp() {
  return new Date().toTimeString().slice(0, 8);
}

export default function KlarnaNetworkPaymentInitiateWithIdScreen() {
  const route = useRoute<RouteProp>();
  const { sdk, paymentRequestId } = route.params;
  const theme = useTheme();
  const [log, setLog] = useState('');

  function appendLog(message: string) {
    setLog((prev) =>
      prev
        ? `${prev}\n${timestamp()}: ${message}`
        : `${timestamp()}: ${message}`
    );
  }

  function handleInitiate() {
    sdk.payment
      .initiate(paymentRequestId)
      .then((response) => {
        appendLog(`Success:\n${JSON.stringify(response, null, 2)}`);
      })
      .catch((error: unknown) => {
        appendLog(String(error));
      });
  }

  return (
    <ScrollView
      contentInsetAdjustmentBehavior="automatic"
      style={[styles.scrollView, { backgroundColor: theme.background }]}
    >
      <View style={networkStyles.container}>
        <Button title="initiate" onPress={handleInitiate} />
        <Text style={[networkStyles.logLabel, { color: theme.text }]}>Log</Text>
        <View style={networkStyles.logContainer}>
          <Text
            selectable
            style={[networkStyles.logText, { color: theme.text }]}
          >
            {log}
          </Text>
        </View>
      </View>
    </ScrollView>
  );
}

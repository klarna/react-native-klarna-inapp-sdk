import { useRoute } from '@react-navigation/native';
import type { NativeStackScreenProps } from '@react-navigation/native-stack';
import React, { useState } from 'react';
import { ActivityIndicator, ScrollView, Text, View } from 'react-native';
import type { AppStackParamList } from '../../../../App';
import styles, { useTheme } from '../../../common/ui/Styles';
import Button from '../../../common/ui/view/Button';
import networkStyles from '../../Styles/NetworkIntegrationStyles';
import '@klarna/react-native-klarna-network-payment';

type RouteProp = NativeStackScreenProps<
  AppStackParamList,
  'KlarnaNetworkPaymentFetch'
>['route'];

function timestamp() {
  return new Date().toTimeString().slice(0, 8);
}

export default function KlarnaNetworkPaymentFetchScreen() {
  const route = useRoute<RouteProp>();
  const { sdk, paymentRequestId } = route.params;
  const theme = useTheme();
  const [log, setLog] = useState('');
  const [isLoading, setIsLoading] = useState(false);

  function appendLog(message: string) {
    setLog((prev) =>
      prev
        ? `${prev}\n${timestamp()}: ${message}`
        : `${timestamp()}: ${message}`
    );
  }

  function handleFetch() {
    setIsLoading(true);
    sdk.payment
      .fetch(paymentRequestId)
      .then((response) => {
        appendLog(`Success:\n${JSON.stringify(response, null, 2)}`);
      })
      .catch((error: unknown) => {
        appendLog(String(error));
      })
      .finally(() => setIsLoading(false));
  }

  return (
    <ScrollView
      contentInsetAdjustmentBehavior="automatic"
      style={[styles.scrollView, { backgroundColor: theme.background }]}
    >
      <View style={networkStyles.container}>
        <Button title="Fetch" onPress={handleFetch} disabled={isLoading} />
        {isLoading && (
          <ActivityIndicator style={networkStyles.loadingIndicator} />
        )}
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

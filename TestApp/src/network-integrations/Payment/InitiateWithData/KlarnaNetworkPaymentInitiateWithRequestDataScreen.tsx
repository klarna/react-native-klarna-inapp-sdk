import { useRoute } from '@react-navigation/native';
import type { NativeStackScreenProps } from '@react-navigation/native-stack';
import React, { useEffect, useRef, useState } from 'react';
import Clipboard from '@react-native-clipboard/clipboard';
import { ScrollView, StyleSheet, Text, View } from 'react-native';
import type { AppStackParamList } from '../../../../App';
import styles, { useTheme, Colors } from '../../../common/ui/Styles';
import Button from '../../../common/ui/view/Button';
import networkStyles from '../../Styles/NetworkIntegrationStyles';
import {
  KlarnaPaymentButton,
  type KlarnaPaymentRequestData,
} from '@klarna/react-native-klarna-network-payment';

type RouteProp = NativeStackScreenProps<
  AppStackParamList,
  'KlarnaNetworkPaymentInitiateWithRequestData'
>['route'];

export default function KlarnaNetworkPaymentInitiateWithRequestDataScreen() {
  const route = useRoute<RouteProp>();
  const { sdk } = route.params;
  const theme = useTheme();
  const [errorLog, setErrorLog] = useState('');
  const [paymentRequestId, setPaymentRequestId] = useState<string | null>(null);
  const [copied, setCopied] = useState(false);
  const copyTimeoutRef = useRef<ReturnType<typeof setTimeout> | null>(null);

  useEffect(() => {
    return () => {
      if (copyTimeoutRef.current) clearTimeout(copyTimeoutRef.current);
    };
  }, []);

  function handleInitiate() {
    let data: KlarnaPaymentRequestData;
    try {
      data =
        'jsonData' in route.params && route.params.jsonData
          ? (JSON.parse(route.params.jsonData) as KlarnaPaymentRequestData)
          : {
              amount: parseInt(route.params.amount!, 10),
              currency: route.params.currency!,
            };
    } catch (e) {
      setErrorLog(`Invalid JSON:\n${String(e)}`);
      return;
    }

    console.log('Initiating payment', { sdk, data });
    sdk.payment
      .initiate(data)
      .then((response) => {
        console.log('Payment initiated successfully', response);
        setPaymentRequestId(response.paymentRequestId);
        setErrorLog(
          `Payment initiated successfully:\n${JSON.stringify(response, null, 2)}`
        );
      })
      .catch((error) => {
        console.error('Failed to initiate payment', error);
        setErrorLog(`Failed to initiate payment:\n${String(error)}`);
      });
  }

  return (
    <ScrollView
      contentInsetAdjustmentBehavior="automatic"
      style={[styles.scrollView, { backgroundColor: theme.background }]}
    >
      <View style={networkStyles.container}>
        <KlarnaPaymentButton
          instanceId={sdk.instanceId}
          intent="pay"
          style={localStyles.button}
          onPress={handleInitiate}
        />
        {paymentRequestId && (
          <Button
            title={copied ? 'Copied!' : 'Copy Payment Request ID'}
            onPress={() => {
              Clipboard.setString(paymentRequestId);
              setCopied(true);
              if (copyTimeoutRef.current) clearTimeout(copyTimeoutRef.current);
              copyTimeoutRef.current = setTimeout(() => setCopied(false), 2000);
            }}
          />
        )}
        <Text style={[localStyles.errorLogLabel, { color: theme.text }]}>
          Error Log
        </Text>
        <View style={localStyles.errorLogContainer}>
          <Text style={[localStyles.errorLogText, { color: theme.text }]}>
            {errorLog}
          </Text>
        </View>
      </View>
    </ScrollView>
  );
}

const localStyles = StyleSheet.create({
  button: {
    height: 48,
    width: '80%',
    alignSelf: 'center',
    marginBottom: 16,
  },
  errorLogLabel: {
    marginTop: 12,
    fontSize: 14,
  },
  errorLogContainer: {
    marginTop: 8,
    borderWidth: 1,
    borderColor: Colors.lightGray,
    borderRadius: 4,
    minHeight: 400,
    padding: 8,
  },
  errorLogText: {
    fontSize: 12,
  },
});

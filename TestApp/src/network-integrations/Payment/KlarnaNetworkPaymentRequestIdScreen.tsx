import { useNavigation, useRoute } from '@react-navigation/native';
import type {
  NativeStackNavigationProp,
  NativeStackScreenProps,
} from '@react-navigation/native-stack';
import React, { useState } from 'react';
import { ScrollView, Text, View } from 'react-native';
import type { AppStackParamList } from '../../../App';
import styles, { useTheme } from '../../common/ui/Styles';
import Button from '../../common/ui/view/Button';
import TextField from '../../common/ui/view/TextField';
import networkStyles from '../Styles/NetworkIntegrationStyles';

type RouteProp = NativeStackScreenProps<
  AppStackParamList,
  'KlarnaNetworkPaymentRequestId'
>['route'];
type NavigationProp = NativeStackNavigationProp<
  AppStackParamList,
  'KlarnaNetworkPaymentRequestId'
>;

export default function KlarnaNetworkPaymentRequestIdScreen() {
  const route = useRoute<RouteProp>();
  const navigation = useNavigation<NavigationProp>();
  const { sdk, method } = route.params;
  const theme = useTheme();
  const [paymentRequestId, setPaymentRequestId] = useState('');
  const [error, setError] = useState('');

  function handleChangeText(text: string) {
    setPaymentRequestId(text);
    if (error) setError('');
  }

  function handleContinue() {
    if (!paymentRequestId.trim()) {
      setError('Payment Request ID is required');
      return;
    }
    const params = { sdk, paymentRequestId };
    if (method === 'fetch') {
      navigation.navigate('KlarnaNetworkPaymentFetch', params);
    } else if (method === 'cancel') {
      navigation.navigate('KlarnaNetworkPaymentCancel', params);
    } else {
      navigation.navigate('KlarnaNetworkPaymentInitiateWithId', params);
    }
  }

  return (
    <ScrollView
      contentInsetAdjustmentBehavior="automatic"
      style={[styles.scrollView, { backgroundColor: theme.background }]}
    >
      <View style={networkStyles.container}>
        <TextField
          label="Payment Request Id"
          value={paymentRequestId}
          onChangeText={handleChangeText}
          placeholder="payment request id"
          labelStyle={networkStyles.fieldLabel}
        />
        {error ? <Text style={networkStyles.errorText}>{error}</Text> : null}
        <View style={networkStyles.buttonSpacing}>
          <Button title="Continue" onPress={handleContinue} />
        </View>
      </View>
    </ScrollView>
  );
}

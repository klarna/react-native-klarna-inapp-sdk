import { useNavigation, useRoute } from '@react-navigation/native';
import type {
  NativeStackNavigationProp,
  NativeStackScreenProps,
} from '@react-navigation/native-stack';
import React, { useState } from 'react';
import { Alert, ScrollView, View } from 'react-native';
import type { AppStackParamList } from '../../../../App';
import styles, { useTheme } from '../../../common/ui/Styles';
import Button from '../../../common/ui/view/Button';
import OptionPicker from '../../../common/ui/view/OptionPicker';
import TextField from '../../../common/ui/view/TextField';
import networkStyles from '../../Styles/NetworkIntegrationStyles';

type RouteProp = NativeStackScreenProps<
  AppStackParamList,
  'KlarnaNetworkPaymentPresentationData'
>['route'];
type NavigationProp = NativeStackNavigationProp<
  AppStackParamList,
  'KlarnaNetworkPaymentPresentationData'
>;

const INTENT_OPTIONS = ['', 'PAY', 'SUBSCRIBE', 'ADD_TO_WALLET'] as const;
type IntentOption = (typeof INTENT_OPTIONS)[number];

const BILLING_INTERVAL_OPTIONS = ['', 'DAY', 'WEEK', 'MONTH', 'YEAR'] as const;
type BillingIntervalOption = (typeof BILLING_INTERVAL_OPTIONS)[number];

export default function KlarnaNetworkPaymentPresentationDataScreen() {
  const route = useRoute<RouteProp>();
  const navigation = useNavigation<NavigationProp>();
  const { sdk } = route.params;
  const theme = useTheme();

  const [amount, setAmount] = useState('1000');
  const [currency, setCurrency] = useState('USD');
  const [intent, setIntent] = useState<IntentOption>('');
  const [programEnablementCodes, setProgramEnablementCodes] = useState('');
  const [billingInterval, setBillingInterval] =
    useState<BillingIntervalOption>('');
  const [billingIntervalFrequency, setBillingIntervalFrequency] = useState('');

  function handleContinue() {
    if (!amount.trim()) {
      Alert.alert('Error', 'Amount is required.');
      return;
    }
    if (!currency.trim()) {
      Alert.alert('Error', 'Currency is required.');
      return;
    }
    navigation.navigate('KlarnaNetworkPaymentPresentation', {
      sdk,
      amount: amount.trim(),
      currency: currency.trim(),
      intent: intent || undefined,
      programEnablementCodes: programEnablementCodes.trim() || undefined,
      billingInterval: billingInterval || undefined,
      billingIntervalFrequency: billingIntervalFrequency.trim() || undefined,
    });
  }

  return (
    <ScrollView
      contentInsetAdjustmentBehavior="automatic"
      style={[styles.scrollView, { backgroundColor: theme.background }]}
    >
      <View style={networkStyles.container}>
        <TextField
          label="Amount"
          value={amount}
          onChangeText={setAmount}
          placeholder="Amount"
          labelStyle={networkStyles.fieldLabel}
        />
        <TextField
          label="Currency"
          value={currency}
          onChangeText={setCurrency}
          placeholder="Currency"
          labelStyle={networkStyles.fieldLabel}
        />
        <OptionPicker
          label="Intent"
          options={INTENT_OPTIONS}
          selected={intent}
          onSelect={setIntent}
          formatOption={(v) => v || 'None'}
        />
        <TextField
          label="Program Enablement Codes (comma separated)"
          value={programEnablementCodes}
          onChangeText={setProgramEnablementCodes}
          placeholder="Program Enablement Codes"
          labelStyle={networkStyles.fieldLabel}
        />
        <OptionPicker
          label="Billing Interval"
          options={BILLING_INTERVAL_OPTIONS}
          selected={billingInterval}
          onSelect={setBillingInterval}
          formatOption={(v) => v || 'None'}
        />
        <TextField
          label="Billing Interval Frequency"
          value={billingIntervalFrequency}
          onChangeText={setBillingIntervalFrequency}
          placeholder="Billing Interval Frequency"
          labelStyle={networkStyles.fieldLabel}
        />
        <View style={networkStyles.buttonSpacing}>
          <Button title="Continue" onPress={handleContinue} />
        </View>
      </View>
    </ScrollView>
  );
}

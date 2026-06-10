import { useNavigation, useRoute } from '@react-navigation/native';
import type {
  NativeStackNavigationProp,
  NativeStackScreenProps,
} from '@react-navigation/native-stack';
import React, { useState } from 'react';
import { ScrollView, View } from 'react-native';
import type { AppStackParamList } from '../../../App';
import styles, { useTheme } from '../../common/ui/Styles';
import Button from '../../common/ui/view/Button';
import TextField from '../../common/ui/view/TextField';
import networkStyles from '../Styles/NetworkIntegrationStyles';

type RouteProp = NativeStackScreenProps<
  AppStackParamList,
  'KlarnaNetworkPaymentCustomData'
>['route'];
type NavigationProp = NativeStackNavigationProp<
  AppStackParamList,
  'KlarnaNetworkPaymentCustomData'
>;

export default function KlarnaNetworkPaymentCustomDataScreen() {
  const route = useRoute<RouteProp>();
  const navigation = useNavigation<NavigationProp>();
  const { sdk } = route.params;
  const theme = useTheme();

  const [amount, setAmount] = useState('100');
  const [currency, setCurrency] = useState('USD');

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
          labelStyle={networkStyles.fieldLabel}
        />
        <TextField
          label="Currency"
          value={currency}
          onChangeText={setCurrency}
          labelStyle={networkStyles.fieldLabel}
        />
        <View style={networkStyles.buttonSpacing}>
          <Button
            title="Continue"
            onPress={() =>
              navigation.navigate(
                'KlarnaNetworkPaymentInitiateWithRequestData',
                {
                  sdk,
                  amount,
                  currency,
                }
              )
            }
          />
        </View>
      </View>
    </ScrollView>
  );
}

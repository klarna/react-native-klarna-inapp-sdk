import { useRoute } from '@react-navigation/native';
import type { NativeStackScreenProps } from '@react-navigation/native-stack';
import React, { useState } from 'react';
import { ScrollView, Text, TextInput, View } from 'react-native';
import type { AppStackParamList } from '../../App';
import styles, { useTheme } from '../common/ui/Styles';
import Button from '../common/ui/view/Button';
import OptionPicker from '../common/ui/view/OptionPicker';
import testProps from '../common/util/TestProps';
import {
  KlarnaMessagingPlacementView,
  KlarnaMessagingPlacementType,
  KlarnaTheme,
} from '@klarna/react-native-klarna-network-messaging';
import networkStyles from './Styles/NetworkIntegrationStyles';

type RouteProp = NativeStackScreenProps<
  AppStackParamList,
  'KlarnaNetworkMessaging'
>['route'];

const PLACEMENT_TYPES = [
  KlarnaMessagingPlacementType.CreditPromotionAutoSize,
  KlarnaMessagingPlacementType.CreditPromotionBadge,
] as const;

const THEMES = [
  KlarnaTheme.Automatic,
  KlarnaTheme.Light,
  KlarnaTheme.Dark,
] as const;

export default function KlarnaNetworkMessagingScreen() {
  const route = useRoute<RouteProp>();
  const { sdk } = route.params;
  const theme = useTheme();

  const [placementType, setPlacementType] =
    useState<KlarnaMessagingPlacementType>(PLACEMENT_TYPES[0]);
  const [messagingTheme, setMessagingTheme] = useState<KlarnaTheme>(THEMES[1]);
  const [amount, setAmount] = useState('9999');
  const [currency, setCurrency] = useState('USD');
  const [lastError, setLastError] = useState<string | null>(null);
  const [showView, setShowView] = useState(false);

  const parsedAmount = Number(amount);
  const isAmountValid = !Number.isNaN(parsedAmount) && parsedAmount > 0;

  return (
    <ScrollView
      contentInsetAdjustmentBehavior="automatic"
      style={[styles.scrollView, { backgroundColor: theme.background }]}
    >
      <View style={networkStyles.container}>
        <Text
          style={[
            styles.title,
            networkStyles.sectionTitle,
            { color: theme.text },
          ]}
        >
          Configuration
        </Text>

        <OptionPicker
          label="Placement Type"
          options={PLACEMENT_TYPES}
          selected={placementType}
          onSelect={setPlacementType}
        />

        <OptionPicker
          label="Theme"
          options={THEMES}
          selected={messagingTheme}
          onSelect={setMessagingTheme}
        />

        <View style={networkStyles.fieldContainer}>
          <Text
            style={[
              styles.title,
              networkStyles.fieldLabel,
              { color: theme.text },
            ]}
          >
            Amount (minor units)
          </Text>
          <TextInput
            {...testProps('messagingAmount')}
            style={[styles.tokenInput, { color: theme.text }]}
            placeholderTextColor={theme.placeholder}
            value={amount}
            placeholder="e.g. 9999"
            onChangeText={setAmount}
            keyboardType="numeric"
          />
        </View>

        <View style={networkStyles.fieldContainer}>
          <Text
            style={[
              styles.title,
              networkStyles.fieldLabel,
              { color: theme.text },
            ]}
          >
            Currency
          </Text>
          <TextInput
            {...testProps('messagingCurrency')}
            style={[styles.tokenInput, { color: theme.text }]}
            placeholderTextColor={theme.placeholder}
            value={currency}
            placeholder="e.g. USD"
            onChangeText={setCurrency}
            autoCapitalize="characters"
          />
        </View>

        <View style={networkStyles.buttonSpacing}>
          <Button
            title="Render"
            onPress={() => {
              setLastError(null);
              setShowView(true);
            }}
          />
        </View>

        <Text
          style={[
            styles.title,
            networkStyles.sectionTitle,
            { color: theme.text },
          ]}
        >
          Preview
        </Text>

        {showView && isAmountValid && currency.length > 0 ? (
          <View style={networkStyles.previewContainer}>
            <KlarnaMessagingPlacementView
              instance={sdk}
              configuration={{
                type: placementType,
                theme: messagingTheme,
                amount: parsedAmount,
                currency,
              }}
              onError={(error) => {
                console.log('KlarnaMessagingPlacementView error:', error);
                setLastError(`${error.name}: ${error.message}`);
              }}
            />
          </View>
        ) : (
          <Text style={networkStyles.hint}>
            {showView
              ? 'Enter a valid amount and currency to render the placement.'
              : 'Press Render to display the messaging placement.'}
          </Text>
        )}

        {lastError && (
          <View style={networkStyles.errorContainer}>
            <Text style={networkStyles.errorTitle}>Last Error</Text>
            <Text
              {...testProps('messagingError')}
              style={networkStyles.errorText}
            >
              {lastError}
            </Text>
          </View>
        )}

        <View style={networkStyles.infoContainer}>
          <Text style={networkStyles.infoTitle}>SDK Info</Text>
          <Text style={networkStyles.infoText}>
            Initialized with client ID from Klarna Network Core
          </Text>
        </View>
      </View>
    </ScrollView>
  );
}

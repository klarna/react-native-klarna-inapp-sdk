import { Klarna } from '@klarna/react-native-klarna-network-core';
import { useNavigation } from '@react-navigation/native';
import type { NativeStackNavigationProp } from '@react-navigation/native-stack';
import React, { useRef, useState } from 'react';
import { Alert, Platform, ScrollView, StyleSheet, View } from 'react-native';
import type { AppStackParamList } from '../../App';
import styles, { useTheme } from '../common/ui/Styles';
import Button from '../common/ui/view/Button.tsx';
import TextField from '../common/ui/view/TextField';
import networkStyles from './Styles/NetworkIntegrationStyles.ts';

type NavigationProp = NativeStackNavigationProp<
  AppStackParamList,
  'KlarnaNetworkInitialization'
>;

export default function KlarnaNetworkInitializationScreen() {
  const navigation = useNavigation<NavigationProp>();
  const theme = useTheme();

  const [clientId, setClientId] = useState('');
  const [accountId, setAccountId] = useState('');
  const [locale, setLocale] = useState('en-US');
  const [sessionToken, setSessionToken] = useState('');
  const [returnUrl, setReturnUrl] = useState('test-app://');

  const klarnaRef = useRef<Klarna | null>(null);

  async function handleInitialize() {
    if (!clientId || clientId.trim().length === 0) {
      Alert.alert('Error!', 'Client ID can not be null or empty.');
      return;
    }

    Klarna.initialize({
      clientId: clientId.trim(),
      appReturnUrl: returnUrl.trim() || 'test-app://',
      accountId: accountId.trim() || undefined,
      locale: locale.trim() || undefined,
      klarnaNetworkSessionToken: sessionToken.trim() || undefined,
    })
      .then((sdk) => {
        klarnaRef.current = sdk;
        navigation.navigate('KlarnaNetworkIntegrations', { sdk });
      })
      .catch((error) => {
        Alert.alert(
          'Klarna SDK Error',
          `Failed to initialize Klarna SDK: ${String(error)}`
        );
      });
  }

  async function handleClearInstance() {
    if (!klarnaRef.current) {
      Alert.alert('Error!', 'No active Klarna SDK instance to clear.');
      return;
    }
    klarnaRef.current
      .dispose()
      .then(() => {
        klarnaRef.current = null;
        Alert.alert('Success', 'Klarna instance cleared.');
      })
      .catch((error) => {
        Alert.alert(
          'Klarna SDK Error',
          `Failed to clear instance: ${String(error)}`
        );
      });
  }

  return (
    <ScrollView
      contentInsetAdjustmentBehavior="automatic"
      style={[styles.scrollView, { backgroundColor: theme.background }]}
    >
      <View style={networkStyles.container}>
        <TextField
          label="Client ID"
          value={clientId}
          onChangeText={setClientId}
          labelStyle={networkStyles.fieldLabel}
        />
        <TextField
          label="Account ID"
          value={accountId}
          onChangeText={setAccountId}
          labelStyle={networkStyles.fieldLabel}
        />
        <TextField
          label="Locale"
          value={locale}
          onChangeText={setLocale}
          labelStyle={networkStyles.fieldLabel}
        />
        <TextField
          label="Klarna Network Session Token"
          value={sessionToken}
          onChangeText={setSessionToken}
          labelStyle={networkStyles.fieldLabel}
        />
        {Platform.OS !== 'android' && (
          <TextField
            label="App Return URL"
            value={returnUrl}
            onChangeText={setReturnUrl}
            labelStyle={networkStyles.fieldLabel}
          />
        )}
        <View style={networkStyles.buttonSpacing}>
          <Button title="Initialize" onPress={handleInitialize} />
        </View>
        <View style={localStyles.clearButton}>
          <Button title="Clear Klarna instance" onPress={handleClearInstance} />
        </View>
      </View>
    </ScrollView>
  );
}

const localStyles = StyleSheet.create({
  clearButton: {
    marginTop: 12,
  },
});

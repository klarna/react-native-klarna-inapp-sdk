import { useRoute } from '@react-navigation/native';
import type { NativeStackScreenProps } from '@react-navigation/native-stack';
import React, { useState } from 'react';
import { Alert, ScrollView, StyleSheet, Text, View } from 'react-native';
import type { AppStackParamList } from '../../App';
import styles, { useTheme } from '../common/ui/Styles';
import Button from '../common/ui/view/Button.tsx';
import networkStyles from './Styles/NetworkIntegrationStyles.ts';

type RouteProp = NativeStackScreenProps<
  AppStackParamList,
  'KlarnaSession'
>['route'];

export default function KlarnaSessionScreen() {
  const route = useRoute<RouteProp>();
  const { sdk } = route.params;
  const theme = useTheme();
  const [token, setToken] = useState<string | null>(null);
  const [tokenError, setTokenError] = useState<string | null>(null);

  async function handleClearSession() {
    sdk.network.session
      .clear()
      .then(() => {
        Alert.alert('Clear Session', 'Session cleared successfully.');
      })
      .catch((error) => {
        Alert.alert(
          'Clear Session',
          `Failed to clear session: ${String(error)}`
        );
      });
  }

  async function handleToken() {
    sdk.network.session
      .token()
      .then((result) => {
        setToken(result);
        setTokenError(null);
      })
      .catch((error) => {
        setTokenError(String(error));
        setToken(null);
      });
  }

  return (
    <ScrollView
      contentInsetAdjustmentBehavior="automatic"
      style={[styles.scrollView, { backgroundColor: theme.background }]}
    >
      <View style={networkStyles.container}>
        <View style={networkStyles.buttonSpacing}>
          <Button title="Clear" onPress={handleClearSession} />
        </View>
        <View style={networkStyles.buttonSpacing}>
          <Button title="Token" onPress={handleToken} />
        </View>
        {token !== null && (
          <View style={networkStyles.container}>
            <Text style={[styles.title, { color: theme.text }]}>
              Session Token:
            </Text>
            <Text style={localStyles.tokenValue}>{token}</Text>
          </View>
        )}
        {tokenError !== null && (
          <View style={networkStyles.container}>
            <Text style={[styles.title, { color: theme.text }]}>Error:</Text>
            <Text style={localStyles.errorValue}>{tokenError}</Text>
          </View>
        )}
      </View>
    </ScrollView>
  );
}

const localStyles = StyleSheet.create({
  tokenValue: {
    marginTop: 8,
    fontSize: 16,
    color: 'gray',
  },
  errorValue: {
    marginTop: 8,
    fontSize: 16,
    color: 'red',
  },
});

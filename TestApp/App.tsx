import React from 'react';
import { NavigationContainer } from '@react-navigation/native';
import { createNativeStackNavigator } from '@react-navigation/native-stack';
import HomeScreen from './src/home/HomeScreen';
import PaymentsScreen from './src/payments/PaymentsScreen';
import StandaloneWebViewScreen from './src/standalonewebview/StandaloneWebViewScreen';
import KlarnaCheckoutScreen from './src/checkout/KlarnaCheckoutScreen';
import SignInScreen from './src/signIn/SignInScreen.tsx';
import KlarnaExpressCheckoutScreen from './src/expresscheckout/KlarnaExpressCheckoutScreen';
import KlarnaOSMScreen from './src/osm/KlarnaOSMScreen';
import KlarnaNetworkInitializationScreen from './src/network-integrations/KlarnaNetworkInitializationScreen';
import KlarnaNetworkIntegrationsScreen from './src/network-integrations/KlarnaNetworkIntegrationsScreen.tsx';
import KlarnaNetworkMessagingScreen from './src/network-integrations/KlarnaNetworkMessagingScreen';
import KlarnaSessionScreen from './src/network-integrations/KlarnaSessionScreen.tsx';
import type { Klarna } from '@klarna/react-native-klarna-network-core';

const Stack = createNativeStackNavigator<AppStackParamList>();

const AppStack = () => {
  return (
    <Stack.Navigator initialRouteName="Home">
      <Stack.Screen
        name="Home"
        component={HomeScreen}
        options={{ title: 'Klarna Mobile SDK Test App' }}
      />
      <Stack.Screen name="Payments" component={PaymentsScreen} />
      <Stack.Screen
        name="StandaloneWebView"
        component={StandaloneWebViewScreen}
      />
      <Stack.Screen name="KlarnaCheckout" component={KlarnaCheckoutScreen} />
      <Stack.Screen name="SignIn" component={SignInScreen} />
      <Stack.Screen
        name="ExpressCheckout"
        component={KlarnaExpressCheckoutScreen}
      />
      <Stack.Screen name="KlarnaOSM" component={KlarnaOSMScreen} />
      <Stack.Screen
        name="KlarnaNetworkInitialization"
        component={KlarnaNetworkInitializationScreen}
        options={{ title: 'Klarna Network Initialization' }}
      />
      <Stack.Screen
        name="KlarnaNetworkIntegrations"
        component={KlarnaNetworkIntegrationsScreen}
        options={{ title: 'Klarna Network Integrations' }}
      />
      <Stack.Screen
        name="KlarnaSession"
        component={KlarnaSessionScreen}
        options={{ title: 'Klarna Session' }}
      />
      <Stack.Screen
        name="KlarnaNetworkMessaging"
        component={KlarnaNetworkMessagingScreen}
        options={{ title: 'Klarna Network Messaging' }}
      />
    </Stack.Navigator>
  );
};

function App() {
  return (
    <NavigationContainer>
      <AppStack />
    </NavigationContainer>
  );
}

type AppStackParamList = {
  Home: undefined;
  Payments: undefined;
  StandaloneWebView: undefined;
  KlarnaCheckout: undefined;
  SignIn: undefined;
  ExpressCheckout: undefined;
  KlarnaOSM: undefined;
  KlarnaNetworkInitialization: undefined;
  KlarnaNetworkIntegrations: { sdk: Klarna };
  KlarnaSession: { sdk: Klarna };
  KlarnaNetworkMessaging: { sdk: Klarna };
};

export default App;
export type { AppStackParamList };

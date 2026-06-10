import React, { useEffect } from 'react';
import { Linking, Platform } from 'react-native';
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
import KlarnaNetworkPaymentScreen from './src/network-integrations/Payment/KlarnaNetworkPaymentScreen.tsx';
import KlarnaNetworkPaymentCustomDataScreen from './src/network-integrations/Payment/KlarnaNetworkPaymentCustomDataScreen.tsx';
import KlarnaNetworkPaymentInitiateWithRequestDataScreen from './src/network-integrations/Payment/InitiateWithData/KlarnaNetworkPaymentInitiateWithRequestDataScreen.tsx';
import KlarnaNetworkPaymentRequestIdScreen from './src/network-integrations/Payment/KlarnaNetworkPaymentRequestIdScreen';
import KlarnaNetworkPaymentFetchScreen from './src/network-integrations/Payment/Fetch/KlarnaNetworkPaymentFetchScreen';
import KlarnaNetworkPaymentCancelScreen from './src/network-integrations/Payment/Cancel/KlarnaNetworkPaymentCancelScreen';
import KlarnaNetworkPaymentInitiateWithIdScreen from './src/network-integrations/Payment/InitiateWithId/KlarnaNetworkPaymentInitiateWithIdScreen';
import KlarnaNetworkPaymentPresentationDataScreen from './src/network-integrations/Payment/Presentation/KlarnaNetworkPaymentPresentationDataScreen';
import KlarnaNetworkPaymentPresentationScreen from './src/network-integrations/Payment/Presentation/KlarnaNetworkPaymentPresentationScreen';
import KlarnaNetworkPaymentButtonScreen from './src/network-integrations/Payment/KlarnaNetworkPaymentButtonScreen';
import KlarnaNetworkMessagingScreen from './src/network-integrations/KlarnaNetworkMessagingScreen';
import KlarnaSessionScreen from './src/network-integrations/KlarnaSessionScreen.tsx';
import { Klarna } from '@klarna/react-native-klarna-network-core';

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
        name="KlarnaNetworkPayment"
        component={KlarnaNetworkPaymentScreen}
        options={{ title: 'Klarna Payment' }}
      />
      <Stack.Screen
        name="KlarnaNetworkPaymentCustomData"
        component={KlarnaNetworkPaymentCustomDataScreen}
        options={{ title: 'Klarna Payment' }}
      />
      <Stack.Screen
        name="KlarnaNetworkPaymentInitiateWithRequestData"
        component={KlarnaNetworkPaymentInitiateWithRequestDataScreen}
        options={{ title: 'Payment Button' }}
      />
      <Stack.Screen
        name="KlarnaNetworkPaymentRequestId"
        component={KlarnaNetworkPaymentRequestIdScreen}
        options={{ title: 'Klarna Payment' }}
      />
      <Stack.Screen
        name="KlarnaNetworkPaymentFetch"
        component={KlarnaNetworkPaymentFetchScreen}
        options={{ title: 'Klarna Payment Request Fetch' }}
      />
      <Stack.Screen
        name="KlarnaNetworkPaymentCancel"
        component={KlarnaNetworkPaymentCancelScreen}
        options={{ title: 'Klarna Payment Request Cancel' }}
      />
      <Stack.Screen
        name="KlarnaNetworkPaymentInitiateWithId"
        component={KlarnaNetworkPaymentInitiateWithIdScreen}
        options={{ title: 'Klarna Payment Request Initiate' }}
      />
      <Stack.Screen
        name="KlarnaNetworkPaymentPresentationData"
        component={KlarnaNetworkPaymentPresentationDataScreen}
        options={{ title: 'Klarna Payment' }}
      />
      <Stack.Screen
        name="KlarnaNetworkPaymentPresentation"
        component={KlarnaNetworkPaymentPresentationScreen}
        options={{ title: 'Klarna Payment Presentation' }}
      />
      <Stack.Screen
        name="KlarnaNetworkPaymentButton"
        component={KlarnaNetworkPaymentButtonScreen}
        options={{ title: 'Klarna Payment Button' }}
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
  useEffect(() => {
    if (Platform.OS !== 'ios') return;
    const subscription = Linking.addEventListener('url', ({ url }) => {
      Klarna.handleReturnUrl(url).catch(console.error);
    });
    Linking.getInitialURL()
      .then((url) => {
        if (url) Klarna.handleReturnUrl(url).catch(console.error);
      })
      .catch(console.error);
    return () => subscription.remove();
  }, []);

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
  KlarnaNetworkPayment: { sdk: Klarna };
  KlarnaNetworkPaymentCustomData: { sdk: Klarna };
  KlarnaNetworkPaymentInitiateWithRequestData:
    | { sdk: Klarna; amount: string; currency: string; jsonData?: never }
    | { sdk: Klarna; jsonData: string; amount?: never; currency?: never };
  KlarnaNetworkPaymentRequestId: {
    sdk: Klarna;
    method: 'fetch' | 'cancel' | 'initiate';
  };
  KlarnaNetworkPaymentFetch: { sdk: Klarna; paymentRequestId: string };
  KlarnaNetworkPaymentCancel: { sdk: Klarna; paymentRequestId: string };
  KlarnaNetworkPaymentInitiateWithId: { sdk: Klarna; paymentRequestId: string };
  KlarnaNetworkPaymentPresentationData: { sdk: Klarna };
  KlarnaNetworkPaymentPresentation: {
    sdk: Klarna;
    amount: string;
    currency: string;
    intent?: string;
    programEnablementCodes?: string;
    billingInterval?: string;
    billingIntervalFrequency?: string;
  };
  KlarnaNetworkPaymentButton: { sdk: Klarna };
  KlarnaNetworkMessaging: { sdk: Klarna };
};

export default App;
export type { AppStackParamList };

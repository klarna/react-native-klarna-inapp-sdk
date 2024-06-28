/**
 * Sample React Native App
 * https://github.com/facebook/react-native
 *
 * @format
 */

import React from 'react';
import {NavigationContainer} from '@react-navigation/native';
import {createNativeStackNavigator} from '@react-navigation/native-stack';
import HomeScreen from './src/home/HomeScreen';
import PaymentsScreen from './src/payments/PaymentsScreen';
import StandaloneWebViewScreen from './src/standalonewebview/StandaloneWebViewScreen';
import KlarnaCheckoutScreen from './src/checkout/KlarnaCheckoutScreen';
import KlarnaSnippetScreen from './src/checkout/KlarnaSnippetScreen.tsx';

const Stack = createNativeStackNavigator<AppStackParamList>();

const AppStack = () => {
  return (
    <Stack.Navigator initialRouteName="Home">
      <Stack.Screen
        name="Home"
        component={HomeScreen}
        options={{title: 'Klarna Mobile SDK Test App'}}
      />
      <Stack.Screen name="Payments" component={PaymentsScreen} />
      <Stack.Screen
        name="StandaloneWebView"
        component={StandaloneWebViewScreen}
      />
      <Stack.Screen name="KlarnaCheckout" component={KlarnaCheckoutScreen} />
      <Stack.Screen name="KlarnaSnippet" component={KlarnaSnippetScreen} />
    </Stack.Navigator>
  );
};

export default function App(): JSX.Element {
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
  KlarnaSnippet: undefined;
};

export type {AppStackParamList};

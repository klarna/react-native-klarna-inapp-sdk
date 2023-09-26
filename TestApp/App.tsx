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

const Stack = createNativeStackNavigator<AppStackParamList>();

const AppStack = () => {
  return (
    <Stack.Navigator initialRouteName="Home">
      <Stack.Screen
        name="Home"
        component={HomeScreen}
        options={{title: 'Klarna MSDK TestApp'}}
      />
      <Stack.Screen name="Payments" component={PaymentsScreen} />
      <Stack.Screen name="StandaloneWebView" component={StandaloneWebViewScreen} />
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
};

export type {AppStackParamList};

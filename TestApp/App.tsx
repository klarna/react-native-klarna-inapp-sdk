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

function App(): JSX.Element {
  const AppStack = () => {
    const Stack = createNativeStackNavigator();

    return (
      <Stack.Navigator initialRouteName="Home">
        <Stack.Screen
          name="Home"
          component={HomeScreen}
          options={{title: 'Klarna MSDK TestApp'}}
        />
        <Stack.Screen name="Payments" component={PaymentsScreen} />
      </Stack.Navigator>
    );
  };

  return (
    <NavigationContainer>
      <AppStack />
    </NavigationContainer>
  );
}

export default App;

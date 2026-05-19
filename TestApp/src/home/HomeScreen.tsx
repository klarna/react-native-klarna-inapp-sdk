import React from 'react';
import { ScrollView, Text, View } from 'react-native';
import type { NativeStackNavigationProp } from '@react-navigation/native-stack';
import styles, { useTheme } from '../common/ui/Styles';
import testProps from '../common/util/TestProps';
import type { AppStackParamList } from '../../App';
import { useNavigation } from '@react-navigation/native';

type HomeNavigationProp = NativeStackNavigationProp<AppStackParamList, 'Home'>;

export default function HomeScreen() {
  const navigation = useNavigation<HomeNavigationProp>();
  const theme = useTheme();
  const navItemStyle = [styles.navMenuItem, { color: theme.text }];

  return (
    <ScrollView
      contentInsetAdjustmentBehavior="automatic"
      style={[styles.scrollView, { backgroundColor: theme.background }]}
    >
      <View
        style={{
          backgroundColor: theme.surface,
        }}
      >
        <Text
          style={navItemStyle}
          {...testProps('navKlarnaPayments')}
          onPress={() => {
            console.log('Navigating to Payments');
            navigation.navigate('Payments');
          }}
        >
          Klarna Payments
        </Text>
      </View>
      <View
        style={{
          backgroundColor: theme.surface,
        }}
      >
        <Text
          style={navItemStyle}
          {...testProps('navKlarnaStandaloneWebView')}
          onPress={() => {
            console.log('Navigating to StandaloneWebView');
            navigation.navigate('StandaloneWebView');
          }}
        >
          Klarna Standalone WebView
        </Text>
      </View>
      <View
        style={{
          backgroundColor: theme.surface,
        }}
      >
        <Text
          style={navItemStyle}
          {...testProps('navKlarnaCheckoutView')}
          onPress={() => {
            console.log('Navigating to Checkout');
            navigation.navigate('KlarnaCheckout');
          }}
        >
          Klarna Checkout
        </Text>
      </View>
      <View
        style={{
          backgroundColor: theme.surface,
        }}
      >
        <Text
          style={navItemStyle}
          {...testProps('navKlarnaSignIn')}
          onPress={() => {
            console.log('Navigating to KlarnaSignIn');
            navigation.navigate('SignIn');
          }}
        >
          Klarna Sign In
        </Text>
      </View>
      <View
        style={{
          backgroundColor: theme.surface,
        }}
      >
        <Text
          style={navItemStyle}
          {...testProps('navKlarnaExpressCheckout')}
          onPress={() => {
            console.log('Navigating to ExpressCheckout');
            navigation.navigate('ExpressCheckout');
          }}
        >
          Klarna Express Checkout
        </Text>
      </View>
      <View
        style={{
          backgroundColor: theme.surface,
        }}
      >
        <Text
          style={navItemStyle}
          {...testProps('navKlarnaOSM')}
          onPress={() => {
            console.log('Navigating to KlarnaOSM');
            navigation.navigate('KlarnaOSM');
          }}
        >
          Klarna On-site Messaging
        </Text>
      </View>
      <View
        style={{
          backgroundColor: theme.surface,
        }}
      >
        <Text
          style={navItemStyle}
          {...testProps('navKlarnaNetworkIntegrations')}
          onPress={() => {
            console.log('Navigating to Klarna Network Initialization');
            navigation.navigate('KlarnaNetworkInitialization');
          }}
        >
          Klarna Network Integrations
        </Text>
      </View>
    </ScrollView>
  );
}

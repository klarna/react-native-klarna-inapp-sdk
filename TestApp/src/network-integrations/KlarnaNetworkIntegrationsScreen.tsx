import { useNavigation, useRoute } from '@react-navigation/native';
import type {
  NativeStackNavigationProp,
  NativeStackScreenProps,
} from '@react-navigation/native-stack';
import React from 'react';
import { ScrollView, Text, View } from 'react-native';
import type { AppStackParamList } from '../../App';
import styles, { useTheme } from '../common/ui/Styles';
import testProps from '../common/util/TestProps';

type RouteProp = NativeStackScreenProps<
  AppStackParamList,
  'KlarnaNetworkIntegrations'
>['route'];
type NavigationProp = NativeStackNavigationProp<
  AppStackParamList,
  'KlarnaNetworkIntegrations'
>;

export default function KlarnaNetworkIntegrationsScreen() {
  const route = useRoute<RouteProp>();
  const navigation = useNavigation<NavigationProp>();
  const { sdk } = route.params;
  const theme = useTheme();
  const navItemStyle = [styles.navMenuItem, { color: theme.text }];

  return (
    <ScrollView
      contentInsetAdjustmentBehavior="automatic"
      style={[styles.scrollView, { backgroundColor: theme.background }]}
    >
      <View style={{ backgroundColor: theme.surface }}>
        <Text
          style={navItemStyle}
          onPress={() => navigation.navigate('KlarnaSession', { sdk })}
        >
          Klarna Session
        </Text>
      </View>
      {/* KlarnaNetworkMessaging Module */}
      <View style={{ backgroundColor: theme.surface }}>
        <Text
          style={navItemStyle}
          {...testProps('navKlarnaNetworkMessaging')}
          onPress={() => navigation.navigate('KlarnaNetworkMessaging', { sdk })}
        >
          Klarna Network Messaging
        </Text>
      </View>
    </ScrollView>
  );
}

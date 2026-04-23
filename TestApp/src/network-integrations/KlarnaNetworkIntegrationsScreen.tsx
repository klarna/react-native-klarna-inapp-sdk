import React from 'react';
import { ScrollView, Text, useColorScheme, View } from 'react-native';
import type { NativeStackNavigationProp } from '@react-navigation/native-stack';
import styles, { backgroundStyle, Colors } from '../common/ui/Styles';
// eslint-disable-next-line @typescript-eslint/no-unused-vars
import testProps from '../common/util/TestProps';
import type { AppStackParamList } from '../../App';
import { useNavigation } from '@react-navigation/native';

type NetworkIntegrationsNavigationProp = NativeStackNavigationProp<
  AppStackParamList,
  'KlarnaNetworkIntegrations'
>;

export default function KlarnaNetworkIntegrationsScreen() {
  // eslint-disable-next-line @typescript-eslint/no-unused-vars
  const navigation = useNavigation<NetworkIntegrationsNavigationProp>();
  const isDarkMode = useColorScheme() === 'dark';

  return (
    <ScrollView
      contentInsetAdjustmentBehavior="automatic"
      style={backgroundStyle(styles.scrollView, isDarkMode)}
    >
      {/* Add new module buttons here as you create them with /rn-modules skill */}
      {/* Example:
      <View
        style={{
          backgroundColor: isDarkMode ? Colors.black : Colors.white,
        }}
      >
        <Text
          style={styles.navMenuItem}
          {...testProps('navYourModule')}
          onPress={() => {
            console.log('Navigating to YourModule');
            navigation.navigate('YourModule');
          }}
        >
          Your Module Name
        </Text>
      </View>
      */}

      {/* Placeholder when no modules are added yet */}
      <View
        // eslint-disable-next-line react-native/no-inline-styles
        style={{
          backgroundColor: isDarkMode ? Colors.black : Colors.white,
          padding: 20,
        }}
      >
        <Text style={styles.sectionDescription}>
          No network integration modules added yet.
        </Text>
      </View>
    </ScrollView>
  );
}

import React from 'react';
import {ScrollView, Text, useColorScheme, View} from 'react-native';
import type {NativeStackNavigationProp} from '@react-navigation/native-stack';
import styles, {backgroundStyle, Colors} from '../common/ui/Styles';
import testProps from '../common/util/TestProps';
import type {AppStackParamList} from '../../App';

type HomeNavigationProp = NativeStackNavigationProp<AppStackParamList, 'Home'>;

type Props = {
  navigation: HomeNavigationProp;
};

const HomeScreen = ({navigation}: Props) => {
  const isDarkMode = useColorScheme() === 'dark';

  return (
    <ScrollView
      contentInsetAdjustmentBehavior="automatic"
      style={backgroundStyle(isDarkMode)}>
      <View
        style={{
          backgroundColor: isDarkMode ? Colors.black : Colors.white,
        }}>
        <Text
          style={styles.navMenuItem}
          {...testProps('navKlarnaPayments')}
          onPress={() => {
            console.log('Navigating to Payments');
            navigation.navigate('Payments');
          }}>
          Klarna Payments
        </Text>
      </View>
    </ScrollView>
  );
};

export default HomeScreen;

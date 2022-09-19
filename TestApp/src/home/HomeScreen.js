import React from 'react';
import {ScrollView, Text, useColorScheme, View} from 'react-native';
import styles, {backgroundStyle, Colors} from '../Styles';
import testProps from '../TestProps';

const HomeScreen = ({navigation}) => {
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
          onPress={() => navigation.navigate('Payments')}>
          Klarna Payments
        </Text>
      </View>
    </ScrollView>
  );
};

export default HomeScreen;

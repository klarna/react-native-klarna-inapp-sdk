import React, {useState} from 'react';
import {ScrollView, TextInput, useColorScheme, View} from 'react-native';
import styles, {backgroundStyle, Colors} from '../Styles';
import testProps from '../TestProps';
import PaymentsContainer from './PaymentsContainer';

let authToken = ''; // set your token here

const PaymentsScreen = ({navigation}) => {
  const isDarkMode = useColorScheme() === 'dark';

  const [clientToken, setClientToken] = useState(authToken);

  const paymentMethods = [
    'pay_now',
    'pay_later',
    'pay_over_time',
    'pay_in_parts',
  ];

  const renderSetTokenInput = () => {
    return (
      <TextInput
        style={styles.tokenInput}
        defaultValue={clientToken}
        placeholder="Set client token here..."
        multiline={true}
        blurOnSubmit={true}
        {...testProps('setTokenInput')}
        onChangeText={text => {
          setClientToken(text);
        }}
      />
    );
  };

  return (
    <ScrollView
      contentInsetAdjustmentBehavior="automatic"
      style={backgroundStyle(isDarkMode)}>
      <View
        style={{
          backgroundColor: isDarkMode ? Colors.black : Colors.white,
        }}>
        {renderSetTokenInput()}
        {paymentMethods.map(paymentMethod => {
          return (
            <PaymentsContainer
              key={paymentMethod}
              clientToken={clientToken}
              paymentMethodName={paymentMethod}
            />
          );
        })}
      </View>
    </ScrollView>
  );
};

export default PaymentsScreen;

import React, { useState } from 'react';
import { ScrollView, TextInput, useColorScheme, View } from 'react-native';
import styles, { backgroundStyle, Colors } from '../common/ui/Styles';
import testProps from '../common/util/TestProps';
import PaymentsContainer from './PaymentsContainer';

let authToken = ''; // set your token here

export default function PaymentsScreen() {
  const isDarkMode = useColorScheme() === 'dark';

  const [clientToken, setClientToken] = useState(authToken);

  const paymentMethods = [
    'klarna',
    'pay_now',
    'pay_later',
    'pay_over_time',
    'pay_in_parts',
    'direct_debit',
    'direct_bank_transfer',
    'card',
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
      style={backgroundStyle(styles.scrollView, isDarkMode)}
    >
      <View
        style={{
          backgroundColor: isDarkMode ? Colors.black : Colors.white,
        }}
      >
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
}

import React, { useState } from 'react';
import { ScrollView, TextInput, View } from 'react-native';
import styles, { useTheme } from '../common/ui/Styles';
import testProps from '../common/util/TestProps';
import PaymentsContainer from './PaymentsContainer';

let authToken = ''; // set your token here

export default function PaymentsScreen() {
  const theme = useTheme();

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
        style={[styles.tokenInput, { color: theme.text }]}
        placeholderTextColor={theme.placeholder}
        defaultValue={clientToken}
        placeholder="Set client token here..."
        multiline={true}
        blurOnSubmit={true}
        {...testProps('setTokenInput')}
        onChangeText={(text) => {
          setClientToken(text);
        }}
      />
    );
  };

  return (
    <ScrollView
      contentInsetAdjustmentBehavior="automatic"
      style={[styles.scrollView, { backgroundColor: theme.background }]}
    >
      <View style={{ backgroundColor: theme.surface }}>
        {renderSetTokenInput()}
        {paymentMethods.map((paymentMethod) => {
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

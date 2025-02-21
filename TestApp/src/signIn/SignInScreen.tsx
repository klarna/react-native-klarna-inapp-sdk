import React, {useEffect, useState} from 'react';
import {ScrollView, TextInput, Text, useColorScheme, View} from 'react-native';
import styles, {backgroundStyle} from '../common/ui/Styles';
import testProps from '../common/util/TestProps';
import Button from '../common/ui/view/Button.tsx';
import {RNKlarnaSignIn} from 'react-native-klarna-inapp-sdk';

export default function SignInScreen() {
  const isDarkMode = useColorScheme() === 'dark';

  const [clientId, setClientId] = useState('');
  const [scope, setScope] = useState('');
  const [market, setMarket] = useState('');
  const [locale, setLocale] = useState('');
  const [tokenizationId, setTokenizationId] = useState('');
  const [isButtonEnabled, setIsButtonEnabled] = useState(false);

  useEffect(() => {
    setIsButtonEnabled(
      clientId !== '' &&
        scope !== '' &&
        market !== '' &&
        locale !== '' &&
        tokenizationId !== '',
    );
  }, [clientId, scope, market, locale, tokenizationId]);

  const renderTextField = (
    label: string,
    value: string,
    setValue: (text: string) => void,
  ) => {
    return (
      <View style={{marginBottom: 20, width: '80%'}}>
        <Text style={styles.title}>{label}</Text>
        <TextInput
          style={styles.tokenInput}
          value={value}
          placeholder={`Enter ${label}`}
          onChangeText={setValue}
          {...testProps(`${label}Input`)}
        />
      </View>
    );
  };

  return (
    <ScrollView
      contentInsetAdjustmentBehavior="automatic"
      style={backgroundStyle(styles.scrollView, isDarkMode)}>
      <View style={styles.container}>
        {renderTextField('Client ID', clientId, setClientId)}
        {renderTextField('Scope', scope, setScope)}
        {renderTextField('Market', market, setMarket)}
        {renderTextField('Locale', locale, setLocale)}
        {renderTextField('Tokenization ID', tokenizationId, setTokenizationId)}
      </View>
      <View style={styles.buttonsContainer}>
        <Button
          title="Sign In"
          onPress={() => {
            if (isButtonEnabled) {
              RNKlarnaSignIn.signIn(
                clientId,
                scope,
                market,
                locale,
                tokenizationId,
              );
              console.log(
                'Klarna sign in with KlarnaMobileSDK should start now on the native side',
              );
            }
          }}
        />
      </View>
    </ScrollView>
  );
}

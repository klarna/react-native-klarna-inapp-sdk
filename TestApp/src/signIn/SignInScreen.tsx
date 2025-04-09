import React, {useState} from 'react';
import {ScrollView, TextInput, Text, useColorScheme, View} from 'react-native';
import styles, {backgroundStyle} from '../common/ui/Styles';
import testProps from '../common/util/TestProps';
import Button from '../common/ui/view/Button.tsx';
import {KlarnaSignIn} from 'react-native-klarna-inapp-sdk';
import {KlarnaEnvironment} from '../../../src/types/common/KlarnaEnvironment.ts';
import {KlarnaRegion} from '../../../src/types/common/KlarnaRegion.ts';

export default function SignInScreen() {
  const isDarkMode = useColorScheme() === 'dark';

  const [clientId, setClientId] = useState('');
  const [scope, setScope] = useState('');
  const [market, setMarket] = useState('');
  const [locale, setLocale] = useState('');
  const [tokenizationId, setTokenizationId] = useState('');
  const [event, setEvent] = useState<string>();
  const [klarnaSignIn, setKlarnaSignIn] = useState<KlarnaSignIn | null>(null);

  const onEvent = (...params: Array<string | boolean | null>) => {
    console.log('onEvent', params);
    setEvent(prevState =>
      prevState
        ? `${prevState} ${params.join('\n ----- \n')}`
        : params.join('\n ----- \n'),
    );
  };

  const renderTextField = (
    label: string,
    value: string,
    setValue: (text: string) => void,
  ) => {
    return (
      <View style={styles.signInTextFieldStyle}>
        <Text style={styles.title}>{label}</Text>
        <TextInput
          autoCapitalize="none"
          style={styles.tokenInput}
          value={value}
          placeholder={`Enter ${label}`}
          onChangeText={setValue}
          {...testProps(`${label}Input`)}
        />
      </View>
    );
  };

  function handleSignIn() {
    klarnaSignIn
      ?.signIn(clientId, scope, market, locale, tokenizationId)
      .then(r => {
        console.log('Sign in success with result: ', r);
        onEvent('Sign in success with result: ', JSON.stringify(r));
      })
      .catch(e => {
        console.error('Sign in failed with error: ', e);
        onEvent('Sign in failed with error: ', JSON.stringify(e));
      });
  }

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
          title="Initialise KlarnaSignIn"
          onPress={() => {
            if (klarnaSignIn != null) {
              console.log(
                'Disposing of previous KlarnaSignIn instance: ',
                klarnaSignIn?.instanceId,
              );
              klarnaSignIn?.dispose();
            }
            KlarnaSignIn.createInstance({
              environment: KlarnaEnvironment.Staging,
              region: KlarnaRegion.EU,
              returnUrl: 'in-app-test://siwk',
            })
              .then(instance => {
                console.log('KlarnaSignIn instance created: ', instance);
                setKlarnaSignIn(instance);
                setEvent(
                  _ =>
                    'KlarnaSignIn instance created: ' +
                    JSON.stringify(instance),
                );
              })
              .catch(e => {
                console.error('KlarnaSignIn instance creation failed: ', e);
                setEvent(
                  _ =>
                    'KlarnaSignIn instance creation failed: ' +
                    JSON.stringify(e),
                );
              });
          }}
        />
      </View>
      <View style={styles.buttonsContainer}>
        <Button
          title="Sign In"
          onPress={() => {
            console.log(
              'Klarna sign in with KlarnaMobileSDK should start now on the native side',
            );
            handleSignIn();
          }}
        />
      </View>
      <Text style={styles.title}>"Events Log"</Text>
      <Text style={styles.title}>{event}</Text>
    </ScrollView>
  );
}

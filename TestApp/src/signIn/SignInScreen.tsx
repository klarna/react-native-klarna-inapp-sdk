import React, { useState } from 'react';
import {
  ScrollView,
  TextInput,
  Text,
  useColorScheme,
  View,
} from 'react-native';
import styles, { backgroundStyle } from '../common/ui/Styles';
import testProps from '../common/util/TestProps';
import Button from '../common/ui/view/Button.tsx';
import { KlarnaSignInSDK } from 'react-native-klarna-inapp-sdk';
import { KlarnaEnvironment } from '../../../src/types/common/KlarnaEnvironment.ts';
import { KlarnaRegion } from '../../../src/types/common/KlarnaRegion.ts';

export default function SignInScreen() {
  const isDarkMode = useColorScheme() === 'dark';

  const [clientId, setClientId] = useState('');
  const [scope, setScope] = useState('');
  const [market, setMarket] = useState('');
  const [locale, setLocale] = useState('');
  const [tokenizationId, setTokenizationId] = useState('');
  const [event, setEvent] = useState<string>();
  const [klarnaSignIn, setKlarnaSignIn] = useState<KlarnaSignInSDK | null>(
    null,
  );

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
        switch (r.action) {
          case 'KlarnaSignInUserCancelled':
            console.log('User cancelled sign in', JSON.stringify(r, null, 2));
            onEvent('User cancelled sign in', JSON.stringify(r, null, 2));
            break;
          case 'KlarnaSignInToken':
            console.log(
              'Token params received: ',
              JSON.stringify(r.params, null, 2),
            );
            onEvent(
              'Token received: ',
              JSON.stringify(r.params?.KlarnaSignInToken.access_token, null, 2),
            );
            break;
          default:
            console.log(
              'Sign in event received: ',
              JSON.stringify(r.params, null, 2),
            );
            onEvent('Sign in event received: ', JSON.stringify(r, null, 2));
            break;
        }
      })
      .catch(e => {
        console.error('Sign in failed with error: ', e);
        onEvent('Sign in failed with error: ', JSON.stringify(e, null, 2));
      });
  }

  return (
    <ScrollView
      contentInsetAdjustmentBehavior="automatic"
      style={backgroundStyle(styles.scrollView, isDarkMode)}
    >
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
            KlarnaSignInSDK.createInstance({
              environment: KlarnaEnvironment.Playground,
              region: KlarnaRegion.EU,
              returnUrl: 'in-app-test://siwk',
            })
              .then(instance => {
                console.log('KlarnaSignIn instance created: ', instance);
                setKlarnaSignIn(instance);
                setEvent(
                  _ =>
                    'KlarnaSignIn instance created: ' +
                    JSON.stringify(instance, null, 2) +
                    '\n',
                );
              })
              .catch(e => {
                console.error(
                  'KlarnaSignIn instance creation failed: ',
                  JSON.stringify(e, null, 2),
                );
                setEvent(
                  _ =>
                    'KlarnaSignIn instance creation failed: ' +
                    JSON.stringify(e, null, 2) +
                    '\n',
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

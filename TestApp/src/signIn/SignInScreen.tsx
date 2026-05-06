import React, { useState } from 'react';
import { ScrollView, Text, useColorScheme, View } from 'react-native';
import styles, { backgroundStyle } from '../common/ui/Styles';
import Button from '../common/ui/view/Button.tsx';
import TextField from '../common/ui/view/TextField';
import { KlarnaSignInSDK } from 'react-native-klarna-inapp-sdk';
import { KlarnaEnvironment } from 'react-native-klarna-inapp-sdk';
import { KlarnaRegion } from 'react-native-klarna-inapp-sdk';

export default function SignInScreen() {
  const isDarkMode = useColorScheme() === 'dark';

  const [clientId, setClientId] = useState('');
  const [scope, setScope] = useState('');
  const [market, setMarket] = useState('');
  const [locale, setLocale] = useState('');
  const [tokenizationId, setTokenizationId] = useState('');
  const [event, setEvent] = useState<string>();
  const [klarnaSignIn, setKlarnaSignIn] = useState<KlarnaSignInSDK | null>(
    null
  );

  const onEvent = (...params: Array<string | boolean | null>) => {
    console.log('onEvent', params);
    setEvent((prevState) =>
      prevState
        ? `${prevState} ${params.join('\n ----- \n')}`
        : params.join('\n ----- \n')
    );
  };

  function handleSignIn() {
    klarnaSignIn
      ?.signIn(clientId, scope, market, locale, tokenizationId)
      .then((r) => {
        switch (r.action) {
          case 'KlarnaSignInUserCancelled':
            console.log('User cancelled sign in', JSON.stringify(r, null, 2));
            onEvent('User cancelled sign in', JSON.stringify(r, null, 2));
            break;
          case 'KlarnaSignInToken':
            console.log(
              'Token params received: ',
              JSON.stringify(r.params, null, 2)
            );
            onEvent(
              'Token received: ',
              JSON.stringify(r.params?.KlarnaSignInToken.access_token, null, 2)
            );
            break;
          default:
            console.log(
              'Sign in event received: ',
              JSON.stringify(r.params, null, 2)
            );
            onEvent('Sign in event received: ', JSON.stringify(r, null, 2));
            break;
        }
      })
      .catch((e) => {
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
        <TextField
          label="Client ID"
          value={clientId}
          onChangeText={setClientId}
          containerStyle={styles.signInTextFieldStyle}
          placeholder="Enter Client ID"
          testID="Client IDInput"
        />
        <TextField
          label="Scope"
          value={scope}
          onChangeText={setScope}
          containerStyle={styles.signInTextFieldStyle}
          placeholder="Enter Scope"
          testID="ScopeInput"
        />
        <TextField
          label="Market"
          value={market}
          onChangeText={setMarket}
          containerStyle={styles.signInTextFieldStyle}
          placeholder="Enter Market"
          testID="MarketInput"
        />
        <TextField
          label="Locale"
          value={locale}
          onChangeText={setLocale}
          containerStyle={styles.signInTextFieldStyle}
          placeholder="Enter Locale"
          testID="LocaleInput"
        />
        <TextField
          label="Tokenization ID"
          value={tokenizationId}
          onChangeText={setTokenizationId}
          containerStyle={styles.signInTextFieldStyle}
          placeholder="Enter Tokenization ID"
          testID="Tokenization IDInput"
        />
      </View>
      <View style={styles.buttonsContainer}>
        <Button
          title="Initialise KlarnaSignIn"
          onPress={() => {
            if (klarnaSignIn != null) {
              console.log(
                'Disposing of previous KlarnaSignIn instance: ',
                klarnaSignIn?.instanceId
              );
              klarnaSignIn?.dispose();
            }
            KlarnaSignInSDK.createInstance({
              environment: KlarnaEnvironment.Playground,
              region: KlarnaRegion.EU,
              returnUrl: 'in-app-test://siwk',
            })
              .then((instance) => {
                console.log('KlarnaSignIn instance created: ', instance);
                setKlarnaSignIn(instance);
                setEvent(
                  (_) =>
                    'KlarnaSignIn instance created: ' +
                    JSON.stringify(instance, null, 2) +
                    '\n'
                );
              })
              .catch((e) => {
                console.error(
                  'KlarnaSignIn instance creation failed: ',
                  JSON.stringify(e, null, 2)
                );
                setEvent(
                  (_) =>
                    'KlarnaSignIn instance creation failed: ' +
                    JSON.stringify(e, null, 2) +
                    '\n'
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
              'Klarna sign in with KlarnaMobileSDK should start now on the native side'
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

import React, { useState } from 'react';
import {
  ScrollView,
  StyleSheet,
  Switch,
  Text,
  TextInput,
  useColorScheme,
  View,
} from 'react-native';
import {
  KlarnaExpressCheckoutView,
  KlarnaEnvironment,
  KlarnaRegion,
  KlarnaButtonTheme,
  KlarnaButtonShape,
  KlarnaButtonStyle,
} from 'react-native-klarna-inapp-sdk';
import styles, { backgroundStyle, Colors } from '../common/ui/Styles';
import Button from '../common/ui/view/Button';
import OptionPicker from '../common/ui/view/OptionPicker';
import testProps from '../common/util/TestProps';

export default function KlarnaExpressCheckoutScreen(): React.JSX.Element {
  const isDarkMode = useColorScheme() === 'dark';

  // Session
  type SessionType = 'clientSideSession' | 'serverSideSession';
  const [sessionType, setSessionType] =
    useState<SessionType>('clientSideSession');
  const [clientId, setClientId] = useState('');
  const [clientToken, setClientToken] = useState('');
  const [locale, setLocale] = useState('en-US');
  const [returnUrl, setReturnUrl] = useState('test-app://klarna');
  const [sessionData, setSessionData] = useState('');

  // Options
  const [autoFinalize, setAutoFinalize] = useState(true);
  const [collectShippingAddress, setCollectShippingAddress] = useState(false);

  // Style
  const [theme, setTheme] = useState<KlarnaButtonTheme>(KlarnaButtonTheme.Dark);
  const [shape, setShape] = useState<KlarnaButtonShape>(
    KlarnaButtonShape.RoundedRect
  );
  const [buttonStyle, setButtonStyle] = useState<KlarnaButtonStyle>(
    KlarnaButtonStyle.Filled
  );

  // Environment
  const [environment, setEnvironment] = useState<KlarnaEnvironment>(
    KlarnaEnvironment.Playground
  );
  const [region, setRegion] = useState<KlarnaRegion>(KlarnaRegion.EU);

  // Button visibility
  const [showButton, setShowButton] = useState(false);
  const [buttonKey, setButtonKey] = useState(0);

  // Events
  const [events, setEvents] = useState<string[]>([]);

  // Response client token
  const [responseClientToken, setResponseClientToken] = useState<string | null>(
    null
  );

  const addEvent = (event: string) => {
    console.log('KEC Event:', event);
    setEvents((prev) => {
      const updated = [...prev, `[${prev.length + 1}] ${event}`];
      return updated;
    });
  };

  const createButton = () => {
    setButtonKey((prev) => prev + 1);
    setShowButton(true);
    setEvents([]);
    setResponseClientToken(null);
  };

  return (
    <ScrollView
      contentInsetAdjustmentBehavior="automatic"
      style={backgroundStyle(styles.scrollView, isDarkMode)}
    >
      <View style={styles.screenContent}>
        {/* Session Type */}
        <OptionPicker
          label="Session Type"
          options={['clientSideSession', 'serverSideSession'] as SessionType[]}
          selected={sessionType}
          onSelect={setSessionType}
        />

        {/* Client ID / Token input */}
        {sessionType === 'clientSideSession' ? (
          <View>
            <Text style={styles.title}>Client ID</Text>
            <TextInput
              style={styles.tokenInput}
              value={clientId}
              onChangeText={setClientId}
              placeholder="Client ID"
              {...testProps('clientIdInput')}
            />
          </View>
        ) : (
          <View>
            <Text style={styles.title}>Client Token</Text>
            <TextInput
              style={styles.tokenInput}
              value={clientToken}
              onChangeText={setClientToken}
              placeholder="Client Token"
              {...testProps('clientTokenInput')}
            />
          </View>
        )}

        {/* Locale */}
        <View>
          <Text style={styles.title}>Locale</Text>
          <TextInput
            style={styles.tokenInput}
            value={locale}
            onChangeText={setLocale}
            placeholder="e.g. en-US"
            {...testProps('localeInput')}
          />
        </View>

        {/* Return URL */}
        <View>
          <Text style={styles.title}>Return URL</Text>
          <TextInput
            style={styles.tokenInput}
            value={returnUrl}
            onChangeText={setReturnUrl}
            placeholder="e.g. https://klarna.com"
            {...testProps('returnUrlInput')}
          />
        </View>

        {/* Session Data */}
        <View>
          <Text style={styles.title}>Session Data</Text>
          <TextInput
            style={styles.tokenInput}
            value={sessionData}
            onChangeText={setSessionData}
            placeholder="Optional session data"
            {...testProps('sessionDataInput')}
          />
        </View>

        {/* Auto Finalize */}
        <View style={styles.switchRow}>
          <Text style={styles.title}>Auto Finalize</Text>
          <Switch value={autoFinalize} onValueChange={setAutoFinalize} />
        </View>

        {/* Collect Shipping Address */}
        <View style={styles.switchRow}>
          <Text style={styles.title}>Collect Shipping Address</Text>
          <Switch
            value={collectShippingAddress}
            onValueChange={setCollectShippingAddress}
          />
        </View>

        {/* Style Options */}
        <OptionPicker
          label="Theme"
          options={Object.values(KlarnaButtonTheme)}
          selected={theme}
          onSelect={setTheme}
        />

        <OptionPicker
          label="Shape"
          options={Object.values(KlarnaButtonShape)}
          selected={shape}
          onSelect={setShape}
        />

        <OptionPicker
          label="Button Style"
          options={Object.values(KlarnaButtonStyle)}
          selected={buttonStyle}
          onSelect={setButtonStyle}
        />

        {/* Environment & Region */}
        <OptionPicker
          label="Environment"
          options={Object.values(KlarnaEnvironment)}
          selected={environment}
          onSelect={setEnvironment}
        />

        <OptionPicker
          label="Region"
          options={Object.values(KlarnaRegion)}
          selected={region}
          onSelect={setRegion}
        />

        {/* Create Button */}
        <View
          style={styles.buttonsContainer}
          {...testProps('createExpressCheckoutButton')}
        >
          <Button
            onPress={createButton}
            title="Create Express Checkout Button"
          />
        </View>

        {/* Express Checkout Button */}
        {showButton && (
          <View style={localStyles.buttonContainer}>
            <KlarnaExpressCheckoutView
              key={buttonKey}
              sessionOptions={
                sessionType === 'clientSideSession'
                  ? { clientId }
                  : { clientToken }
              }
              locale={locale}
              environment={environment}
              region={region}
              returnUrl={returnUrl}
              theme={theme}
              shape={shape}
              buttonStyle={buttonStyle}
              autoFinalize={autoFinalize}
              collectShippingAddress={collectShippingAddress}
              sessionData={sessionData}
              onAuthorized={(response) => {
                addEvent(`onAuthorized: ${JSON.stringify(response, null, 2)}`);
                setResponseClientToken(response.clientToken || null);
              }}
              onError={(error) => {
                addEvent(`onError: ${error.name}: ${error.message}`);
                setResponseClientToken(null);
              }}
            />
          </View>
        )}

        {/* Events Log */}
        <Text style={styles.title}>
          Events {events.length > 0 ? `(${events.length})` : ''}
        </Text>
        <Text
          style={localStyles.eventLog}
          selectable
          {...testProps('state_events')}
        >
          {events.length > 0 ? events.join('\n\n') : 'No events yet.'}
        </Text>

        {/* Response Client Token */}
        {responseClientToken != null && (
          <View style={styles.card}>
            <Text style={styles.title}>Response Client Token</Text>
            <Text style={localStyles.responseTokenText} selectable>
              {responseClientToken}
            </Text>
          </View>
        )}
      </View>
    </ScrollView>
  );
}

const localStyles = StyleSheet.create({
  buttonContainer: {
    minHeight: 68,
  },
  responseTokenText: {
    fontSize: 12,
    color: Colors.dark,
    marginBottom: 8,
    fontFamily: 'monospace',
  },
  eventLog: {
    fontSize: 12,
    color: Colors.dark,
    padding: 10,
    backgroundColor: Colors.lighter,
    borderRadius: 6,
    minHeight: 40,
    marginBottom: 20,
  },
});

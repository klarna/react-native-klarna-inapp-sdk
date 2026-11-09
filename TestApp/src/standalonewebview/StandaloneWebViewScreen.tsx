import { Keyboard, ScrollView, Text, TextInput, View } from 'react-native';
import styles, { useTheme } from '../common/ui/Styles';
import React, { useRef, useState } from 'react';
import {
  KlarnaStandaloneWebView,
  KlarnaTheme,
  KlarnaWebViewKlarnaMessageEvent,
  KlarnaWebViewError,
  KlarnaWebViewNavigationEvent,
  KlarnaWebViewProgressEvent,
  KlarnaWebViewRenderProcessGoneEvent,
  KlarnaWebViewSource,
} from 'react-native-klarna-inapp-sdk';
import Button from '../common/ui/view/Button';
import OptionPicker from '../common/ui/view/OptionPicker';
import testProps from '../common/util/TestProps.tsx';

type ThemeOption = KlarnaTheme | 'unset';
const themeOptions: ThemeOption[] = ['unset', ...Object.values(KlarnaTheme)];

export default function StandaloneWebViewScreen() {
  const theme = useTheme();
  const klarnaStandaloneWebViewRef = useRef<KlarnaStandaloneWebView>(null);
  const [url, setUrl] = useState('');
  const [eventState, setEventState] = useState<string>();
  const [source, setSource] = useState<KlarnaWebViewSource>();
  const sourceLoads = useRef(0);
  const [webViewTheme, setWebViewTheme] = useState<ThemeOption>('unset');

  const logEvent = (...params: Array<string>) => {
    console.log('onEvent', params);
    setEventState(params.join(', '));
  };

  const renderUrlTextInput = () => {
    return (
      <TextInput
        keyboardType={`url`}
        autoCorrect={false}
        autoCapitalize={'none'}
        numberOfLines={1}
        style={[styles.urlInput, { color: theme.text }]}
        placeholderTextColor={theme.placeholder}
        defaultValue={url}
        placeholder="Enter the URL to load..."
        onChangeText={(text) => {
          setUrl(text);
        }}
      />
    );
  };

  const renderLoadUrlButton = () => {
    return (
      <View>
        <Button
          onPress={() => {
            if (url !== '') {
              klarnaStandaloneWebViewRef.current?.load(url);
            }
            Keyboard.dismiss();
          }}
          title="Load URL"
        />
      </View>
    );
  };

  // Loads through the source prop and echoes the request; the Cookie header is stored as cookies first.
  const renderLoadWithSourceButton = () => {
    return (
      <View>
        <Button
          onPress={() => {
            sourceLoads.current += 1;
            setSource({
              uri: url !== '' ? url : 'https://httpbin.org/anything',
              headers: {
                'Cookie': 'testapp_source=1',
                'X-TestApp-Load': String(sourceLoads.current),
              },
            });
            Keyboard.dismiss();
          }}
          title="Load With Source"
        />
      </View>
    );
  };

  const renderGoBackButton = () => {
    return (
      <View>
        <Button
          onPress={() => {
            klarnaStandaloneWebViewRef.current?.goBack();
          }}
          title="Go Back"
        />
      </View>
    );
  };

  const renderGoForwardButton = () => {
    return (
      <View>
        <Button
          onPress={() => {
            klarnaStandaloneWebViewRef.current?.goForward();
          }}
          title="Go Forward"
        />
      </View>
    );
  };

  const renderReloadButton = () => {
    return (
      <View>
        <Button
          onPress={() => {
            klarnaStandaloneWebViewRef.current?.reload();
          }}
          title="Reload"
        />
      </View>
    );
  };

  const renderStopLoadingButton = () => {
    return (
      <View>
        <Button
          onPress={() => {
            klarnaStandaloneWebViewRef.current?.stopLoading();
          }}
          title="Stop Loading"
        />
      </View>
    );
  };

  const renderThemePicker = () => {
    return (
      <View style={styles.screenContent}>
        <OptionPicker
          label="Theme"
          options={themeOptions}
          selected={webViewTheme}
          onSelect={setWebViewTheme}
        />
      </View>
    );
  };

  return (
    <View
      /* eslint-disable-next-line react-native/no-inline-styles */
      style={{
        flex: 1,
        backgroundColor: theme.background,
      }}
    >
      <View
        /* eslint-disable-next-line react-native/no-inline-styles */
        style={{
          flexDirection: 'row',
          width: '100%',
          alignItems: 'center',
          padding: 10,
        }}
      >
        {renderUrlTextInput()}
      </View>
      <View style={styles.buttonsContainer}>
        {renderLoadUrlButton()}
        {renderLoadWithSourceButton()}
        {renderReloadButton()}
        {renderStopLoadingButton()}
        {renderGoBackButton()}
        {renderGoForwardButton()}
      </View>
      {renderThemePicker()}
      <ScrollView style={styles.eventsContainer}>
        <Text style={{ color: theme.text }} {...testProps('state_events')}>
          {eventState}
        </Text>
      </ScrollView>
      <KlarnaStandaloneWebView
        ref={klarnaStandaloneWebViewRef}
        /* eslint-disable-next-line react-native/no-inline-styles */
        style={{
          flex: 1,
        }}
        returnUrl={'returnUrl://'}
        source={source}
        theme={webViewTheme === 'unset' ? undefined : webViewTheme}
        onLoadStart={(event: KlarnaWebViewNavigationEvent) => {
          logEvent('onLoadStart', JSON.stringify(event));
        }}
        onLoadEnd={(event: KlarnaWebViewNavigationEvent) => {
          logEvent('onLoadEnd', JSON.stringify(event));
        }}
        onError={(event: KlarnaWebViewError) => {
          logEvent('onError', JSON.stringify(event));
        }}
        onLoadProgress={(event: KlarnaWebViewProgressEvent) => {
          logEvent('onLoadProgress', JSON.stringify(event));
        }}
        onKlarnaMessage={(event: KlarnaWebViewKlarnaMessageEvent) => {
          logEvent('onKlarnaMessage', JSON.stringify(event));
        }}
        onRenderProcessGone={(event: KlarnaWebViewRenderProcessGoneEvent) => {
          logEvent('onRenderProcessGone', JSON.stringify(event));
        }}
      />
    </View>
  );
}

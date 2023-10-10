import {Keyboard, TextInput, View} from 'react-native';
import styles from '../common/ui/Styles';
import React, {useRef, useState} from 'react';
import {
  KlarnaStandaloneWebView,
  KlarnaWebViewNavigationEvent,
} from 'react-native-klarna-inapp-sdk';
import Button from '../common/ui/view/Button';

// TODO pass the required props
export default function StandaloneWebViewScreen() {
  const klarnaStandaloneWebViewRef = useRef<KlarnaStandaloneWebView>(null);
  const [url, setUrl] = useState('');

  const onEvent = (...params: Array<string>) => {
    console.log('onEvent', params);
  };

  const renderUrlTextInput = () => {
    return (
      <TextInput
        keyboardType={`url`}
        autoCorrect={false}
        autoCapitalize={'none'}
        style={styles.urlInput}
        defaultValue={url}
        placeholder="Enter the URL to load..."
        onChangeText={text => {
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

  return (
    <View>
      <View
        /* eslint-disable-next-line react-native/no-inline-styles */
        style={{
          flexDirection: 'row',
          width: '100%',
          alignItems: 'center',
          padding: 10,
        }}>
        {renderUrlTextInput()}
      </View>
      <View style={styles.buttonsContainer}>
        {renderLoadUrlButton()}
        {renderReloadButton()}
        {renderGoBackButton()}
        {renderGoForwardButton()}
      </View>
      <KlarnaStandaloneWebView
        ref={klarnaStandaloneWebViewRef}
        /* eslint-disable-next-line react-native/no-inline-styles */
        style={{
          width: '100%',
          height: '100%',
        }}
        returnUrl={'returnUrl://'}
        onBeforeLoad={(event: KlarnaWebViewNavigationEvent) => {
          onEvent('onBeforeLoad', JSON.stringify(event));
        }}
        onLoad={(event: KlarnaWebViewNavigationEvent) => {
          onEvent('onLoad', JSON.stringify(event));
        }}
      />
    </View>
  );
}

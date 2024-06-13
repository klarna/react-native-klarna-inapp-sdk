import {Keyboard, TextInput, View} from 'react-native';
import styles from '../common/ui/Styles';
import React, {useRef, useState} from 'react';
import {
  KlarnaStandaloneWebView,
  KlarnaWebViewKlarnaMessageEvent,
  KlarnaWebViewError,
  KlarnaWebViewNavigationEvent,
  KlarnaWebViewProgressEvent,
  KlarnaWebViewRenderProcessGoneEvent,
} from 'react-native-klarna-inapp-sdk';
import Button from '../common/ui/view/Button';

export default function StandaloneWebViewScreen() {
  const klarnaStandaloneWebViewRef = useRef<KlarnaStandaloneWebView>(null);
  const [url, setUrl] = useState('');

  const logEvent = (...params: Array<string>) => {
    console.log('onEvent', params);
  };

  const renderUrlTextInput = () => {
    return (
      <TextInput
        keyboardType={`url`}
        autoCorrect={false}
        autoCapitalize={'none'}
        numberOfLines={1}
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
    <View
      /* eslint-disable-next-line react-native/no-inline-styles */
      style={{
        flex: 1,
      }}>
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
          flex: 1,
        }}
        returnUrl={'returnUrl://'}
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

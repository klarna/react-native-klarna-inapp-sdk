import {Keyboard, Text, TextInput, View} from 'react-native';
import styles from '../common/ui/Styles';
import React, {useRef, useState} from 'react';
import {
  KlarnaStandaloneWebView,
  KlarnaWebViewError,
  KlarnaWebViewKlarnaMessageEvent,
  KlarnaWebViewNavigationEvent,
  KlarnaWebViewProgressEvent,
  KlarnaWebViewRenderProcessGoneEvent,
} from 'react-native-klarna-inapp-sdk';
import Button from '../common/ui/view/Button';

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
      <View style={{flex: 1}}>
        <Text
          style={{
            position: 'absolute',
            width: '100%',
            height: '100%',
            textAlign: 'center',
            backgroundColor: 'lightblue',
            color: 'black',
            fontSize: 20,
            textAlignVertical: 'center',
          }}>
          On top of this blue area there is a StandaloneWebView with transparent
          background!
        </Text>
        <KlarnaStandaloneWebView
          ref={klarnaStandaloneWebViewRef}
          /* eslint-disable-next-line react-native/no-inline-styles */
          style={{
            position: 'absolute',
            width: '100%',
            height: '100%',
            backgroundColor: 'transparent',
          }}
          returnUrl={'returnUrl://'}
          onLoadStart={(event: KlarnaWebViewNavigationEvent) => {
            onEvent('onLoadStart', JSON.stringify(event));
          }}
          onLoadEnd={(event: KlarnaWebViewNavigationEvent) => {
            onEvent('onLoadEnd', JSON.stringify(event));
          }}
          onError={(event: KlarnaWebViewError) => {
            onEvent('onError', JSON.stringify(event));
          }}
          onLoadProgress={(event: KlarnaWebViewProgressEvent) => {
            onEvent('onLoadProgress', JSON.stringify(event));
          }}
          onKlarnaMessage={(event: KlarnaWebViewKlarnaMessageEvent) => {
            onEvent('onKlarnaMessage', JSON.stringify(event));
          }}
          onRenderProcessGone={(event: KlarnaWebViewRenderProcessGoneEvent) => {
            onEvent('onRenderProcessGone', JSON.stringify(event));
          }}
        />
      </View>
    </View>
  );
}

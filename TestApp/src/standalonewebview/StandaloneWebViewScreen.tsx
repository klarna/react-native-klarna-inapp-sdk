import {Keyboard, TextInput, useColorScheme, View} from 'react-native';
import styles, {Colors} from '../common/ui/Styles';
import React, {useRef, useState} from 'react';
import {KlarnaStandaloneWebView} from 'react-native-klarna-inapp-sdk';
import Button from '../common/ui/view/Button';

// TODO pass the required props
export default function StandaloneWebViewScreen() {
  const klarnaStandaloneWebViewRef = useRef<KlarnaStandaloneWebView>(null);
  const isDarkMode = useColorScheme() === 'dark';
  const [url, setUrl] = useState('');

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

  return (
    <View
      style={{
        backgroundColor: isDarkMode ? Colors.black : Colors.white,
      }}>
      <View
        /* eslint-disable-next-line react-native/no-inline-styles */
        style={{
          flexDirection: 'row',
          width: '100%',
          alignItems: 'center',
          padding: 10,
          backgroundColor: Colors.lightGray,
        }}>
        {renderUrlTextInput()}
        {renderLoadUrlButton()}
      </View>
      <KlarnaStandaloneWebView
        ref={klarnaStandaloneWebViewRef}
        /* eslint-disable-next-line react-native/no-inline-styles */
        style={{
          width: '100%',
          height: '100%',
        }}
        returnUrl={'returnUrl://'}
        onBeforeLoad={() => {
          console.log('onEvent: onBeforeLoad');
        }}
        onLoad={() => {
          console.log('onEvent: onLoad');
        }}
      />
    </View>
  );
}

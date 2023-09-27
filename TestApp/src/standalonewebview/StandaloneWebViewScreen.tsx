import { useColorScheme, View } from 'react-native';
import { Colors } from '../common/ui/Styles';
import React from 'react';
import { KlarnaStandaloneWebView } from 'react-native-klarna-inapp-sdk';

// TODO pass the required props
export default function StandaloneWebViewScreen() {

  const isDarkMode = useColorScheme() === 'dark';

  return (
    <View
      style={{
        backgroundColor: isDarkMode ? Colors.black : Colors.white,
      }}>
      <KlarnaStandaloneWebView
        style={{
          width: '100%',
          height: '100%',
        }}
        returnUrl={'returnUrl://'}
      />
    </View>
  );

}

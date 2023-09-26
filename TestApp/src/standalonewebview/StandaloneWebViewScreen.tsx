import { ScrollView, useColorScheme, View } from 'react-native';
import styles, { backgroundStyle, Colors } from '../common/ui/Styles';
import React from 'react';
import { KlarnaStandaloneWebView } from 'react-native-klarna-inapp-sdk';

// TODO pass the required props
export default function StandaloneWebViewScreen() {

  const isDarkMode = useColorScheme() === 'dark';

  return (
    <ScrollView
      contentInsetAdjustmentBehavior='automatic'
      style={backgroundStyle(styles.scrollView, isDarkMode)}>
      <View
        style={{
          backgroundColor: isDarkMode ? Colors.black : Colors.white,
        }}>
        <KlarnaStandaloneWebView
          style={styles.container}
          returnUrl={"returnUrl://"}
        />
      </View>
    </ScrollView>
  );

}

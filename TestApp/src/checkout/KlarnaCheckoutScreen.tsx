import {SafeAreaView, useColorScheme} from 'react-native';
import {KlarnaCheckoutView} from 'react-native-klarna-inapp-sdk';
import React, {useRef, useState} from 'react';
import styles, {backgroundStyle} from '../common/ui/Styles';
import {useRoute} from '@react-navigation/native';

export default function KlarnaCheckoutScreen(): React.JSX.Element {
  const isDarkMode = useColorScheme() === 'dark';
  const checkoutViewRef = useRef<KlarnaCheckoutView>(null);
  const [eventState, setEventState] = useState<string>();
  const route = useRoute();
  const snippet = route.params?.snippet;
  const [timeoutExecuted, setTimeoutExecuted] = useState(false);

  const onEvent = (...params: Array<string | boolean | null>) => {
    console.log('onEvent', params);
    setEventState(params.join(', '));
    if (!timeoutExecuted) {
      setTimeout(() => {
        checkoutViewRef.current?.setSnippet(snippet);
        setTimeoutExecuted(true);
      }, 3000);
    }
  };
  console.log('Snippet value: ', snippet);
  return (
    <SafeAreaView
      style={{...backgroundStyle(styles.container, isDarkMode), flex: 1}}>
      <KlarnaCheckoutView
        ref={checkoutViewRef}
        style={styles.componentView}
        returnUrl={'returnUrl://'}
        onEvent={klarnaProductEvent => {
          onEvent(JSON.stringify(klarnaProductEvent));
        }}
        onError={error => {
          onEvent(JSON.stringify(error));
        }}
      />
    </SafeAreaView>
  );
}

import {Keyboard, Text, TextInput, useColorScheme, View} from 'react-native';
import {KlarnaCheckoutView} from 'react-native-klarna-inapp-sdk';
import React, {useRef, useState} from 'react';
import styles, {backgroundStyle} from '../common/ui/Styles';
import Button from '../common/ui/view/Button';
import testProps from '../common/util/TestProps';

export default function KlarnaCheckoutScreen(): React.JSX.Element {
  const isDarkMode = useColorScheme() === 'dark';
  const checkoutViewRef = useRef<KlarnaCheckoutView>(null);
  const [snippet, setSnippet] = useState<string>();
  const [eventState, setEventState] = useState<string>();

  const onEvent = (...params: Array<string | boolean | null>) => {
    console.log('onEvent', params);
    setEventState(params.join(', '));
  };

  const renderSnippetInput = () => {
    return (
      <TextInput
        style={styles.tokenInput}
        defaultValue={snippet}
        placeholder="Checkout snippet here..."
        multiline={true}
        blurOnSubmit={true}
        {...testProps('snippetInput')}
        onChangeText={text => {
          setSnippet(text);
        }}
      />
    );
  };

  const renderSetSnippetButton = () => {
    return (
      <View>
        <Button
          onPress={() => {
            if (snippet !== '' && snippet !== undefined) {
              checkoutViewRef.current?.setSnippet(snippet);
            }
            Keyboard.dismiss();
          }}
          title="Set Snippet"
        />
      </View>
    );
  };

  const renderSuspendButton = () => {
    return (
      <View>
        <Button
          onPress={() => {
            checkoutViewRef.current?.suspend();
          }}
          title="Suspend"
        />
      </View>
    );
  };

  const renderResumeButton = () => {
    return (
      <View>
        <Button
          onPress={() => {
            checkoutViewRef.current?.resume();
          }}
          title="Resume"
        />
      </View>
    );
  };

  return (
    <View style={backgroundStyle(styles.column, isDarkMode)}>
      <View style={styles.columnHeader}>
        {renderSnippetInput()}
        <View style={styles.buttonsContainer}>
          {renderSetSnippetButton()}
          {renderSuspendButton()}
          {renderResumeButton()}
        </View>
        <Text {...testProps('state_events')}>{eventState}</Text>
      </View>
      <KlarnaCheckoutView
        ref={checkoutViewRef}
        style={styles.columnItemFill}
        returnUrl={'returnUrl://'}
        onEvent={klarnaProductEvent => {
          onEvent(JSON.stringify(klarnaProductEvent));
        }}
        onError={error => {
          onEvent(JSON.stringify(error));
        }}
      />
    </View>
  );
}

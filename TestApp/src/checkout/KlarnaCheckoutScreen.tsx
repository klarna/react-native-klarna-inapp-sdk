import {Keyboard, Text, TextInput, View} from 'react-native';
import {KlarnaCheckoutView} from '../../../src/KlarnaCheckoutView';
import React, {useRef, useState} from 'react';
import styles from '../common/ui/Styles';
import Button from '../common/ui/view/Button';
import testProps from '../common/util/TestProps';

export default function KlarnaCheckoutScreen(): React.JSX.Element {
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
    <View
      style={{
        flex: 1,
      }}>
      {renderSnippetInput()}
      <View style={styles.buttonsContainer}>
        {renderSetSnippetButton()}
        {renderSuspendButton()}
        {renderResumeButton()}
      </View>
      <KlarnaCheckoutView
        ref={checkoutViewRef}
        style={styles.componentView}
        returnUrl={'returnUrl://'}
        checkoutOptions={{
          merchantHandlesEPM: false,
          merchantHandlesValidationErrors: false,
        }}
        onEvent={klarnaProductEvent => {
          onEvent(
            klarnaProductEvent.action,
            JSON.stringify(klarnaProductEvent.params),
            klarnaProductEvent.sessionId,
          );
        }}
        onError={error => {
          onEvent(
            error.name,
            error.message,
            error.isFatal,
            JSON.stringify(error.params),
            error.sessionId,
          );
        }}
      />
      <Text {...testProps('state_events')}>{eventState}</Text>
    </View>
  );
}

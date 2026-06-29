import { Keyboard, ScrollView, Text, TextInput, View } from 'react-native';
import { KlarnaCheckoutView } from 'react-native-klarna-inapp-sdk';
import React, { useRef, useState } from 'react';
import styles, { useTheme } from '../common/ui/Styles';
import Button from '../common/ui/view/Button';
import testProps from '../common/util/TestProps';

export default function KlarnaCheckoutScreen(): React.JSX.Element {
  const theme = useTheme();
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
        style={[styles.tokenInput, { color: theme.text }]}
        placeholderTextColor={theme.placeholder}
        defaultValue={snippet}
        placeholder="Checkout snippet here..."
        multiline={true}
        blurOnSubmit={true}
        {...testProps('snippetInput')}
        onChangeText={(text) => {
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
    <View style={[styles.column, { backgroundColor: theme.background }]}>
      <Text style={{ color: 'orange', padding: 8, textAlign: 'center' }}>
        ⚠️ Deprecated: KCO has been migrated to Kustom Mobile SDK and is no
        longer supported in the react-native-klarna-inapp-sdk.
      </Text>
      <View style={styles.columnHeader}>
        {renderSnippetInput()}
        <View style={styles.buttonsContainer}>
          {renderSetSnippetButton()}
          {renderSuspendButton()}
          {renderResumeButton()}
        </View>
        <Text style={{ color: theme.text }} {...testProps('state_events')}>
          {eventState}
        </Text>
      </View>
      <ScrollView>
        <KlarnaCheckoutView
          ref={checkoutViewRef}
          style={styles.columnItemFill}
          returnUrl={'returnUrl://'}
          onEvent={(klarnaProductEvent) => {
            onEvent(JSON.stringify(klarnaProductEvent));
          }}
          onError={(error) => {
            onEvent(JSON.stringify(error));
          }}
        />
      </ScrollView>
    </View>
  );
}

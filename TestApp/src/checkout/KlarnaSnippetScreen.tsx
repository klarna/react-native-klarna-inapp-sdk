import {
  Keyboard,
  SafeAreaView,
  TextInput,
  useColorScheme,
  View,
} from 'react-native';
import {KlarnaCheckoutView} from 'react-native-klarna-inapp-sdk';
import React, {useRef, useState} from 'react';
import styles, {backgroundStyle, Colors} from '../common/ui/Styles';
import Button from '../common/ui/view/Button';
import testProps from '../common/util/TestProps';
import {useNavigation} from '@react-navigation/native';
import {NativeStackNavigationProp} from '@react-navigation/native-stack';
import {AppStackParamList} from '../../App.tsx';

export default function KlarnaSnippetScreen(): React.JSX.Element {
  const isDarkMode = useColorScheme() === 'dark';
  const checkoutViewRef = useRef<KlarnaCheckoutView>(null);
  const [snippet, setSnippet] = useState<string>();

  type HomeNavigationProp = NativeStackNavigationProp<
    AppStackParamList,
    'KlarnaSnippet'
  >;
  const navigation = useNavigation<HomeNavigationProp>();

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
              // @ts-ignore
              navigation.navigate('KlarnaCheckout', {snippet: snippet});
              Keyboard.dismiss();
            }
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
    <SafeAreaView
      style={{...backgroundStyle(styles.scrollView, isDarkMode), flex: 1}}>
      <View
        style={{
          backgroundColor: isDarkMode ? Colors.black : Colors.white,
        }}>
        {renderSnippetInput()}
        <View style={styles.buttonsContainer}>
          {renderSetSnippetButton()}
          {renderSuspendButton()}
          {renderResumeButton()}
        </View>
      </View>
    </SafeAreaView>
  );
}

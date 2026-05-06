import React from 'react';
import {
  StyleProp,
  StyleSheet,
  Text,
  TextInput,
  TextStyle,
  View,
  ViewStyle,
} from 'react-native';
import testProps from '../../util/TestProps';
import styles from '../Styles';

interface TextFieldProps {
  label: string;
  value: string;
  onChangeText: (text: string) => void;
  placeholder?: string;
  containerStyle?: StyleProp<ViewStyle>;
  labelStyle?: StyleProp<TextStyle>;
  testID?: string;
}

export default function TextField({
  label,
  value,
  onChangeText,
  placeholder,
  containerStyle,
  labelStyle,
  testID,
}: TextFieldProps) {
  return (
    <View style={[localStyles.container, containerStyle]}>
      <Text style={[styles.title, labelStyle]}>{label}</Text>
      <TextInput
        autoCapitalize="none"
        style={styles.tokenInput}
        value={value}
        placeholder={placeholder ?? label}
        onChangeText={onChangeText}
        {...(testID ? testProps(testID) : {})}
      />
    </View>
  );
}

const localStyles = StyleSheet.create({
  container: {
    marginBottom: 16,
  },
});

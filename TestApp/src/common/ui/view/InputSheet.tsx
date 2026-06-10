import React, { useState } from 'react';
import {
  Modal,
  Pressable,
  StyleSheet,
  Text,
  TextInput,
  View,
} from 'react-native';
import { Colors, useTheme } from '../Styles';
import Button from './Button';

type InputSheetProps = {
  visible: boolean;
  title: string;
  inputLabel: string;
  onConfirm: (value: string) => void;
  onDismiss: () => void;
};

export default function InputSheet({
  visible,
  title,
  inputLabel,
  onConfirm,
  onDismiss,
}: InputSheetProps) {
  const [value, setValue] = useState('');
  const theme = useTheme();

  function handleConfirm() {
    onConfirm(value);
    setValue('');
  }

  function handleDismiss() {
    setValue('');
    onDismiss();
  }

  return (
    <Modal
      transparent
      visible={visible}
      animationType="fade"
      onRequestClose={handleDismiss}
    >
      <Pressable style={localStyles.overlay} onPress={handleDismiss}>
        <Pressable style={[localStyles.card, { backgroundColor: theme.card }]}>
          <Text style={[localStyles.title, { color: theme.text }]}>
            {title}
          </Text>
          <TextInput
            style={[
              localStyles.input,
              { color: theme.text, backgroundColor: theme.surface },
            ]}
            placeholder={inputLabel}
            placeholderTextColor={theme.placeholder ?? Colors.lightGray}
            value={value}
            onChangeText={setValue}
            multiline
            autoCapitalize="none"
            autoCorrect={false}
          />
          <View style={localStyles.confirmButton}>
            <Button title="Confirm" onPress={handleConfirm} />
          </View>
        </Pressable>
      </Pressable>
    </Modal>
  );
}

const localStyles = StyleSheet.create({
  overlay: {
    flex: 1,
    backgroundColor: 'rgba(0,0,0,0.3)',
    justifyContent: 'center',
    alignItems: 'center',
    padding: 24,
  },
  card: {
    borderRadius: 16,
    padding: 16,
    width: '100%',
  },
  title: {
    fontSize: 17,
    fontWeight: '700',
    textAlign: 'center',
    marginBottom: 12,
  },
  input: {
    borderWidth: 1,
    borderColor: Colors.lightGray,
    borderRadius: 8,
    padding: 10,
    minHeight: 120,
    textAlignVertical: 'top',
    fontSize: 13,
    marginBottom: 12,
  },
  confirmButton: {
    marginTop: 4,
  },
});

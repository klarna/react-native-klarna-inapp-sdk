import React from 'react';
import { Modal, Pressable, StyleSheet, Text } from 'react-native';
import { useTheme } from '../Styles';

export type ActionSheetOption = {
  label: string;
  isCancel?: boolean;
  onPress: () => void;
};

type ActionSheetProps = {
  visible: boolean;
  title: string;
  subtitle?: string;
  options: ActionSheetOption[];
  onDismiss: () => void;
};

export default function ActionSheet({
  visible,
  title,
  subtitle,
  options,
  onDismiss,
}: ActionSheetProps) {
  const theme = useTheme();
  return (
    <Modal
      transparent
      visible={visible}
      animationType="fade"
      onRequestClose={onDismiss}
    >
      <Pressable style={localStyles.overlay} onPress={onDismiss}>
        <Pressable style={[localStyles.card, { backgroundColor: theme.card }]}>
          <Text style={[localStyles.title, { color: theme.text }]}>
            {title}
          </Text>
          {subtitle ? (
            <Text
              style={[
                localStyles.subtitle,
                { color: theme.placeholder ?? 'gray' },
              ]}
            >
              {subtitle}
            </Text>
          ) : null}
          {options.map((option, index) => (
            <Pressable
              key={index}
              style={[
                localStyles.optionButton,
                { backgroundColor: theme.optionItem },
              ]}
              onPress={option.onPress}
            >
              <Text
                style={[
                  localStyles.optionText,
                  { color: theme.text },
                  option.isCancel && localStyles.cancelText,
                ]}
              >
                {option.label}
              </Text>
            </Pressable>
          ))}
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
    marginBottom: 4,
  },
  subtitle: {
    fontSize: 13,
    textAlign: 'center',
    marginBottom: 12,
  },
  optionButton: {
    borderRadius: 20,
    paddingVertical: 14,
    alignItems: 'center',
    marginTop: 8,
  },
  optionText: {
    fontSize: 16,
    fontWeight: '600',
  },
  cancelText: {
    color: 'red',
    fontWeight: '400',
  },
});

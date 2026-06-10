import React from 'react';
import { Text, Pressable, StyleSheet } from 'react-native';
import styles, { useTheme } from '../Styles';

type ButtonProps = {
  onPress: () => void;
  title: string;
  disabled?: boolean;
};

export default function Button(props: ButtonProps) {
  const theme = useTheme();
  return (
    <Pressable
      style={[styles.button, props.disabled && localStyles.disabled]}
      onPress={props.onPress}
      disabled={props.disabled}
    >
      <Text style={[styles.buttonText, { color: theme.buttonText }]}>
        {props.title}
      </Text>
    </Pressable>
  );
}

const localStyles = StyleSheet.create({
  disabled: {
    opacity: 0.5,
  },
});

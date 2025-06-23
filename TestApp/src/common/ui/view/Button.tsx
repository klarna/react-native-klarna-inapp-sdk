import React from 'react';
import { Text, Pressable } from 'react-native';
import styles from '../Styles';

type ButtonProps = {
  onPress: () => void;
  title: string;
};

export default function Button(props: ButtonProps) {
  return (
    <Pressable style={styles.button} onPress={props.onPress}>
      <Text style={styles.buttonText}>{props.title}</Text>
    </Pressable>
  );
}

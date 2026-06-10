import React from 'react';
import type { StyleProp, ViewStyle } from 'react-native';
import RNKlarnaPaymentButton from './Specs/KlarnaPaymentButtonNativeComponent';

/** Visual state of the Klarna payment button. */
export type KlarnaPaymentButtonState =
  /** Default interactive state. */
  | 'default'
  /** Button is non-interactive and visually dimmed. */
  | 'disabled'
  /** Button shows a loading indicator. */
  | 'loading';

/** Intent of the Klarna payment button, determining the label and flow it initiates. */
export type KlarnaPaymentButtonIntent = 'pay' | 'subscribe' | 'addToWallet';

/** Shape of the Klarna payment button. */
export type KlarnaPaymentButtonShape =
  /** Rectangle with rounded corners. */
  | 'roundedRect'
  /** Pill shaped button. */
  | 'pill'
  /** Rectangle with square corners. */
  | 'rectangle';

/** Fill style of the Klarna payment button. */
export type KlarnaPaymentButtonStyle = 'filled' | 'outlined';

/** Color theme of the Klarna payment button. Defaults to light. */
export type KlarnaPaymentButtonTheme =
  /** Light background with dark foreground content. */
  | 'light'
  /** Dark background with light foreground content. */
  | 'dark'
  /** Automatically follows the system appearance. */
  | 'automatic';

/** Props for the KlarnaPaymentButton component. */
export interface KlarnaPaymentButtonProps {
  /** The instance ID of the KlarnaNetworkPayment session this button is associated with. */
  instanceId: string;
  /** Visual state of the button. */
  state?: KlarnaPaymentButtonState;
  /** Intent determining the button label and flow. */
  intent?: KlarnaPaymentButtonIntent;
  /** Shape of the button. */
  shape?: KlarnaPaymentButtonShape;
  /** Fill style of the button. */
  buttonStyle?: KlarnaPaymentButtonStyle;
  /** Color theme of the button. */
  theme?: KlarnaPaymentButtonTheme;
  /** Called when the button is tapped. */
  onPress?: () => void;
  /** Style applied to the button container. */
  style?: StyleProp<ViewStyle>;
}

export function KlarnaPaymentButton({
  instanceId,
  state,
  intent,
  shape,
  buttonStyle,
  theme,
  onPress,
  style,
}: KlarnaPaymentButtonProps) {
  return (
    <RNKlarnaPaymentButton
      instanceId={instanceId}
      state={state}
      intent={intent}
      shape={shape}
      buttonStyle={buttonStyle}
      theme={theme}
      onButtonPress={onPress ? () => onPress() : () => {}}
      style={style}
    />
  );
}

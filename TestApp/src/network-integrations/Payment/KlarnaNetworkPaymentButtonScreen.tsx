import { useNavigation, useRoute } from '@react-navigation/native';
import type {
  NativeStackNavigationProp,
  NativeStackScreenProps,
} from '@react-navigation/native-stack';
import React, { useCallback, useLayoutEffect, useMemo, useState } from 'react';
import {
  Alert,
  Modal,
  Pressable,
  ScrollView,
  StyleSheet,
  Text,
  TouchableOpacity,
  View,
} from 'react-native';
import type { AppStackParamList } from '../../../App';
import styles, { useTheme, Colors } from '../../common/ui/Styles';
import Button from '../../common/ui/view/Button';
import OptionPicker from '../../common/ui/view/OptionPicker';
import networkStyles from '../Styles/NetworkIntegrationStyles';
import {
  KlarnaPaymentButton,
  type KlarnaPaymentButtonIntent,
  type KlarnaPaymentButtonShape,
  type KlarnaPaymentButtonStyle,
  type KlarnaPaymentButtonTheme,
  type KlarnaPaymentButtonState,
} from '@klarna/react-native-klarna-network-payment';

type RouteProp = NativeStackScreenProps<
  AppStackParamList,
  'KlarnaNetworkPaymentButton'
>['route'];
type NavigationProp = NativeStackNavigationProp<
  AppStackParamList,
  'KlarnaNetworkPaymentButton'
>;

const INTENTS: readonly KlarnaPaymentButtonIntent[] = [
  'pay',
  'subscribe',
  'addToWallet',
];
const SHAPES: readonly KlarnaPaymentButtonShape[] = [
  'roundedRect',
  'pill',
  'rectangle',
];
const BUTTON_STYLES: readonly KlarnaPaymentButtonStyle[] = [
  'filled',
  'outlined',
];
const THEMES: readonly KlarnaPaymentButtonTheme[] = [
  'light',
  'dark',
  'automatic',
];
const STATES: readonly KlarnaPaymentButtonState[] = [
  'default',
  'disabled',
  'loading',
];

type ButtonOptions = {
  intent: KlarnaPaymentButtonIntent;
  shape: KlarnaPaymentButtonShape;
  buttonStyle: KlarnaPaymentButtonStyle;
  theme: KlarnaPaymentButtonTheme;
  state: KlarnaPaymentButtonState;
};

const DEFAULT_OPTIONS: ButtonOptions = {
  intent: 'pay',
  shape: 'roundedRect',
  buttonStyle: 'filled',
  theme: 'automatic',
  state: 'default',
};

const WIDTH_STEP = 20;
const HEIGHT_STEP = 8;

function HeaderGearButton({ onPress }: { onPress: () => void }) {
  return (
    <TouchableOpacity onPress={onPress} style={localStyles.gearButton}>
      <Text style={localStyles.gearIcon}>⚙</Text>
    </TouchableOpacity>
  );
}

export default function KlarnaNetworkPaymentButtonScreen() {
  const route = useRoute<RouteProp>();
  const navigation = useNavigation<NavigationProp>();
  const { sdk } = route.params;
  const theme = useTheme();

  const [width, setWidth] = useState(300);
  const [height, setHeight] = useState(48);
  const [applied, setApplied] = useState<ButtonOptions>(DEFAULT_OPTIONS);

  const [sheetVisible, setSheetVisible] = useState(false);
  const [draft, setDraft] = useState<ButtonOptions>(DEFAULT_OPTIONS);

  const buttonSizeStyle = useMemo(
    () => ({ width, height, alignSelf: 'center' as const }),
    [width, height]
  );

  const openSheet = useCallback(() => {
    setDraft(applied);
    setSheetVisible(true);
  }, [applied]);

  useLayoutEffect(() => {
    navigation.setOptions({
      headerRight: () => <HeaderGearButton onPress={openSheet} />,
    });
  }, [navigation, openSheet]);

  function handleConfirm() {
    setApplied(draft);
    setSheetVisible(false);
  }

  return (
    <>
      <ScrollView
        contentInsetAdjustmentBehavior="automatic"
        style={[styles.scrollView, { backgroundColor: theme.background }]}
      >
        <View style={networkStyles.container}>
          <Text style={localStyles.label}>Native Button</Text>
          <KlarnaPaymentButton
            instanceId={sdk.instanceId}
            intent={applied.intent}
            shape={applied.shape}
            buttonStyle={applied.buttonStyle}
            theme={applied.theme}
            state={applied.state}
            style={buttonSizeStyle}
            onPress={() => Alert.alert('onPress', 'KlarnaPaymentButton tapped')}
          />

          <View style={localStyles.sizeControls}>
            <View style={localStyles.sizeGroup}>
              <Text style={localStyles.sizeLabel}>Width</Text>
              <View style={localStyles.sizeRow}>
                <TouchableOpacity
                  style={localStyles.sizeBtn}
                  onPress={() => setWidth((w) => Math.max(80, w - WIDTH_STEP))}
                >
                  <Text style={localStyles.sizeBtnText}>W-</Text>
                </TouchableOpacity>
                <TouchableOpacity
                  style={localStyles.sizeBtn}
                  onPress={() => setWidth((w) => w + WIDTH_STEP)}
                >
                  <Text style={localStyles.sizeBtnText}>W+</Text>
                </TouchableOpacity>
              </View>
            </View>
            <View style={localStyles.sizeGroup}>
              <Text style={localStyles.sizeLabel}>Height</Text>
              <View style={localStyles.sizeRow}>
                <TouchableOpacity
                  style={localStyles.sizeBtn}
                  onPress={() =>
                    setHeight((h) => Math.max(32, h - HEIGHT_STEP))
                  }
                >
                  <Text style={localStyles.sizeBtnText}>H-</Text>
                </TouchableOpacity>
                <TouchableOpacity
                  style={localStyles.sizeBtn}
                  onPress={() => setHeight((h) => h + HEIGHT_STEP)}
                >
                  <Text style={localStyles.sizeBtnText}>H+</Text>
                </TouchableOpacity>
              </View>
            </View>
          </View>
        </View>
      </ScrollView>

      <Modal
        visible={sheetVisible}
        transparent
        animationType="slide"
        onRequestClose={() => setSheetVisible(false)}
      >
        <Pressable
          style={localStyles.overlay}
          onPress={() => setSheetVisible(false)}
        />
        <View style={localStyles.sheet}>
          <Text style={localStyles.sheetTitle}>Payment Button Options</Text>
          <ScrollView style={localStyles.sheetScroll}>
            <OptionPicker
              label="Button Intent"
              options={INTENTS}
              selected={draft.intent}
              onSelect={(v) => setDraft((d) => ({ ...d, intent: v }))}
            />
            <OptionPicker
              label="Button Shape"
              options={SHAPES}
              selected={draft.shape}
              onSelect={(v) => setDraft((d) => ({ ...d, shape: v }))}
            />
            <OptionPicker
              label="Button Style"
              options={BUTTON_STYLES}
              selected={draft.buttonStyle}
              onSelect={(v) => setDraft((d) => ({ ...d, buttonStyle: v }))}
            />
            <OptionPicker
              label="Button Theme"
              options={THEMES}
              selected={draft.theme}
              onSelect={(v) => setDraft((d) => ({ ...d, theme: v }))}
            />
            <OptionPicker
              label="Button State"
              options={STATES}
              selected={draft.state}
              onSelect={(v) => setDraft((d) => ({ ...d, state: v }))}
            />
          </ScrollView>
          <View style={localStyles.confirmWrapper}>
            <Button title="Confirm" onPress={handleConfirm} />
          </View>
        </View>
      </Modal>
    </>
  );
}

const localStyles = StyleSheet.create({
  label: {
    fontWeight: 'bold',
    marginBottom: 12,
    color: Colors.dark,
  },
  gearButton: {
    paddingHorizontal: 12,
    paddingVertical: 4,
  },
  gearIcon: {
    fontSize: 22,
    color: Colors.darker,
  },
  sizeControls: {
    flexDirection: 'row',
    marginTop: 24,
    gap: 24,
  },
  sizeGroup: {
    flex: 1,
  },
  sizeLabel: {
    fontWeight: '600',
    fontSize: 13,
    color: Colors.dark,
    marginBottom: 8,
  },
  sizeRow: {
    flexDirection: 'row',
    gap: 8,
  },
  sizeBtn: {
    flex: 1,
    backgroundColor: Colors.pink,
    borderRadius: 6,
    paddingVertical: 12,
    alignItems: 'center',
  },
  sizeBtnText: {
    fontWeight: '700',
    color: Colors.white,
    fontSize: 14,
  },
  overlay: {
    flex: 1,
    backgroundColor: 'rgba(0,0,0,0.3)',
  },
  sheet: {
    backgroundColor: Colors.white,
    borderTopLeftRadius: 16,
    borderTopRightRadius: 16,
    paddingTop: 20,
    paddingHorizontal: 20,
    paddingBottom: 32,
    maxHeight: '70%',
  },
  sheetTitle: {
    fontSize: 17,
    fontWeight: '700',
    color: Colors.darker,
    marginBottom: 16,
    textAlign: 'center',
  },
  sheetScroll: {
    flexGrow: 0,
  },
  confirmWrapper: {
    marginTop: 16,
  },
});

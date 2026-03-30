import {
  Keyboard,
  KeyboardAvoidingView,
  Platform,
  Pressable,
  ScrollView,
  Text,
  TextInput,
  useColorScheme,
  View,
  StyleSheet,
} from 'react-native';
import { KlarnaOSMView } from 'react-native-klarna-inapp-sdk';
import React, { useRef, useState } from 'react';
import styles, { backgroundStyle, Colors } from '../common/ui/Styles';
import Button from '../common/ui/view/Button';
import testProps from '../common/util/TestProps';

interface OSMRenderParams {
  clientId: string;
  placementKey: string;
  locale: string;
  purchaseAmount: string;
  environment: string;
  region: string;
  theme: string;
  bgColor: string;
  textColor: string;
}

const ENVIRONMENTS = ['production', 'staging', 'playground'];
const REGIONS = ['eu', 'na', 'oc'];
const THEMES = ['automatic', 'dark', 'light'];
const COLORS = ['', 'black', 'white', 'yellow', 'green', 'red'];

const COLOR_HEX: Record<string, string> = {
  '': '',
  black: '#000000',
  white: '#FFFFFF',
  yellow: '#FFFF00',
  green: '#00FF00',
  red: '#FF0000',
};

function OptionPicker({
  label,
  options,
  selected,
  onSelect,
  testId,
}: {
  label: string;
  options: string[];
  selected: string;
  onSelect: (value: string) => void;
  testId?: string;
}) {
  return (
    <View style={localStyles.pickerContainer} {...(testId ? testProps(testId) : {})}>
      <Text style={localStyles.pickerLabel}>{label}</Text>
      <View style={localStyles.optionsRow}>
        {options.map(option => {
          const isSelected = selected === option;
          const displayLabel = option === '' ? 'none' : option;
          return (
            <Pressable
              key={option}
              style={[
                localStyles.optionChip,
                isSelected && localStyles.optionChipSelected,
              ]}
              onPress={() => onSelect(option)}>
              <Text
                style={[
                  localStyles.optionChipText,
                  isSelected && localStyles.optionChipTextSelected,
                ]}>
                {displayLabel}
              </Text>
            </Pressable>
          );
        })}
      </View>
    </View>
  );
}

function ColorPicker({
  label,
  options,
  selected,
  onSelect,
  testId,
}: {
  label: string;
  options: string[];
  selected: string;
  onSelect: (value: string) => void;
  testId?: string;
}) {
  return (
    <View style={localStyles.pickerContainer} {...(testId ? testProps(testId) : {})}>
      <Text style={localStyles.pickerLabel}>{label}</Text>
      <View style={localStyles.optionsRow}>
        {options.map(option => {
          const isSelected = selected === option;
          const hex = COLOR_HEX[option];
          if (option === '') {
            return (
              <Pressable
                key="none"
                style={[
                  localStyles.optionChip,
                  isSelected && localStyles.optionChipSelected,
                ]}
                onPress={() => onSelect(option)}>
                <Text
                  style={[
                    localStyles.optionChipText,
                    isSelected && localStyles.optionChipTextSelected,
                  ]}>
                  none
                </Text>
              </Pressable>
            );
          }
          return (
            <Pressable
              key={option}
              style={[
                localStyles.colorChip,
                { backgroundColor: hex },
                isSelected && localStyles.colorChipSelected,
              ]}
              onPress={() => onSelect(option)}>
              <Text style={[
                localStyles.colorChipText,
                { color: option === 'black' || option === 'green' ? '#FFF' : '#000' },
              ]}>
                {isSelected ? option : ''}
              </Text>
            </Pressable>
          );
        })}
      </View>
    </View>
  );
}

export default function KlarnaOSMScreen(): React.JSX.Element {
  const isDarkMode = useColorScheme() === 'dark';
  const [clientId, setClientId] = useState<string>('');
  const [placementKey, setPlacementKey] = useState<string>('');
  const [locale, setLocale] = useState<string>('en-us');
  const [purchaseAmount, setPurchaseAmount] = useState<string>('');
  const [environment, setEnvironment] = useState<string>('playground');
  const [region, setRegion] = useState<string>('eu');
  const [theme, setTheme] = useState<string>('automatic');
  const [bgColor, setBgColor] = useState<string>('');
  const [textColor, setTextColor] = useState<string>('');
  const [eventState, setEventState] = useState<string>('');
  const [renderParams, setRenderParams] = useState<OSMRenderParams | null>(null);
  const renderKey = useRef(0);

  const onEvent = (...params: Array<string | boolean | null>) => {
    console.log('onEvent', params);
    setEventState(params.join(', '));
  };

  return (
    <KeyboardAvoidingView
      style={backgroundStyle(styles.column, isDarkMode)}
      behavior={Platform.OS === 'ios' ? 'padding' : undefined}>
      <ScrollView
        keyboardShouldPersistTaps="handled"
        contentContainerStyle={{paddingBottom: 60}}>
        <TextInput
          style={styles.tokenInput}
          defaultValue={clientId}
          placeholder="Client ID"
          {...testProps('clientIdInput')}
          onChangeText={text => setClientId(text)}
        />
        <TextInput
          style={styles.tokenInput}
          defaultValue={placementKey}
          placeholder="Placement Key"
          {...testProps('placementKeyInput')}
          onChangeText={text => setPlacementKey(text)}
        />
        <TextInput
          style={styles.tokenInput}
          defaultValue={locale}
          placeholder="Locale (e.g. en-us)"
          {...testProps('localeInput')}
          onChangeText={text => setLocale(text)}
        />
        <TextInput
          style={styles.tokenInput}
          defaultValue={purchaseAmount}
          placeholder="Purchase Amount (minor units)"
          keyboardType="number-pad"
          {...testProps('purchaseAmountInput')}
          onChangeText={text => setPurchaseAmount(text.replace(/[^0-9]/g, ''))}
        />
        <OptionPicker
          label="Environment"
          options={ENVIRONMENTS}
          selected={environment}
          onSelect={setEnvironment}
          testId="environmentPicker"
        />
        <OptionPicker
          label="Region"
          options={REGIONS}
          selected={region}
          onSelect={setRegion}
          testId="regionPicker"
        />
        <OptionPicker
          label="Theme"
          options={THEMES}
          selected={theme}
          onSelect={setTheme}
          testId="themePicker"
        />
        <ColorPicker
          label="Background Color"
          options={COLORS}
          selected={bgColor}
          onSelect={setBgColor}
          testId="bgColorPicker"
        />
        <ColorPicker
          label="Text Color"
          options={COLORS}
          selected={textColor}
          onSelect={setTextColor}
          testId="textColorPicker"
        />
        <View style={styles.buttonsContainer}>
          <Button
            onPress={() => {
              setEventState('');
              renderKey.current += 1;
              setRenderParams({
                clientId,
                placementKey,
                locale,
                purchaseAmount,
                environment,
                region,
                theme,
                bgColor: COLOR_HEX[bgColor] || '',
                textColor: COLOR_HEX[textColor] || '',
              });
              Keyboard.dismiss();
            }}
            title="Render"
          />
        </View>
        <Text {...testProps('state_events')}>{eventState}</Text>
        {renderParams && (
          <KlarnaOSMView
            key={renderKey.current}
            style={styles.columnItemFill}
            clientId={renderParams.clientId}
            placementKey={renderParams.placementKey}
            locale={renderParams.locale}
            purchaseAmount={renderParams.purchaseAmount}
            environment={renderParams.environment}
            region={renderParams.region}
            theme={renderParams.theme || undefined}
            backgroundColor={renderParams.bgColor || undefined}
            textColor={renderParams.textColor || undefined}
            returnUrl={'returnUrl://'}
            onError={error => {
              onEvent(JSON.stringify(error));
            }}
          />
        )}
      </ScrollView>
    </KeyboardAvoidingView>
  );
}

const localStyles = StyleSheet.create({
  pickerContainer: {
    marginHorizontal: 20,
    marginVertical: 8,
  },
  pickerLabel: {
    fontSize: 13,
    color: '#888',
    marginBottom: 6,
  },
  optionsRow: {
    flexDirection: 'row',
    flexWrap: 'wrap',
    gap: 8,
  },
  optionChip: {
    paddingVertical: 6,
    paddingHorizontal: 14,
    borderRadius: 16,
    borderWidth: 1,
    borderColor: Colors.lightGray,
    backgroundColor: Colors.white,
  },
  optionChipSelected: {
    backgroundColor: Colors.pink,
    borderColor: Colors.pink,
  },
  optionChipText: {
    fontSize: 13,
    color: Colors.dark,
  },
  optionChipTextSelected: {
    color: Colors.white,
  },
  colorChip: {
    width: 40,
    height: 40,
    borderRadius: 20,
    borderWidth: 2,
    borderColor: Colors.lightGray,
    alignItems: 'center',
    justifyContent: 'center',
  },
  colorChipSelected: {
    borderColor: Colors.pink,
    borderWidth: 3,
  },
  colorChipText: {
    fontSize: 9,
    fontWeight: 'bold',
  },
});

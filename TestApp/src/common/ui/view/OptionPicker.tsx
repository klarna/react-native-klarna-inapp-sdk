import React from 'react';
import {StyleSheet, Text, TouchableOpacity, View} from 'react-native';
import styles, {Colors} from '../Styles';

type OptionPickerProps<T extends string> = {
  label: string;
  options: readonly T[];
  selected: T;
  onSelect: (value: T) => void;
  formatOption?: (value: T) => string;
  disabled?: boolean;
};

export default function OptionPicker<T extends string>({
  label,
  options,
  selected,
  onSelect,
  formatOption,
  disabled = false,
}: OptionPickerProps<T>) {
  return (
    <View style={localStyles.optionRow}>
      <Text style={styles.title}>{label}</Text>
      <View style={localStyles.optionChips}>
        {options.map(option => {
          const isSelected = selected === option;
          return (
            <TouchableOpacity
              key={option}
              style={[
                localStyles.chip,
                isSelected && localStyles.chipSelected,
                disabled && localStyles.chipDisabled,
              ]}
              onPress={() => onSelect(option)}
              disabled={disabled}>
              <Text
                style={[
                  localStyles.chipText,
                  isSelected && localStyles.chipTextSelected,
                ]}>
                {formatOption ? formatOption(option) : option}
              </Text>
            </TouchableOpacity>
          );
        })}
      </View>
    </View>
  );
}

const localStyles = StyleSheet.create({
  optionRow: {
    marginVertical: 6,
  },
  optionChips: {
    flexDirection: 'row',
    flexWrap: 'wrap',
    gap: 6,
  },
  chip: {
    paddingVertical: 6,
    paddingHorizontal: 12,
    borderRadius: 4,
    backgroundColor: Colors.light,
  },
  chipSelected: {
    backgroundColor: Colors.pink,
  },
  chipDisabled: {
    opacity: 0.5,
  },
  chipText: {
    fontSize: 13,
    color: Colors.dark,
  },
  chipTextSelected: {
    color: Colors.white,
    fontWeight: '600',
  },
});

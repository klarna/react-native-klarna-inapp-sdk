import {Platform} from 'react-native';

export default function testProps(id: string) {
  return Platform.OS === 'android'
    ? {testID: id, accessibilityLabel: id}
    : {testID: id};
}

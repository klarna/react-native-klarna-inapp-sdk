import {Platform} from 'react-native';

const testProps = id => {
  return Platform.OS === 'android'
    ? {testID: id, accessibilityLabel: id}
    : {testID: id};
};

export default testProps;

import { useNavigation, useRoute } from '@react-navigation/native';
import type {
  NativeStackNavigationProp,
  NativeStackScreenProps,
} from '@react-navigation/native-stack';
import React, { useState } from 'react';
import { ScrollView, Text, View } from 'react-native';
import type { AppStackParamList } from '../../../App';
import styles, { useTheme } from '../../common/ui/Styles';
import ActionSheet from '../../common/ui/view/ActionSheet';
import InputSheet from '../../common/ui/view/InputSheet';

type RouteProp = NativeStackScreenProps<
  AppStackParamList,
  'KlarnaNetworkPayment'
>['route'];
type NavigationProp = NativeStackNavigationProp<
  AppStackParamList,
  'KlarnaNetworkPayment'
>;

const MENU_ITEMS = [
  'Initiate',
  'Fetch',
  'Cancel',
  'Presentation',
  'Payment Button',
] as const;

export default function KlarnaNetworkPaymentScreen() {
  const route = useRoute<RouteProp>();
  const navigation = useNavigation<NavigationProp>();
  const { sdk } = route.params;
  const theme = useTheme();
  const navItemStyle = [styles.navMenuItem, { color: theme.text }];

  const [initiateSheetVisible, setInitiateSheetVisible] = useState(false);
  const [requestDataSheetVisible, setRequestDataSheetVisible] = useState(false);
  const [jsonSheetVisible, setJsonSheetVisible] = useState(false);

  function handleMenuPress(item: (typeof MENU_ITEMS)[number]) {
    if (item === 'Initiate') {
      setInitiateSheetVisible(true);
    } else if (item === 'Fetch') {
      navigation.navigate('KlarnaNetworkPaymentRequestId', {
        sdk,
        method: 'fetch',
      });
    } else if (item === 'Cancel') {
      navigation.navigate('KlarnaNetworkPaymentRequestId', {
        sdk,
        method: 'cancel',
      });
    } else if (item === 'Presentation') {
      navigation.navigate('KlarnaNetworkPaymentPresentationData', { sdk });
    } else if (item === 'Payment Button') {
      navigation.navigate('KlarnaNetworkPaymentButton', { sdk });
    }
  }

  return (
    <>
      <ScrollView
        contentInsetAdjustmentBehavior="automatic"
        style={[styles.scrollView, { backgroundColor: theme.background }]}
      >
        <View style={{ backgroundColor: theme.background }}>
          {MENU_ITEMS.map((item) => (
            <Text
              key={item}
              style={navItemStyle}
              onPress={() => handleMenuPress(item)}
            >
              {item}
            </Text>
          ))}
        </View>
      </ScrollView>
      <ActionSheet
        visible={initiateSheetVisible}
        title="Initiate Options"
        subtitle="Choose an option"
        options={[
          {
            label: 'Payment Request Data',
            onPress: () => {
              setInitiateSheetVisible(false);
              setRequestDataSheetVisible(true);
            },
          },
          {
            label: 'Payment Request ID',
            onPress: () => {
              setInitiateSheetVisible(false);
              navigation.navigate('KlarnaNetworkPaymentRequestId', {
                sdk,
                method: 'initiate',
              });
            },
          },
          {
            label: 'Cancel',
            isCancel: true,
            onPress: () => setInitiateSheetVisible(false),
          },
        ]}
        onDismiss={() => setInitiateSheetVisible(false)}
      />
      <ActionSheet
        visible={requestDataSheetVisible}
        title="Initiate Options"
        subtitle="Choose an option"
        options={[
          {
            label: 'Custom Data',
            onPress: () => {
              setRequestDataSheetVisible(false);
              navigation.navigate('KlarnaNetworkPaymentCustomData', { sdk });
            },
          },
          {
            label: 'JSON Data',
            onPress: () => {
              setRequestDataSheetVisible(false);
              setJsonSheetVisible(true);
            },
          },
          {
            label: 'Cancel',
            isCancel: true,
            onPress: () => setRequestDataSheetVisible(false),
          },
        ]}
        onDismiss={() => setRequestDataSheetVisible(false)}
      />
      <InputSheet
        visible={jsonSheetVisible}
        title="Initiate Options"
        inputLabel="Payment Request Data JSON"
        onConfirm={(jsonData) => {
          setJsonSheetVisible(false);
          navigation.navigate('KlarnaNetworkPaymentInitiateWithRequestData', {
            sdk,
            jsonData,
          });
        }}
        onDismiss={() => setJsonSheetVisible(false)}
      />
    </>
  );
}

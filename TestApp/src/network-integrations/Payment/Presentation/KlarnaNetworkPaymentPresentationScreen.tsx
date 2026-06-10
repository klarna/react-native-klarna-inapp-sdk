import { useRoute } from '@react-navigation/native';
import type { NativeStackScreenProps } from '@react-navigation/native-stack';
import React, { useEffect, useState } from 'react';
import {
  ActivityIndicator,
  Image,
  ScrollView,
  StyleSheet,
  Text,
  View,
} from 'react-native';
import type { ImageResizeMode, ImageStyle } from 'react-native';
import { SvgXml } from 'react-native-svg';
import type { AppStackParamList } from '../../../../App';
import styles, { useTheme, Colors } from '../../../common/ui/Styles';
import Button from '../../../common/ui/view/Button';
import networkStyles from '../../Styles/NetworkIntegrationStyles';
import type {
  KlarnaPaymentPresentationContent,
  KlarnaPaymentPresentationPaymentOption,
  KlarnaPaymentPresentationText,
  KlarnaPaymentPresentationTextPart,
} from '@klarna/react-native-klarna-network-payment';

type RouteProp = NativeStackScreenProps<
  AppStackParamList,
  'KlarnaNetworkPaymentPresentation'
>['route'];

function renderTextPart(
  part: KlarnaPaymentPresentationTextPart,
  index: number,
  onLinkPress: (url: string) => void
): React.ReactNode {
  if (part.type === 'link' && part.url) {
    return (
      <Text
        key={index}
        style={localStyles.linkText}
        onPress={() => onLinkPress(part.url!)}
      >
        {part.text}
      </Text>
    );
  }
  return <Text key={index}>{part.text}</Text>;
}

function renderText(
  value: KlarnaPaymentPresentationText | undefined,
  onLinkPress: (url: string) => void
): React.ReactNode {
  if (!value) return null;
  if (value.type === 'plainText') return value.text || null;
  if (!value.parts?.length) return null;
  return value.parts.map((part, i) => renderTextPart(part, i, onLinkPress));
}

function NetworkImage({
  uri,
  style,
  resizeMode,
}: {
  uri: string | null | undefined;
  style: ImageStyle;
  resizeMode?: ImageResizeMode;
}) {
  const isSvg = !!uri && uri.toLowerCase().endsWith('.svg');
  const [svgXml, setSvgXml] = useState<string | null>(null);

  useEffect(() => {
    if (!isSvg || !uri) {
      setSvgXml(null);
      return;
    }
    fetch(uri)
      .then((r) => {
        if (!r.ok) throw new Error(`HTTP ${r.status}`);
        return r.text();
      })
      .then(setSvgXml)
      .catch((e) =>
        console.warn(`[NetworkImage] SVG fetch failed for ${uri}:`, e)
      );
  }, [uri, isSvg]);

  if (!uri) return null;
  if (isSvg) {
    if (!svgXml) return null;
    return (
      <SvgXml
        xml={svgXml}
        width={style.width as number | string | undefined}
        height={style.height as number | string | undefined}
      />
    );
  }
  return <Image source={{ uri }} style={style} resizeMode={resizeMode} />;
}

function PaymentOptionCard({
  option,
  onLinkPress,
}: {
  option: KlarnaPaymentPresentationPaymentOption;
  onLinkPress: (url: string) => void;
}) {
  const iconUrl = option.icon?.rectangleImageUrl ?? option.icon?.squareImageUrl;
  const badgeContent = renderText(option.badge, onLinkPress);
  const headerContent = renderText(option.header, onLinkPress);
  const subheaderContent = renderText(option.subheader, onLinkPress);
  const messageContent = renderText(option.message, onLinkPress);
  const termsContent = renderText(option.terms, onLinkPress);
  const buttonText = option.paymentButton?.text;

  return (
    <View style={localStyles.optionCard}>
      {badgeContent ? (
        <View style={localStyles.badge}>
          <Text style={localStyles.badgeText}>{badgeContent}</Text>
        </View>
      ) : null}

      {headerContent ? (
        <Text style={localStyles.headerText}>{headerContent}</Text>
      ) : null}
      {subheaderContent ? (
        <Text style={localStyles.subheaderText}>{subheaderContent}</Text>
      ) : null}
      {messageContent ? (
        <Text style={localStyles.messageText}>{messageContent}</Text>
      ) : null}
      {termsContent ? (
        <Text style={localStyles.termsText}>{termsContent}</Text>
      ) : null}

      {buttonText ? (
        <View style={localStyles.ctaRow}>
          <View style={localStyles.ctaButton}>
            <Text style={localStyles.ctaButtonText}>{buttonText}</Text>
            <NetworkImage
              uri={iconUrl}
              style={localStyles.ctaIcon}
              resizeMode="contain"
            />
          </View>
        </View>
      ) : null}
    </View>
  );
}

export default function KlarnaNetworkPaymentPresentationScreen() {
  const route = useRoute<RouteProp>();
  const {
    sdk,
    amount,
    currency,
    intent,
    programEnablementCodes,
    billingInterval,
    billingIntervalFrequency,
  } = route.params;

  const theme = useTheme();
  const [content, setContent] =
    useState<KlarnaPaymentPresentationContent | null>(null);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');

  function buildPresentationData() {
    return {
      amount: parseInt(amount, 10),
      currency,
      intent: intent || undefined,
      paymentProgramEnablementCodes: programEnablementCodes
        ? programEnablementCodes
            .split(',')
            .map((s) => s.trim())
            .filter(Boolean)
        : undefined,
      subscriptionBillingInterval: billingInterval || undefined,
      subscriptionBillingIntervalFrequency: billingIntervalFrequency
        ? parseInt(billingIntervalFrequency, 10)
        : undefined,
    };
  }

  function handleFetch() {
    const parsedAmount = parseInt(amount, 10);
    if (!Number.isFinite(parsedAmount) || parsedAmount <= 0) {
      setError('Amount must be a positive integer');
      return;
    }
    if (billingIntervalFrequency) {
      const parsedFrequency = parseInt(billingIntervalFrequency, 10);
      if (!Number.isFinite(parsedFrequency) || parsedFrequency <= 0) {
        setError('Billing interval frequency must be a positive integer');
        return;
      }
    }
    setError('');
    setContent(null);
    setLoading(true);
    sdk.payment.presentation
      .fetch(buildPresentationData() as any)
      .then((response) => {
        setContent(response as KlarnaPaymentPresentationContent);
      })
      .catch((err: unknown) => {
        setError(String(err));
      })
      .finally(() => {
        setLoading(false);
      });
  }

  function handleLink(url: string) {
    setError('');
    setLoading(true);
    sdk.payment.presentation
      .handleLink(url)
      .then((newContent) => {
        setContent(newContent);
      })
      .catch((err: unknown) => {
        setError(String(err));
      })
      .finally(() => {
        setLoading(false);
      });
  }

  return (
    <ScrollView
      contentInsetAdjustmentBehavior="automatic"
      style={[styles.scrollView, { backgroundColor: theme.background }]}
    >
      <View style={networkStyles.container}>
        <View style={localStyles.fetchButtonWrapper}>
          <Button title="Fetch Payment Presentation" onPress={handleFetch} />
        </View>

        {loading ? (
          <ActivityIndicator
            size="large"
            color={Colors.darker}
            style={localStyles.loader}
          />
        ) : null}

        {content ? (
          <View style={localStyles.resultContainer}>
            {content.paymentOption ? (
              <PaymentOptionCard
                option={content.paymentOption}
                onLinkPress={handleLink}
              />
            ) : null}
            {content.savedPaymentOption ? (
              <PaymentOptionCard
                option={content.savedPaymentOption}
                onLinkPress={handleLink}
              />
            ) : null}
          </View>
        ) : null}

        {error ? <Text style={localStyles.errorText}>{error}</Text> : null}
      </View>
    </ScrollView>
  );
}

const localStyles = StyleSheet.create({
  errorText: {
    marginTop: 12,
    fontSize: 13,
    color: 'red',
  },
  linkText: {
    color: Colors.darker,
    textDecorationLine: 'underline',
  },
  fetchButtonWrapper: {
    alignSelf: 'center',
    width: '70%',
  },
  loader: {
    marginTop: 48,
  },
  resultContainer: {
    marginTop: 16,
    width: '100%',
  },
  optionCard: {
    backgroundColor: Colors.white,
    borderRadius: 12,
    padding: 16,
    marginBottom: 12,
    shadowColor: Colors.black,
    shadowOffset: { width: 0, height: 1 },
    shadowOpacity: 0.08,
    shadowRadius: 4,
    elevation: 2,
  },
  icon: {
    width: '100%',
    height: 40,
    marginBottom: 10,
  },
  badge: {
    alignSelf: 'flex-start',
    backgroundColor: Colors.pink,
    borderRadius: 4,
    paddingHorizontal: 8,
    paddingVertical: 2,
    marginBottom: 8,
  },
  badgeText: {
    fontSize: 11,
    fontWeight: '700',
    color: Colors.darker,
  },
  headerText: {
    fontSize: 17,
    fontWeight: '700',
    color: Colors.darker,
    marginBottom: 4,
  },
  subheaderText: {
    fontSize: 14,
    fontWeight: '500',
    color: Colors.dark,
    marginBottom: 6,
  },
  messageText: {
    fontSize: 13,
    color: Colors.dark,
    lineHeight: 18,
    marginBottom: 6,
  },
  termsText: {
    fontSize: 11,
    color: 'gray',
    lineHeight: 15,
    marginBottom: 10,
  },
  ctaRow: {
    flexDirection: 'row',
    alignItems: 'center',
    gap: 8,
    marginTop: 12,
    paddingTop: 12,
    borderTopWidth: StyleSheet.hairlineWidth,
    borderTopColor: Colors.lightGray,
  },
  ctaImage: {
    width: 48,
    height: 28,
  },
  ctaButton: {
    flex: 1,
    flexDirection: 'row',
    backgroundColor: Colors.darker,
    borderRadius: 8,
    paddingVertical: 10,
    paddingHorizontal: 12,
    alignItems: 'center',
    justifyContent: 'center',
    gap: 8,
  },
  ctaButtonText: {
    fontSize: 14,
    fontWeight: '600',
    color: Colors.white,
  },
  ctaIcon: {
    width: 48,
    height: 24,
  },
});

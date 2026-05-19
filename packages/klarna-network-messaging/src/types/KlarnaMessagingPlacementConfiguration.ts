import type { KlarnaTheme } from '@klarna/react-native-klarna-network-core';
import type { KlarnaMessagingPlacementType } from './KlarnaMessagingPlacementType';

/** Base configuration shared by all messaging placement types. */
interface KlarnaMessagingPlacementConfigurationBase {
  /** The visual theme for the placement. Defaults to the system theme when omitted. */
  readonly theme?: KlarnaTheme;
  /** The purchase amount in minor units (e.g. cents). */
  readonly amount: number;
  /** The ISO 4217 currency code (e.g. "USD", "SEK"). */
  readonly currency: string;
}

/** Configuration for a credit-promotion placement that automatically sizes itself to fit its content. */
export interface KlarnaMessagingCreditPromotionAutoSizeConfiguration extends KlarnaMessagingPlacementConfigurationBase {
  /** Discriminator identifying this as a credit-promotion auto-size placement. */
  readonly type: KlarnaMessagingPlacementType.CreditPromotionAutoSize;
}

/** Configuration for a credit-promotion placement rendered as a compact badge. */
export interface KlarnaMessagingCreditPromotionBadgeConfiguration extends KlarnaMessagingPlacementConfigurationBase {
  /** Discriminator identifying this as a credit-promotion badge placement. */
  readonly type: KlarnaMessagingPlacementType.CreditPromotionBadge;
}

/** Union of all supported messaging placement configurations. */
export type KlarnaMessagingPlacementConfiguration =
  | KlarnaMessagingCreditPromotionAutoSizeConfiguration
  | KlarnaMessagingCreditPromotionBadgeConfiguration;

import type { KlarnaInterval } from './KlarnaInterval';

/** Intent of a payment presentation request. Use this to specify the type of payment scenario for which you want to receive presentation content. */
export type KlarnaPaymentPresentationIntent =
  | 'PAY'
  | 'SUBSCRIBE'
  | 'ADD_TO_WALLET';

/** Instruction for the payment presentation. */
export type KlarnaPaymentPresentationInstruction =
  /** Display Klarna as payment option. */
  | 'SHOW_KLARNA'
  /** Klarna should be preselected. */
  | 'PRESELECT_KLARNA'
  /** Hide all other payment options except Klarna. */
  | 'SHOW_ONLY_KLARNA';

/** Payment status of a presentation. */
export type KlarnaPaymentPresentationPaymentStatus =
  /** Waiting for partner authorization to proceed. */
  | 'PENDING_PARTNER_AUTHORIZATION'
  /** Customer needs to take action to proceed. */
  | 'REQUIRES_CUSTOMER_ACTION';

/** Alignment of the image in the payment button. */
export type KlarnaPaymentPresentationImageAlignment = 'LEFT' | 'RIGHT';

/** Text style applied to a text part. */
export type KlarnaPaymentPresentationTextPartStyle =
  | 'BOLD'
  | 'ITALIC'
  | 'UNDERLINE';

/** Context for a link text part. */
export type KlarnaPaymentPresentationTextPartLinkContext =
  /** The link is for authentication purposes. */
  | 'AUTH'
  /** The link is for informational purposes. */
  | 'INFO';

/** A part of a text in the payment presentation. Each part can be either plain text or a hyperlink. */
export type KlarnaPaymentPresentationTextPart =
  | {
      type: 'plain';
      /** The human-readable text content for this part. */
      text: string;
      /** Text styles applied to the text. */
      styles?: KlarnaPaymentPresentationTextPartStyle[];
    }
  | {
      type: 'link';
      /** The human-readable text content for this part. */
      text: string;
      /** Target URL for the link. */
      url?: string;
      /** Link context for the target URL. */
      context?: KlarnaPaymentPresentationTextPartLinkContext;
      /** Text styles applied to the text. */
      styles?: KlarnaPaymentPresentationTextPartStyle[];
    };

/** Presentation text, either plain or attributed with linked parts. */
export type KlarnaPaymentPresentationText =
  | { type: 'plainText'; text: string }
  | { type: 'attributedText'; parts?: KlarnaPaymentPresentationTextPart[] };

/** Elements to properly display the Klarna payment button. */
export type KlarnaPaymentPresentationPaymentButton = {
  /** Text for the payment button. */
  text: string;
  /** Image URL for the payment button. */
  imageUrl?: string;
  /** Alignment of the image in the payment button. */
  imageAlignment?: KlarnaPaymentPresentationImageAlignment;
};

/** Icon for the payment option heading. */
export type KlarnaPaymentPresentationIcon = {
  /** Alternative text for the icon. */
  alt?: string;
  /** Curved edged badge shaped Klarna icon URL for the payment option heading. */
  badgeImageUrl?: string;
  /** Square shaped Klarna icon URL with just the 'K' logo. */
  rectangleImageUrl?: string;
  /** Square shaped Klarna icon URL for the payment option heading. */
  squareImageUrl?: string;
};

/** Contains objects for heading, subheading, and action of a payment option. */
export type KlarnaPaymentPresentationPaymentOption = {
  /** Identifier for the selected payment option. Treat as an opaque string. */
  paymentOptionId: string;
  /** The heading of the payment option. */
  header?: KlarnaPaymentPresentationText;
  /** The badge of the payment option. */
  badge?: KlarnaPaymentPresentationText;
  /** The subheading of the payment option. */
  subheader?: KlarnaPaymentPresentationText;
  /** The message of the payment option, potentially with an action. */
  message?: KlarnaPaymentPresentationText;
  /** The terms and disclosures of the payment option, potentially with links. */
  terms?: KlarnaPaymentPresentationText;
  /** Represents the payment button of the payment option. */
  paymentButton?: KlarnaPaymentPresentationPaymentButton;
  /** Represents the icon of the payment option. */
  icon?: KlarnaPaymentPresentationIcon;
};

/** Data for a payment presentation request. */
export type KlarnaPaymentPresentationData = {
  /** Total payment amount in minor units (e.g. cents). Required for pay and subscribe intents. */
  amount: number;
  /** ISO 4217 currency code (e.g. "SEK"). */
  currency: string;
  /** Intent of the payment request to receive appropriate presentation content for your use case. */
  intent?: KlarnaPaymentPresentationIntent;
  /** List of payment program enablement codes for the payment request. */
  paymentProgramEnablementCodes?: string[];
  /** The interval of which the customer is charged for the subscription. */
  subscriptionBillingInterval?: KlarnaInterval;
  /** Defines the subscription billing interval frequency. Required if subscriptionBillingInterval is set. */
  subscriptionBillingIntervalFrequency?: number;
};

/** Content for the payment presentation returned by a fetch call. */
export type KlarnaPaymentPresentationContent = {
  /** Klarna display instruction (e.g., SHOW_KLARNA). */
  instruction: KlarnaPaymentPresentationInstruction;
  /** Status of the payment process. */
  paymentStatus?: KlarnaPaymentPresentationPaymentStatus;
  /** Klarna display assets for the payment selector. */
  paymentOption?: KlarnaPaymentPresentationPaymentOption;
  /** Klarna display assets for a saved payment option. */
  savedPaymentOption?: KlarnaPaymentPresentationPaymentOption;
};

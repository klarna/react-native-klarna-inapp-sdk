import type { KlarnaAddress } from '@klarna/react-native-klarna-network-core';
import type { KlarnaBillingPlan } from './types/models/KlarnaBillingPlan';
import type { KlarnaLineItem } from './types/models/KlarnaLineItem';
import type { KlarnaOndemandService } from './types/models/KlarnaOndemandService';
import type { KlarnaPartnerCustomer } from './types/models/KlarnaPartnerCustomer';
import type { KlarnaPaymentRequest } from './types/KlarnaPaymentRequest';
import type { KlarnaPaymentRequestData } from './types/models/KlarnaPaymentRequestData';
import type { KlarnaPaymentRequestState } from './types/KlarnaPaymentRequestState';
import type { KlarnaPaymentRequestStateReason } from './types/KlarnaPaymentRequestStateReason';
import type { KlarnaRequestCustomerToken } from './types/models/KlarnaRequestCustomerToken';
import type { KlarnaShipping } from './types/models/KlarnaShipping';
import type { KlarnaShippingConfig } from './types/models/KlarnaShippingConfig';
import type { KlarnaSubscription } from './types/models/KlarnaSubscription';
import type { KlarnaSupplementaryPurchaseData } from './types/models/KlarnaSupplementaryPurchaseData';
import type { KlarnaPaymentPresentationContent } from './types/models/KlarnaPaymentPresentation';

function requireFiniteNumber(value: unknown, label: string): number {
  if (typeof value !== 'number' || !Number.isFinite(value)) {
    throw new Error(`${label} must be a finite number, got ${value}`);
  }
  return value;
}

export function mapAddress(address: KlarnaAddress | undefined) {
  if (!address) return null;
  return {
    streetAddress: address.streetAddress ?? null,
    streetAddress2: address.streetAddress2 ?? null,
    city: address.city ?? null,
    region: address.region ?? null,
    postalCode: address.postalCode ?? null,
    country: address.country ?? null,
  };
}

export function mapLineItem(item: KlarnaLineItem) {
  requireFiniteNumber(
    item.totalAmount,
    `KlarnaLineItem "${item.name}": totalAmount`
  );
  requireFiniteNumber(item.quantity, `KlarnaLineItem "${item.name}": quantity`);
  return {
    currency: item.currency ?? null,
    imageUrl: item.imageUrl ?? null,
    name: item.name,
    productIdentifier: item.productIdentifier ?? null,
    productUrl: item.productUrl ?? null,
    quantity: item.quantity,
    lineItemReference: item.lineItemReference ?? null,
    shippingReference: item.shippingReference ?? null,
    subscriptionReference: item.subscriptionReference ?? null,
    totalAmount: item.totalAmount,
    totalTaxAmount: item.totalTaxAmount ?? null,
    unitPrice: item.unitPrice ?? null,
  };
}

export function mapPartnerCustomer(
  customer: KlarnaPartnerCustomer | undefined
) {
  if (!customer) return null;
  return {
    address: mapAddress(customer.address),
    email: customer.email ?? null,
    familyName: customer.familyName ?? null,
    givenName: customer.givenName ?? null,
    phone: customer.phone ?? null,
  };
}

export function mapShipping(shipping: KlarnaShipping) {
  return {
    address: mapAddress(shipping.address),
    recipient: shipping.recipient
      ? {
          attention: shipping.recipient.attention ?? null,
          email: shipping.recipient.email ?? null,
          familyName: shipping.recipient.familyName,
          givenName: shipping.recipient.givenName,
          phone: shipping.recipient.phone ?? null,
        }
      : null,
    shippingOption: shipping.shippingOption
      ? {
          shippingCarrier: shipping.shippingOption.shippingCarrier ?? null,
          shippingType: shipping.shippingOption.shippingType,
          shippingTypeAttributes:
            shipping.shippingOption.shippingTypeAttributes ?? null,
        }
      : null,
    shippingReference: shipping.shippingReference ?? null,
  };
}

export function mapOndemandService(service: KlarnaOndemandService | undefined) {
  if (!service) return null;
  return {
    currency: service.currency ?? null,
    averageAmount: service.averageAmount ?? null,
    minimumAmount: service.minimumAmount ?? null,
    maximumAmount: service.maximumAmount ?? null,
    purchaseInterval: service.purchaseInterval ?? null,
    purchaseIntervalFrequency: service.purchaseIntervalFrequency ?? null,
  };
}

export function mapBillingPlan(plan: KlarnaBillingPlan) {
  requireFiniteNumber(plan.billingAmount, 'KlarnaBillingPlan: billingAmount');
  requireFiniteNumber(
    plan.intervalFrequency,
    'KlarnaBillingPlan: intervalFrequency'
  );
  return {
    billingAmount: plan.billingAmount,
    currency: plan.currency ?? null,
    from: plan.from,
    interval: plan.interval,
    intervalFrequency: plan.intervalFrequency,
  };
}

export function mapSubscription(s: KlarnaSubscription) {
  return {
    subscriptionReference: s.subscriptionReference,
    name: s.name ?? null,
    freeTrial: s.freeTrial ?? null,
    billingPlans: s.billingPlans ? s.billingPlans.map(mapBillingPlan) : null,
  };
}

export function mapRequestCustomerToken(
  token: KlarnaRequestCustomerToken | undefined
) {
  if (!token) return null;
  return {
    scopes: token.scopes,
    customerTokenReference: token.customerTokenReference ?? null,
  };
}

export function mapShippingConfig(config: KlarnaShippingConfig | undefined) {
  if (!config) return null;
  return {
    supportedCountries: config.supportedCountries ?? null,
    mode: config.mode,
  };
}

export function mapSupplementaryPurchaseData(
  d: KlarnaSupplementaryPurchaseData | undefined
) {
  if (!d) return null;
  return {
    customer: mapPartnerCustomer(d.customer),
    lineItems: d.lineItems ? d.lineItems.map(mapLineItem) : null,
    purchaseReference: d.purchaseReference ?? null,
    shipping: d.shipping ? d.shipping.map(mapShipping) : null,
    ondemandService: mapOndemandService(d.ondemandService),
    subscriptions: d.subscriptions
      ? d.subscriptions.map(mapSubscription)
      : null,
  };
}

export function mapPaymentRequestData(data: KlarnaPaymentRequestData) {
  requireFiniteNumber(data.amount, 'KlarnaPaymentRequestData: amount');
  return {
    amount: data.amount,
    currency: data.currency,
    paymentOptionId: data.paymentOptionId ?? null,
    paymentRequestReference: data.paymentRequestReference ?? null,
    requestCustomerToken: mapRequestCustomerToken(data.requestCustomerToken),
    shippingConfig: mapShippingConfig(data.shippingConfig),
    collectCustomerProfile: data.collectCustomerProfile ?? null,
    supplementaryPurchaseData: mapSupplementaryPurchaseData(
      data.supplementaryPurchaseData
    ),
  };
}

export function mapPaymentRequest(raw: any): KlarnaPaymentRequest {
  const spec = raw as {
    paymentRequestId: string;
    state: string;
    previousState: string | null;
    stateContext: { klarnaNetworkSessionToken: string | null } | null;
    stateReason: string | null;
    paymentRequestReference: string | null;
  };
  return {
    paymentRequestId: spec.paymentRequestId,
    state: spec.state as KlarnaPaymentRequestState,
    previousState: spec.previousState as KlarnaPaymentRequestState | null,
    stateContext: spec.stateContext,
    stateReason: spec.stateReason as KlarnaPaymentRequestStateReason | null,
    paymentRequestReference: spec.paymentRequestReference,
  };
}

export function mapPresentationContent(
  raw: any
): KlarnaPaymentPresentationContent {
  return raw as KlarnaPaymentPresentationContent;
}

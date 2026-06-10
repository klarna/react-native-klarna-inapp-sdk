import type { KlarnaLineItem } from './KlarnaLineItem';
import type { KlarnaOndemandService } from './KlarnaOndemandService';
import type { KlarnaPartnerCustomer } from './KlarnaPartnerCustomer';
import type { KlarnaShipping } from './KlarnaShipping';
import type { KlarnaSubscription } from './KlarnaSubscription';

/** Supplementary purchase data for a payment request. */
export type KlarnaSupplementaryPurchaseData = {
  /** Customer information. */
  customer?: KlarnaPartnerCustomer;
  /** List of line items in the order. */
  lineItems?: KlarnaLineItem[];
  /** Reference for the purchase. */
  purchaseReference?: string;
  /** List of shipping information. */
  shipping?: KlarnaShipping[];
  /** On-demand service information. */
  ondemandService?: KlarnaOndemandService;
  /** List of subscriptions. */
  subscriptions?: KlarnaSubscription[];
};

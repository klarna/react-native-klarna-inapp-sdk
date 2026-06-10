/** A line item in the order. */
export type KlarnaLineItem = {
  /** ISO 4217 currency code. */
  currency?: string;
  /** URL of the product image. */
  imageUrl?: string;
  /** Product name. */
  name: string;
  /** GTIN (UPC, ISBN) of the item. */
  productIdentifier?: string;
  /** URL of the product page. */
  productUrl?: string;
  /** Quantity of the product. */
  quantity: number;
  /** Reference for the line item. */
  lineItemReference?: string;
  /** Reference to the associated shipping. */
  shippingReference?: string;
  /** Reference to the associated subscription. */
  subscriptionReference?: string;
  /** Total amount in minor currency units. */
  totalAmount: number;
  /** Total tax amount in minor currency units. */
  totalTaxAmount?: number;
  /** Unit price in minor currency units. */
  unitPrice?: number;
};

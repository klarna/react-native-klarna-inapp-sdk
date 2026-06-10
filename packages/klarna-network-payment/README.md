# Klarna Network Payment SDK React Native

[![NPM][npm-badge]][npm-url]
[![React Native][dependency-badge]][dependency-url]
[![Platform][platform-badge]][platform-url]
[![License][license-badge]][license-url]
[![Developed at Klarna][klarna-badge]][klarna-url]

React Native package for Klarna Network Payment. This library wraps KlarnaNetworkPayment and exposes payment request functionality directly on the `Klarna` instance via `klarna.payment`.

Requires [`@klarna/react-native-klarna-network-core`](https://www.npmjs.com/package/@klarna/react-native-klarna-network-core) to be installed and initialized first.

**New Architecture only** (TurboModule).

## Requirements

- iOS 13 or later.
- Android 5.0 (API 21) or later.
- React Native 0.76.0 or later (New Architecture required).
- `@klarna/react-native-klarna-network-core` installed and initialized.

## Getting started

### Add Dependency

#### NPM

```shell
npm install @klarna/react-native-klarna-network-payment --save
```

#### Yarn

```shell
yarn add @klarna/react-native-klarna-network-payment
```

## Usage

Import this package once at app startup (e.g. in your root `index.js`) to activate the `payment` property on `Klarna`. After that, `klarna.payment` is available anywhere you have a `Klarna` instance.

```ts
import '@klarna/react-native-klarna-network-payment';
```

### Initiate with a payment request ID

Use this when a payment request already exists on the server side.

```ts
import { Klarna } from '@klarna/react-native-klarna-network-core';

const klarna = await Klarna.initialize({
  clientId: '...',
  appReturnUrl: '...',
});

const paymentRequest = await klarna.payment.initiate('payment-request-id');
console.log(paymentRequest.state); // 'IN_PROGRESS'
```

### Initiate with payment request data

Use this to create and initiate a payment request in one step.

```ts
import type { KlarnaPaymentRequestData } from '@klarna/react-native-klarna-network-payment';

const data: KlarnaPaymentRequestData = {
  amount: 10000, // in minor currency units (e.g. cents)
  currency: 'SEK',
  paymentOptionId: 'option-id', // optional
  paymentRequestReference: 'ref-123', // optional
};

const paymentRequest = await klarna.payment.initiate(data);
```

### Fetch a payment request

```ts
const paymentRequest = await klarna.payment.fetch('payment-request-id');
```

### Cancel a payment request

```ts
const paymentRequest = await klarna.payment.cancel('payment-request-id');
```

## API

### `klarna.payment`

Accessible on any initialized `Klarna` instance after importing this package.

| Method     | Signature                                                                                       | Description                                                               |
| ---------- | ----------------------------------------------------------------------------------------------- | ------------------------------------------------------------------------- |
| `initiate` | `(paymentRequestIdOrData: string \| KlarnaPaymentRequestData) => Promise<KlarnaPaymentRequest>` | Initiates a payment request by ID or creates and initiates one from data. |
| `fetch`    | `(paymentRequestId: string) => Promise<KlarnaPaymentRequest>`                                   | Fetches the current state of a payment request.                           |
| `cancel`   | `(paymentRequestId: string) => Promise<KlarnaPaymentRequest>`                                   | Cancels a payment request.                                                |

### `KlarnaPaymentRequest`

| Field                     | Type                                       | Description                                    |
| ------------------------- | ------------------------------------------ | ---------------------------------------------- |
| `paymentRequestId`        | `string`                                   | Unique identifier for the payment request.     |
| `state`                   | `KlarnaPaymentRequestState`                | Current state of the payment request.          |
| `previousState`           | `KlarnaPaymentRequestState \| null`        | Previous state, if any.                        |
| `stateContext`            | `KlarnaPaymentRequestStateContext \| null` | Additional context for the current state.      |
| `stateReason`             | `KlarnaPaymentRequestStateReason \| null`  | Reason for the current state, if any.          |
| `paymentRequestReference` | `string \| null`                           | Partner-supplied reference for reconciliation. |

### `KlarnaPaymentRequestState`

`'SUBMITTED' | 'IN_PROGRESS' | 'COMPLETED' | 'EXPIRED' | 'CANCELED' | 'DECLINED'`

### `KlarnaPaymentRequestStateReason`

`'PARTNER_CANCELED' | 'PURCHASE_FLOW_ABORTED' | 'TECHNICAL_ERROR' | 'PAYMENT_DECLINED' | 'PAYMENT_REQUEST_SUBMITTED'`

### `KlarnaPaymentRequestData`

| Field                       | Type                                 | Required | Description                                                 |
| --------------------------- | ------------------------------------ | -------- | ----------------------------------------------------------- |
| `amount`                    | `number`                             | Yes      | Total amount in minor currency units (e.g. cents).          |
| `currency`                  | `string`                             | Yes      | ISO 4217 currency code (e.g. `"SEK"`).                      |
| `paymentOptionId`           | `string`                             | No       | Pre-select a specific payment option in the flow.           |
| `paymentRequestReference`   | `string`                             | No       | Partner reference for webhook correlation.                  |
| `requestCustomerToken`      | `KlarnaRequestCustomerToken`         | No       | Request a customer token to be returned on completion.      |
| `shippingConfig`            | `KlarnaShippingConfig`               | No       | Configure shipping address collection.                      |
| `collectCustomerProfile`    | `KlarnaCollectCustomerProfileType[]` | No       | Customer profile fields to collect during the flow.         |
| `supplementaryPurchaseData` | `KlarnaSupplementaryPurchaseData`    | No       | Line items, shipping, subscriptions, and on-demand details. |

## Support

If you are having any issues using the SDK in your project or if you think that something is wrong with the SDK itself, please follow our [support guide](https://github.com/klarna/react-native-klarna-inapp-sdk/blob/master/SUPPORT.md).

## Contribution

If you want to contribute to this project please follow our [contribution guide](https://github.com/klarna/react-native-klarna-inapp-sdk/blob/master/CONTRIBUTING.md).

## License

This project is licensed under
[Apache License, Version 2.0](https://github.com/klarna/react-native-klarna-inapp-sdk/blob/master/LICENSE).

<!-- Markdown images & links -->

[npm-badge]: https://img.shields.io/npm/v/@klarna/react-native-klarna-network-payment?style=flat-square
[npm-url]: https://www.npmjs.com/package/@klarna/react-native-klarna-network-payment
[dependency-badge]: https://img.shields.io/npm/dependency-version/@klarna/react-native-klarna-network-payment/peer/react-native?style=flat-square
[dependency-url]: https://www.npmjs.com/package/@klarna/react-native-klarna-network-payment?activeTab=dependencies
[platform-badge]: https://img.shields.io/badge/platform-React%20Native-lightgrey?style=flat-square
[platform-url]: https://reactnative.dev
[license-badge]: https://img.shields.io/github/license/klarna/react-native-klarna-inapp-sdk?style=flat-square
[license-url]: https://github.com/klarna/react-native-klarna-inapp-sdk/blob/master/LICENSE
[klarna-badge]: https://img.shields.io/badge/%20-Developed%20at%20Klarna-black?labelColor=ffb3c7&style=flat-square&logo=data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAABAAAAAOCAYAAAAmL5yKAAAAAXNSR0IArs4c6QAAAIRlWElmTU0AKgAAAAgABQESAAMAAAABAAEAAAEaAAUAAAABAAAASgEbAAUAAAABAAAAUgEoAAMAAAABAAIAAIdpAAQAAAABAAAAWgAAAAAAAALQAAAAAQAAAtAAAAABAAOgAQADAAAAAQABAACgAgAEAAAAAQAAABCgAwAEAAAAAQAAAA4AAAAA0LMKiwAAAAlwSFlzAABuugAAbroB1t6xFwAAAVlpVFh0WE1MOmNvbS5hZG9iZS54bXAAAAAAADx4OnhtcG1ldGEgeG1sbnM6eD0iYWRvYmU6bnM6bWV0YS8iIHg6eG1wdGs9IlhNUCBDb3JlIDUuNC4wIj4KICAgPHJkZjpSREYgeG1sbnM6cmRmPSJodHRwOi8vd3d3LnczLm9yZy8xOTk5LzAyLzIyLXJkZi1zeW50YXgtbnMjIj4KICAgICAgPHJkZjpEZXNjcmlwdGlvbiByZGY6YWJvdXQ9IiIKICAgICAgICAgICAgeG1sbnM6dGlmZj0iaHR0cDovL25zLmFkb2JlLmNvbS90aWZmLzEuMC8iPgogICAgICAgICA8dGlmZjpPcmllbnRhdGlvbj4xPC90aWZmOk9yaWVudGF0aW9uPgogICAgICA8L3JkZjpEZXNjcmlwdGlvbj4KICAgPC9yZGY6UkRGPgo8L3g6eG1wbWV0YT4KTMInWQAAAVBJREFUKBVtkz0vREEUhsdXgo5qJXohkUgQ0fgFNFpR2V5ClP6CQu9PiB6lEL1I7B9A4/treZ47c252s97k2ffMmZkz5869m1JKL/AFbzAHaiRbmsIf4BdaMAZqMFsOXNxXkroKbxCPV5l8yHOJLVipn9/vEreLa7FguSN3S2ynA/ATeQuI8tTY6OOY34DQaQnq9mPCDtxoBwuRxPfAvPMWnARlB12KAi6eLTPruOOP4gcl33O6+Sjgc83DJkRH+h2MgorLzaPy68W48BG2S+xYnmAa1L+nOxEduMH3fgjGFvZeVkANZau68B6CrgJxWosFFpF7iG+h5wKZqwt42qIJtARu/ix+gqsosEq8D35o6R3c7OL4lAnTDljEe9B3Qa2BYzmHemDCt6Diwo6JY7E+A82OnN9HuoBruAQvUQ1nSxP4GVzBDRyBfygf6RW2/gD3NmEv+K/DZgAAAABJRU5ErkJggg==
[klarna-url]: https://github.com/klarna

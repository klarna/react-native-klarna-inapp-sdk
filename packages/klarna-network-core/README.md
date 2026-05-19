# Klarna Network Core SDK React Native

[![NPM][npm-badge]][npm-url]
[![React Native][dependency-badge]][dependency-url]
[![Platform][platform-badge]][platform-url]
[![License][license-badge]][license-url]
[![Developed at Klarna][klarna-badge]][klarna-url]

Core package for React Native Klarna Network packages. This library wraps KlarnaNetworkCore sdk and provides the shared `Klarna` entry point, session management, and integration metadata used by other React Native Klarna Network packages:

- KlarnaNetworkPayments React Native
- KlarnaNetworkMessaging React Native
- KlarnaNetworkIdentity React Native

**New Architecture only** (TurboModule).

## Requirements

- iOS 13 or later.
- Android 5.0 (API 21) or later.
- React Native 0.76.0 or later (New Architecture required).

## Getting started

### Add Dependency

#### NPM

```shell
npm install @klarna/react-native-klarna-network-core --save
```

#### Yarn

```shell
yarn add @klarna/react-native-klarna-network-core
```

## Usage

### Initialize

Call `Klarna.initialize` once at app startup before using any Klarna Network SDK.

```ts
import { Klarna } from '@klarna/react-native-klarna-network-core';

const klarna = await Klarna.initialize({
  clientId: 'your-client-id',
  appReturnUrl: 'your-app://return',
  // optional
  accountId: 'account-id',
  locale: 'en-US',
  klarnaNetworkSessionToken: 'token',
});
```

### Session token

```ts
const token = await klarna.network.session.token();

// Clear the session when done
await klarna.network.session.clear();
```

### Handle return URL (iOS only — deep link)

Call this from your deep-link handler to let the SDK process redirect callbacks.

```ts
const handled = await Klarna.handleReturnUrl(url);
```

### Integration metadata

Optionally tag the integration for analytics and debugging:

```ts
klarna.setIntegrationMetadata({
  integrator: {
    name: 'MyApp',
    sessionReference: 'session-ref',
    moduleName: 'klarna-network-payment',
    moduleVersion: '1.0.0',
  },
});
```

## API

### `Klarna`

| Method                       | Signature                                          | Description                                                                        |
| ---------------------------- | -------------------------------------------------- | ---------------------------------------------------------------------------------- |
| `initialize`                 | `(config: KlarnaConfiguration) => Promise<Klarna>` | Initializes the native SDK. Must be called before any other method.                |
| `handleReturnUrl` _(static)_ | `(url: string) => Promise<boolean>`                | Processes a deep-link return URL (iOS only). Returns `true` if the SDK handled it. |
| `getIntegrationMetadata`     | `() => KlarnaIntegrationMetadata \| null`          | Returns the currently set integration metadata.                                    |
| `setIntegrationMetadata`     | `(metadata: KlarnaIntegrationMetadata) => void`    | Sets integration metadata on the native layer.                                     |
| `dispose`                    | `() => Promise<void>`                              | Releases the native instance. Call when the SDK is no longer needed.               |

### `KlarnaConfiguration`

| Field                       | Type     | Required | Description                              |
| --------------------------- | -------- | -------- | ---------------------------------------- |
| `clientId`                  | `string` | Yes      | Klarna client ID                         |
| `appReturnUrl`              | `string` | Yes      | Deep-link URL scheme for OAuth redirects |
| `accountId`                 | `string` | No       | Klarna account ID                        |
| `locale`                    | `string` | No       | BCP 47 locale string (e.g. `"en-US"`)    |
| `klarnaNetworkSessionToken` | `string` | No       | Pre-existing session token               |

### `KlarnaNetworkSession`

| Method  | Signature               | Description                              |
| ------- | ----------------------- | ---------------------------------------- |
| `token` | `() => Promise<string>` | Returns the active network session token |
| `clear` | `() => Promise<void>`   | Clears the current session               |

## Support

If you are having any issues using the SDK in your project or if you think that something is wrong with the SDK itself, please follow our [support guide](https://github.com/klarna/react-native-klarna-inapp-sdk/blob/master/SUPPORT.md).

## Contribution

If you want to contribute to this project please follow our [contribution guide](https://github.com/klarna/react-native-klarna-inapp-sdk/blob/master/CONTRIBUTING.md).

## License

This project is licensed under
[Apache License, Version 2.0](https://github.com/klarna/react-native-klarna-inapp-sdk/blob/master/LICENSE).

<!-- Markdown images & links -->

[npm-badge]: https://img.shields.io/npm/v/@klarna/react-native-klarna-network-core?style=flat-square
[npm-url]: https://www.npmjs.com/package/@klarna/react-native-klarna-network-core
[dependency-badge]: https://img.shields.io/npm/dependency-version/@klarna/react-native-klarna-network-core/peer/react-native?style=flat-square
[dependency-url]: https://www.npmjs.com/package/@klarna/react-native-klarna-network-core?activeTab=dependencies
[platform-badge]: https://img.shields.io/badge/platform-React%20Native-lightgrey?style=flat-square
[platform-url]: https://reactnative.dev
[license-badge]: https://img.shields.io/github/license/klarna/react-native-klarna-inapp-sdk?style=flat-square
[license-url]: https://github.com/klarna/react-native-klarna-inapp-sdk/blob/master/LICENSE
[klarna-badge]: https://img.shields.io/badge/%20-Developed%20at%20Klarna-black?labelColor=ffb3c7&style=flat-square&logo=data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAABAAAAAOCAYAAAAmL5yKAAAAAXNSR0IArs4c6QAAAIRlWElmTU0AKgAAAAgABQESAAMAAAABAAEAAAEaAAUAAAABAAAASgEbAAUAAAABAAAAUgEoAAMAAAABAAIAAIdpAAQAAAABAAAAWgAAAAAAAALQAAAAAQAAAtAAAAABAAOgAQADAAAAAQABAACgAgAEAAAAAQAAABCgAwAEAAAAAQAAAA4AAAAA0LMKiwAAAAlwSFlzAABuugAAbroB1t6xFwAAAVlpVFh0WE1MOmNvbS5hZG9iZS54bXAAAAAAADx4OnhtcG1ldGEgeG1sbnM6eD0iYWRvYmU6bnM6bWV0YS8iIHg6eG1wdGs9IlhNUCBDb3JlIDUuNC4wIj4KICAgPHJkZjpSREYgeG1sbnM6cmRmPSJodHRwOi8vd3d3LnczLm9yZy8xOTk5LzAyLzIyLXJkZi1zeW50YXgtbnMjIj4KICAgICAgPHJkZjpEZXNjcmlwdGlvbiByZGY6YWJvdXQ9IiIKICAgICAgICAgICAgeG1sbnM6dGlmZj0iaHR0cDovL25zLmFkb2JlLmNvbS90aWZmLzEuMC8iPgogICAgICAgICA8dGlmZjpPcmllbnRhdGlvbj4xPC90aWZmOk9yaWVudGF0aW9uPgogICAgICA8L3JkZjpEZXNjcmlwdGlvbj4KICAgPC9yZGY6UkRGPgo8L3g6eG1wbWV0YT4KTMInWQAAAVBJREFUKBVtkz0vREEUhsdXgo5qJXohkUgQ0fgFNFpR2V5ClP6CQu9PiB6lEL1I7B9A4/treZ47c252s97k2ffMmZkz5869m1JKL/AFbzAHaiRbmsIf4BdaMAZqMFsOXNxXkroKbxCPV5l8yHOJLVipn9/vEreLa7FguSN3S2ynA/ATeQuI8tTY6OOY34DQaQnq9mPCDtxoBwuRxPfAvPMWnARlB12KAi6eLTPruOOP4gcl33O6+Sjgc83DJkRH+h2MgorLzaPy68W48BG2S+xYnmAa1L+nOxEduMH3fgjGFvZeVkANZau68B6CrgJxWosFFpF7iG+h5wKZqwt42qIJtARu/ix+gqsosEq8D35o6R3c7OL4lAnTDljEe9B3Qa2BYzmHemDCt6Diwo6JY7E+A82OnN9HuoBruAQvUQ1nSxP4GVzBDRyBfygf6RW2/gD3NmEv+K/DZgAAAABJRU5ErkJggg==
[klarna-url]: https://github.com/klarna

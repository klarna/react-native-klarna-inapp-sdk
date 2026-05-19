# @klarna/react-native-klarna-network-messaging

[![NPM][npm-badge]][npm-url]
[![React Native][dependency-badge]][dependency-url]
[![Platform][platform-badge]][platform-url]
[![License][license-badge]][license-url]
[![Developed at Klarna][klarna-badge]][klarna-url]

React Native bindings for Klarna **messaging placements**: a Fabric-native `KlarnaMessagingPlacementView` that renders on-device messaging (for example credit promotion placements) with **amount**, **currency**, and **placement type**, an optional **theme**, automatic height from native resize events, and **error** callbacks.

This package builds on [`@klarna/react-native-klarna-network-core`](https://www.npmjs.com/package/@klarna/react-native-klarna-network-core): initialize `Klarna` from core before showing placements; this module re-exports `KlarnaTheme` for convenience.

## Requirements

- iOS 13 or later.
- Android 5.0 (API 21) or later.
- React Native 0.76.0 or later.
- React Native New Architecture enabled.

### NPM

```shell
npm install @klarna/react-native-klarna-network-messaging --save
```

### Yarn

```shell
yarn add @klarna/react-native-klarna-network-messaging
```

You also need [`@klarna/react-native-klarna-network-core`](https://www.npmjs.com/package/@klarna/react-native-klarna-network-core) (peer dependency) for initialization and shared types.

## Usage

Initialize a `Klarna` instance from core (see the [core package README](https://github.com/klarna/react-native-klarna-inapp-sdk/blob/master/packages/klarna-network-core/README.md)) and pass it to the placement view along with its configuration:

```tsx
import {
  KlarnaMessagingPlacementView,
  KlarnaMessagingPlacementType,
  KlarnaTheme,
} from '@klarna/react-native-klarna-network-messaging';
import type { Klarna } from '@klarna/react-native-klarna-network-core';

export function CheckoutMessaging({ sdk }: { sdk: Klarna }) {
  return (
    <KlarnaMessagingPlacementView
      instance={sdk}
      configuration={{
        type: KlarnaMessagingPlacementType.CreditPromotionAutoSize,
        amount: 19900,
        currency: 'EUR',
        theme: KlarnaTheme.Automatic,
      }}
      onError={(error) => {
        console.warn(error.name, error.message);
      }}
    />
  );
}
```

**Props (high level)**

| Prop            | Notes                                                                                                                                                               |
| --------------- | ------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| `instance`      | Required. A `Klarna` instance obtained from `@klarna/react-native-klarna-network-core` after initialization.                                                        |
| `configuration` | Required. `{ type, amount, currency, theme? }`. `type` is a `KlarnaMessagingPlacementType`; `theme` is optional — when omitted, the native SDK chooses the default. |
| `onError`       | Optional. Receives `KlarnaMessagingError` (`name`, `message`) when native messaging fails.                                                                          |
| `style`         | Optional. Passed through; height is driven by native content after load.                                                                                            |

## Support

If you are having any issues using the SDK in your project or if you think that something is wrong with the SDK itself, please follow our [support guide](https://github.com/klarna/react-native-klarna-inapp-sdk/blob/master/SUPPORT.md).

## Contribution

If you want to contribute to this project please follow our [contribution guide](https://github.com/klarna/react-native-klarna-inapp-sdk/blob/master/CONTRIBUTING.md).

## License

This project is licensed under
[Apache License, Version 2.0](https://github.com/klarna/react-native-klarna-inapp-sdk/blob/master/LICENSE).

<!-- Markdown images & links -->

[npm-badge]: https://img.shields.io/npm/v/@klarna/react-native-klarna-network-messaging?style=flat-square
[npm-url]: https://www.npmjs.com/package/@klarna/react-native-klarna-network-messaging
[dependency-badge]: https://img.shields.io/npm/dependency-version/@klarna/react-native-klarna-network-messaging/peer/react-native?style=flat-square
[dependency-url]: https://www.npmjs.com/package/@klarna/react-native-klarna-network-messaging?activeTab=dependencies
[platform-badge]: https://img.shields.io/badge/platform-React%20Native-lightgrey?style=flat-square
[platform-url]: https://reactnative.dev
[license-badge]: https://img.shields.io/github/license/klarna/react-native-klarna-inapp-sdk?style=flat-square
[license-url]: https://github.com/klarna/react-native-klarna-inapp-sdk/blob/master/LICENSE
[klarna-badge]: https://img.shields.io/badge/%20-Developed%20at%20Klarna-black?labelColor=ffb3c7&style=flat-square&logo=data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAABAAAAAOCAYAAAAmL5yKAAAAAXNSR0IArs4c6QAAAIRlWElmTU0AKgAAAAgABQESAAMAAAABAAEAAAEaAAUAAAABAAAASgEbAAUAAAABAAAAUgEoAAMAAAABAAIAAIdpAAQAAAABAAAAWgAAAAAAAALQAAAAAQAAAtAAAAABAAOgAQADAAAAAQABAACgAgAEAAAAAQAAABCgAwAEAAAAAQAAAA4AAAAA0LMKiwAAAAlwSFlzAABuugAAbroB1t6xFwAAAVlpVFh0WE1MOmNvbS5hZG9iZS54bXAAAAAAADx4OnhtcG1ldGEgeG1sbnM6eD0iYWRvYmU6bnM6bWV0YS8iIHg6eG1wdGs9IlhNUCBDb3JlIDUuNC4wIj4KICAgPHJkZjpSREYgeG1sbnM6cmRmPSJodHRwOi8vd3d3LnczLm9yZy8xOTk5LzAyLzIyLXJkZi1zeW50YXgtbnMjIj4KICAgICAgPHJkZjpEZXNjcmlwdGlvbiByZGY6YWJvdXQ9IiIKICAgICAgICAgICAgeG1sbnM6dGlmZj0iaHR0cDovL25zLmFkb2JlLmNvbS90aWZmLzEuMC8iPgogICAgICAgICA8dGlmZjpPcmllbnRhdGlvbj4xPC90aWZmOk9yaWVudGF0aW9uPgogICAgICA8L3JkZjpEZXNjcmlwdGlvbj4KICAgPC9yZGY6UkRGPgo8L3g6eG1wbWV0YT4KTMInWQAAAVBJREFUKBVtkz0vREEUhsdXgo5qJXohkUgQ0fgFNFpR2V5ClP6CQu9PiB6lEL1I7B9A4/treZ47c252s97k2ffMmZkz5869m1JKL/AFbzAHaiRbmsIf4BdaMAZqMFsOXNxXkroKbxCPV5l8yHOJLVipn9/vEreLa7FguSN3S2ynA/ATeQuI8tTY6OOY34DQaQnq9mPCDtxoBwuRxPfAvPMWnARlB12KAi6eLTPruOOP4gcl33O6+Sjgc83DJkRH+h2MgorLzaPy68W48BG2S+xYnmAa1L+nOxEduMH3fgjGFvZeVkANZau68B6CrgJxWosFFpF7iG+h5wKZqwt42qIJtARu/ix+gqsosEq8D35o6R3c7OL4lAnTDljEe9B3Qa2BYzmHemDCt6Diwo6JY7E+A82OnN9HuoBruAQvUQ1nSxP4GVzBDRyBfygf6RW2/gD3NmEv+K/DZgAAAABJRU5ErkJggg==
[klarna-url]: https://github.com/klarna

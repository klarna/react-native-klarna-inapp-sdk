# Klarna Mobile SDK React Native

[![NPM][npm-badge]][npm-url]
[![React Native][dependency-badge]][dependency-url]
[![Platform][platform-badge]][platform-url]
[![License][license-badge]][license-url]
[![Developed at Klarna][klarna-badge]][klarna-url]

This library wraps Klarna Mobile SDK and exposes its functionality as React Native components. It
currently has the following components:
- `KlarnaPaymentView` to integrate Klarna Payments
- `KlarnaStandaloneWebView` to integrate Klarna Standalone WebView
- `KlarnaCheckoutView` to integrate Klarna Checkout
- `KlarnaSignInSDK` to integrate Klarna Sign In

This repository also includes a test application that you can use to see how it works.

### SDK for Other Platforms

* [Android](https://github.com/klarna/klarna-mobile-sdk-android)
* [iOS](https://github.com/klarna/klarna-mobile-sdk)
* [Flutter](https://github.com/klarna/klarna-mobile-sdk-flutter)

## Requirements
* iOS 10 or later.
* Android 4.4 or later.

### Documentations

* [Overview](https://docs.klarna.com/mobile-sdk/)
* [Using the Mobile SDK on React Native](https://docs.klarna.com/mobile-sdk/reactnative/)

## Getting started

### Add Dependency

#### NPM

```shell
npm install react-native-klarna-inapp-sdk --save
```

#### Yarn

```shell
yarn add react-native-klarna-inapp-sdk
```

### Warning regarding Android integration

Both the iOS and Android integrations depend on the native SDK.

We've experienced issues with React Native 59 and above where 3rd party Gradle repositories won't
be recognized in the Android project's `build.gradle`. To address this, you'll need to add a
reference to the repository in your own app's `build.gradle`.

You can do it by adding the lines between the comments below:

```groovy
allprojects {
    repositories {
        ...
        // Add the lines below
        maven {
            url 'https://x.klarnacdn.net/mobile-sdk/'
        }
    }
}
```
## Support

If you are having any issues using the SDK in your project or if you think that something is wrong with the SDK itself, please follow our [support guide](https://github.com/klarna/react-native-klarna-inapp-sdk/blob/master/SUPPORT.md).

## Contribution

If you want to contribute to this project please follow our [contribution guide](https://github.com/klarna/react-native-klarna-inapp-sdk/blob/master/CONTRIBUTING.md).

## License

This project is licensed under
[Apache License, Version 2.0](https://github.com/klarna/react-native-klarna-inapp-sdk/blob/master/LICENSE).

<!-- Markdown images & links -->
[npm-badge]: https://img.shields.io/npm/v/react-native-klarna-inapp-sdk?style=flat-square
[npm-url]: https://www.npmjs.com/package/react-native-klarna-inapp-sdk
[dependency-badge]: https://img.shields.io/npm/dependency-version/react-native-klarna-inapp-sdk/peer/react-native?style=flat-square
[dependency-url]: https://www.npmjs.com/package/react-native-klarna-inapp-sdk?activeTab=dependencies
[platform-badge]: https://img.shields.io/badge/platform-React%20Native-lightgrey?style=flat-square
[platform-url]: https://reactnative.dev
[license-badge]: https://img.shields.io/github/license/klarna/react-native-klarna-inapp-sdk?style=flat-square
[license-url]: https://github.com/klarna/react-native-klarna-inapp-sdk/blob/master/LICENSE
[klarna-badge]: https://img.shields.io/badge/%20-Developed%20at%20Klarna-black?labelColor=ffb3c7&style=flat-square&logo=data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAABAAAAAOCAYAAAAmL5yKAAAAAXNSR0IArs4c6QAAAIRlWElmTU0AKgAAAAgABQESAAMAAAABAAEAAAEaAAUAAAABAAAASgEbAAUAAAABAAAAUgEoAAMAAAABAAIAAIdpAAQAAAABAAAAWgAAAAAAAALQAAAAAQAAAtAAAAABAAOgAQADAAAAAQABAACgAgAEAAAAAQAAABCgAwAEAAAAAQAAAA4AAAAA0LMKiwAAAAlwSFlzAABuugAAbroB1t6xFwAAAVlpVFh0WE1MOmNvbS5hZG9iZS54bXAAAAAAADx4OnhtcG1ldGEgeG1sbnM6eD0iYWRvYmU6bnM6bWV0YS8iIHg6eG1wdGs9IlhNUCBDb3JlIDUuNC4wIj4KICAgPHJkZjpSREYgeG1sbnM6cmRmPSJodHRwOi8vd3d3LnczLm9yZy8xOTk5LzAyLzIyLXJkZi1zeW50YXgtbnMjIj4KICAgICAgPHJkZjpEZXNjcmlwdGlvbiByZGY6YWJvdXQ9IiIKICAgICAgICAgICAgeG1sbnM6dGlmZj0iaHR0cDovL25zLmFkb2JlLmNvbS90aWZmLzEuMC8iPgogICAgICAgICA8dGlmZjpPcmllbnRhdGlvbj4xPC90aWZmOk9yaWVudGF0aW9uPgogICAgICA8L3JkZjpEZXNjcmlwdGlvbj4KICAgPC9yZGY6UkRGPgo8L3g6eG1wbWV0YT4KTMInWQAAAVBJREFUKBVtkz0vREEUhsdXgo5qJXohkUgQ0fgFNFpR2V5ClP6CQu9PiB6lEL1I7B9A4/treZ47c252s97k2ffMmZkz5869m1JKL/AFbzAHaiRbmsIf4BdaMAZqMFsOXNxXkroKbxCPV5l8yHOJLVipn9/vEreLa7FguSN3S2ynA/ATeQuI8tTY6OOY34DQaQnq9mPCDtxoBwuRxPfAvPMWnARlB12KAi6eLTPruOOP4gcl33O6+Sjgc83DJkRH+h2MgorLzaPy68W48BG2S+xYnmAa1L+nOxEduMH3fgjGFvZeVkANZau68B6CrgJxWosFFpF7iG+h5wKZqwt42qIJtARu/ix+gqsosEq8D35o6R3c7OL4lAnTDljEe9B3Qa2BYzmHemDCt6Diwo6JY7E+A82OnN9HuoBruAQvUQ1nSxP4GVzBDRyBfygf6RW2/gD3NmEv+K/DZgAAAABJRU5ErkJggg==
[klarna-url]: https://github.com/klarna

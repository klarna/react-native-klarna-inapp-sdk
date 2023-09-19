# Klarna Mobile SDK React Native

[![NPM][npm-badge]][npm-url]
[![React Native][dependency-badge]][dependency-url]
[![Platforms][platforms-badge]][platforms-url]
[![License][license-badge]][license-url]
[![Developed at Klarna][klarna-badge]][klarna-url]

This library wraps Klarna Mobile SDK and exposes its functionality as React Native components. It
currently supports Klarna Payments via a Payment View component.

This repository also includes a test application that you can use to see how it works.

### SDK for Other Platforms

* [Android](https://github.com/klarna/klarna-mobile-sdk)
* [iOS](https://github.com/klarna/klarna-mobile-sdk)
* [Flutter](https://github.com/klarna/klarna-mobile-sdk-flutter)

## Requirements
* iOS 10 or later.
* Android 4.4 or later.

### Documentations

* [Overview](https://docs.klarna.com/mobile-sdk/)
* [Android](https://docs.klarna.com/mobile-sdk/android/)
* [iOS](https://docs.klarna.com/mobile-sdk/ios/)
* [React Native](https://docs.klarna.com/mobile-sdk/reactnative/)

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

---

## Usage

You can import the `KlarnaPaymentView` from the library. You'll then be able to add it as a
component to your app. This component exposes callbacks as props and methods you can call via the
component's ref.

The view will self-size height-wise and grow it fill it containing view's width.

### Props

It has the following props:

| Name                    | Type | Description                                                                   |
|-------------------------| --- |-------------------------------------------------------------------------------|
| `category`              | `String` | The payment method category you want to render in your view.                  |
| `returnUrl`             | `String` | An app-defined URL scheme the component uses to return customers to your app. |
| `onInitialized`         | `() => {}` | The initialize call completed.                                                        |
| `onLoaded`              | `() => {}` | The load call completed.                                                      |
| `onLoadedPaymentReview` | `() => {}` | The load payment review call completed.                                       |
| `onAuthorized`          | `(approved, authToken, finalizeRequired) => {}` | The authorize call completed.                                                 |
| `onReauthorized`        | `(approved, authToken) => {}` | The reauthorize call completed.                                               |
| `onFinalized`           | `(approved, authToken) => {}` | The finalize call completed.                                                  |
| `onError`               | `(error) => {}` | An error occurred.                                                            |

---

#### Return URL

You can read more about how the return URL works and how to add it tou your iOS application [here](https://developers.klarna.com/documentation/in-app/ios/getting-started/#return-url) and for your Android application [here](https://developers.klarna.com/documentation/in-app/android/getting-started/#return-url).

---

### Prop callback parameters

---

#### approved: `boolean`

Determines whether the previous operation was successful and yielded an authorization token.

##### Available on:
- `onAuthorized`
- `onReauthorized`
- `onFinalized`

---

#### authToken: `string | null`

If `authorize()`, `reauthorize()` or `finalize()` succeeded, they will return a token you can submit
to your backend.

##### Available on:
- `onAuthorized`
- `onReauthorized`
- `onFinalized`

---

#### finalizeRequired: `boolean | null`

If `authorize()` requires that you additionally call `finalize()`, .

##### Available on:
- `onAuthorized`

---

#### error: `KlarnaPaymentsSDKError`

If a method failed, `onError()` will let you know via an error object. Contains `name`, `message`,
`action`, `isFatal`, `sessionId` properties.

##### Available on:
- `onError`

---
---

### View methods

Each payment view exposes a set of methods via a view's ref. You can see in the test app or in the
below example how these can be called. The methods are the following:

---

#### initialize()
Initializes the view with a session token. You have to have added the view to your application and
supplied a payment method category.

##### Parameters

| Name | Type     | Description |
|------|----------| ----------- |
| client    | `string` | The session token you get from Klarna.|

---

#### load()
Loads the view. This will render content in the view.

##### Parameters

| Name | Type                  | Description |
| ---- |-----------------------|------------|
| sessionData | `string \| undefined` | A JSON object with updated session data serialized into a string.|

---

#### loadPaymentReview()
Renders a description of the payment terms your customer has agreed to.

Once a session is authorized, you can then either render a payment review in the existing
payment view or `initialize()` a new payment view with the same session token and call this method.

##### Note:
This only works with specific payment methods and countries.

---

#### authorize()
Authorizes the payment session.

##### Parameters

| Name | Type                   | Description                                                          |
| ---- |------------------------|----------------------------------------------------------------------|
| autoFinalize | `boolean \| undefined` | A flag used to turn on/off auto-finalization for direct bank transfer. |
| sessionData | `string \| undefined`  | A JSON object with updated session data serialized into a string.|

---

#### reauthorize()
If the details of the session (e.g. cart contents, customer data) have changed, call this to update
the session and get a new authorization token.

##### Parameters

| Name | Type                  | Description |
| ---- |-----------------------| ----------- |
| sessionData | `string \| undefined` | A JSON object with updated session data serialized into a string.|

---

#### finalize()
If a specific payment method needs you to trigger a second authorization, call finalize when you're
ready.

##### Parameters

| Name | Type                  | Description |
| ---- |-----------------------|------------|
| sessionData | `String \| undefined` | A JSON object with updated session data serialized into a string.|

---

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
[platforms-badge]: https://img.shields.io/badge/platform-react%20native-lightgrey?style=flat-square
[platforms-url]: https://reactnative.dev
[license-badge]: https://img.shields.io/github/license/klarna/react-native-klarna-inapp-sdk?style=flat-square
[license-url]: https://github.com/klarna/react-native-klarna-inapp-sdk/blob/master/LICENSE
[klarna-badge]: https://img.shields.io/badge/%20-Developed%20at%20Klarna-black?labelColor=ffb3c7&style=flat-square&logo=data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAABAAAAAOCAYAAAAmL5yKAAAAAXNSR0IArs4c6QAAAIRlWElmTU0AKgAAAAgABQESAAMAAAABAAEAAAEaAAUAAAABAAAASgEbAAUAAAABAAAAUgEoAAMAAAABAAIAAIdpAAQAAAABAAAAWgAAAAAAAALQAAAAAQAAAtAAAAABAAOgAQADAAAAAQABAACgAgAEAAAAAQAAABCgAwAEAAAAAQAAAA4AAAAA0LMKiwAAAAlwSFlzAABuugAAbroB1t6xFwAAAVlpVFh0WE1MOmNvbS5hZG9iZS54bXAAAAAAADx4OnhtcG1ldGEgeG1sbnM6eD0iYWRvYmU6bnM6bWV0YS8iIHg6eG1wdGs9IlhNUCBDb3JlIDUuNC4wIj4KICAgPHJkZjpSREYgeG1sbnM6cmRmPSJodHRwOi8vd3d3LnczLm9yZy8xOTk5LzAyLzIyLXJkZi1zeW50YXgtbnMjIj4KICAgICAgPHJkZjpEZXNjcmlwdGlvbiByZGY6YWJvdXQ9IiIKICAgICAgICAgICAgeG1sbnM6dGlmZj0iaHR0cDovL25zLmFkb2JlLmNvbS90aWZmLzEuMC8iPgogICAgICAgICA8dGlmZjpPcmllbnRhdGlvbj4xPC90aWZmOk9yaWVudGF0aW9uPgogICAgICA8L3JkZjpEZXNjcmlwdGlvbj4KICAgPC9yZGY6UkRGPgo8L3g6eG1wbWV0YT4KTMInWQAAAVBJREFUKBVtkz0vREEUhsdXgo5qJXohkUgQ0fgFNFpR2V5ClP6CQu9PiB6lEL1I7B9A4/treZ47c252s97k2ffMmZkz5869m1JKL/AFbzAHaiRbmsIf4BdaMAZqMFsOXNxXkroKbxCPV5l8yHOJLVipn9/vEreLa7FguSN3S2ynA/ATeQuI8tTY6OOY34DQaQnq9mPCDtxoBwuRxPfAvPMWnARlB12KAi6eLTPruOOP4gcl33O6+Sjgc83DJkRH+h2MgorLzaPy68W48BG2S+xYnmAa1L+nOxEduMH3fgjGFvZeVkANZau68B6CrgJxWosFFpF7iG+h5wKZqwt42qIJtARu/ix+gqsosEq8D35o6R3c7OL4lAnTDljEe9B3Qa2BYzmHemDCt6Diwo6JY7E+A82OnN9HuoBruAQvUQ1nSxP4GVzBDRyBfygf6RW2/gD3NmEv+K/DZgAAAABJRU5ErkJggg==
[klarna-url]: https://github.com/klarna

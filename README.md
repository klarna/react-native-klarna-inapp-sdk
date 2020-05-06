# React Native Klarna In-App SDK

![](https://img.shields.io/github/license/klarna/react-native-klarna-inapp-sdk)
![](https://img.shields.io/npm/v/react-native-klarna-inapp-sdk)
![](https://img.shields.io/npm/dependency-version/react-native-klarna-inapp-sdk/peer/react-native)

This library wraps Klarna's In-App SDK and exposes its functionality as React Native components. It
currently supports Klarna Payments via a Payment View component.

This repository also includes a test application that you can use to see how it works.

**Looking for the pure native Klarna In-App SDK?** Check out the [Klarna In-App SDK](https://github.com/klarna/klarna-inapp-sdk) repo instead.

## Requirements
* iOS 10 or later.
* Android 4.4 or later.

## Support
If you are having any issues using the library in your project, please create a question on [Stack Overflow](https://stackoverflow.com/questions/tagged/klarna-inapp-sdk) tagged with `klarna-inapp-sdk`. If you think that something is wrong with the library itself, please create an issue.

### Klarna In-App SDK documentation
[Overview of the SDK](https://developers.klarna.com/documentation/in-app/)

## What Does React Native Klarna In-App SDK Offer?
This library allows React Native apps to add views with Klarna content to their app. We currently support Klarna Payments, allowing you to add payment views to your checkout and authorizing a session to create an order natively.

### Why should you use the SDK?
The SDK removes any possible friction in your app's checkout flow by leveraging native functionality in iOS and Android. Some of the things the SDK does are:

* **Plays nicely with 3rd-party apps.** Many customers complete their purchase through their banking application or other 3rd-party apps. We make this experience seamless not just by opening these apps, but returning your users automatically when they’re done.
* **Safeguards your users' identity.** The SDK adds an extra layer of security lowering the risk of fraudulent purchases. It also insures that your customer doesn’t have to write in any redundant info. (e.g. address or credentials) on successive purchases.
* **Improves 3D Secure flow.** If certain payment methods require opening banking pages, we’ll display an in-app browser. Your customer can safely authenticate with their bank without ever leaving your app.
* **Open links without making your customers leave your app.** As with 3D Secure, we open most resources in an in-app browser or a fullscreen overlay. This insures that your customer doesn’t ever have to leave your app.

## Getting started

`$ npm install react-native-klarna-inapp-sdk --save`

### Mostly automatic installation

`$ react-native link react-native-klarna-inapp-sdk`

### Manual installation


#### iOS

We strongly encourage you to use CocoaPods to manage the library and follow the instructions below to add the SDK as a dependency to the React Native app. If you add the SDK to CocoaPods elsewhere in the application it may cause the SDK to not be visible to the library.

1. After running `$ npm install react-native-klarna-inapp-sdk --save` go to `[your project]/ios` folder.
2. Make sure you have a Podfile ready, if not use `pod init`. Check that `platform :ios, ‘10.0’`.
3. Go to `[your project]/node_modules/react-native-klarna-inapp-sdk/react-native-klarna-inapp-sdk.podspec` and make sure that `s.dependency ‘KlarnaMobileSDK’, ’~> 2.0.12` and `s.platform = :ios, “10.0”`.
4. Add `react-native-klarna-inapp-sdk` as a dependency to your podfile (`[your project]/ios` folder)  `pod ‘react-native-klarna-inapp-sdk’, :podspec => ‘../node_modules/react-native-klarna-inapp-sdk/react-native-klarna-inapp-sdk.podspec’`.
5. Go back to `[your project]/ios` and run `pod install`.

**Note:** If you encounter the build failure error Library not loaded: `@rpath/libswiftCore.dylib`, you should create a `Swift Bridging Header`. in Xcode,  create a new .swift file in the project, it should ask if you would like to configure the bridging header, click `yes`, clean the project, clean DerivedData and build the project again.

#### Android

1. Open up `android/app/src/main/java/[...]/MainApplication.java`
  - Add `import com.klarna.inapp.sdk.KlarnaPaymentViewPackage;` to the imports at the top of the file
  - Add `new KlarnaPaymentViewPackage()` to the list returned by the `getPackages()` method
2. Append the following lines to `android/settings.gradle`:
    ```
    include ':react-native-klarna-inapp-sdk'
    project(':react-native-klarna-inapp-sdk').projectDir = new File(rootProject.projectDir, 	'../node_modules/react-native-klarna-inapp-sdk/android')
    ```
3. Insert the following lines inside the dependencies block in `android/app/build.gradle`:
    ```
    compile project(':react-native-klarna-inapp-sdk')
    ```
    
**Note:** If you encounter any build failure errors regarding the `androidx.annotations` package missing, you should enable `react-native-jetifier` in the `gradle.properties` file inside your project. See how to enable this [here](https://github.com/jumpn/react-native-jetifier).

### Warning regarding Android integration

Both the iOS and Android integrations depend on the native SDK. 

We've experienced issues with React Native 59 and above where 3rd party Gradle repositories won't
be recognized in the Android project's `build.gradle`. To address this, you'll need to add a 
reference to the repository in your own app's `build.gradle`. 

You can do it by adding the lines between the comments below:   

```gradle
allprojects {
    repositories {
        mavenLocal()
        google()
        jcenter()
        maven {
            // All of React Native (JS, Obj-C sources, Android binaries) is installed from npm
            url "$rootDir/../node_modules/react-native/android"
        }
        // Add the lines below vvv
        maven {
            url 'https://x.klarnacdn.net/mobile-sdk/'
        }
        // Add the lines above ^^^
    }
}

```

---

## Usage

You can import the `KlarnaPaymentView` from the library. You'll then be able to add it as a 
component to your app. This component exposes callbacks as props and methods you can call via the
compoent's ref. 

The view will self-size height-wise and grow it fill it containing view's width. 

### Props

It has the following props:

| Name | Type | Description |
| --- | --- | --- |
| `category`              | `String`   | The payment method category you want to render in your view.
| `onInitialized`         | `() => {}` | The initialize call succeeded. 
| `onLoaded`              | `() => {}` | The load call succeeded. 
| `onLoadedPaymentReview` | `() => {}` | The load payment review call succeeded. 
| `onAuthorized`          | `({}) => {}` | The authorize call succeeded. 
| `onReauthorized`        | `({}) => {}` | The reauthorize call succeeded. 
| `onFinalized`           | `({}) => {}` | The finalize call succeeded. 
| `onError`               | `({}) => {}` | An error occurred. 

---
---

### Prop callback parameters

The `onAuthorized`, `onReauthorized`, `onFinalized` and `onError` will provide an object (via 
`nativeEvent`) with the following parameters: 

---

#### authorized: `Boolean`

Determines whether the previous operation was successful and yielded an authorization token. 

##### Available on:
- `onAuthorized`
- `onReauthorized` 
- `onFinalized`  

---

#### authToken: `String | undefined`

If `authorize()`, `reauthorize()` or `finalize()` succeeded, they will return a token you can submit
to your backend.

##### Available on:
- `onAuthorized`
- `onReauthorized` 
- `onFinalized`  

--

#### finalizeRequired: `Boolean | undefined`

If `authorize()` requires that you additionally call `finalize()`, .

##### Available on:
- `onAuthorized`

---

#### error: `Object | undefined`

If a method failed, `onError()` will let you know via an error object.

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

| Name | Type | Description |
| ---- | ---- | ----------- |
| sessionToken | `String` | The session token you get from Klarna.
| returnUrl    | `String` | An app-defined URL scheme the component uses to return customers to your app.   

##### Return URL

You can read more about how the return URL works and how to add it tou your iOS application [here](https://developers.klarna.com/documentation/in-app/ios/getting-started/#return-url) and for your Android application [here](https://developers.klarna.com/documentation/in-app/android/getting-started/#return-url).

---

#### load()
Loads the view. This will render content in the view.

##### Parameters

| Name | Type | Description |
| ---- | ---- | ----------- |
| sessionData | `String | undefined` | A JSON object with updated session data serialized into a string.  

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

| Name | Type | Description |
| ---- | ---- | ----------- |
| autoFinalize | `String | undefined` | A JSON object with session data serialized into a string.  
| sessionData | `String | undefined` | A JSON object with updated session data serialized into a string.  

---

#### reauthorize()
If the details of the session (e.g. cart contents, customer data) have changed, call this to update
the session and get a new authorization token.

##### Parameters

| Name | Type | Description |
| ---- | ---- | ----------- |
| sessionData | `String | undefined` | A JSON object with updated session data serialized into a string.  

---

#### finalize()
If a specific payment method needs you to trigger a second authorization, call finalize when you're
ready.

##### Parameters

| Name | Type | Description |
| ---- | ---- | ----------- |
| sessionData | `String | undefined` | A JSON object with updated session data serialized into a string.  

---

### More information

You can read more about refs and how they're used [here](https://reactjs.org/docs/refs-and-the-dom.html).
If you'd like to understand what each method does, you can read about it [on Klarna Developers](https://developers.klarna.com/documentation/in-app/overview/steps-klarna-payments/).


---
---

## Example

In addition to the test app in [/TestApp](https://github.com/klarna/react-native-klarna-inapp-sdk/tree/master/TestApp), the you can see an abridged version below.

```jsx
import KlarnaPaymentView from 'react-native-klarna-inapp-sdk';

class MyCheckoutView extends React.Component {

    constructor(props) {
        super(props);
        this.state = {
            paymentViewLoaded: false
        };
    }

    componentDidMount() {
        // To initialize the component, you'll need call the component's initialize method with your
        // backend's session token and a return URL for your application. 
        // 
        // This should occur at some point when the payment view is added to your application's view, 
        // but it should only be repeated if initialization fails.
        this.refs['my_ref'].initialize('my_session_token', 'my_apps_return_url')
    }

    onInitialized = () => {
        // Once the view is initialized, you can render content into it.
        this.refs['my_ref'].load()
    }

    onLoaded = () => {
        // Once the view is loaded, the user should be able to see the payment method. They can then
        // choose to use it (it's up to you to determine how that is recognized) and tap a "buy-like"
        // button.
        this.setState({paymentViewLoaded: true})
    }

    buyButtonPressed = () => {
        // When the button is tapped, call authorize() on the view.
        this.refs['my_ref'].authorize()
    }

    onAuthorized = (event) => {
        // If successfully authorized, the authorization token can be used by your backend to create
        // an order. 
        // 
        // You can also load a payment review view to allow the user to evaluate their
        // payment choice. 
        let params = event.nativeEvent

        if (params.authorized) {
            submitAuthToken(params.authToken)
        }
    }

    render() {
        return(
            // Other parts of the view
            // ...
            // The payment view
            <KlarnaPaymentView
                category={'pay_later'}
                ref={'my_ref'}
                onInitialized={this.onInitialized}
                onLoaded={this.onLoaded}
                onAuthorized={this.onAuthorized} />
            // ...
            // Your buy button
            <Button 
                disabled={!this.state.paymentViewLoaded}
                onPress={this.buyButtonPressed} />
        );
    }
}

```

## Contributing
Thank you for reading this and taking time to contribute to React Native Klarna In-App SDK! Below is a set of guidelines to help you contribute whether you want to report a bug, come with suggestions or modify code.

### How can I contribute?
#### Reporting Bugs
This section will guide you through submitting a bug report for Klarna In-App SDK.

Before submitting a bug report, please check that the issue hasn't been reported before. If you find a *Closed* issue that seem to describe an issue that is similar to what you want to report, open a new issue and link to the original issue in the new one. When you have checked that the issue hasn't been reported before **fill out [the required template](https://github.com/klarna/react-native-klarna-inapp-sdk/blob/master/.github/ISSUE_TEMPLATE/bug_report.md)** which will help us resolve the issue faster. 

##### Submitting a Bug Report
Bugs that are submitted are tracked as [GitHub issues](https://guides.github.com/features/issues/). To report a bug, create an issue and use [the template](https://github.com/klarna/react-native-klarna-inapp-sdk/blob/master/.github/ISSUE_TEMPLATE/bug_report.md) to provide information about the bug. Explain the problem thoroughly and include any additional information that you think might help the maintainers reproduce the issue. When creating the GitHub issue please make sure that you:

* **Use a clear and descriptive title** for the issue.
* **Describe the exact steps which reproduce the problem** with as many details as possible. It's generally better to provide too much than too little information.
* **Describe the behavior you observed after following the steps** and explain exactly what the problem is with that behavior.
* **Explain which behavior you expected instead** and why.
* **Provide screenshots and/or screen recordings** that might help explain the issue you are facing. To screen record a phone connected to Android Studio or an emulator follow the steps [here](https://developer.android.com/studio/debug/am-video). To screen record on iOS follow the steps described [here](https://support.apple.com/en-us/HT207935).
* **Include relevant logs in the bug report** by putting it in a [code block](https://help.github.com/en/github/writing-on-github/getting-started-with-writing-and-formatting-on-github#multiple-lines), a [file attachment](https://help.github.com/en/github/managing-your-work-on-github/file-attachments-on-issues-and-pull-requests) or in a [gist](https://help.github.com/en/github/writing-on-github/creating-gists) and provide a link to that gist.
* **Tell how recently you started having the issue.** When was the first time you experienced the issue and was it after updating the SDK version? Or has it always been a problem?
* If the problem started happening recently, **can you reproduce it in an older version of the SDK?** What's the most recent version in which the problem doesn't happen?
* **Can you reliably reproduce the issue?** If not, explain how often it occurs and under what conditions it normally happens. For example in what environment you are running.

Include details about the device/emulator/simulator you are experiencing the issue on:

* **Which version of the SDK are you using?**
* **Which OS is this a problem in, iOS, Android or both?** What version(s)? Also add the appropriate label to the issue.
* **Did you experience the issue in simulator/emulator or on real device(s)?**

#### Contributing with Code
Before contributing please read through the [Klarna In-App SDK documentation](https://developers.klarna.com/documentation/in-app/).

##### Branching
Prefix the branch you are going to work on depending on what you are working on (bug fix or feature). Use the following prefixes when creating a new branch:

* **feature/** if the branch contains a new feature, example: `feature/my-shiny-feature`.
* **bugfix/**  if the branch contains a bug fix, example: `bugfix/my-bug-fix`.

##### Pull Requests
When creating a PR include as much information as possible about the type of enhancement, whether if it's a bugfix, new functionality or any other change. There's [a template](https://github.com/klarna/react-native-klarna-inapp-sdk/blob/master/.github/ISSUE_TEMPLATE/pull-request.md) for you to fill out which will make the review process for the maintainers faster. When creating a PR do it against the `master` branch. The PR should include:

* **A clear and descriptive title**.
* **Description of the issue** if you are fixing a bug together with a link to the relevant issue or **background for introducing a new feature**.


## License

Copyright 2019 Klarna Bank AB

Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.

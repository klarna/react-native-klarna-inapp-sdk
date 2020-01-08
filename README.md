# React Native Klarna In-App SDK

This library wraps Klarna's In-App SDK and exposes its functionality as React Native components. It
currently supports Klarna Payments via a Payment View component.

This repository also includes a test application that you can use to see how it works. 


## Getting started

`$ npm install react-native-klarna-inapp-sdk --save`

### Mostly automatic installation

`$ react-native link react-native-klarna-inapp-sdk`

### Manual installation


#### iOS

1. In XCode, in the project navigator, right click `Libraries` ➜ `Add Files to [your project's name]`
2. Go to `node_modules` ➜ `react-native-klarna-inapp-sdk` and add `KlarnaPaymentView.xcodeproj`
3. In XCode, in the project navigator, select your project. Add `libKlarnaPaymentView.a` to your project's `Build Phases` ➜ `Link Binary With Libraries`
4. Run your project (`Cmd+R`)<

#### Android

1. Open up `android/app/src/main/java/[...]/MainApplication.java`
  - Add `import com.klarna.KlarnaPaymentViewPackage;` to the imports at the top of the file
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

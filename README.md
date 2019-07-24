# react-native-klarna-payment-view

## Getting started

`$ npm install react-native-klarna-payment-view --save`

### Mostly automatic installation

`$ react-native link react-native-klarna-payment-view`

### Manual installation


#### iOS

1. In XCode, in the project navigator, right click `Libraries` ➜ `Add Files to [your project's name]`
2. Go to `node_modules` ➜ `react-native-klarna-payment-view` and add `KlarnaPaymentView.xcodeproj`
3. In XCode, in the project navigator, select your project. Add `libKlarnaPaymentView.a` to your project's `Build Phases` ➜ `Link Binary With Libraries`
4. Run your project (`Cmd+R`)<

#### Android

1. Open up `android/app/src/main/java/[...]/MainApplication.java`
  - Add `import com.klarna.KlarnaPaymentViewPackage;` to the imports at the top of the file
  - Add `new KlarnaPaymentViewPackage()` to the list returned by the `getPackages()` method
2. Append the following lines to `android/settings.gradle`:
  	```
  	include ':react-native-klarna-payment-view'
  	project(':react-native-klarna-payment-view').projectDir = new File(rootProject.projectDir, 	'../node_modules/react-native-klarna-payment-view/android')
  	```
3. Insert the following lines inside the dependencies block in `android/app/build.gradle`:
  	```
      compile project(':react-native-klarna-payment-view')
  	```


## Usage
```javascript
import KlarnaPaymentView from 'react-native-klarna-payment-view';

// TODO: What to do with the module?
KlarnaPaymentView;
```

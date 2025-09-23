# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [2.4.6] - 2025-09-22
- Kotlin and kotlin serialization versions downgraded to be compatible with kotlin versions < 2.0.0
- Changes added to allow Java version 8 compatibility.
- Updated iOS SDK version to 2.7.3

## [2.4.5] - 2025-08-18
- Updated the native iOS SDK version to 2.7.2.

## [2.4.4] - 2025-08-12
- Updated the native Android SDK version to 2.7.1.
- Added Kotlin and Kotlin Serialization support to Android implementation.

## [2.4.3] - 2025-07-03
- Fix an intermittent issue in `KlarnaCheckoutView` where the content was not displaying at all on Android.

## [2.4.2] - 2025-06-13
- Updated the native Android SDK version to 2.6.29

## [2.4.1] - 2025-06-03
- Fixed an issue on iOS where `KlarnaCheckoutView` did not display any content at all after setting the snippet.

## [2.4.0] - 2025-05-19
- Added KlarnaSignInSDK implementation.
- Updated the native iOS SDK version to 2.6.29

## [2.3.15] - 2025-05-05
- Updated the native Android SDK version to 2.6.28

## [2.3.14] - 2025-04-17
- Updated the native iOS SDK version to 2.6.28

## [2.3.13] - 2025-03-11
- Updated the native Android SDK version to 2.6.27

## [2.3.12] - 2025-03-06
- Updated the native iOS SDK version to 2.6.27
- Fixed a critical error, affecting the last 2 releases, causing a crash in certain cases with cached old SDK configurations.

## [2.3.11] - 2025-03-05
- Updated the native Android SDK version to 2.6.26
- Updated the native iOS SDK version to 2.6.26
- :warning: **This version has a critical issue on iOS only**: Please use a newer version of the SDK. Future releases will be cautiously checked to prevent these warnings.

## [2.3.10] - 2025-02-19
- Updated the native Android SDK version to 2.6.25
- Updated the native iOS SDK version to 2.6.25
- :warning: **This version has a critical issue on iOS only**: Please use a newer version of the SDK. Future releases will be cautiously checked to prevent these warnings.

## [2.3.9] - 2024-11-20
- Updated the native Android SDK version to 2.6.23

## [2.3.8] - 2024-10-30
- Updated the native Android SDK version to 2.6.22
- Updated the native iOS SDK version to 2.6.23

## [2.3.7] - 2024-08-22
- Updated iOS native SDK version to 2.6.22
- Added Kotlin Gradle Plugin

## [2.3.6] - 2024-08-06
- Updated Android native SDK version to 2.6.19

## [2.3.5] - 2024-07-04
- Fixed SDK dialogs with using activity as context for KlarnaStandaloneWebView.

## [2.3.4] - 2024-07-01
- Added flex-shrink to child checkout and payment views.

## [2.3.3] - 2024-06-27
- Added the following optional props to the `KlarnaStandaloneWebView` component:
  - `overScrollMode: 'always' | 'content' | 'never'`: allows setting the over scroll mode of the web view (Android-only).
  - `bounces: boolean`: allows controlling whether the web view's scroll view bounces past the edge of content and back again (iOS-only).

## [2.3.2] - 2024-06-18
- Added single ObjC header file for both old and new architecture implementations.

## [2.3.1] - 2024-06-11
- Fixed an issue in the KlarnaStandaloneWebView component.

## [2.3.0] - 2024-05-22
- Added KlarnaCheckoutView component

## [2.2.5] - 2024-05-13
- Updated Android native SDK version to 2.6.17
- Updated iOS native SDK version to 2.6.19

## [2.2.4] - 2024-04-19
- Updated Android native SDK version to 2.6.16
- Updated iOS native SDK version to 2.6.17

## [2.2.3] - 2024-04-16
- Added support for passing parameters as part of KlarnaWebViewKlarnaMessageEvent in KlarnaStandaloneWebView component.

## [2.2.2] - 2024-03-22
- Update Android native SDK version to 2.6.14
- Update iOS native SDK version to 2.6.16

## [2.2.1] - 2024-02-08
- Fixed a Java 8 compatibility issue.

## [2.2.0] - 2023-12-22
- Added KlarnaStandaloneWebView component
- Added support for React Native's new architecture

## [2.1.13] - 2023-09-19
- Update Android native SDK version to 2.6.8
- Update iOS native SDK version to 2.6.10
- Applied dependabot version updates

## [2.1.12] - 2023-05-11
- Update Android native SDK version to 2.6.2
- Applied dependabot version updates

## [2.1.11] - 2023-05-09
- Update iOS native SDK version to 2.6.4

## [2.1.10] - 2022-12-14
- Update Android native SDK version to 2.4.1
- Update iOS native SDK version to 2.4.1

## [2.1.9] - 2022-10-04
- Update Android native SDK version to the latest (2.3.1)

## [2.1.8] - 2022-09-20
- Fix react peer dependency (>=16.5.0)
- Add Gradle 7 compatibility

## [2.1.7] - 2022-09-12
- Update iOS native SDK version to the latest (2.2.2)
- Update Android native SDK version to the latest (2.2.1)

## [2.1.6] - 2022-03-25
- Update Android Gradle plugin version (4.2.0)

## [2.1.5] - 2022-03-25
- Update Android Gradle version to the latest (7.4.1)

## [2.1.4] - 2022-03-24
- Update iOS SDK version to the latest (2.1.5)

## [2.1.3] - 2022-02-10
- Updated Android SDK (2.1.4) & iOS SDK (2.1.4)

## [2.1.0] - 2021-11-16
- Update Android SDK & iOS SDK version to the latest (2.1.0)

## [2.0.18] - 2021-05-31
- Update Android SDK version to the latest (2.0.39)

## [2.0.17] - 2021-03-23
- Update iOS SDK version to the latest (2.0.36)

## [2.0.16] - 2020-12-09

## [2.0.15] - 2020-12-01
- Added arm64 in EXCLUDED_ARCHS for iOS.

## [2.0.14] - 2020-10-30
- Update iOS SDK version to the latest (2.0.28)

## [2.0.13] - 2020-10-21
- Update Android SDK version to the latest (2.0.28)

## [2.0.12] - 2020-10-21
- Change Android SDK version to 2.0.26

## [2.0.11] - 2020-10-08
- Update iOS SDK version to the latest (2.0.24)

## [2.0.10] - 2020-09-10
- Update iOS SDK version to the latest (2.0.21)

## [2.0.9] - 2020-07-14
- Updated web view height calculation on Android.

## [2.0.8] - 2020-06-29
- Fixed a resizing issue on Android when the web view's height was not properly updated.

## [2.0.7] - 2020-06-12
- Updated the iOS SDK version to the latest (2.0.17)

## [2.0.6] - 2020-06-08
- Updated the parameters of reauthorize and finalize methods.

## [2.0.5] - 2020-05-27
- Fixed a resizing issue on Android when the web view's scroll height value was returned null.

## [2.0.4] - 2020-05-06
- Updated the iOS SDK version to the latest (2.0.13)

## [2.0.3] - 2020-04-27
- Fixed a rendering issue while loading the payment view in Android SDK

## [2.0.2] - 2020-04-20
- Updated the iOS SDK version to the latest (2.0.12)
- Updated the Android SDK version to the latest (2.0.16)
- Updated the README file with a note about androidx annotations package
- Update dependencies

## [2.0.1] - 2020-03-31
- Update the iOS SDK version to support Swift 5.2

## [2.0.0] - 2020-03-06

## [1.0.10] - 2020-03-04
- Fixed `No such property: username for class: java.lang.String` build issue for Android.
- Migrate to AndroidX.

## [1.0.9] - 2020-02-27
- Updated the iOS SDK version to the latest (2.0.10)

## [1.0.8] - 2020-01-14
- Updated the Android SDK version to the latest (2.0.10)

## [1.0.7] - 2020-01-08
- Updated the Android SDK package name.
- Updated the Android SDK version to the latest (2.0.9)

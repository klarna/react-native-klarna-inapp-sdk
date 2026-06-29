require "json"

package = JSON.parse(File.read(File.join(__dir__, "package.json")))

Pod::Spec.new do |s|
  s.name         = "react-native-klarna-network-payment"
  s.version      = package["version"]
  s.summary      = package["description"]
  s.homepage     = package["homepage"]
  s.license      = package["license"]
  s.authors      = package["author"]

  s.platforms    = { :ios => min_ios_version_supported }
  s.swift_version = '5.0'
  s.source       = { :git => "https://github.com/klarna/react-native-klarna-inapp-sdk.git", :tag => "#{s.version}" }

  s.source_files = "ios/Sources/**/*.{h,m,mm,swift,cpp}"
  s.private_header_files = "ios/Sources/**/*.h"

  install_modules_dependencies(s)

  s.dependency 'KlarnaMobileSDK/KlarnaNetworkPayment', '2.12.0'
  s.dependency 'KlarnaMobileSDK/KlarnaNetworkPaymentButton', '2.12.0'
  s.dependency 'react-native-klarna-network-core', s.version.to_s

  s.test_spec 'Tests' do |ts|
    ts.source_files = 'ios/Tests/**/*.swift'
  end
end

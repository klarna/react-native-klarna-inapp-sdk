require "json"

package = JSON.parse(File.read(File.join(__dir__, "package.json")))

folly_compiler_flags = '-DFOLLY_NO_CONFIG -DFOLLY_MOBILE=1 -DFOLLY_USE_LIBCPP=1 -Wno-comma -Wno-shorten-64-to-32'
Pod::Spec.new do |s|
  s.name         = "react-native-klarna-inapp-sdk"
  s.version      = package["version"]
  s.summary      = package["description"]
  s.description  = <<-DESC
                  react-native-klarna-inapp-sdk
                   DESC
  s.homepage     = "https://github.com/klarna/react-native-klarna-inapp-sdk"
  s.license      = "MIT"
  # s.license    = { :type => "MIT", :file => "FILE_LICENSE" }
  s.authors      = { "Your Name" => "yourname@email.com" }
  s.platform     = :ios, "10.0"
  s.source       = { :git => "https://github.com/klarna/react-native-klarna-inapp-sdk.git", :tag => "v#{s.version}" }

  s.source_files = "ios/Sources/**/*.{h,m,mm,swift}"
  s.requires_arc = true

  s.test_spec 'KlarnaInAppSDKTests' do |test_spec|
    test_spec.source_files = 'ios/Tests/**/*.{h,m,mm}'
    test_spec.dependency 'OCMock'
  end

  s.dependency "React"
  s.dependency 'KlarnaMobileSDK', '2.4.1'
  # s.dependency "..."

  # This guard prevent to install the dependencies when we run `pod install` in the old architecture.
  if ENV['RCT_NEW_ARCH_ENABLED'] == '1' then
    s.compiler_flags = folly_compiler_flags + " -DRCT_NEW_ARCH_ENABLED=1"
    s.pod_target_xcconfig    = {
        "HEADER_SEARCH_PATHS" => "\"$(PODS_ROOT)/boost\"",
        "OTHER_CPLUSPLUSFLAGS" => "-DFOLLY_NO_CONFIG -DFOLLY_MOBILE=1 -DFOLLY_USE_LIBCPP=1",
        "CLANG_CXX_LANGUAGE_STANDARD" => "c++17",
        "OTHER_SWIFT_FLAGS" => "-DNEW_ARCH_ENABLED_SWIFT"
    }

    s.dependency "React-RCTFabric"
    s.dependency "React-Codegen"
    s.dependency "RCT-Folly"
    s.dependency "RCTRequired"
    s.dependency "RCTTypeSafety"
    s.dependency "ReactCommon/turbomodule/core"
  end

end

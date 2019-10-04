require "json"

package = JSON.parse(File.read(File.join(__dir__, "package.json")))

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
  s.platform     = :ios, "7.0"
  s.source       = { :git => "https://github.com/klarna/react-native-klarna-inapp-sdk.git", :tag => "#{s.version}" }

  s.source_files = "ios/**/*.{h,m}"
  s.requires_arc = true

  s.dependency "React"
  s.dependency 'KlarnaMobileSDK', '~> 2.0.2'
  # s.dependency "..."
end


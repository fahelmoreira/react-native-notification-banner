require 'json'

package = JSON.parse(File.read(File.join(__dir__, 'package.json')))

Pod::Spec.new do |s|
  s.name         = "RNCAsyncStorage"
  s.version      = package['version']
  s.summary      = package['description']
  s.license      = package['license']

  s.authors      = package['author']
  s.homepage     = package['homepage']
  s.platforms    = { :ios => "9.0", :tvos => "9.2" }

  s.source       = { :git => "https://github.com/fahelmoreira/react-native-notification-banner.git", :tag => "v#{s.version}" }
  s.source_files  = "ios/**/*.{h,m}"
  s.static_framework = true
  
  s.dependency 'React'
  s.dependency 'BRYXBanner'
end

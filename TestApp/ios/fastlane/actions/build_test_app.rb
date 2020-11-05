module Fastlane
    module Actions
      module SharedValues
      end
  
      class BuildTestAppAction < Action
        def self.run(params)
          test_app = params[:test_app]
          output_dir = params[:output_dir]

          other_action.xcode_select('/Applications/Xcode_12_1.app')
  
          other_action.gym(
            workspace: "../TestApp.xcworkspace",
            scheme: test_app,
            clean: true,
            output_directory: "../build/" + output_dir,
            configuration: "Release",
            export_method: "enterprise",
          )
        end
  
        #####################################################
        # @!group Documentation
        #####################################################
  
        def self.description
          "Build a test application."
        end
  
        def self.details
          "You can use this action to do cool things..."
        end
  
        def self.available_options
          [
            FastlaneCore::ConfigItem.new(key: :test_app,
                                         description: "Name of Test App to build"),
            FastlaneCore::ConfigItem.new(key: :output_dir,
                                         description: "Subdirectory to output files to"),
          ]
        end
  
        def self.authors
          ["In-App Team @ Klarna"]
        end
  
        def self.is_supported?(platform)
          platform == :ios
        end
      end
    end
  end
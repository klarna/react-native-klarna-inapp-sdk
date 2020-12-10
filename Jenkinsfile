def currentSdkVersion = ""
def gitCommit = ""

pipeline {

    agent {
        label 'mac-mini'
    }

    stages {
        stage('Initialize') {
            steps {
                script {
                    gitCommit = sh(returnStdout: true, script: 'git rev-parse HEAD').trim()
                    echo "gitCommit: ${gitCommit}"
                    currentSdkVersion = sdkVersion()
                    echo "currentSdkVersion: ${currentSdkVersion}"
                }
                withCredentials([[$class: 'UsernamePasswordMultiBinding', credentialsId: 'sys-user', usernameVariable: 'MSDK_USER', passwordVariable: 'MSDK_PASSWORD']]) {
                    sh 'security unlock-keychain -p $MSDK_PASSWORD login.keychain'
                }
            }
        }
        
        stage('Yarn Install Lib') {
            steps {
                sh "yarn install"
            }
        }

        stage('Bundle Install Lib') {
            steps {
                bash 'bundle install'
            }
        }
        
        stage('Pod Install Lib') {
            steps {
                sh "cd ios && pod install && cd .."
            }
        }

        stage('Android Native Unit Tests') {
            steps {
                sh 'cd android && ./gradlew clean && ./gradlew testDebugUnitTest && cd ..'
                junit 'android/build/test-results/testDebugUnitTest/*.xml'
            }
        }

        stage('iOS Native Unit Tests') {
            steps {
                bash 'bundle exec fastlane ios run_lib_tests'
            }
        }

        stage('Yarn Install TestApp') {
            steps {
                sh 'cd TestApp && yarn install && cd ..'
            }
        }

        stage('Pod Install TestApp') {
            steps {
                bash 'cd TestApp/ios && pod install && cd ../..'
            }
        }

        stage('Build Android TestApp') {
            steps {
                sh 'cd TestApp/android && ./gradlew clean && ./gradlew app:assembleDebug && cd ../..'
                // apk location: TestApp/android/app/build/outputs/apk/debug/app-debug.apk
                // TODO : Upload apk
            }
        }

        stage('Build iOS TestApp') {
            steps {
                bash 'bundle exec fastlane ios build_test_apps'
            }
        }

        stage('Clean Directory') {
            steps {
                script {
                    sh 'git reset --hard'
                    sh 'git clean -d -x -f'
                }
            }
        }
        
    }
}

String sdkVersion() {
    return sh(returnStdout: true, script: "sed -n -e '/\"version\"/ s/.*\\: *//p' package.json").trim().replaceAll("\"", "").replaceAll(",", "")
}

def bash(custom) {
    sh '''#!/bin/bash -l
    ''' + custom
}

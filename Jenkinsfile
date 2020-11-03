def currentSdkVersion = ""
def newSdkVersion = ""
def gitCommit = ""

pipeline {

    agent {
        label 'mac-mini'
    }

    stages {
        stage('Initialize') {
            steps {
                script {
                    currentSdkVersion = sdkVersion()
                    echo "currentSdkVersion: ${currentSdkVersion}"
                    gitCommit = sh(returnStdout: true, script: 'git rev-parse HEAD').trim()
                    echo "gitCommit: ${gitCommit}"
                }
            }
        }
        
        stage('Yarn Install Lib') {
            steps {
                sh "yarn install"
            }
        }

        stage('Android Native Unit Tests') {
            steps {
                sh 'cd android && ./gradlew clean && ./gradlew testDebugUnitTest && cd ..'
                junit 'android/build/test-results/testDebugUnitTest/*.xml'
            }
        }

        // TODO : iOS Tests

        stage('Yarn Install TestApp') {
            steps {
                sh 'cd TestApp && yarn install && cd ..'
            }
        }

        stage('Bundle Install TestApp') {
            steps {
                bash 'cd TestApp/ios && bundle install && cd ../..'
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
                bash 'cd TestApp/ios && bundle exec fastlane ios build_test_apps && cd ../..'
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

        stage('Set new version for npm package') {
            when {
                expression { env.BRANCH_NAME == 'master' }
            }
            steps {
                script {
                    timeout(time: 5, unit: 'HOURS') {
                        def choices = ["keep","patch","minor","major"];
                        def version = input  message: 'How should we version this release?',ok : 'Continue',id :'id_version',
                                        parameters:[choice(choices: choices, description: 'Select a version type for this build.', name: 'VERSION')]
                        if (version != "keep") {
                            sh "npm version $version"
                            newSdkVersion = sdkVersion()
                            echo "newSdkVersion: ${newSdkVersion}"
                            sh "git push origin master"
                        } else {
                            echo "keeping current version: ${currentSdkVersion}"
                        }
                    }
                }
            }
        }
        
        stage('Publish to npm and create a release') {
            when {
                expression { env.BRANCH_NAME == 'master' }
            }
            steps {
                script {
                    timeout(time: 1, unit: 'HOURS') {
                        newSdkVersion = sdkVersion()
                        input  message: "Publish package to npm? (v$newSdkVersion)",ok : 'Publish',id :'id_publish'
                        withCredentials([string(credentialsId: 'npm-auth-token', variable: 'NPM_TOKEN')]) {
                            sh "echo //registry.npmjs.org/:_authToken=${env.NPM_TOKEN} > .npmrc"
                            sh 'npm whoami'
                            sh 'npm publish'
                            sh 'rm .npmrc'
                        }
                        input  message: "Create release on Github? (v$newSdkVersion)",ok : 'Release',id :'id_release'
                        withCredentials([string(credentialsId: 'github-token', variable: 'GITHUB_TOKEN')]) {
                            sh "gh release create v$newSdkVersion"
                        }
                    }
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

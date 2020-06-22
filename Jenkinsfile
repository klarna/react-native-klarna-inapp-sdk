def sdkVersion = ""
def gitCommit = ""

pipeline {

    agent {
        label 'mac-mini'
    }

    stages {
        stage('Initialize') {
            steps {
                script {
                    sdkVersion = sh(returnStdout: true, script: "sed -n -e '/\"version\"/ s/.*\\: *//p' package.json").trim().replaceAll("\"", "").replaceAll(",", "")
                    echo "sdkVersion: ${sdkVersion}"
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
                sh 'cd TestApp/ios && bundle install && cd ../..'
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
                sh 'cd TestApp/ios && bundle exec fastlane ios build_test_apps && cd ../..'
            }
        }

        // TODO : npm version (parameterized, on master)
        
        stage('Publish to npm') {
            when {
                expression { env.BRANCH_NAME == 'master' }
            }
            steps {
                withCredentials([string(credentialsId: 'npm-auth-token', variable: 'NPM_TOKEN')]) {
                    sh "echo //registry.npmjs.org/:_authToken=${env.NPM_TOKEN} > .npmrc"
                    sh 'npm whoami'
                    sh 'npm publish'
                    sh 'rm .npmrc'
                }
            }
        }

    }
}

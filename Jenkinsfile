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

        stage('Build Android TestApp') {
            steps {
                sh 'cd TestApp/android && ./gradlew clean && ./gradlew app:assembleDebug && cd ../..'
                // apk location: TestApp/android/app/build/outputs/apk/debug/app-debug.apk
                // TODO : Upload apk
            }
        }

        // TODO : iOS TestApp

        stage('Clean Directory') {
            steps {
                script {
                    sh 'git reset --hard'
                    sh 'git clean -d -x -f'
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

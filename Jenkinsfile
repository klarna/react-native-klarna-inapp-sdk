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
        
        stage('Yarn Install') {
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

        // TODO : TestApp & integration tests

        // TODO : npm version (patameterized, on master)
        // TODO : npm publish (on master)

    }
}

pipeline {
    agent any

    environment {
//        PORT = "8082"
    }

    stages {
        stage('Pull') {
            steps {
//                echo "target branch ${Branch}"
//                git url: "${GIT_URL}", branch: "${Branch}", poll: true, changelog: true
            }
        }

        stage('Gradle Build') {
            steps {
//                sh 'chmod +x gradlew &amp;&amp; ./gradlew clean &amp;&amp; ./gradlew build -x test --stacktrace'
                // sh 'yarn --cwd ./front install --network-timeout 100000'
                // sh 'yarn --cwd ./front build'
            }
        }

        stage('Docker Build') {
            steps {
            }
        }

        stage('Deploy') {
            steps{
            }
        }

       stage('Finish') {
            steps{
            }
        }
    }
}
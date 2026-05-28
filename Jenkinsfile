pipeline {
    agent any

    environment {
        PORT = "8082"
    }

    stages {
        stage('Pull') {
            steps {
                echo "Pull"
//                echo "target branch ${Branch}"
//                git url: "${GIT_URL}", branch: "${Branch}", poll: true, changelog: true
            }
        }

        stage('Gradle Build') {
            steps {
                echo "Gradle Build"

//                sh 'chmod +x gradlew &amp;&amp; ./gradlew clean &amp;&amp; ./gradlew build -x test --stacktrace'
                // sh 'yarn --cwd ./front install --network-timeout 100000'
                // sh 'yarn --cwd ./front build'
            }
        }

        stage('Docker Build') {
            steps {
                echo "Docker Build"

            }
        }

        stage('Deploy') {
            steps{
                echo "Deploy"
            }
        }

       stage('Finish') {
            steps{
                echo "Finish"
            }
        }
    }
}
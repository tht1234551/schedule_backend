pipeline {
    agent any

    environment {
        PORT = "8082"
    }

    stages {
        stage('Gradle Build') {
            steps {
                withEnv(["JAVA_HOME=${tool 'openjdk-21.0.2'}", "PATH=${tool 'openjdk-21.0.2'}/bin:${env.PATH}"]) {

                }
                sh "chmod +x gradlew"
                sh "./gradlew clean"
                sh "./gradlew build"

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
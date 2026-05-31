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
                sh "./gradlew build -x test --stacktrace"

//                sh 'chmod +x gradlew &amp;&amp; ./gradlew clean &amp;&amp; ./gradlew build -x test --stacktrace'
                // sh 'yarn --cwd ./front install --network-timeout 100000'
                // sh 'yarn --cwd ./front build'
            }
        }

        stage('make deploy file'){
            steps{
                script{
                    //baseline 커맨드를 이용해 jar파일명을 가져온다
                    env.jarname = sh (script: 'basename build/libs/*.jar', returnStdout: true ).trim()
                    echo env.jarname

                    //war 파일과 config폴더를 묶어서 압축한다.
                    sh ("mv build/libs/*.jar ./")
                    sh ("tar -czvf ${env.jarname}.tar.gz *.jar")
                    sh ("rm -f *.jar")
                }
            }
        }

        stage('SSH transfer') {
            steps([$class: 'BapSshPromotionPublisherPlugin']) {
                sshPublisher(
                    continueOnError: false,
                    failOnError: true,
                    publishers: [
                        sshPublisherDesc(
                            configName: "oracle server",//Jenkins 시스템 정보에 사전 입력한 서버 ID
                            verbose: true,
                            transfers: [
                                sshTransfer(
                                    sourceFiles: "${env.warname}.tar.gz", //전송할 파일
                                    removePrefix: "", //파일에서 삭제할 경로가 있다면 작성
                                    remoteDirectory: "/home/rocky", //배포할 위치
                                    execCommand: "ls -al /home/rocky" //원격지에서 실행할 커맨드
                                )
                            ]
                        )
                    ]
                )
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
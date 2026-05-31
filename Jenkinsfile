pipeline {
    agent any

    tools {
        jdk 'openjdk-21.0.2'
    }

    stages {
        stage('Gradle Build') {
            steps {
                withCredentials([file(credentialsId: 'application-secret.yaml', variable: 'secretFile')]) {
                    sh 'cp $secretFile ./src/main/resources/application-secret.yaml'
//                    sh 'sudo chmod 644 src/main/resources/application-secret.yaml'
                }

                sh "chmod +x gradlew"
                sh "./gradlew clean"
                sh "./gradlew build -x test --stacktrace"

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

        stage('SSH transfer & Deploy') {
            steps([$class: 'BapSshPromotionPublisherPlugin']) {
                sshPublisher(
                    continueOnError: false,
                    failOnError: true,
                    publishers: [
                        sshPublisherDesc(
                            configName: "oracle server",//Jenkins 시스템 정보에 사전 입력한 서버 ID
                            verbose: true,
                            transfers: [
                                // 1) 배포 스크립트/nginx 설정 전송 (deploy/ 하위 구조 유지)
                                sshTransfer(
                                    sourceFiles: "deploy/**", //전송할 파일
                                    remoteDirectory: "./", //배포할 위치 (~/deploy/ 로 전개됨)
                                    execCommand: "" //이 전송 후 실행할 커맨드 없음
                                ),
                                // 2) 빌드 산출물(tar.gz) 전송 후 무중단 배포 스크립트 실행
                                sshTransfer(
                                    sourceFiles: "${env.jarname}.tar.gz", //전송할 파일
                                    remoteDirectory: "./", //배포할 위치 (~/ 에 위치)
                                    execCommand: "bash ~/deploy/deploy.sh ~/${env.jarname}.tar.gz" //무중단 배포 실행
                                ),
                            ]
                        )
                    ]
                )
            }
        }

       stage('Finish') {
            steps{
                echo "Deploy finished"
            }
        }
    }
}
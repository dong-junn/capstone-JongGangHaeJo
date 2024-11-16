pipeline {
    agent any

    environment {
        JAVA_HOME = '/usr/lib/jvm/java-17-amazon-corretto.x86_64'
        PATH = "$JAVA_HOME/bin:$PATH"

        AWS_DEFAULT_REGION = 'ap-northeast-2'
        ECR_ACCOUNT_ID = '842676009106'
        ECR_REGISTRY = "${ECR_ACCOUNT_ID}.dkr.ecr.${AWS_DEFAULT_REGION}.amazonaws.com"
        ECR_REPOSITORY = 'ecr'
    }

    stages {

        stage('Git Pull') {
            steps {
                echo 'Pulling code from GitHub...'
                git branch: 'master', credentialsId: 'github', url: 'https://github.com/dong-junn/capstone-JongGangHaeJo.git'
            }
            post {
                success {
                    echo 'Git Pull stage completed successfully.'
                }
                failure {
                    echo 'Git Pull stage failed.'
                    error('Stopping pipeline due to Git Pull failure.')
                }
            }
        }

        stage('Verify Java Version') {
            steps {
                sh 'java -version'
                echo "JAVA_HOME: $JAVA_HOME"
                echo "PATH: $PATH"
            }
        }

        stage('Check Gradle Java Toolchains') {
            steps {
                sh './gradlew -q javaToolchains'
            }
        }

        stage('Load Environment Variables') {
            steps {
                withCredentials([file(credentialsId: 'env_file', variable: 'ENV_FILE')]) {
                    script {
                        // .env 파일 읽기
                        def envContent = readFile(file: ENV_FILE)
                        // 각 라인별로 처리
                        envContent.split('\n').each { line ->
                            line = line.trim()
                            // 빈 줄이나 주석은 무시
                            if (line && !line.startsWith('#')) {
                                // export 키워드 제거
                                line = line.replaceFirst(/^export\s+/, '')
                                // 변수명과 값 분리
                                def idx = line.indexOf('=')
                                if (idx > 0) {
                                    def key = line.substring(0, idx).trim()
                                    def value = line.substring(idx + 1).trim()
                                    // 따옴표 제거
                                    value = value.replaceAll(/^['"]|['"]$/, '')
                                    // 환경 변수 설정
                                    env."${key}" = value
                                }
                            }
                        }
                    }
                }
            }
        }

        stage('Check Environment Variables') {
            steps {
                script {
                    echo "DB_URL is ${env.DB_URL}"
                    echo "DB_PORT is ${env.DB_PORT}"
                }
            }
        }

        stage('Build Application with Gradle') {
            steps {
                echo 'Building the application using Gradle...'

                sh 'chmod +x ./gradlew'
                sh '''
                    ./gradlew --version
                    ./gradlew clean build --info --stacktrace
                '''
            }
        }

        stage('Build Docker Image') {
            steps {
                echo 'Building Docker image...'
                script {
                    ECR_REGISTRY = "${ECR_ACCOUNT_ID}.dkr.ecr.${AWS_DEFAULT_REGION}.amazonaws.com"
                    dockerImage = docker.build("${ECR_REGISTRY}/${ECR_REPOSITORY}:latest")
                }
            }
        }

        stage('Push Docker Image to ECR') {
            steps {
                echo 'Pushing Docker image to ECR...'
                script {
                    sh "aws ecr get-login-password --region ${AWS_DEFAULT_REGION} | docker login --username AWS --password-stdin ${ECR_REGISTRY}"

                    sh "docker push ${ECR_REGISTRY}/${ECR_REPOSITORY}:${IMAGE_TAG}"
                }
            }
        }
    }
}
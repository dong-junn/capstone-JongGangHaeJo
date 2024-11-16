pipeline {
    agent any

    environment {
        JAVA_HOME = '/usr/lib/jvm/java-17-amazon-corretto.x86_64'
        PATH = "$JAVA_HOME/bin:$PATH"
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
                echo 'Building the application...'
                sh 'chmod +x ./gradlew'
                sh '''
                    # 먼저 clean
                    ./gradlew clean

                    # bootJar 생성 (테스트 제외)
                    ./gradlew bootJar -x test -x asciidoctor --info \
                    --parallel \
                    --build-cache
                '''
            }
        }

        stage('Docker Build') {
            steps {
                withCredentials([
                    string(credentialsId: 'app-aws-access-key-id', variable: 'APP_AWS_ACCESS_KEY_ID'),
                    string(credentialsId: 'app-aws-secret-access-key', variable: 'APP_AWS_SECRET_ACCESS_KEY')
                ]) {
                    sh '''
                        docker build \
                            --secret id=aws_access_key_id,env=APP_AWS_ACCESS_KEY_ID \
                            --secret id=aws_secret_access_key,env=APP_AWS_SECRET_ACCESS_KEY \
                            --build-arg DB_URL=${DB_URL} \
                            --build-arg DB_PORT=${DB_PORT} \
                            --build-arg DB_USERNAME=${DB_USERNAME} \
                            --build-arg DB_PASSWORD=${DB_PASSWORD} \
                            --build-arg AWS_ACCESS_KEY_ID=${APP_AWS_ACCESS_KEY_ID} \
                            --build-arg AWS_SECRET_ACCESS_KEY=${APP_AWS_SECRET_ACCESS_KEY} \
                            --build-arg S3_BUCKET_NAME=${S3_BUCKET_NAME} \
                            --build-arg JWT_SECRET_KEY=${JWT_SECRET_KEY} \
                            -t repo:latest .
                    '''
                }
            }
        }

        stage('Login to ECR') {
            steps {
                sh '''
                    aws ecr get-login-password --region us-east-2 | docker login --username AWS --password-stdin 730335373015.dkr.ecr.us-east-2.amazonaws.com
                '''
            }
        }

        stage('Push Docker Image to ECR') {
            steps {
                echo 'Pushing Docker image to ECR...'
                script {
                        sh '''
                            docker push ${AWS_ACCOUNT_ID}.dkr.ecr.us-east-2.amazonaws.com/{ECR_REPO_NAME}:latest
                        '''
                }
            }
        }
    }
}
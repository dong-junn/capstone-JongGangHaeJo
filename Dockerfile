# Build stage
FROM amazoncorretto:17.0.13-al2023 AS builder
WORKDIR /build

# 그래들 파일들 복사
COPY build.gradle settings.gradle /build/
COPY gradle /build/gradle
COPY gradlew /build/
RUN chmod +x gradlew
RUN ./gradlew dependencies --no-daemon

# 소스 복사 및 빌드
COPY src /build/src
RUN ./gradlew build -x test --no-daemon

# Runtime stage
FROM amazoncorretto:17.0.13-al2023
WORKDIR /app

# 환경변수 정의
ARG DB_URL
ARG DB_PORT
ARG DB_USERNAME
ARG DB_PASSWORD
ARG AWS_ACCESS_KEY_ID
ARG AWS_SECRET_ACCESS_KEY
ARG S3_BUCKET_NAME
ARG JWT_SECRET_KEY
ARG PROFILE

# 환경변수 설정
ENV DB_URL=${DB_URL}
ENV DB_PORT=${DB_PORT}
ENV DB_USERNAME=${DB_USERNAME}
ENV DB_PASSWORD=${DB_PASSWORD}
ENV AWS_ACCESS_KEY_ID=${AWS_ACCESS_KEY_ID}
ENV AWS_SECRET_ACCESS_KEY=${AWS_SECRET_ACCESS_KEY}
ENV S3_BUCKET_NAME=${S3_BUCKET_NAME}
ENV JWT_SECRET_KEY=${JWT_SECRET_KEY}
ENV SPRING_PROFILES_ACTIVE=${PROFILE}

# 빌드된 JAR 파일 복사
COPY --from=builder /build/build/libs/*.jar app.jar

# 컨테이너 실행
ENTRYPOINT ["java", "-jar", "app.jar"]
# Build stage
FROM amazoncorretto:17.0.13-al2023 AS builder
WORKDIR /build

# 그래들 파일들 복사
COPY build.gradle settings.gradle /build/
COPY gradle /build/gradle
RUN ./gradlew dependencies --no-daemon

# 소스 복사 및 빌드
COPY src /build/src
RUN ./gradlew build -x test --no-daemon
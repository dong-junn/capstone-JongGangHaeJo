# 베이스 이미지 선택 (Java 17 사용)
FROM amazoncorretto:17-alpine

# 작업 디렉토리 설정
WORKDIR /app

# 빌드된 JAR 파일을 컨테이너로 복사
COPY build/libs/*.jar app.jar

# 컨테이너가 실행될 때 실행할 명령어
ENTRYPOINT ["java", "-jar", "app.jar"]

package jeiu.capstone.jongGangHaejo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;

@Configuration
public class AwsS3Config {

    @Bean
    public S3Client s3Client() {
        return S3Client.builder()
                .region(Region.AP_NORTHEAST_2) // 환경 변수에서 리전 정보 가져오기
                .credentialsProvider(DefaultCredentialsProvider.create()) // 환경 변수, 시스템 프로퍼티, IAM 역할 등에서 자격 증명 로드
                .build();
    }
}

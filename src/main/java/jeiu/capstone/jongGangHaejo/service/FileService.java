package jeiu.capstone.jongGangHaejo.service;

import io.awspring.cloud.s3.S3Exception;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.exception.SdkClientException;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.InputStream;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FileService {


    // application.yml에서 환경변수로 지정한 값 불러오기
    @Value("${spring.cloud.aws.s3.bucket}")
    private String bucketName;

    @Value("${spring.cloud.aws.credentials.access-key}")
    private String accessKey;

    @Value("${spring.cloud.aws.credentials.secret-key}")
    private String secretKey;

    @Value("${spring.cloud.aws.region.static}")
    private String region;

    // S3Client 초기화
    private S3Client initS3Client() {
        AwsBasicCredentials awsCreds = AwsBasicCredentials.create(accessKey, secretKey);

        return S3Client.builder()
                .region(Region.of(region))
                .credentialsProvider(StaticCredentialsProvider.create(awsCreds))
                .build();
    }


    public String uploadFile(MultipartFile file) {
        // 파일 이름이 null일 경우 기본 이름 설정
        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null || originalFilename.isBlank()) {
            throw new RuntimeException("파일 이름이 유효하지 않습니다.");
        }
        String fileName = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();  // 고유 파일명 생성

        try (InputStream inputStream = file.getInputStream()) {
            // S3에 파일 업로드 (환경 변수를 통해 S3 설정을 가져옴)
            S3Client s3 = initS3Client();  // S3Client 초기화
            s3.putObject(PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(fileName)
                    .build(),
                    software.amazon.awssdk.core.sync.RequestBody.fromInputStream(inputStream, file.getSize()));

            // 파일이 저장된 S3의 URL을 반환
            return "https://s3." + region + ".amazonaws.com/" + bucketName + "/" + fileName;

        } catch (S3Exception e) {
            throw new RuntimeException("S3에서 파일 업로드 중 오류가 발생했습니다.", e);
        } catch (SdkClientException e) {
            throw new RuntimeException("AWS SDK 오류가 발생했습니다.", e);
        } catch (Exception e) {
            throw new RuntimeException("파일 업로드 중 알 수 없는 오류가 발생했습니다.", e);
        }
    }
}

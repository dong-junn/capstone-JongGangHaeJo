package jeiu.capstone.jongGangHaejo.service;

import jeiu.capstone.jongGangHaejo.domain.File;
import jeiu.capstone.jongGangHaejo.exception.*;
import jeiu.capstone.jongGangHaejo.repository.FileRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.InputStream;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FileService {

    private final FileRepository fileRepository;

    private static final Logger logger = LoggerFactory.getLogger(FileService.class);

    @Value("${S3_BUCKET_NAME}") // 환경 변수에서 버킷 이름 가져오기
    private String bucketName;

    private final S3Client s3Client;

    /**
     * 파일을 S3에 업로드하고 파일 URL을 반환합니다.
     *
     * @param file 업로드할 파일
     * @return 업로드된 파일의 S3 URL
     */

    public File saveFile(File file) {
        return fileRepository.save(file);
    }

    public String uploadFile(MultipartFile file) {
        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null || originalFilename.isBlank()) {
            throw new InvalidFileNameException("파일 이름이 유효하지 않습니다.");
        }

        String fileName = UUID.randomUUID().toString() + "_" + originalFilename;

        try (InputStream inputStream = file.getInputStream()) {
            // S3에 파일 업로드
            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(fileName)
                    .contentType(file.getContentType())
                    .build();

            s3Client.putObject(putObjectRequest,
                    software.amazon.awssdk.core.sync.RequestBody.fromInputStream(inputStream, file.getSize()));

            // S3 URL 생성
            String fileUrl = generateFileUrl(fileName);
            logger.info("파일 업로드 성공: {}", fileUrl);
            return fileUrl;

        } catch (software.amazon.awssdk.services.s3.model.S3Exception e) {
            logger.error("S3에서 파일 업로드 중 오류 발생: {}", e.getMessage(), e);
            throw new S3UploadException("S3에서 파일 업로드 중 오류가 발생했습니다.", e);
        } catch (software.amazon.awssdk.core.exception.SdkClientException e) {
            logger.error("AWS SDK 오류 발생: {}", e.getMessage(), e);
            throw new AwsSdkException("AWS SDK 오류가 발생했습니다.", e);
        } catch (Exception e) {
            logger.error("파일 업로드 중 알 수 없는 오류 발생: {}", e.getMessage(), e);
            throw new FileUploadException("파일 업로드 중 알 수 없는 오류가 발생했습니다.", e);
        }
    }

    /**
     * 파일의 S3 URL을 생성하는 메서드
     *
     * @param fileName 업로드된 파일명
     * @return 파일의 S3 URL
     */
    private String generateFileUrl(String fileName) {
        return s3Client.utilities().getUrl(builder -> builder.bucket(bucketName).key(fileName)).toExternalForm();
    }

    // 특정 게시물에 연결된 파일들을 조회하는 메서드
    public List<File> getFilesByIds(List<Long> fileIds) {
        if (fileIds == null || fileIds.isEmpty()) {
            return Collections.emptyList();
        }
        return fileRepository.findByFileIdIn(fileIds);
    }
}

package jeiu.capstone.jongGangHaejo.service;

import jakarta.transaction.Transactional;
import jeiu.capstone.jongGangHaejo.domain.File;
import jeiu.capstone.jongGangHaejo.exception.*;
import jeiu.capstone.jongGangHaejo.exception.common.CommonErrorCode;
import jeiu.capstone.jongGangHaejo.repository.FileRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.InputStream;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FileService {

    private final FileRepository fileRepository;

    private static final Logger logger = LoggerFactory.getLogger(FileService.class);

    @Value("${S3_BUCKET_NAME}") // 환경 변수에서 버킷 이름 가져오기
    private String bucketName;

    private final S3Client s3Client;

    /**
     * 파일을 DB에 저장합니다.
     *
     * @param file 저장할 파일 엔티티
     * @return 저장된 파일 엔티티
     */
    public File saveFile(File file) {
        return fileRepository.save(file);
    }

    /**
     * 파일을 S3에 업로드하고 파일 URL을 반환합니다.
     *
     * @param file 업로드할 파일
     * @return 업로드된 파일의 S3 URL
     */

    //단일 파일를 업로드하는 메서드, 내부적으로만 사용됨
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

    /**
     * 게시물 ID와 파일 목록을 받아 파일을 업로드하고 DB에 저장한 후 파일 ID 목록을 반환합니다.
     *
     * @param files 업로드할 파일 목록
     * @return 저장된 파일의 ID 목록
     */

    //다중 파일을 업로드하는 메서드, 이를 주로 사용함
    public List<Long> uploadFiles(List<MultipartFile> files) {
        if (files == null || files.isEmpty()) {
            return Collections.emptyList();
        }

        return files.stream()
                .filter(file -> !file.isEmpty())
                .map(file -> {
                    String fileUrl = uploadFile(file); // S3에 파일 업로드 후 URL 획득

                    File fileEntity = File.builder()
                            .fileName(file.getOriginalFilename())
                            .s3Path(fileUrl)
                            .fileType(file.getContentType())
                            .fileSize(file.getSize())
                            .build();

                    // 파일을 DB에 저장하고 파일 ID 반환
                    File savedFile = saveFile(fileEntity);
                    return savedFile != null ? savedFile.getFileId() : null;
                })
                .filter(fileId -> fileId != null) // Null ID는 제외
                .collect(Collectors.toList());
    }

    /**
     * 특정 게시물에 연결된 파일들을 조회하는 메서드
     *
     * @param fileIds 파일 ID 목록
     * @return 파일 목록
     */
    public List<File> getFilesByIds(List<Long> fileIds) {
        if (fileIds == null || fileIds.isEmpty()) {
            return Collections.emptyList();
        }
        return fileRepository.findByFileIdIn(fileIds);
    }


    //단일 파일을 처리함 다중 파일은 아래에서 처리
    @Transactional
    public void deleteFile(Long fileId) {
        File file = fileRepository.findById(fileId)
                .orElseThrow(() -> new ResourceNotFoundException("파일을 찾을 수 없습니다. 파일 ID: " + fileId, CommonErrorCode.RESOURCE_NOT_FOUND));

        try {
            // S3에서 파일 삭제
            DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest.builder()
                    .bucket(bucketName)
                    .key(getFileKeyFromUrl(file.getS3Path()))
                    .build();

            s3Client.deleteObject(deleteObjectRequest);
            logger.info("S3에서 파일 삭제 성공: {}", file.getS3Path());

            // DB에서 파일 삭제
            fileRepository.delete(file);
            logger.info("DB에서 파일 삭제 성공: 파일 ID {}", fileId);

        } catch (software.amazon.awssdk.services.s3.model.S3Exception e) {
            logger.error("S3에서 파일 삭제 중 오류 발생: {}", e.getMessage(), e);
            throw new S3UploadException("S3에서 파일 삭제 중 오류가 발생했습니다.", e);
        } catch (Exception e) {
            logger.error("파일 삭제 중 알 수 없는 오류 발생: {}", e.getMessage(), e);
            throw new FileUploadException("파일 삭제 중 알 수 없는 오류가 발생했습니다.", e);
        }
    }

    /**
     * 파일 URL에서 S3 키를 추출하는 메서드
     *
     * @param fileUrl 파일의 S3 URL
     * @return S3 키
     */
    private String getFileKeyFromUrl(String fileUrl) {
        // 예시: https://bucket-name.s3.region.amazonaws.com/fileName
        // 여기서는 파일 이름만 추출한다고 가정
        return fileUrl.substring(fileUrl.lastIndexOf("/") + 1);
    }

    /**
     * 여러 파일을 삭제하는 메서드
     *
     * @param fileIds 삭제할 파일의 ID 목록
     */
    @Transactional
    public void deleteFiles(List<Long> fileIds) {
        if (fileIds == null || fileIds.isEmpty()) {
            return;
        }

        fileIds.forEach(this::deleteFile);
    }
}

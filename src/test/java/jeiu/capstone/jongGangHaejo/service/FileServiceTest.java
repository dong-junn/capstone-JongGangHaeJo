/*
package jeiu.capstone.jongGangHaejo.service;

import jeiu.capstone.jongGangHaejo.domain.File;
import jeiu.capstone.jongGangHaejo.exception.AwsSdkException;
import jeiu.capstone.jongGangHaejo.exception.FileUploadException;
import jeiu.capstone.jongGangHaejo.exception.InvalidFileNameException;
import jeiu.capstone.jongGangHaejo.exception.S3UploadException;
import jeiu.capstone.jongGangHaejo.repository.FileRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.exception.SdkClientException;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.S3Utilities;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Exception;

import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class FileServiceTest {

    @InjectMocks
    private FileService fileService;

    @Mock
    private S3Client s3Client;

    @Mock
    private S3Utilities s3Utilities;

    @Mock
    private FileRepository fileRepository;

    @BeforeEach
    void setUp() throws Exception {
        MockitoAnnotations.openMocks(this);
        // Mock S3Client utilities to return the S3Utilities mock
        when(s3Client.utilities()).thenReturn(s3Utilities);
        // Mock S3Utilities to return a valid URL
        when(s3Utilities.getUrl(any(Consumer.class))).thenReturn(new URL("http://mock-s3-url/test.txt"));
    }

    @Test
    void 파일_업로드_테스트() throws IOException {
        // given
        MockMultipartFile mockFile = new MockMultipartFile(
                "file",
                "test.txt",
                "text/plain",
                "Hello, World!".getBytes()
        );

        // when
        String fileUrl = fileService.uploadFile(mockFile, "posts");

        // then
        ArgumentCaptor<PutObjectRequest> requestCaptor = ArgumentCaptor.forClass(PutObjectRequest.class);
        verify(s3Client, times(1)).putObject(requestCaptor.capture(), any(RequestBody.class));

        PutObjectRequest capturedRequest = requestCaptor.getValue();
        assertTrue(capturedRequest.key().contains("_test.txt")); // UUID가 포함된 파일명 확인
        assertEquals("text/plain", capturedRequest.contentType());

        assertEquals("http://mock-s3-url/test.txt", fileUrl);
    }

    @Test
    void 이름이_유효하지_않은_파일_업로드() {
        // given
        MockMultipartFile mockFile = new MockMultipartFile(
                "file",
                "",
                "text/plain",
                "Hello, World!".getBytes()
        );

        // when & then
        InvalidFileNameException exception = assertThrows(InvalidFileNameException.class, () -> {
            fileService.uploadFile(mockFile, "posts");
        });

        assertEquals("파일 이름이 유효하지 않습니다.", exception.getMessage());
    }

    @Test
    void S3_업로드_중_예외_발생() throws IOException {
        // given
        MockMultipartFile mockFile = new MockMultipartFile(
                "file",
                "test.txt",
                "text/plain",
                "Hello, World!".getBytes()
        );

        when(s3Client.putObject(any(PutObjectRequest.class), any(RequestBody.class)))
                .thenThrow(S3Exception.builder().message("S3 Error").build());

        // when & then
        S3UploadException exception = assertThrows(S3UploadException.class, () -> {
            fileService.uploadFile(mockFile, "posts");
        });

        assertEquals("S3에서 파일 업로드 중 오류가 발생했습니다.", exception.getMessage());
    }

    @Test
    void AWS_SDK_예외_발생() throws IOException {
        // given
        MockMultipartFile mockFile = new MockMultipartFile(
                "file",
                "test.txt",
                "text/plain",
                "Hello, World!".getBytes()
        );

        when(s3Client.putObject(any(PutObjectRequest.class), any(RequestBody.class)))
                .thenThrow(SdkClientException.builder().message("AWS SDK Error").build());

        // when & then
        AwsSdkException exception = assertThrows(AwsSdkException.class, () -> {
            fileService.uploadFile(mockFile, "posts");
        });

        assertEquals("AWS SDK 오류가 발생했습니다.", exception.getMessage());
    }

    @Test
    void 알_수_없는_예외_발생() throws IOException {
        // given
        MockMultipartFile mockFile = new MockMultipartFile(
                "file",
                "test.txt",
                "text/plain",
                "Hello, World!".getBytes()
        );

        when(s3Client.putObject(any(PutObjectRequest.class), any(RequestBody.class)))
                .thenThrow(new RuntimeException("Unknown error"));

        // when & then
        FileUploadException exception = assertThrows(FileUploadException.class, () -> {
            fileService.uploadFile(mockFile, "posts");
        });

        assertEquals("파일 업로드 중 알 수 없는 오류가 발생했습니다.", exception.getMessage());
    }

    @Test
    void 게시물_없어도_파일_업로드_및_저장_테스트() throws IOException {
        // given
        MockMultipartFile mockFile = new MockMultipartFile(
                "file",
                "test.txt",
                "text/plain",
                "File Content".getBytes()
        );

        // Mock S3 업로드 성공
        when(fileRepository.save(any(File.class))).thenAnswer(invocation -> {
            File file = invocation.getArgument(0);
            file.setFileId(UUID.randomUUID().getMostSignificantBits() & Long.MAX_VALUE);
            return file;
        });

        // when
        List<Long> fileIds = fileService.uploadFiles(Arrays.asList(mockFile), "posts");

        // then
        assertEquals(1, fileIds.size());
        assertNotNull(fileIds.get(0));

        ArgumentCaptor<File> fileCaptor = ArgumentCaptor.forClass(File.class);
        verify(fileRepository, times(1)).save(fileCaptor.capture());

        File savedFile = fileCaptor.getValue();
        assertEquals("test.txt", savedFile.getFileName());
        assertEquals("text/plain", savedFile.getFileType());
        assertEquals("http://mock-s3-url/test.txt", savedFile.getS3Path()); // 수정된 부분
    }

    @Test
    void 첨부파일_없는_파일_업로드_테스트() {
        // given
        List<MultipartFile> files = Arrays.asList(
                new MockMultipartFile("files", "", "text/plain", new byte[0])
        );

        // when
        List<Long> fileIds = fileService.uploadFiles(files, "posts");

        // then
        assertTrue(fileIds.isEmpty());
        verify(fileRepository, never()).save(any(File.class));
    }
}
*/

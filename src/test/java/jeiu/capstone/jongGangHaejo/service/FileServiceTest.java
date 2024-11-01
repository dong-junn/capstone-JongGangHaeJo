package jeiu.capstone.jongGangHaejo.service;

import jeiu.capstone.jongGangHaejo.exception.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.mock.web.MockMultipartFile;
import software.amazon.awssdk.core.exception.SdkClientException;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.S3Utilities;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Exception;

import java.io.IOException;
import java.net.URL;
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
        String fileUrl = fileService.uploadFile(mockFile);

        // then
        ArgumentCaptor<PutObjectRequest> requestCaptor = ArgumentCaptor.forClass(PutObjectRequest.class);
        verify(s3Client, times(1)).putObject(requestCaptor.capture(), any(RequestBody.class));

        PutObjectRequest capturedRequest = requestCaptor.getValue();
        assertEquals("test.txt", capturedRequest.key().substring(capturedRequest.key().lastIndexOf('_') + 1));
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
            fileService.uploadFile(mockFile);
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
            fileService.uploadFile(mockFile);
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
            fileService.uploadFile(mockFile);
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
            fileService.uploadFile(mockFile);
        });

        assertEquals("파일 업로드 중 알 수 없는 오류가 발생했습니다.", exception.getMessage());
    }
}
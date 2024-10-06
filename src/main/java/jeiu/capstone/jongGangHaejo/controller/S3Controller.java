package jeiu.capstone.jongGangHaejo.controller;

import io.awspring.cloud.s3.S3Resource;
import io.awspring.cloud.s3.S3Template;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

    @RestController
    @RequestMapping("/api/s3")
    public class S3Controller {

        @Autowired
        private S3Template s3Template;

        @Value("${spring.cloud.aws.s3.bucket}")
        private String bucketName;

        @PostMapping("/upload")
        public ResponseEntity<String> uploadFile(@RequestParam("file") MultipartFile file) {
            String fileName = generateFileName(file.getOriginalFilename());
            try {
                // S3에 파일 업로드
                s3Template.upload(bucketName, fileName, file.getInputStream());

                return new ResponseEntity<>("File uploaded successfully: " + fileName, HttpStatus.OK);

            } catch (IOException e) {
                return new ResponseEntity<>("Error occurred while uploading file: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }

        private String generateFileName(String originalFileName) {
            return UUID.randomUUID().toString() + "_" + originalFileName;
        }

        @GetMapping("/download/{fileName}")
        public ResponseEntity<InputStreamResource> downloadFile(@PathVariable String fileName) {
            try {
                // S3에서 파일 다운로드
                S3Resource fileInputStream = s3Template.download(bucketName, fileName);

                // 파일의 MIME 타입을 설정 (여기서는 기본적으로 바이너리로 설정)
                HttpHeaders headers = new HttpHeaders();
                headers.setContentDispositionFormData("attachment", fileName);  // 다운로드를 위해 Content-Disposition 설정
                headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);

                // InputStreamResource로 파일을 반환
                return new ResponseEntity<>(new InputStreamResource(fileInputStream), headers, HttpStatus.OK);

            } catch (Exception e) {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);  // 파일을 찾지 못한 경우 404 반환
            }
        }

        private String GetgenerateFileName(String originalFileName) {
            return UUID.randomUUID().toString() + "_" + originalFileName;
        }

        @PutMapping("/upload/{fileName}")
        public ResponseEntity<String> replaceFile(
                @PathVariable String fileName,
                @RequestParam("file") MultipartFile newFile) {
            try {
                // 새로운 파일명으로 업로드하기 위해 기존 파일 이름에서 확장자 추출
                String newFileName = newFile.getOriginalFilename();

                // 기존 파일이 존재하는지 확인
                if (s3Template.objectExists(bucketName, fileName)) {
                    // 기존 파일 삭제
                    s3Template.deleteObject(bucketName, fileName);
                }

                // 새로운 파일을 S3에 업로드 (새로운 파일명으로 저장)
                s3Template.upload(bucketName, newFileName, newFile.getInputStream());

                return new ResponseEntity<>("File replaced successfully with new file name: " + newFileName, HttpStatus.OK);

            } catch (IOException e) {
                return new ResponseEntity<>("Error occurred while replacing file: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }

        @DeleteMapping("/delete/{fileName}")
        public ResponseEntity<String> deleteFile(@PathVariable String fileName) {
            try {
                // 파일 존재 여부 확인
                if (s3Template.objectExists(bucketName, fileName)) {
                    // 파일 삭제
                    s3Template.deleteObject(bucketName, fileName);
                    return new ResponseEntity<>("File deleted successfully: " + fileName, HttpStatus.OK);
                } else {
                    // 파일이 존재하지 않을 경우 404 반환
                    return new ResponseEntity<>("File not found: " + fileName, HttpStatus.NOT_FOUND);
                }

            } catch (Exception e) {
                return new ResponseEntity<>("Error occurred while deleting file: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }
    }

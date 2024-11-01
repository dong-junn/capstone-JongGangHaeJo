package jeiu.capstone.jongGangHaejo.dto.form;

import lombok.Data;

import java.util.List;

@Data //@getter, @setter, @ToString 등등 종합 선물 세트
public class SinglePostResponse {

    private String s3path;
    private String filename;
    private String username;
    private String title; //게시물 제목
    private String team; //팀원 정보
    private String content; //게시물 내용
    private String youtubelink; // 유튜브 링크

    //여러 첨부파일 가져오기
    private List<FileDTO> files;

    @Data
    public static class FileDTO {
        private String s3path; // S3에 저장된 파일 경로
        private String filename; // 파일 이름

        public FileDTO(String s3path, String filename) {
            this.s3path = s3path;
            this.filename = filename;
        }
    }
}

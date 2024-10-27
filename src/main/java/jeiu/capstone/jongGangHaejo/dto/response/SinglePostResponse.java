package jeiu.capstone.jongGangHaejo.dto.response;

import lombok.Data;

@Data //@getter, @setter, @ToString 등등 종합 선물 세트
public class SinglePostResponse {

    private String s3path;
    private String filename;
    private String username;
    private String title; //게시물 제목
    private String team; //팀원 정보
    private String content; //게시물 내용
    private String youtubelink; // 유튜브 링크
}

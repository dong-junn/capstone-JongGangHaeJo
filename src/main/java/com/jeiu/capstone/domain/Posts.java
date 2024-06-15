package com.jeiu.capstone.domain;

import lombok.*;

import javax.persistence.*;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Entity
public class Posts extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 500, nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String content;

    @Column(nullable = false)
    private String writer;

    @Column(columnDefinition = "integer default 0", nullable = false)
    private int view;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @OneToMany(mappedBy = "posts", fetch = FetchType.EAGER, cascade = CascadeType.REMOVE)
    @OrderBy("id asc") // 댓글 정렬
    private List<Comment> comments;


    @Column(length = 255)
    private String imageUrl;


    @Column(length = 255)
    private String pdfUrl;


    @Column(length = 255)
    private String videoUrl; //유튜브

    /* 게시글 수정 */
    public void update(String title, String content, String imageUrl, String pdfUrl, String videoUrl) {
        this.title = title;
        this.content = content;
        this.imageUrl = imageUrl;
        this.pdfUrl = pdfUrl;
        this.videoUrl = videoUrl;
    }
}
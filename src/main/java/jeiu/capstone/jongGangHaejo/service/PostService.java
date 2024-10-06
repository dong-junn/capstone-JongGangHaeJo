package jeiu.capstone.jongGangHaejo.service;

import jeiu.capstone.jongGangHaejo.domain.Post;
import jeiu.capstone.jongGangHaejo.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;

    public void savePost(Post post) {
        postRepository.save(post);
    }
}

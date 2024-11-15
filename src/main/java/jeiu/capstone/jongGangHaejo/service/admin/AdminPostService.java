package jeiu.capstone.jongGangHaejo.service.admin;

import jeiu.capstone.jongGangHaejo.security.dto.admin.AdminPostResponse;
import jeiu.capstone.jongGangHaejo.dto.paging.PagingDto;
import jeiu.capstone.jongGangHaejo.repository.admin.post.AdminPostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class AdminPostService {

    private final AdminPostRepository adminPostRepository;

    public List<AdminPostResponse> getList(PagingDto postSearch) {
        return adminPostRepository.getList(postSearch).stream()
                .map(AdminPostResponse::new)
                .collect(Collectors.toList());
    }
}

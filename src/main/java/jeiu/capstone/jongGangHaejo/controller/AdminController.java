package jeiu.capstone.jongGangHaejo.controller;

import jeiu.capstone.jongGangHaejo.dto.admin.AdminPostResponse;
import jeiu.capstone.jongGangHaejo.dto.paging.PagingDto;
import jeiu.capstone.jongGangHaejo.service.admin.AdminPostService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {

    private final AdminPostService adminPostService;

    @GetMapping("/posts")
    public List<AdminPostResponse> getList(@ModelAttribute PagingDto postSearch) {
        return adminPostService.getList(postSearch);
    }

}

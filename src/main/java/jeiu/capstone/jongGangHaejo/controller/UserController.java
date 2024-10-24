package jeiu.capstone.jongGangHaejo.controller;

import jeiu.capstone.jongGangHaejo.domain.User;
import jeiu.capstone.jongGangHaejo.dto.request.UserRequestDto;
import jeiu.capstone.jongGangHaejo.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/register")
    public void register(UserRequestDto dto) {
        User user = User.builder()
                .id(dto.getId())
                .password(dto.getPassword())
                .build();

        userService.join(user);
    }
}

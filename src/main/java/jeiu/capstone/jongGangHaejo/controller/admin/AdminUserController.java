package jeiu.capstone.jongGangHaejo.controller.admin;

import jakarta.persistence.EntityNotFoundException;
import jeiu.capstone.jongGangHaejo.domain.user.User;
import jeiu.capstone.jongGangHaejo.dto.admin.user.PageResponse;
import jeiu.capstone.jongGangHaejo.dto.admin.user.UserResponse;
import jeiu.capstone.jongGangHaejo.dto.admin.user.UserSearchCondition;
import jeiu.capstone.jongGangHaejo.dto.admin.user.UserUpdateDto;
import jeiu.capstone.jongGangHaejo.service.admin.AdminUserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/admin/user")
@RequiredArgsConstructor
public class AdminUserController {
    private final AdminUserService adminUserService;

    @GetMapping
    public ResponseEntity<PageResponse<UserResponse>> getUsers(
            UserSearchCondition condition,
            @PageableDefault(size = 10, sort = "id") Pageable pageable) {
        Page<User> userPage = adminUserService.findUsers(condition, pageable);
        Page<UserResponse> responsePage = userPage.map(UserResponse::from);
        return ResponseEntity.ok(new PageResponse<>(responsePage));
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> getUser(@PathVariable String id) {
        return adminUserService.findUser(id)
                .map(UserResponse::from)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new EntityNotFoundException("없는 회원입니다"));
    }

    @PostMapping
    public ResponseEntity<UserResponse> createUser(@RequestBody User user) {
        User savedUser = adminUserService.saveUser(user);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(UserResponse.from(savedUser));
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserResponse> updateUser(
            @PathVariable String id,
            @RequestBody UserUpdateDto userUpdateDto) {
        User updatedUser = adminUserService.updateUser(id, userUpdateDto);
        return ResponseEntity.ok(UserResponse.from(updatedUser));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable String id) {
        adminUserService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }
}
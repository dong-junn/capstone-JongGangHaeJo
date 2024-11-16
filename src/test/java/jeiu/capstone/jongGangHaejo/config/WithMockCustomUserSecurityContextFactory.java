package jeiu.capstone.jongGangHaejo.config;

import jeiu.capstone.jongGangHaejo.annotation.WithMockCustomUser;
import jeiu.capstone.jongGangHaejo.domain.user.Role;
import jeiu.capstone.jongGangHaejo.domain.user.User;
import jeiu.capstone.jongGangHaejo.security.config.UserConfig;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithSecurityContextFactory;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class WithMockCustomUserSecurityContextFactory implements WithSecurityContextFactory<WithMockCustomUser> {

    @Override
    public SecurityContext createSecurityContext(WithMockCustomUser customUser) {
        SecurityContext context = SecurityContextHolder.createEmptyContext();

        Set<Role> roles = new HashSet<>(Arrays.asList(customUser.roles()));

        User user = User.builder()
                .name(customUser.username())
                .password(customUser.password())
                .roles(roles)
                .build();

        UserConfig userConfig = new UserConfig(user);

        Authentication auth = new UsernamePasswordAuthenticationToken(
                userConfig,
                null,
                userConfig.getAuthorities()
        );

        context.setAuthentication(auth);
        return context;
    }
}


package jeiu.capstone.jongGangHaejo.annotation;

import jeiu.capstone.jongGangHaejo.config.WithMockCustomUserSecurityContextFactory;
import jeiu.capstone.jongGangHaejo.domain.user.Role;
import org.springframework.security.test.context.support.WithSecurityContext;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.TYPE})
@WithSecurityContext(factory = WithMockCustomUserSecurityContextFactory.class)
public @interface WithMockCustomUser {
    String username() default "testUser";
    String password() default "password";
    Role[] roles() default {Role.ROLE_USER};
}

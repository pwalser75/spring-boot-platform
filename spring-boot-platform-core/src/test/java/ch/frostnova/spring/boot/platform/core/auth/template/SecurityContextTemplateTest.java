package ch.frostnova.spring.boot.platform.core.auth.template;

import ch.frostnova.spring.boot.platform.api.auth.UserInfo;
import ch.frostnova.spring.boot.platform.api.auth.UserInfoProvider;
import ch.frostnova.spring.boot.platform.core.auth.UserTestBase;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static ch.frostnova.spring.boot.platform.api.auth.UserInfo.userInfo;
import static java.util.UUID.randomUUID;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for the {@link SecurityContextTemplate}.
 */
@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {SecurityContextTemplateImpl.class})
public class SecurityContextTemplateTest extends UserTestBase {

    @Autowired
    private SecurityContextTemplate securityContextTemplate;

    @Autowired
    private UserInfoProvider userInfoProvider;

    @Test
    void shouldRunAsUser() {
        UserInfo userInfo = randomUserInfo();
        assertThat(userInfoProvider.getUserInfo().isAuthenticated()).isFalse();

        securityContextTemplate.runAs(userInfo, () -> {
            UserInfo runAsUserInfo = userInfoProvider.getUserInfo();
            assertThat(runAsUserInfo.isAuthenticated()).isTrue();
            assertThat(runAsUserInfo).isEqualTo(userInfo);
        });

        assertThat(userInfoProvider.getUserInfo().isAuthenticated()).isFalse();
    }

    @Test
    void shouldSupplyAsUser() {
        UserInfo userInfo = randomUserInfo();
        assertThat(userInfoProvider.getUserInfo().isAuthenticated()).isFalse();

        String message = securityContextTemplate.runAs(userInfo, () -> {
            UserInfo runAsUserInfo = userInfoProvider.getUserInfo();
            assertThat(runAsUserInfo.isAuthenticated()).isTrue();
            assertThat(runAsUserInfo).isEqualTo(userInfo);
            return "Hello " + runAsUserInfo.getLogin();
        });

        assertThat(userInfoProvider.getUserInfo().isAuthenticated()).isFalse();
        assertThat(message).isEqualTo("Hello " + userInfo.getLogin());

    }

    private UserInfo randomUserInfo() {
        return userInfo(randomUUID().toString()).tenant(randomUUID().toString()).build();
    }
}

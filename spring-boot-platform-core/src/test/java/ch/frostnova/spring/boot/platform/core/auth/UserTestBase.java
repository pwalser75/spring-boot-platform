package ch.frostnova.spring.boot.platform.core.auth;

import ch.frostnova.spring.boot.platform.api.auth.UserInfo;
import ch.frostnova.spring.boot.platform.core.scope.TaskScope;
import ch.frostnova.spring.boot.platform.core.scope.TaskScopeConfig;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static ch.frostnova.spring.boot.platform.api.auth.UserInfo.userInfo;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {TaskScopeConfig.class, CurrentUserInfo.class})
public abstract class UserTestBase {

    @Autowired
    private CurrentUserInfo currentUserInfo;

    @BeforeEach
    public void initUserTestbase() {
        if (TaskScope.isActive()) {
            TaskScope.destroy();
        }
        TaskScope.init();
        logout();
    }

    @AfterEach
    public void cleanupUserTestbase() {
        logout();
        TaskScope.destroy();
    }

    /**
     * Login as specific user without tenant.
     */
    protected void login(String loginId) {
        login(null, loginId);
    }

    /**
     * Login as specific user with a specific tenant and the optionally given roles.
     */
    protected void login(String tenant, String loginId, String... roles) {
        login(userInfo(loginId).tenant(tenant).roles(roles).build());
    }

    /**
     * Login with the given userInfo.
     */
    protected void login(UserInfo userInfo) {
        currentUserInfo.setUserInfo(userInfo);
    }

    public void logout() {
        currentUserInfo.clear();
    }
}

package ch.frostnova.spring.boot.platform.core.auth;

import ch.frostnova.spring.boot.platform.core.scope.TaskScope;
import ch.frostnova.spring.boot.platform.security.api.UserInfo;
import ch.frostnova.spring.boot.platform.security.api.UserInfoProvider;
import org.slf4j.MDC;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * This task-scoped bean retains the current {@link UserInfo}.
 */
@Component
@Scope(value = TaskScope.NAME, proxyMode = ScopedProxyMode.TARGET_CLASS)
public class CurrentUserInfo implements UserInfoProvider {

    private final static String MDC_KEY_TENANT = "tenant";
    private final static String MDC_KEY_USER = "user";

    private UserInfo userInfo;

    public UserInfo getUserInfo() {
        return Optional.ofNullable(userInfo).orElseGet(UserInfo::anonymous);
    }

    public void setUserInfo(UserInfo userInfo) {
        if (userInfo == null) {
            throw new IllegalArgumentException("UserInfo is required");
        }
        this.userInfo = userInfo;

        Optional.ofNullable(userInfo.getTenant()).ifPresent(tenant -> MDC.put(MDC_KEY_TENANT, tenant));
        MDC.put(MDC_KEY_USER, userInfo.getLogin());
    }

    public void clear() {
        this.userInfo = null;
        MDC.remove(MDC_KEY_TENANT);
        MDC.remove(MDC_KEY_USER);
    }
}

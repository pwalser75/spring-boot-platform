package ch.frostnova.spring.boot.platform.core.security.template;

import ch.frostnova.spring.boot.platform.api.auth.UserInfo;
import ch.frostnova.spring.boot.platform.core.scope.CheckedRunnable;
import ch.frostnova.spring.boot.platform.core.scope.CheckedSupplier;

public interface SecurityContextTemplate {

    void runAs(UserInfo userInfo, CheckedRunnable runnable);

    <T> T runAs(UserInfo userInfo, CheckedSupplier<T> supplier);
}

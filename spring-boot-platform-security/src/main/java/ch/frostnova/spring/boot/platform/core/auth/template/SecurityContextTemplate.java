package ch.frostnova.spring.boot.platform.core.auth.template;

import ch.frostnova.spring.boot.platform.core.scope.CheckedRunnable;
import ch.frostnova.spring.boot.platform.core.scope.CheckedSupplier;
import ch.frostnova.spring.boot.platform.security.api.UserInfo;

public interface SecurityContextTemplate {

    void runAs(UserInfo userInfo, CheckedRunnable runnable);

    <T> T runAs(UserInfo userInfo, CheckedSupplier<T> supplier);
}

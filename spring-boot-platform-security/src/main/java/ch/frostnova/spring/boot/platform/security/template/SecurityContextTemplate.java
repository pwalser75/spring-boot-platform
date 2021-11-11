package ch.frostnova.spring.boot.platform.security.template;

import ch.frostnova.spring.boot.platform.security.api.UserInfo;
import ch.frostnova.spring.boot.platform.security.scope.CheckedRunnable;
import ch.frostnova.spring.boot.platform.security.scope.CheckedSupplier;

public interface SecurityContextTemplate {

    void runAs(UserInfo userInfo, CheckedRunnable runnable);

    <T> T runAs(UserInfo userInfo, CheckedSupplier<T> supplier);
}

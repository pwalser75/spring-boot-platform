package ch.frostnova.spring.boot.platform.core.auth.template;

import ch.frostnova.spring.boot.platform.api.auth.UserInfo;
import ch.frostnova.spring.boot.platform.core.auth.CurrentUserInfo;
import ch.frostnova.spring.boot.platform.core.scope.CheckedRunnable;
import ch.frostnova.spring.boot.platform.core.scope.CheckedSupplier;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static java.util.Objects.requireNonNull;

@Component
public class SecurityContextTemplateImpl implements SecurityContextTemplate {

    @Autowired
    private CurrentUserInfo currentUserInfo;

    @Override
    public void runAs(UserInfo userInfo, CheckedRunnable runnable) {
        requireNonNull(userInfo);
        requireNonNull(runnable);

        UserInfo previous = currentUserInfo.getUserInfo();
        try {
            currentUserInfo.setUserInfo(userInfo);
            runnable.runUnchecked();
        } finally {
            currentUserInfo.setUserInfo(previous);
        }
    }

    @Override
    public <T> T runAs(UserInfo userInfo, CheckedSupplier<T> supplier) {
        requireNonNull(userInfo);
        requireNonNull(supplier);

        UserInfo previous = currentUserInfo.getUserInfo();
        try {
            currentUserInfo.setUserInfo(userInfo);
            return supplier.supplyUnchecked();
        } finally {
            currentUserInfo.setUserInfo(previous);
        }
    }
}
package ch.frostnova.spring.boot.platform.core.auth.impl;

import ch.frostnova.spring.boot.platform.api.auth.UserInfo;
import ch.frostnova.spring.boot.platform.api.auth.UserInfoProvider;
import ch.frostnova.spring.boot.platform.core.scope.TaskScope;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope(TaskScope.NAME)
public class UserInfoProviderImpl implements UserInfoProvider {

    @Autowired
    private AuthorizationServiceImpl authorizationService;

    private UserInfo userInfo;

    @Override
    public UserInfo get() {
        if (userInfo == null) {
            userInfo = authorizationService.getUserInfo();
        }
        return userInfo;
    }
}

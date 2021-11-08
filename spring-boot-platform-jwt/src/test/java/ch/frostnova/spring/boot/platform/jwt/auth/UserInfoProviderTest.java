package ch.frostnova.spring.boot.platform.jwt.auth;

import ch.frostnova.spring.boot.platform.api.auth.UserInfo;
import ch.frostnova.spring.boot.platform.api.auth.UserInfoProvider;
import ch.frostnova.spring.boot.platform.core.auth.impl.UserInfoProviderImpl;
import org.junit.jupiter.api.extension.ExtendWith;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {UserInfoProviderImpl.class})
public class UserInfoProviderTest {

    @Autowired
    private UserInfoProvider userInfoProvider;

    private Logger logger;

    private void foo() {

        //TODO
        UserInfo userInfo = userInfoProvider.get();

    }

}

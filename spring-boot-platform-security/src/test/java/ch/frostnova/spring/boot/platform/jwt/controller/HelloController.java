package ch.frostnova.spring.boot.platform.jwt.controller;

import ch.frostnova.spring.boot.platform.security.api.UserInfo;
import ch.frostnova.spring.boot.platform.security.api.UserInfoProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

import static org.springframework.http.MediaType.TEXT_PLAIN_VALUE;

@RestController
@RequestMapping(path = "hello")
public class HelloController {

    @Autowired
    private UserInfoProvider userInfoProvider;

    @GetMapping(produces = TEXT_PLAIN_VALUE)
    public String hello() {
        UserInfo userInfo = userInfoProvider.getUserInfo();
        String name = Optional.ofNullable(userInfo.getAdditionalClaims().get("display-name")).orElseGet(userInfo::getLogin);
        return String.format("Hello %s", name);
    }
}

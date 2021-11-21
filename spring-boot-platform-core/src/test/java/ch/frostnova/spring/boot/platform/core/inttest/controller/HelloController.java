package ch.frostnova.spring.boot.platform.core.inttest.controller;

import ch.frostnova.spring.boot.platform.api.auth.UserInfo;
import ch.frostnova.spring.boot.platform.api.auth.UserInfoProvider;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
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

    @ApiOperation(value = "Greets the user (authenticated or anonymous) with a welcome message", response = String.class)
    @ApiImplicitParam(name = "Authorization", value = "Authorization header, e.g. \"Bearer {access-token}\"", required = true, paramType = "header", dataTypeClass = String.class, example = "Bearer access_token")
    @ApiResponses({
            @ApiResponse(code = 200, message = "ok")
    })
    @GetMapping(produces = TEXT_PLAIN_VALUE)
    public String hello() {
        UserInfo userInfo = userInfoProvider.getUserInfo();
        String name = Optional.ofNullable(userInfo.getAdditionalClaims().get("display-name")).orElseGet(userInfo::getLogin);
        return String.format("Hello %s", name);
    }
}

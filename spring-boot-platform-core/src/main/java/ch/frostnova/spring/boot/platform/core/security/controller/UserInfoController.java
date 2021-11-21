package ch.frostnova.spring.boot.platform.core.security.controller;

import ch.frostnova.spring.boot.platform.api.auth.UserInfo;
import ch.frostnova.spring.boot.platform.api.auth.UserInfoProvider;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Api(value = "User Info controller")
@RequestMapping(path = "user")
@CrossOrigin(origins = "*",
        allowedHeaders = "origin, content-type, accept, authorization",
        methods = {RequestMethod.GET},
        maxAge = 1209600)
public class UserInfoController {

    @Autowired
    private UserInfoProvider userInfoProvider;

    @ApiOperation(value = "Exposes information for the current user", response = UserInfo.class)
    @ApiImplicitParam(name = "Authorization", value = "Authorization header, e.g. \"Bearer {access-token}\"", required = true, paramType = "header", dataTypeClass = String.class, example = "Bearer access_token")
    @ApiResponses({
            @ApiResponse(code = 200, message = "ok")
    })
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public UserInfo getUserInfo() {
        return userInfoProvider.getUserInfo();
    }
}

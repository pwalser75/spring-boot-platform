package ch.frostnova.spring.boot.platform.jwt.controller;

import ch.frostnova.spring.boot.platform.security.api.RequireRole;
import ch.frostnova.spring.boot.platform.security.api.UserInfoProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.http.MediaType.TEXT_PLAIN_VALUE;

@RestController
@RequestMapping(path = "access")
public class AccessTestController {

    @Autowired
    private UserInfoProvider userInfoProvider;

    @GetMapping(value = "/public", produces = TEXT_PLAIN_VALUE)
    public String publicResource() {
        return "public";
    }

    @GetMapping(value = "/private", produces = TEXT_PLAIN_VALUE)
    @RequireRole
    public String privateResource() {
        return "private";
    }

    @GetMapping(value = "/admin", produces = TEXT_PLAIN_VALUE)
    @RequireRole("ADMIN")
    public String adminResource() {
        return "admin";
    }
}

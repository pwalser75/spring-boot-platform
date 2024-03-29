package ch.frostnova.spring.boot.platform.jwt.controller;

import ch.frostnova.spring.boot.platform.api.auth.UserInfo;
import ch.frostnova.spring.boot.platform.jwt.service.JWTSigningService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.NotBlank;
import java.time.Duration;
import java.time.OffsetDateTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import static ch.frostnova.spring.boot.platform.api.auth.UserInfo.userInfo;
import static java.time.OffsetDateTime.now;
import static java.util.stream.Collectors.joining;
import static org.springframework.http.MediaType.TEXT_PLAIN_VALUE;

@RestController
@ConditionalOnBean(JWTSigningService.class)
@Api(value = "Login Controller - use for testing only, never in production")
@RequestMapping(path = "dev/login")
@CrossOrigin(origins = "*",
        allowedHeaders = "origin, content-type, accept, authorization",
        methods = {RequestMethod.GET},
        maxAge = 1209600)
public class LoginController {

    @Autowired
    private JWTSigningService jwtSigningService;

    @ApiOperation(value = "Issue a JWT for the given tenant/user and claims", response = String.class)
    @ApiResponses({
            @ApiResponse(code = 200, message = "ok")
    })
    @GetMapping(path = "/{tenant}/{user}", produces = TEXT_PLAIN_VALUE)
    public String login(@ApiParam(value = "Tenant id, required")
                        @PathVariable("tenant") @NotBlank String tenant,
                        @ApiParam(value = "User id (subject), required")
                        @PathVariable("user") @NotBlank String login,
                        @ApiParam(value = "Set of granted roles (optional)")
                        @RequestParam(value = "roles", required = false) Set<String> roles,
                        @ApiParam(value = "Valid from, in ISO date time format, e.g. 2020-01-01T12:34:56+01:00 (optional, defaults to now)")
                        @RequestParam(value = "valid-from", required = false) OffsetDateTime validFrom,
                        @ApiParam(value = "Validity (duration, optional (default: 1h) in ?w?d?h?m?s?ms format, e.g. 5d, or 5m30s, or 1h23m56s")
                        @RequestParam(value = "duration", required = false, defaultValue = "1h") Duration duration,
                        HttpServletRequest request) {

        Map<String, String> additionalClaims = new HashMap<>();

        Map<String, String[]> parameterMap = request.getParameterMap();
        Set<String> reserved = Set.of("roles", "valid-from", "duration");
        parameterMap.forEach((k, v) -> {
            if (!reserved.contains(k)) {
                additionalClaims.put(k, Arrays.stream(v).map(String::valueOf).collect(joining(",")));
            }
        });

        UserInfo userInfo = userInfo(login).tenant(tenant).roles(roles).additionalClaims(additionalClaims).build();
        return jwtSigningService.createJWT(userInfo, Optional.ofNullable(validFrom).orElse(now()), duration);
    }
}

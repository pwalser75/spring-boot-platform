package ch.frostnova.spring.boot.platform.security.controller;

import ch.frostnova.spring.boot.platform.security.api.RequireRole;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.http.MediaType.TEXT_PLAIN_VALUE;

@RestController
@RequestMapping(path = "access")
public class AccessTestController {

    @ApiOperation(value = "Public access to a resource, not authentication or role required", response = String.class)
    @ApiImplicitParam(name = "Authorization", value = "Authorization header, e.g. \"Bearer {access-token}\"", required = true, paramType = "header", dataTypeClass = String.class, example = "Bearer access_token")
    @ApiResponses({
            @ApiResponse(code = 200, message = "ok")
    })
    @GetMapping(value = "/public", produces = TEXT_PLAIN_VALUE)
    public String publicResource() {
        return "public";
    }

    @ApiOperation(value = "Private access to a resource, authentication but no roles required", response = String.class)
    @ApiImplicitParam(name = "Authorization", value = "Authorization header, e.g. \"Bearer {access-token}\"", required = true, paramType = "header", dataTypeClass = String.class, example = "Bearer access_token")
    @ApiResponses({
            @ApiResponse(code = 200, message = "ok"),
            @ApiResponse(code = 401, message = "unauthenticated")
    })
    @GetMapping(value = "/private", produces = TEXT_PLAIN_VALUE)
    @RequireRole
    public String privateResource() {
        return "private";
    }

    @ApiOperation(value = "Restricted access to a resource, authentication and mapped role 'ADMIN''", response = String.class)
    @ApiImplicitParam(name = "Authorization", value = "Authorization header, e.g. \"Bearer {access-token}\"", required = true, paramType = "header", dataTypeClass = String.class, example = "Bearer access_token")
    @ApiResponses({
            @ApiResponse(code = 200, message = "ok"),
            @ApiResponse(code = 401, message = "unauthenticated"),
            @ApiResponse(code = 403, message = "unauthorized")
    })
    @GetMapping(value = "/admin", produces = TEXT_PLAIN_VALUE)
    @RequireRole("ADMIN")
    public String adminResource() {
        return "admin";
    }
}

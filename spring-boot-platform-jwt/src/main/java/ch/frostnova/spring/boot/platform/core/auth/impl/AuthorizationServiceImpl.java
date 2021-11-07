package ch.frostnova.spring.boot.platform.core.auth.impl;

import ch.frostnova.spring.boot.platform.api.auth.UserInfo;
import ch.frostnova.spring.boot.platform.core.auth.AuthorizationService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import static ch.frostnova.spring.boot.platform.api.auth.UserInfo.anonymous;
import static ch.frostnova.spring.boot.platform.api.auth.UserInfo.userInfo;
import static java.util.stream.Collectors.toSet;

@Service
public class AuthorizationServiceImpl implements AuthorizationService {

    @Override
    public UserInfo getUserInfo() {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        Object details = authentication.getDetails();
        if (details instanceof UserInfo) {
            return (UserInfo) details;
        }
        if (!authentication.isAuthenticated()) {
            return anonymous();
        }
        return userInfo(String.valueOf(authentication.getPrincipal()))
                .roles(authentication.getAuthorities().stream().map(String::valueOf).collect(toSet()))
                .build();
    }
}

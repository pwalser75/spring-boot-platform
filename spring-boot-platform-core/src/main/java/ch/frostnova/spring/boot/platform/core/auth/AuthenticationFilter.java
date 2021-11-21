package ch.frostnova.spring.boot.platform.core.auth;

import ch.frostnova.spring.boot.platform.api.auth.AuthenticationProvider;
import ch.frostnova.spring.boot.platform.api.auth.UserInfo;
import ch.frostnova.spring.boot.platform.api.exception.InvalidCredentialsException;
import ch.frostnova.spring.boot.platform.api.exception.UnauthenticatedException;
import ch.frostnova.spring.boot.platform.core.exception.ErrorResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Set;

import static ch.frostnova.spring.boot.platform.api.auth.UserInfo.anonymous;
import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.toSet;
import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

/**
 * An HTTP request filter which attempts to authenticate a user using the value of the 'Authorization' request header
 * and the discovered implementations of {@link AuthenticationProvider}.
 *
 * @author pwalser
 * @since 2021-11-21
 */
public class AuthenticationFilter extends OncePerRequestFilter {

    private final static Logger logger = LoggerFactory.getLogger(AuthenticationFilter.class);

    private final List<AuthenticationProvider> authenticationProviders;
    private final CurrentUserInfo currentUserInfo;
    private final ObjectMapper objectMapper;

    public AuthenticationFilter(List<AuthenticationProvider> authenticationProviders, CurrentUserInfo currentUserInfo, ObjectMapper objectMapper) {
        this.authenticationProviders = authenticationProviders != null ? authenticationProviders : emptyList();
        this.currentUserInfo = currentUserInfo;
        this.objectMapper = objectMapper;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws IOException {

        try {
            UserInfo userInfo = authenticate(request);
            if (userInfo == null) {
                userInfo = anonymous();
            }
            currentUserInfo.setUserInfo(userInfo);
            SecurityContextHolder.getContext().setAuthentication(authentication(userInfo));
            filterChain.doFilter(request, response);
        } catch (Exception ex) {
            ErrorResponse errorResponse = new ErrorResponse(FORBIDDEN.name(), ex, ex.getMessage());
            response.setContentType(APPLICATION_JSON_VALUE);
            response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
            response.setStatus(FORBIDDEN.value());
            String logMessage = ex.getClass().getSimpleName() + ": " + ex.getMessage();
            if (logger.isDebugEnabled()) {
                logger.debug(logMessage, ex);
            } else {
                logger.info(logMessage);
            }
        } finally {
            currentUserInfo.clear();
        }
    }

    private UserInfo authenticate(HttpServletRequest request) {
        String authorizationHeader = request.getHeader("Authorization");
        if (authorizationHeader == null || authorizationHeader.isEmpty()) {
            // Unauthenticated request
            return null;
        }
        try {
            for (AuthenticationProvider authenticationProvider : authenticationProviders) {
                UserInfo userInfo = authenticationProvider.authenticate(authorizationHeader);
                if (userInfo != null) {
                    // authenticated by provider
                    return userInfo;
                }
            }
        } catch (Exception ex) {
            String message = String.format("authentication failed: %s: %s", ex.getClass().getSimpleName(), ex.getMessage());
            logger.error(message, ex);
            throw new UnauthenticatedException(message);
        }

        // could not be authenticated by any provider
        throw new InvalidCredentialsException();
    }

    private static Authentication authentication(UserInfo userInfo) {
        if (userInfo == null) {
            return null;
        }
        Set<SimpleGrantedAuthority> grantedAuthorities = userInfo.getRoles().stream()
                .map(SimpleGrantedAuthority::new)
                .collect(toSet());

        PreAuthenticatedAuthenticationToken authentication = new PreAuthenticatedAuthenticationToken(userInfo.getLogin(), null, grantedAuthorities);
        authentication.setDetails(userInfo);
        authentication.setAuthenticated(userInfo.isAuthenticated());
        return authentication;
    }
}

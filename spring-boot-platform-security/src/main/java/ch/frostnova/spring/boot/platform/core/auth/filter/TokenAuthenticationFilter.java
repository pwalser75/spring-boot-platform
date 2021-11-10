package ch.frostnova.spring.boot.platform.core.auth.filter;

import ch.frostnova.spring.boot.platform.core.auth.CurrentUserInfo;
import ch.frostnova.spring.boot.platform.core.auth.TokenAuthenticator;
import ch.frostnova.spring.boot.platform.core.error.ErrorResponse;
import ch.frostnova.spring.boot.platform.security.api.UserInfo;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.JwtException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static ch.frostnova.spring.boot.platform.security.api.UserInfo.anonymous;
import static java.util.stream.Collectors.toSet;
import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

public class TokenAuthenticationFilter extends OncePerRequestFilter {

    private final static Logger logger = LoggerFactory.getLogger(TokenAuthenticationFilter.class);

    private final static Pattern BEARER_TOKEN_PATTERN = Pattern.compile("Bearer (.+)");

    private final TokenAuthenticator tokenAuthenticator;
    private final CurrentUserInfo currentUserInfo;
    private final ObjectMapper objectMapper;

    public TokenAuthenticationFilter(TokenAuthenticator tokenAuthenticator, CurrentUserInfo currentUserInfo, ObjectMapper objectMapper) {
        this.tokenAuthenticator = tokenAuthenticator;
        this.currentUserInfo = currentUserInfo;
        this.objectMapper = objectMapper;
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

    private UserInfo authenticate(HttpServletRequest request) throws AuthenticationException {
        try {
            String requestTokenHeader = request.getHeader("Authorization");
            if (requestTokenHeader == null) {
                // Unauthenticated request
                return null;
            }
            Matcher matcher = BEARER_TOKEN_PATTERN.matcher(requestTokenHeader);
            if (!matcher.matches()) {
                throw new BadCredentialsException("Expected bearer token in Authorization header");
            }
            String token = matcher.group(1);
            return tokenAuthenticator.authenticate(token);
        } catch (AuthenticationException ex) {
            throw ex;
        } catch (JwtException ex) {
            throw new BadCredentialsException(ex.getMessage(), ex);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }
}

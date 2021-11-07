package ch.frostnova.spring.boot.platform.core.auth.filter;

import ch.frostnova.spring.boot.platform.api.auth.UserInfo;
import ch.frostnova.spring.boot.platform.core.auth.TokenAuthenticator;
import ch.frostnova.spring.boot.platform.core.error.ErrorResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.JwtException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
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
import java.util.Optional;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static ch.frostnova.spring.boot.platform.api.auth.UserInfo.anonymous;
import static java.util.stream.Collectors.toSet;

public class BearerTokenAuthenticationFilter extends OncePerRequestFilter {

    private final static Logger logger = LoggerFactory.getLogger(BearerTokenAuthenticationFilter.class);

    private final static Pattern BEARER_TOKEN_PATTERN = Pattern.compile("Bearer (.+)");
    private final static String MDC_KEY_TENANT = "tenant";
    private final static String MDC_KEY_USER = "user";

    private final TokenAuthenticator tokenAuthenticator;
    private final ObjectMapper objectMapper;

    public BearerTokenAuthenticationFilter(TokenAuthenticator tokenAuthenticator, ObjectMapper objectMapper) {
        this.tokenAuthenticator = tokenAuthenticator;
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
            SecurityContextHolder.getContext().setAuthentication(authentication(userInfo));
            Optional.ofNullable(userInfo.getTenant()).ifPresent(tenant -> MDC.put(MDC_KEY_TENANT, tenant));
            MDC.put(MDC_KEY_USER, userInfo.getLogin());
            filterChain.doFilter(request, response);
        } catch (Exception ex) {
            ErrorResponse errorResponse = new ErrorResponse(HttpStatus.FORBIDDEN.name(), ex, ex.getMessage());
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
            response.setStatus(HttpStatus.FORBIDDEN.value());
            String logMessage = ex.getClass().getSimpleName() + ": " + ex.getMessage();
            if (logger.isDebugEnabled()) {
                logger.debug(logMessage, ex);
            } else {
                logger.info(logMessage);
            }
        } finally {
            MDC.remove(MDC_KEY_TENANT);
            MDC.remove(MDC_KEY_USER);
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

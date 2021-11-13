package ch.frostnova.spring.boot.platform.jwt.config;

import ch.frostnova.spring.boot.platform.core.auth.CurrentUserInfo;
import ch.frostnova.spring.boot.platform.jwt.filter.TokenAuthenticationFilter;
import ch.frostnova.spring.boot.platform.jwt.service.TokenAuthenticator;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.util.Optional;

/**
 * Spring security config
 */
@Configuration
@EnableWebSecurity
public class SpringSecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private Optional<TokenAuthenticator> tokenAuthenticator;

    @Autowired
    private CurrentUserInfo currentUserInfo;

    @Autowired
    private ObjectMapper objectMapper;

    @Override
    protected void configure(HttpSecurity httpSecurity) throws Exception {

        httpSecurity.csrf().disable();

        tokenAuthenticator.ifPresent(ta ->
                httpSecurity.addFilterBefore(new TokenAuthenticationFilter(ta, currentUserInfo, objectMapper), UsernamePasswordAuthenticationFilter.class)
        );
        httpSecurity.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
    }

    @Override
    protected void configure(AuthenticationManagerBuilder authManager) {
        // prevent autoconfiguration
    }

    @Override
    @Bean
    public AuthenticationManager authenticationManagerBean() throws Exception {
        // prevent autoconfiguration
        return super.authenticationManagerBean();
    }
}
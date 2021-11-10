package ch.frostnova.spring.boot.platform.security.api;

/**
 * Provider for the user info. The implementation will likely be a bean in REQUEST or TASK scope.
 *
 * @author pwalser
 * @since 2021-11-07
 */
public interface UserInfoProvider {

    UserInfo getUserInfo();
}

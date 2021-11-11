package ch.frostnova.spring.boot.platform.security.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Optional;
import java.util.Set;

import static java.util.Collections.emptyMap;

@Component
@ConfigurationProperties("ch.frostnova.platform.security")
public class SecurityProperties {

    private Map<String, Set<String>> roleMapping;


    public Map<String, Set<String>> getRoleMapping() {
        return Optional.ofNullable(roleMapping).orElse(emptyMap());
    }

    public void setRoleMapping(Map<String, Set<String>> roleMapping) {
        this.roleMapping = roleMapping;
    }
}

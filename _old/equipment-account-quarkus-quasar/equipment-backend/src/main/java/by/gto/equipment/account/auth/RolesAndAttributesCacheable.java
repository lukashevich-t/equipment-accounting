package by.gto.equipment.account.auth;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Контейнер для хранения ролей и атрибутов для {@link io.quarkus.security.identity.SecurityIdentity} с целью кэширования
 */
public class RolesAndAttributesCacheable {
    private Set<String> roles = new HashSet<>();
    private Map<String, Object> attributes = new HashMap<>();

    public Set<String> getRoles() {
        return roles;
    }

    public Map<String, Object> getAttributes() {
        return attributes;
    }

    public void setRoles(Set<String> roles) {
        this.roles = roles;
    }

    public void setAttributes(Map<String, Object> attributes) {
        this.attributes = attributes;
    }

    public RolesAndAttributesCacheable() {
    }

    public RolesAndAttributesCacheable(Set<String> roles, Map<String, Object> attributes) {
        this.roles = roles;
        this.attributes = attributes;
    }

    public void addRole(String role) {
        this.roles.add(role);
    }

    public void addAttribute(String key, Object value) {
        this.attributes.put(key, value);
    }
}

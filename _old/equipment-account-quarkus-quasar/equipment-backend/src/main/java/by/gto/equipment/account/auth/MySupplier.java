package by.gto.equipment.account.auth;

import by.gto.equipment.account.helpers.AutoCloseableHelper;
import io.quarkus.cache.Cache;
import io.quarkus.cache.CacheName;
import io.quarkus.cache.CacheResult;
import io.quarkus.security.identity.SecurityIdentity;
import io.quarkus.security.runtime.QuarkusSecurityIdentity;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.function.Supplier;
import javax.enterprise.context.Dependent;
import javax.enterprise.context.control.ActivateRequestContext;
import javax.inject.Inject;
import javax.sql.DataSource;
import javax.transaction.Transactional;
import io.quarkus.cache.runtime.caffeine.CaffeineCache;

@Dependent
public class MySupplier implements Supplier<SecurityIdentity> {

    //    public static final String ROLE_QUERY = "SELECT r.name FROM developing.user u INNER JOIN developing.user_role ur ON u.id = ur.user_id INNER JOIN developing.role r ON ur.role_id = r.id WHERE u.username = ?";
    public static final String ROLES_AND_ATTRIBUTES_QUERY = "SELECT r.name, u.id, u.position FROM equipment.user u INNER JOIN equipment.user_role " +
        "ur ON u.id = ur.user_id INNER JOIN equipment.role r ON ur.role_id = r.id WHERE u.login = ?";

    @Inject
    DataSource ds;

    @CacheName("security.attributes")
    Cache cache;

    private SecurityIdentity identity;

    @ActivateRequestContext
    @Transactional
    @Override
    public SecurityIdentity get() {
        if (identity.isAnonymous()) {
            return identity;
        }
        // Copy the existing identity to the builder
        QuarkusSecurityIdentity.Builder builder = QuarkusSecurityIdentity.builder()
                .setPrincipal(identity.getPrincipal())
                .addAttributes(identity.getAttributes())
                .addCredentials(identity.getCredentials())
                .addRoles(identity.getRoles());

        final RolesAndAttributesCacheable rolesAndAttributes = getRolesAndAttributes(identity.getPrincipal().getName());
        builder.addRoles(rolesAndAttributes.getRoles()).addAttributes(rolesAndAttributes.getAttributes());
        final QuarkusSecurityIdentity result = builder.build();
        return result;
    }

    @CacheResult(cacheName = "security.attributes")
    RolesAndAttributesCacheable getRolesAndAttributes(String username) {
        System.out.println(((CaffeineCache) cache).getSize());
        RolesAndAttributesCacheable result = new RolesAndAttributesCacheable();
        try (final AutoCloseableHelper ach = new AutoCloseableHelper()) {
            final Connection connection = ach.add(ds.getConnection());
            final PreparedStatement ps = ach.add(connection.prepareStatement(ROLES_AND_ATTRIBUTES_QUERY));
            ps.setString(1, extractLogin(username));
            final ResultSet rs = ach.add(ps.executeQuery());
            boolean attributesAdded = false;
            while (rs.next()) {
                result.addRole(rs.getString(1));
                if (!attributesAdded) {
                    result.addAttribute("bto.security.user.id", rs.getInt(2));
                    result.addAttribute("bto.security.user.position", rs.getString(3));
                    attributesAdded = true;
                }
            }
        } catch (SQLException ignored) {
        }
        return result;
    }

    public void setIdentity(final SecurityIdentity identity) {
        this.identity = identity;
    }

    public static String extractLogin(String login) {
        if (login.toLowerCase().startsWith("cn=")) {
            String[] parts = login.split(",\\s?", 100);
            return parts[0].split("=", 3)[1];
        } else {
            return login;
        }
    }
}

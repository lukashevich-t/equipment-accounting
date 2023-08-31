package by.gto.equipment.account.auth;

import io.quarkus.security.identity.AuthenticationRequestContext;
import io.quarkus.security.identity.SecurityIdentity;
import io.quarkus.security.identity.SecurityIdentityAugmentor;
import io.smallrye.mutiny.Uni;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

@ApplicationScoped
public class MyAugmentor implements SecurityIdentityAugmentor {

    @Inject
    MySupplier supplier;

    @Override
    public Uni<SecurityIdentity> augment(final SecurityIdentity identity, final AuthenticationRequestContext context) {
        supplier.setIdentity(identity);
        return context.runBlocking(supplier);
    }
}

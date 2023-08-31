package by.gto.equipment.account.auth;//package quarkus.auth.http;
//
//import io.quarkus.security.identity.IdentityProviderManager;
//import io.quarkus.security.identity.SecurityIdentity;
//import io.quarkus.security.identity.request.AuthenticationRequest;
//import io.quarkus.vertx.http.runtime.security.BasicAuthenticationMechanism;
//import io.quarkus.vertx.http.runtime.security.ChallengeData;
//import io.quarkus.vertx.http.runtime.security.HttpAuthenticationMechanism;
//import io.quarkus.vertx.http.runtime.security.HttpCredentialTransport;
//import io.smallrye.mutiny.Uni;
//import io.smallrye.mutiny.groups.UniSubscribe;
//import io.vertx.core.impl.logging.Logger;
//import io.vertx.core.impl.logging.LoggerFactory;
//import io.vertx.ext.web.RoutingContext;
//import java.util.Set;
//import javax.annotation.Priority;
//import javax.enterprise.context.ApplicationScoped;
//import javax.enterprise.inject.Alternative;
//import javax.inject.Inject;
//
//@Alternative
//@Priority(1)
//@ApplicationScoped
//public class DelegateBasicAuthMechanism implements HttpAuthenticationMechanism {
//    private static final Logger LOG = LoggerFactory.getLogger(DelegateBasicAuthMechanism.class);
//
//    @Inject
//    BasicAuthenticationMechanism delegate;
//
//    @Override
//    public Uni<SecurityIdentity> authenticate(RoutingContext context, IdentityProviderManager identityProviderManager) {
//        System.out.println("DelegateBasicAuthMechanism.authenticate: arguments:");
//        System.out.println("\tRoutingContext: " + context);
//        System.out.println("\tIdentityProviderManager: " + identityProviderManager);
//        Uni<SecurityIdentity> authenticate = delegate.authenticate(context, identityProviderManager);
//        System.out.println("result: " + authenticate);
//        return authenticate;
//    }
//
//    @Override
//    public Uni<ChallengeData> getChallenge(RoutingContext context) {
//        System.out.println("DelegateBasicAuthMechanism.getChallenge: arguments:");
//        System.out.println("\tRoutingContext: " + context);
//        Uni<ChallengeData> challenge = delegate.getChallenge(context);
//        System.out.println("result: " + challenge);
//        return challenge;
//    }
//
//    @Override
//    public Set<Class<? extends AuthenticationRequest>> getCredentialTypes() {
//        System.out.println("DelegateBasicAuthMechanism.getCredentialTypes");
//        Set<Class<? extends AuthenticationRequest>> credentialTypes = delegate.getCredentialTypes();
//        System.out.println("result: " + credentialTypes);
//        return credentialTypes;
//    }
//
//    @Override
//    public Uni<HttpCredentialTransport> getCredentialTransport(RoutingContext context) {
//        System.out.println("DelegateBasicAuthMechanism.getCredentialTransport: arguments:");
//        System.out.println("\tRoutingContext: " + context);
//        Uni<HttpCredentialTransport> credentialTransport = delegate.getCredentialTransport(context);
//        System.out.println("result: " + credentialTransport);
//        return credentialTransport;
//    }
//}

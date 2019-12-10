package nl.interjel.management.auth;

import io.dropwizard.auth.Authenticator;
import org.jose4j.jwt.GeneralJwtException;
import org.jose4j.jwt.NumericDate;
import org.jose4j.jwt.consumer.JwtContext;

import java.util.Optional;

/**
 * This class checks whether a token is valid or not, and assigns an appropriate role the the User object. The token's encryption has already been verified.
 *
 * @author Finn Bon
 */
public class JwtAuthenticator implements Authenticator<JwtContext, User> {

    @Override
    public Optional<User> authenticate(JwtContext jwtContext) {
        try {

            if (jwtContext.getJwtClaims().getExpirationTime() != null
                && jwtContext.getJwtClaims().getExpirationTime().isBefore(NumericDate.now())
            ) {
                throw new GeneralJwtException("Token expired");
            }

            Object isRoot = jwtContext.getJwtClaims().getClaimValue("root");
            String email = jwtContext.getJwtClaims().getStringClaimValue("email");
            // TODO: Look up in database instead?

            return Optional.of(new User(isRoot instanceof Boolean && ((boolean) isRoot), email));

        } catch (GeneralJwtException e) {
            e.printStackTrace();
        }

        return Optional.empty();
    }

}

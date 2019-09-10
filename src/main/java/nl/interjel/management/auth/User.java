package nl.interjel.management.auth;

import java.security.Principal;

/**
 * @author Finn Bon
 */
public class User implements Principal {

    private final String email;
    private final boolean root;

    public User(boolean root, String email) {
        this.root = root;
        this.email = email;
    }

    @Override
    public String getName() {
        return email;
    }

    public boolean isRoot() {
        return root;
    }
}

package nl.interjel.management.auth;

/**
 * @author Finn Bon
 */
public class TokenSecret {

    private final byte[] key;

    public TokenSecret(byte[] key) {
        this.key = key;
    }

    public byte[] get() {
        return key;
    }

}

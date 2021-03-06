package nl.interjel.management;

import io.dropwizard.Configuration;
import org.hibernate.validator.constraints.NotEmpty;

/**
 * @author Finn Bon
 */
public class ManagementConfiguration extends Configuration {

    @NotEmpty
    private String jwtTokenSecret = "Placeholder";

    private int paginationDefault = 20;

    private int paginationMax = 40;

    public byte[] getJwtTokenSecret() {
        return jwtTokenSecret.getBytes();
    }

    public void setJwtTokenSecret(String jwtTokenSecret) {
        this.jwtTokenSecret = jwtTokenSecret;
    }

    public int getPaginationDefault() {
        return paginationDefault;
    }

    public void setPaginationDefault(int paginationDefault) {
        this.paginationDefault = paginationDefault;
    }

    public int getPaginationMax() {
        return paginationMax;
    }

    public void setPaginationMax(int paginationMax) {
        this.paginationMax = paginationMax;
    }

}

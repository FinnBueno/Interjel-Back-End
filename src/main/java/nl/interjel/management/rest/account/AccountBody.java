package nl.interjel.management.rest.account;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

/**
 * @author Finn Bon
 */
public class AccountBody {

    @NotNull
    @Pattern(regexp = "^([a-zA-Z0-9_\\-.]+)@([a-zA-Z0-9_\\-.]+)\\.([a-zA-Z]{2,5})$", message = "A valid e-mail address is structured like name@provider.topleveldomain.")
    private String email;

    @NotNull
    @Min(5)
    private String password;

    private boolean root;

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public boolean isRoot() {
        return root;
    }
}

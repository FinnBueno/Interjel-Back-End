package nl.interjel.management.model.entity;

import nl.interjel.management.auth.BCrypt;
import nl.interjel.management.rest.account.AccountBody;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Objects;

/**
 * @author Finn Bon
 */
@Entity
@Table(name = "account")
public class Account {

    private int accountId;
    private String email;
    private String password;
    private boolean root;

    public Account() {
    }

    public Account(AccountBody body) {
        this.email = body.getEmail();
        this.password = BCrypt.hashpw(body.getPassword(), BCrypt.gensalt());
        this.root = body.isRoot();
    }

    @Id
    @Column(name = "accountId")
    public int getAccountId() {
        return accountId;
    }

    public void setAccountId(int accountId) {
        this.accountId = accountId;
    }

    @Column(name = "email")
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Column(name = "password")
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Column(name = "root")
    public boolean isRoot() {
        return root;
    }

    public void setRoot(boolean root) {
        this.root = root;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Account account = (Account) o;
        return accountId == account.accountId &&
                Objects.equals(email, account.email) &&
                Objects.equals(password, account.password) &&
                Objects.equals(root, account.root);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(accountId, email);
        result = 31 * result + Objects.hashCode(password);
        result = 31 * result + Objects.hashCode(root);
        return result;
    }
}

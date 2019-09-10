package nl.interjel.management.model.key;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Objects;

/**
 * @author Finn Bon
 */
@Embeddable
public class MemberVisitPK implements Serializable {

    @Column(name = "instance")
    private int instance;

    @Column(name = "member")
    private int member;

    public MemberVisitPK(int instanceId, int memberId) {
        this.instance = instanceId;
        this.member = memberId;
    }

    public MemberVisitPK() {}

    public int getMember() {
        return member;
    }

    public void setMember(int member) {
        this.member = member;
    }

    public int getInstance() {
        return instance;
    }

    public void setInstance(int instance) {
        this.instance = instance;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MemberVisitPK that = (MemberVisitPK) o;
        return instance == that.instance &&
                member == that.member;
    }

    @Override
    public int hashCode() {

        return Objects.hash(instance, member);
    }
}

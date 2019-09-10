package nl.interjel.management.model.relationship;

import nl.interjel.management.model.entity.Instance;
import nl.interjel.management.model.entity.Member;
import nl.interjel.management.model.key.MemberVisitPK;

import javax.persistence.*;
import java.util.Objects;

/**
 * @author Finn Bon
 *
 * Most fields in here are transient to prevent JSON loops
 */
@Entity
@Table(name = "member_visit", schema = "interjel")
public class MemberVisit {

    private MemberVisitPK id;

    private Instance instance;

    private Member member;

    public MemberVisit(Instance instance, Member member) {
        this.instance = instance;
        this.member = member;
        this.id = new MemberVisitPK(instance.getInstanceId(), member.getMemberId());
    }

    public MemberVisit() {}

    @EmbeddedId
    public MemberVisitPK getId() {
        return id;
    }

    public void setId(MemberVisitPK id) {
        this.id = id;
    }

    @ManyToOne(targetEntity = Instance.class)
    @MapsId("instance")
    @JoinColumn(name = "instance")
    public Instance getInstance() {
        return instance;
    }

    public void setInstance(Instance instance) {
        this.instance = instance;
    }

    @ManyToOne(targetEntity = Member.class)
    @MapsId("member")
    @JoinColumn(name = "member")
    public Member getMember() {
        return member;
    }

    public void setMember(Member member) {
        this.member = member;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MemberVisit that = (MemberVisit) o;
        return instance == that.instance &&
                member == that.member;
    }

    @Override
    public int hashCode() {
        return Objects.hash(instance, member);
    }

}

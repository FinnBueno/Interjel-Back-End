package nl.interjel.management.model.entity;

import nl.interjel.management.model.relationship.MemberVisit;
import nl.interjel.management.util.annotation.Optionally;
import nl.interjel.management.util.annotation.SkipAutoGeneration;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author Finn Bon
 */
@Entity
@Table(name = "instance", schema = "interjel")
public class Instance implements OnSelect {

    @SkipAutoGeneration
    private int instanceId;
    private LocalDateTime startDate;
    @Optionally
    private LocalDateTime endDate;
    @Optionally
    private String notes;

    public Instance() {
        this.visitors.size();
    }

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "member", fetch = FetchType.EAGER)
    private transient Set<MemberVisit> visitors = new HashSet<>();

    @Transient
    public Set<Member> getVisitorsAsMembers() {
        return visitors.stream().map(MemberVisit::getMember).filter(Member::isArchived).collect(Collectors.toSet());
    }

    // MySQL columns

    @OneToMany(
            mappedBy = "instance",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    public Set<MemberVisit> getVisitors() {
        return visitors;
    }
    public void setVisitors(Set<MemberVisit> visitors) {
        this.visitors = visitors;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "instanceId")
    public int getInstanceId() {
        return instanceId;
    }
    public void setInstanceId(int instanceId) {
        this.instanceId = instanceId;
    }

    @Column(name = "startDate")
    public LocalDateTime getStartDate() {
        return startDate;
    }
    public void setStartDate(LocalDateTime startDate) {
        this.startDate = startDate;
    }

    @Column(name = "endDate")
    public LocalDateTime getEndDate() {
        return endDate;
    }
    public void setEndDate(LocalDateTime endDate) {
        this.endDate = endDate;
    }

    @Column(name = "notes")
    public String getNotes() {
        return notes == null ? "" : notes;
    }
    public void setNotes(String notes) {
        this.notes = notes == null ? "" : notes;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Instance instance = (Instance) o;
        return instanceId == instance.instanceId &&
                Objects.equals(startDate, instance.startDate) &&
                Objects.equals(endDate, instance.endDate) &&
                Objects.equals(notes, instance.notes);
    }

    @Override
    public int hashCode() {
        return Objects.hash(instanceId, startDate, endDate, notes);
    }

    public MemberVisit addVisitor(Member member) {
        MemberVisit mv = new MemberVisit(this, member);
        this.visitors.add(mv);
        return mv;
    }

    @Transient
    public boolean isClosed() {
        return endDate != null;
    }

    public void assureStartDate() {
        if (startDate == null)
            startDate = LocalDateTime.now();
    }

    @Override
    public void select() {
        // force Hibernate to fetch the visitors
        this.getVisitorsAsMembers().size();
    }
}

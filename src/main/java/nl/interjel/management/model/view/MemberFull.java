package nl.interjel.management.model.view;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.Objects;

/**
 * @author Finn Bon
 */
@Entity
@Table(name = "member_full", schema = "interjel")
public class MemberFull {
    
    private Integer memberId;
    private String firstname;
    private String lastname;
    private LocalDate birth;
    private String email;
    private String notes;
    private String address;
    private String town;
    private String postalCode;
    private Integer phone;
    private String type;
    private long visits;
    private boolean paidContribution;
    private boolean archived;

    @Id
    @Column(name = "memberId")
    public Integer getMemberId() {
        return memberId;
    }

    public void setMemberId(Integer memberId) {
        this.memberId = memberId;
    }

    @Column(name = "firstname")
    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    @Column(name = "lastname")
    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    @Column(name = "birth")
    public LocalDate getBirth() {
        return birth;
    }

    public void setBirth(LocalDate birth) {
        this.birth = birth;
    }

    @Column(name = "email")
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Column(name = "notes")
    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    @Column(name = "address")
    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    @Column(name = "town")
    public String getTown() {
        return town;
    }

    public void setTown(String town) {
        this.town = town;
    }

    @Column(name = "postalCode")
    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    @Column(name = "phone")
    public Integer getPhone() {
        return phone;
    }

    public void setPhone(Integer phone) {
        this.phone = phone;
    }

    @Column(name = "type")
    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Column(name = "visits")
    public long getVisits() {
        return visits;
    }

    public void setVisits(long visits) {
        this.visits = visits;
    }

    @Column(name = "paidContribution")
    public boolean isPaidContribution() {
        return paidContribution;
    }

    public void setPaidContribution(boolean paidContribution) {
        this.paidContribution = paidContribution;
    }

    @Column(name = "archived")
    public boolean isArchived() {
        return archived;
    }

    public void setArchived(boolean archived) {
        this.archived = archived;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MemberFull that = (MemberFull) o;
        return visits == that.visits &&
                Objects.equals(memberId, that.memberId) &&
                Objects.equals(firstname, that.firstname) &&
                Objects.equals(lastname, that.lastname) &&
                Objects.equals(birth, that.birth) &&
                Objects.equals(email, that.email) &&
                Objects.equals(notes, that.notes) &&
                Objects.equals(address, that.address) &&
                Objects.equals(town, that.town) &&
                Objects.equals(postalCode, that.postalCode) &&
                Objects.equals(phone, that.phone) &&
                Objects.equals(type, that.type);
    }

    @Override
    public int hashCode() {

        return Objects.hash(memberId, firstname, lastname, birth, email, notes, address, town, postalCode, phone, type, visits);
    }
}

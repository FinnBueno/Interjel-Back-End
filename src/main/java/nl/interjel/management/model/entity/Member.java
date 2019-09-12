package nl.interjel.management.model.entity;

import nl.interjel.management.util.MemberType;
import nl.interjel.management.util.annotation.Optionally;
import nl.interjel.management.util.annotation.SkipAutoGeneration;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.Objects;

/**
 * @author Finn Bon
 */
@Entity
@Table(name = "member")
public class Member {

    @SkipAutoGeneration
    private int memberId;
    private String firstname;
    private String lastname;
    private LocalDate birth;
    private String email;
    private String notes;
    private String address;
    private String town;
    private String postalCode;
    private int phone;
    private MemberType type;
    @Optionally
    private boolean archived;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "memberId")
    public int getMemberId() {
        return memberId;
    }

    public void setMemberId(int memberId) {
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
        postalCode = postalCode.replace(" ", "");
        if (postalCode.length() > 6)
            postalCode = postalCode.substring(0, 6);
        this.postalCode = postalCode;
    }

    @Column(name = "phone")
    public int getPhone() {
        return phone;
    }

    public void setPhone(int phone) {
        this.phone = Math.min(phone, 999999999);
    }

    @Column(name = "type")
    @Enumerated(value = EnumType.STRING)
    public MemberType getType() {
        return type;
    }

    public void setType(MemberType type) {
        this.type = type;
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
        Member member = (Member) o;
        return memberId == member.memberId &&
                phone == member.phone &&
                Objects.equals(firstname, member.firstname) &&
                Objects.equals(lastname, member.lastname) &&
                Objects.equals(birth, member.birth) &&
                Objects.equals(email, member.email) &&
                Objects.equals(notes, member.notes) &&
                Objects.equals(address, member.address) &&
                Objects.equals(town, member.town) &&
                Objects.equals(postalCode, member.postalCode) &&
                Objects.equals(type, member.type);
    }

    @Override
    public int hashCode() {
        return Objects.hash(memberId, firstname, lastname, birth, email, notes, address, town, postalCode, phone, type);
    }

}

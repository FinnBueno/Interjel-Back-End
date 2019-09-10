package nl.interjel.management.model.entity;

import nl.interjel.management.model.key.SeasonPaymentPK;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.Objects;

/**
 * @author Finn Bon
 */
@Entity
@Table(name = "season_payment", schema = "interjel")
public class SeasonPayment implements Serializable {

    private SeasonPaymentPK id;

    private LocalDate paymentDate;
    private Member member;

    public SeasonPayment(LocalDate paymentDate, Member member) {
        this.paymentDate = paymentDate;
        this.member = member;
        this.id = new SeasonPaymentPK(paymentDate, member.getMemberId());
    }

    public SeasonPayment() {
    }

    @Id
    @Column(name = "paymentDate")
    public LocalDate getPaymentDate() {
        return paymentDate;
    }

    public void setPaymentDate(LocalDate paymentDate) {
        this.paymentDate = paymentDate;
    }

    @Id
    @ManyToOne
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
        SeasonPayment that = (SeasonPayment) o;
        return member == that.member &&
                Objects.equals(paymentDate, that.paymentDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(paymentDate, member);
    }

}

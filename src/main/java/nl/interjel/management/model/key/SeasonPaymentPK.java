package nl.interjel.management.model.key;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.Id;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.Objects;

/**
 * @author Finn Bon
 */
@Embeddable
public class SeasonPaymentPK implements Serializable {

    private LocalDate paymentDate;
    private int member;

    public SeasonPaymentPK() {
    }

    public SeasonPaymentPK(LocalDate paymentDate, int member) {
        this.paymentDate = paymentDate;
        this.member = member;
    }

    @Column(name = "paymentDate")
    @Id
    public LocalDate getPaymentDate() {
        return paymentDate;
    }

    public void setPaymentDate(LocalDate paymentDate) {
        this.paymentDate = paymentDate;
    }

    @Column(name = "member")
    @Id
    public int getMember() {
        return member;
    }

    public void setMember(int member) {
        this.member = member;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SeasonPaymentPK that = (SeasonPaymentPK) o;
        return member == that.member &&
                Objects.equals(paymentDate, that.paymentDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(paymentDate, member);
    }
}

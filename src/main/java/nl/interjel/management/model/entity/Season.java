package nl.interjel.management.model.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.time.LocalDate;
import java.util.Objects;

/**
 * @author Finn Bon
 */
@Entity
@Table(name = "season", schema = "interjel")
public class Season {

    private LocalDate startDate;

    public Season() {
    }

    public Season(LocalDate startDate) {
        this.startDate = startDate;
    }

    @Id
    @Column(name = "startDate")
    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Season season = (Season) o;
        return Objects.equals(startDate, season.startDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(startDate);
    }
}

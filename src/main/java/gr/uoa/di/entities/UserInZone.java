package gr.uoa.di.entities;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "userInZone")
public class UserInZone {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne
    @JoinColumn(name = "userId")
    private User elderlyUser;
    @ManyToOne
    @JoinColumn(name = "zoneId")
    private Zone zone;
    private Date timestamp;

    public User getElderlyUser() {
        return elderlyUser;
    }

    public void setElderlyUser(User elderlyUser) {
        this.elderlyUser = elderlyUser;
    }

    public Zone getZone() {
        return zone;
    }

    public void setZone(Zone zone) {
        this.zone = zone;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public String toString() {
        return "UserInZone{" +
                "elderlyUser=" + elderlyUser +
                ", zone=" + zone +
                ", timestamp=" + timestamp +
                '}';
    }
}

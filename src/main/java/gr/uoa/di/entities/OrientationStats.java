package gr.uoa.di.entities;

import javax.persistence.*;

/**
 * Created by skand on 11/21/2016.
 */

@Entity
@Table(name = "orientationStats")
public class OrientationStats {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne
    @JoinColumn(name = "userId")
    private User user;

    private String timeStamp;
    private String azimut;
    private String pitch;
    private String roll;

    public OrientationStats() {
    }

    public OrientationStats(String azimut, String pitch, String roll, String timeStamp) {
        this.azimut = azimut;
        this.pitch = pitch;
        this.roll = roll;
        this.timeStamp = timeStamp;
    }

    @Override
    public String toString() {
        return "OrientationStats{" +
                "id=" + id +
                ", user=" + user +
                ", timeStamp='" + timeStamp + '\'' +
                ", azimut='" + azimut + '\'' +
                ", pitch='" + pitch + '\'' +
                ", roll='" + roll + '\'' +
                '}';
    }

    public String getAzimut() {
        return azimut;
    }

    public void setAzimut(String azimut) {
        this.azimut = azimut;
    }

    public String getPitch() {
        return pitch;
    }

    public void setPitch(String pitch) {
        this.pitch = pitch;
    }

    public String getRoll() {
        return roll;
    }

    public void setRoll(String roll) {
        this.roll = roll;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(String timeStamp) {
        this.timeStamp = timeStamp;
    }

}

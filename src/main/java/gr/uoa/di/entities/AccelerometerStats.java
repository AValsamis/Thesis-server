package gr.uoa.di.entities;

import javax.persistence.*;

@Entity
@Table(name = "accelerometerStats")
public class AccelerometerStats {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne
    @JoinColumn(name = "userId")
    private User user;

    private String timeStamp;
    private String x;
    private String y;
    private String z;

    public AccelerometerStats() {
    }

    public AccelerometerStats(String x, String y, String z, String timeStamp) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.timeStamp = timeStamp;
    }

    @Override
    public String toString() {
        return "AccelerometerStats{" +
                "id=" + id +
                ", user=" + user +
                ", timeStamp='" + timeStamp + '\'' +
                ", x='" + x + '\'' +
                ", y='" + y + '\'' +
                ", z='" + z + '\'' +
                '}';
    }

    public String getX() {
        return x;
    }

    public void setX(String x) {
        this.x = x;
    }

    public String getY() {
        return y;
    }

    public void setY(String y) {
        this.y = y;
    }

    public String getZ() {
        return z;
    }

    public void setZ(String z) {
        this.z = z;
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

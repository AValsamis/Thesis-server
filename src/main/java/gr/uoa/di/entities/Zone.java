package gr.uoa.di.entities;

import javax.persistence.*;
import java.util.Set;

/**
 * Created by Sevle on 9/23/2016.
 */
@Entity
@Table(name = "zone")
public class Zone {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long Id;
    private String zoneId;
    @ManyToOne
    @JoinColumn(name = "wifiId")
    private Wifi wifi;
    private Double signalStrength;
    @ManyToOne
    @JoinColumn(name = "userId")
    private User user;

    public Zone(){}

    public Zone(Wifi wifi, Double signalStrength, User user) {
        this.wifi = wifi;
        this.signalStrength = signalStrength;
        this.user = user;
    }

    public String getZoneId() {
        return zoneId;
    }

    public void setZoneId(String zoneId) {
        this.zoneId = zoneId;
    }

    public Wifi getWifi() {
        return wifi;
    }

    public void setWifi(Wifi wifi) {
        this.wifi = wifi;
    }

    public Double getSignalStrength() {
        return signalStrength;
    }

    public void setSignalStrength(Double signalStrength) {
        this.signalStrength = signalStrength;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @Override
    public String toString() {
        return "Zone{" +
                "zoneId=" + zoneId +
                ", wifi=" + wifi +
                ", signalStrength=" + signalStrength +
                ", user=" + user +
                '}';
    }
}

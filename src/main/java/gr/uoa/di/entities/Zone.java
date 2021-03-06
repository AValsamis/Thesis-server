package gr.uoa.di.entities;

import javax.persistence.*;
import java.util.Set;

@Entity
@Table(name = "zone")
public class Zone {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long zoneId;
//    private String zoneId;
    private String friendlyName;
    @ManyToOne
    @JoinColumn(name = "userId")
    private User user;
    private Integer isSafe;

    public Zone(){}

    public Long getZoneId() {
        return zoneId;
    }

    //    public Zone(Wifi wifi, Double signalStrength, User user) {
//        this.wifi = wifi;
//        this.signalStrength = signalStrength;
//        this.user = user;
//    }

//    public String getZoneId() {
//        return zoneId;
//    }
//
//    public void setZoneId(String zoneId) {
//        this.zoneId = zoneId;
//    }

//    public Wifi getWifi() {
//        return wifi;
//    }
//
//    public void setWifi(Wifi wifi) {
//        this.wifi = wifi;
//    }
//
//    public Double getSignalStrength() {
//        return signalStrength;
//    }
//
//    public void setSignalStrength(Double signalStrength) {
//        this.signalStrength = signalStrength;
//    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Integer getIsSafe() {
        return isSafe;
    }

    public void setIsSafe(Integer isSafe) {
        this.isSafe = isSafe;
    }

    public String getFriendlyName() {
        return friendlyName;
    }

    public void setFriendlyName(String friendlyName) {
        this.friendlyName = friendlyName;
    }

    @Override
    public String toString() {
        return "Zone{" +
                "zoneId=" + zoneId +
//                ", zoneId='" + zoneId + '\'' +
                ", friendlyName='" + friendlyName + '\'' +
//                ", wifi=" + wifi +
//                ", signalStrength=" + signalStrength +
                ", user=" + user +
                ", isSafe=" + isSafe +
                '}';
    }
}

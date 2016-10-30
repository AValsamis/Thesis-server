package gr.uoa.di.entities;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@IdClass(WifiInZonePK.class)

public class WifiInZone implements Serializable {

    @Id
    @ManyToOne
    @JoinColumn(name = "wifiId")
    private Wifi wifi;
    @Id
    @ManyToOne
    @JoinColumn(name = "zoneId")
    private Zone zone;
    private Integer signalStrength;

    public Wifi getWifi() {
        return wifi;
    }

    public void setWifi(Wifi wifi) {
        this.wifi = wifi;
    }

    public Zone getZone() {
        return zone;
    }

    public void setZone(Zone zone) {
        this.zone = zone;
    }

    public Integer getSignalStrength() {
        return signalStrength;
    }

    public void setSignalStrength(Integer signalStrength) {
        this.signalStrength = signalStrength;
    }

    @Override
    public String toString() {
        return "WifiInZone{" +
                "wifi=" + wifi +
                ", zone=" + zone +
                ", signalStrength=" + signalStrength +
                '}';
    }
}

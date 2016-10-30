package gr.uoa.di.entities;

import javax.persistence.*;

/**
 * Created by Sevle on 9/23/2016.
 */
@Entity
@Table(name = "wifi")
public class Wifi {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long wifiId;

    @Column(unique=true)
    private String macAddress;
    private String name;
    @Transient
    private Integer signalStrength;

    public Wifi() {
    }

    public Wifi(String macAddress, String name) {
        this.macAddress = macAddress;
        this.name = name;
    }

    public Long getWifiId() {
        return wifiId;
    }

    public void setWifiId(Long wifiId) {
        this.wifiId = wifiId;
    }

    public String getMacAddress() {
        return macAddress;
    }

    public void setMacAddress(String macAddress) {
        this.macAddress = macAddress;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getSignalStrength() {
        return signalStrength;
    }

    public void setSignalStrength(Integer signalStrength) {
        this.signalStrength = signalStrength;
    }

    @Override
    public String toString() {
        return "Wifi{" +
                "MacAddress='" + macAddress + '\'' +
                ", name='" + name + '\'' +
                '}';
    }
}

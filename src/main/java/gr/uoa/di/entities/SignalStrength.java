package gr.uoa.di.entities;

/**
 * Created by Sevle on 9/23/2016.
 */
public class SignalStrength {

    private Wifi wifi;
    private Double signalStrength;

    public SignalStrength(){}

    public SignalStrength(Wifi wifi, Double signalStrength) {
        this.wifi = wifi;
        this.signalStrength = signalStrength;
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

    @Override
    public String toString() {
        return "SignalStrength{" +
                "wifi=" + wifi +
                ", signalStrength=" + signalStrength +
                '}';
    }
}

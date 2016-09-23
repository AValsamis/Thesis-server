package gr.uoa.di.entities;

/**
 * Created by Sevle on 9/23/2016.
 */
public class Wifi {

    private String MacAddress;
    private String name;

    public Wifi(String macAddress, String name) {
        MacAddress = macAddress;
        this.name = name;
    }

    public String getMacAddress() {
        return MacAddress;
    }

    public void setMacAddress(String macAddress) {
        MacAddress = macAddress;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "Wifi{" +
                "MacAddress='" + MacAddress + '\'' +
                ", name='" + name + '\'' +
                '}';
    }
}

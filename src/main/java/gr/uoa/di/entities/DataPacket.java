package gr.uoa.di.entities;

import java.util.ArrayList;

/**
 * Created by skand on 11/21/2016.
 */
public class DataPacket {

    private ArrayList<AccelerometerStats> accelerometerStats;
    private User user;

    public DataPacket() {
    }

    public DataPacket(ArrayList<AccelerometerStats> accelerometerStats, User user) {
        this.accelerometerStats = accelerometerStats;
        this.user = user;
    }

    @Override
    public String toString() {
        return "DataPacket{" +
                "accelerometerStats=" + accelerometerStats +
                ", user=" + user.toString() +
                '}';
    }

    public ArrayList<AccelerometerStats> getAccelerometerStats() {
        return accelerometerStats;
    }

    public void setAccelerometerStats(ArrayList<AccelerometerStats> accelerometerStats) {
        this.accelerometerStats = accelerometerStats;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

}

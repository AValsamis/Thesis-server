package gr.uoa.di.entities;

/**
 * Created by Angelos on 6/19/2016.
 */

public class AccelerometerStats {

    private String x;

    private String y;

    private String z;

    public AccelerometerStats() {
    }

    public AccelerometerStats(String x, String y, String z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    @Override
    public String toString() {
        return "AccelerometerStats{" +
                "x='" + x + '\'' +
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
}

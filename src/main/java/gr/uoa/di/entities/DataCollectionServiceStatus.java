package gr.uoa.di.entities;

import javax.persistence.*;

/**
 * Created by skand on 3/12/2017.
 */

@Entity
@Table(name = "dataCollectionServiceStatus")
public class DataCollectionServiceStatus {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne
    @JoinColumn(name = "userId")
    private User elderlyUser;
    private Boolean shouldRun;
    private String timestamp = "";


    public DataCollectionServiceStatus() {
    }

    public DataCollectionServiceStatus(String timestamp, Boolean shouldRun, User elderlyUser) {
        this.timestamp = timestamp;
        this.shouldRun = shouldRun;
        this.elderlyUser = elderlyUser;
    }

    public User getUser() {
        return elderlyUser;
    }

    public void setUser(User elderlyUser) {
        this.elderlyUser = elderlyUser;
    }

    public Boolean getShouldRun() {
        return shouldRun;
    }

    @Override
    public String toString() {
        return "DataCollectionServiceStatus{" +
                "elderlyUser=" + elderlyUser +
                ", shouldRun=" + shouldRun +
                ", timestamp='" + timestamp + '\'' +
                '}';
    }

    public void setShouldRun(Boolean shouldRun) {
        this.shouldRun = shouldRun;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }
}

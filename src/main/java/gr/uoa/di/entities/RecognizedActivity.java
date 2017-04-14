package gr.uoa.di.entities;

import javax.persistence.*;
import java.io.Serializable;

/**
 * Created by skand on 4/14/2017.
 */

@Entity
@Table(name = "recognizedActivity")
public class RecognizedActivity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne
    @JoinColumn(name = "userId")
    private User user;
    private String timestamp;
    private String state;
    private Integer certainty;

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

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public Integer getCertainty() {
        return certainty;
    }

    public void setCertainty(Integer certainty) {
        this.certainty = certainty;
    }

    @Override
    public String toString() {
        return "RecognizedActivity{" +
                "id=" + id +
                ", user=" + user +
                ", timestamp='" + timestamp + '\'' +
                ", state='" + state + '\'' +
                ", certainty=" + certainty +
                '}';
    }
}

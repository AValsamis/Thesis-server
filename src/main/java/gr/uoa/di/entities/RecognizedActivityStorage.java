package gr.uoa.di.entities;

import com.fasterxml.jackson.annotation.JsonFormat;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * Created by skand on 4/14/2017.
 */

@Entity
@Table(name = "recognizedActivityStorage")
public class RecognizedActivityStorage implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne
    @JoinColumn(name = "userId")
    private User user;
    private Date timestamp;
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

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
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

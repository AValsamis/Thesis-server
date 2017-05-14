package gr.uoa.di.entities;

import com.fasterxml.jackson.annotation.JsonFormat;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

public class RecognizedActivity implements Serializable {


    private Long id;
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

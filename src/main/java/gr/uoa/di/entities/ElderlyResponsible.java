package gr.uoa.di.entities;

import com.fasterxml.jackson.annotation.JsonManagedReference;

import javax.persistence.*;
import java.io.Serializable;

/**
 * Created by skand on 2/5/2017.
 */
@Entity
@IdClass(ElderlyResponsiblePK.class)
// TODO check if class is needed after all...
public class ElderlyResponsible implements Serializable {

    @Id
    @ManyToOne
    @JoinColumn(name = "elderly_user_id", referencedColumnName = "userId", nullable = false)
    private User elderlyUser;
    @Id
    @ManyToOne
    @JoinColumn(name = "responsible_user_id", referencedColumnName = "userId", nullable = false)
    private User responsibleUser;

    public User getElderlyUser() {
        return elderlyUser;
    }

    public User getResponsibleUser() {
        return responsibleUser;
    }

    public ElderlyResponsible(){}
    public ElderlyResponsible(User responsibleUser, User elderlyUser) {
        this.responsibleUser = responsibleUser;
        this.elderlyUser = elderlyUser;
    }

    public void setResponsibleUser(User responsibleUser) {
        this.responsibleUser = responsibleUser;
    }

    public void setElderlyUser(User elderlyUser) {
        this.elderlyUser = elderlyUser;
    }
}

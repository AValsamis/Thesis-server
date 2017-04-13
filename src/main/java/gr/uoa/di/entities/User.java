package gr.uoa.di.entities;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

/**
 * Created by Angelos on 9/18/2016.
 */
@Entity
@Table(name = "user")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long userId;

    @Column(unique=true)
    private String username;
    private String name;
    private String surname;
    private String token;

    @Transient
    private String responsibleUserName;

    @NotNull
    private String password;

    public User(){}

    public User(String username, String name, String surname, String password, String responsibleUserName, String token) {
        this.username = username;
        this.name = name;
        this.surname = surname;
        this.password = password;
        this.responsibleUserName = responsibleUserName;
        this.token = token;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public User(long id) {
        this.userId = id;
    }

    public Long getId() {
        return userId;
    }

    public void setId(Long id) {
        this.userId = id;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getResponsibleUserName() {
        return responsibleUserName;
    }

    public void setResponsibleUserName(String responsibleUserName) {
        this.responsibleUserName = responsibleUserName;
    }

    @Override
    public String toString() {
        return "User{" +
                "username='" + username + '\'' +
                ", name='" + name + '\'' +
                ", surname='" + surname + '\'' +
                ", responsibleUserName='" + responsibleUserName +
                '}';
    }
}

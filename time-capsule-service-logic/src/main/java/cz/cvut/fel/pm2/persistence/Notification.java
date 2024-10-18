package cz.cvut.fel.pm2.persistence;

import cz.cvut.fel.pm2.enums.NotificationTypes;
import jakarta.persistence.*;

import java.util.List;
import java.time.LocalDateTime;

@Entity
@Table(name = "T_NOTIFICATION")
public class Notification extends AbstractEntity {

    @Column(name = "content")
    private String content;


    @ManyToMany
    @JoinTable(
            name = "notification_user",
            joinColumns = @JoinColumn(name = "notification_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    private List<User> users;


    @Column(name ="date_of_creation")
    private LocalDateTime dateOfCreation;


    @Enumerated(EnumType.STRING)
    @Column(name = "notification_type")
    private NotificationTypes notification_type;


    @ManyToOne
    @JoinColumn(name = "capsule_id")
    private Capsule capsule;

    public Notification(String content, LocalDateTime dateOfCreation, NotificationTypes notification_type, Capsule capsule, List<User> users) {
        this.dateOfCreation = dateOfCreation;
        this.content = content;
        this.notification_type = notification_type;
        this.capsule = capsule;
        this.users = users;
    }
    public Notification() {
    }
    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public List<User> getUsers() {
        return users;
    }

    public void setUsers(List<User> users) {
        this.users = users;
    }
    public Capsule getCapsule() {
        return capsule;
    }

    public void setCapsule(Capsule capsule) {
        this.capsule = capsule;
    }
}
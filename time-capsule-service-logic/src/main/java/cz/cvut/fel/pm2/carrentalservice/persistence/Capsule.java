package cz.cvut.fel.pm2.carrentalservice.persistence;

import com.fasterxml.jackson.annotation.JsonManagedReference;

import cz.cvut.fel.pm2.carrentalservice.enums.State;
import cz.cvut.fel.pm2.carrentalservice.enums.Type;
import jakarta.persistence.*;
import java.util.List;
import java.math.BigDecimal;
import java.time.LocalDate;

@Table(name = "T_CAPSULE")
@Entity
public class Capsule extends AbstractEntity {

    @ManyToOne
    @JoinColumn(nullable = false,name = "user_id")
    @JsonManagedReference
    private User owner;

    @ManyToMany
    @JoinTable(
            name = "capsule_user",
            joinColumns = @JoinColumn(name = "capsule_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    private List<User> users;

    @OneToMany(mappedBy = "capsule")
    private List<Notification> notifications;

    @Column(name = "capsule_size")
    private Double capsuleSize;

    @Column(name = "name", nullable = false, unique = true)
    private String name;

    @Column(name = "description", nullable = false)
    private String description;


    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @Column(name = "expiration_date", nullable = false)
    private LocalDate expirationDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "state")
    private State state;

    @Enumerated(EnumType.STRING)
    @Column(name = "type")
    private Type type;

    @OneToMany(mappedBy = "capsule")
    private List<Content> contents;

    public Capsule() {
    }

    public Double getCapsuleSize() {
        return capsuleSize;
    }

    public void setCapsuleSize(Double capsuleSize) {
        this.capsuleSize = capsuleSize;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }


    public User getOwner() {
        return owner;
    }

    public void setOwner(User owner) {
        this.owner = owner;
    }

    public List<Notification> getNotifications() {
        return notifications;
    }

    public void setNotifications(List<Notification> notifications) {
        this.notifications = notifications;
    }

    public List<Content> getContents() {
        return contents;
    }

    public void setContents(List<Content> contents) {
        this.contents = contents;
    }
}

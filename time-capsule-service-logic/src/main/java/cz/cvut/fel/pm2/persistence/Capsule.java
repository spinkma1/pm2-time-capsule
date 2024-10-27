package cz.cvut.fel.pm2.persistence;

import com.fasterxml.jackson.annotation.JsonManagedReference;

import cz.cvut.fel.pm2.enums.State;
import cz.cvut.fel.pm2.enums.Type;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Table(name = "T_CAPSULE")
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
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

    @Enumerated(EnumType.STRING)
    @Column(name = "state")
    private State state;

    @Enumerated(EnumType.STRING)
    @Column(name = "type")
    private Type type;

    @OneToMany(mappedBy = "capsule")
    private List<Content> contents;

}

package cz.cvut.fel.pm2.persistence;


import cz.cvut.fel.pm2.enums.Role;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Entity
@Getter
@Setter
@Table(name = "T_USER")
public class User extends AbstractEntity {


    @Basic(optional = false)
    @Column(name = "email", nullable = false, unique = true)
    protected String email;

    @Basic(optional = false)
    @Column(name = "password", nullable = false)
    protected String password;

    @Enumerated(EnumType.STRING)
    @Column(name = "role")
    private Role role;

    @ManyToMany(mappedBy = "users")
    private List<Notification> notifications;

    @ManyToMany(mappedBy = "users")
    private List<Capsule> capsules;

    @ManyToMany
    @JoinTable(
            name = "user_followers",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "follower_id")
    )
    private List<User> followers;

    public User(String email, String password) {
        this.email = email;
        this.password = password;
    }

    public User() {
        this.role = Role.REGISTERED;
    }
}

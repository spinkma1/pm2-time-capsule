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

    @Column(name = "google_id", unique = true)
    private String googleId; // Unique ID for Google SSO users

    @Basic(optional = false)
    @Column(name = "email", nullable = false, unique = true)
    protected String email;

    @Column(name = "password")
    protected String password;

    @Enumerated(EnumType.STRING)
    @Column(name = "role")
    private Role role = Role.REGISTERED;

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

    // Constructor for SSO (no password)
    public User(String email, String googleId) {
        this.email = email;
        this.googleId = googleId;
        this.role = Role.REGISTERED;
    }

    // Default constructor
    public User() {}

    // Standard constructor for non-SSO users
    public User(String email, String password, Role role) {
        this.email = email;
        this.password = password;
        this.role = Role.REGISTERED;
    }
}

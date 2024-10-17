package cz.cvut.fel.pm2.carrentalservice.persistence;


import cz.cvut.fel.pm2.carrentalservice.enums.Role;
import jakarta.persistence.*;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;

@Entity
@Table(name = "T_USER")
public class User extends AbstractEntity {

    @Basic(optional = false)
    @Column(name="email", nullable = false, unique = true)
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
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void encodePassword(PasswordEncoder encoder) {
        this.password = encoder.encode(password);
    }

    public void erasePassword() {
        this.password = null;
    }

    public List<User> getFollowers() {
        return followers;
    }

    public void setFollowers(List<User> followers) {
        this.followers = followers;
    }
    public Role getRole() {
        return role;
    }
    public void setRole(Role role) {
        this.role = role;
    }
    public List<Notification> getNotifications() {
        return notifications;
    }
    public void setNotifications(List<Notification> notifications) {
        this.notifications = notifications;
    }
}

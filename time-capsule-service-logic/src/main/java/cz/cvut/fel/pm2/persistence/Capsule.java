package cz.cvut.fel.pm2.persistence;

import com.fasterxml.jackson.annotation.JsonManagedReference;

import cz.cvut.fel.pm2.enums.State;
import cz.cvut.fel.pm2.enums.Type;
import cz.cvut.fel.pm2.enums.UnlockMethod;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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



    @ElementCollection
    @Enumerated(EnumType.STRING)
    @CollectionTable(name = "unlock_methods", joinColumns = @JoinColumn(name = "capsule_id"))
    @Column(nullable = false)
    //time is set true by default, others false
    private HashMap<UnlockMethod, Boolean> unlockMethods = new HashMap<UnlockMethod, Boolean>() {{
        put(UnlockMethod.TIME, true);
        put(UnlockMethod.QR_CODE, false);
        put(UnlockMethod.GEOLOCATION, false);
    }};

    @Column(name = "unlock_time")
    private LocalDateTime unlockTime;

    @Column(name = "qr_code_password")
    private String qrCodePassword;

    @Column(name = "unlock_lat")
    private Double unlockLat;

    @Column(name = "unlock_longit")
    private Double unlockLongit;


}

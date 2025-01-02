package cz.cvut.fel.pm2.persistence;

import com.fasterxml.jackson.annotation.JsonManagedReference;

import cz.cvut.fel.pm2.UnlockMethodState;
import cz.cvut.fel.pm2.enums.State;
import cz.cvut.fel.pm2.enums.Type;
import cz.cvut.fel.pm2.enums.UnlockMethod;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Table(name = "T_CAPSULE")
@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
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
    private List<User> users =  new ArrayList<>();

    @OneToMany(mappedBy = "capsule")
    private List<Notification> notifications;

    @Column(name = "capsule_size")
    private Long capsuleSize;

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
    private List<Content> contents = new ArrayList<>();

    @Column(name = "team_work")
    private boolean teamWork;

    @ElementCollection
    @Enumerated(EnumType.STRING)
    @CollectionTable(name = "unlock_methods", joinColumns = @JoinColumn(name = "capsule_id"))
    @Column(nullable = false)
    //time is set true by default, others false
    // todo zakomentoval jsem to, protoze s tim nejde spustit aplikace. Pred pushem vzdy zkontrolovat clean package run
    private Map<UnlockMethod, UnlockMethodState> unlockMethods = new HashMap<>();
    @Column(name = "unlock_time")
    private LocalDateTime unlockTime;

    @Column(name = "qr_code_password")
    private String qrCodePassword;

    @Column(name = "unlock_lat")
    private Double unlockLat;

    @Column(name = "unlock_longit")
    private Double unlockLongit;

    public void addContent(Content content) {
        contents.add(content);
    }

}

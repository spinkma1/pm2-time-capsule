package cz.cvut.fel.pm2;

import cz.cvut.fel.pm2.enums.DataType;
import cz.cvut.fel.pm2.enums.State;
import cz.cvut.fel.pm2.enums.Type;
import cz.cvut.fel.pm2.persistence.Capsule;
import cz.cvut.fel.pm2.persistence.Content;
import cz.cvut.fel.pm2.persistence.User;
import cz.cvut.fel.pm2.repository.CapsuleRepository;
import cz.cvut.fel.pm2.repository.ContentRepository;
import cz.cvut.fel.pm2.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class DBStart implements CommandLineRunner {

        @Autowired
        private UserRepository userRepository;

        @Autowired
        private CapsuleRepository capsuleRepository;

        @Autowired
        private ContentRepository contentRepository;

        @Autowired
        private PasswordEncoder passwordEncoder;

        @Override
        public void run(String... args) {
            initializeDatabase();
        }

        private void initializeDatabase() {
            User user = new User();
            user.setEmail("aaaaaa@a.com");
            user.setPassword(passwordEncoder.encode("aaaaaa@a.com"));
            user.setName("User");
            user.setBio("This is bio of User");
            userRepository.save(user);

            User user1 = new User();
            user1.setEmail("karo@a.com");
            user1.setPassword(passwordEncoder.encode("aaa312aaa@a.com"));
            user1.setName("Karovec");
            user1.setBio("This is bio of Karovec");
            userRepository.save(user1);

            User user2 = new User();
            user2.setEmail("aaaa132aa@a.com");
            user2.setPassword(passwordEncoder.encode("aaaaa31a@a.com"));
            user2.setName("User2");
            user2.setBio("This is bio of User2");
            userRepository.save(user2);


            // Create capsules
            List<Capsule> capsules = new ArrayList<>();
            for (int i = 1; i <= 3; i++) {
                Capsule capsule = new Capsule();
                capsule.setName("Capsule " + i);
                capsule.setDescription("Description of Capsule " + i);
                capsule.setOwner(user);
                capsule.setState(State.EDIT);
                capsule.setType(Type.PRIVATE);
                capsule.setCapsuleSize(100L * i);
                capsule.setUnlockTime(LocalDateTime.of(2025, 12, 12, 0, 0, 0));
                capsule.setUsers(List.of(user1, user2));
                capsules.add(capsuleRepository.save(capsule));
            }

            // Create contents
            for (Capsule capsule : capsules) {
                for (int j = 1; j <= 3; j++) {
                    Content content = new Content();
                    content.setDataType(DataType.IMAGE);
                    content.setDateOfUpload(new Date());
                    content.setName("Content " + j + " for " + capsule.getName());
                    content.setUrl("https://example.com/content" + j);
                    content.setCapsule(capsule);
                    contentRepository.save(content);
                }
            }

            System.out.println("Database initialized with sample data.");
        }
}

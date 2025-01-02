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
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

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
            List<User> users = new ArrayList<>();
            for (int i = 1; i <= 5; i++) {
                User user = new User();
                user.setEmail("kingleduc" + i + "@gmail.com");
                user.setPassword(passwordEncoder.encode("12345678"));
                user.setName("User " + i);
                user.setBio("This is bio of User " + i);
                users.add(userRepository.save(user));
            }

            // Create capsules
            List<Capsule> capsules = new ArrayList<>();
            Random random = new Random();
            for (int i = 1; i <= 3; i++) {
                Capsule capsule = new Capsule();
                capsule.setName("Capsule " + i);
                capsule.setDescription("Description of Capsule " + i);
                capsule.setOwner(users.get(random.nextInt(users.size())));
                capsule.setState(State.EDIT);
                capsule.setType(Type.PRIVATE);
                capsule.setCapsuleSize(100L * i);
                capsule.setUnlockTime(null);
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

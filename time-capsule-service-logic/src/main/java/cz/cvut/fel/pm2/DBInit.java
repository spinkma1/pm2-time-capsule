//package cz.cvut.fel.pm2;
//
//import cz.cvut.fel.pm2.enums.*;
//import cz.cvut.fel.pm2.mappers.CapsuleMapper;
//import cz.cvut.fel.pm2.model.UserDto;
//import cz.cvut.fel.pm2.model.CapsuleDto;
//import cz.cvut.fel.pm2.model.ContentDto;
//import cz.cvut.fel.pm2.persistence.Capsule;
//import cz.cvut.fel.pm2.persistence.Content;
//import cz.cvut.fel.pm2.persistence.Notification;
//import cz.cvut.fel.pm2.persistence.User;
//import cz.cvut.fel.pm2.repository.UserRepository;
//import cz.cvut.fel.pm2.repository.CapsuleRepository;
//import cz.cvut.fel.pm2.repository.ContentRepository;
//import cz.cvut.fel.pm2.service.UserService;
//import cz.cvut.fel.pm2.service.CapsuleService;
//import cz.cvut.fel.pm2.service.ContentService;
//import jakarta.persistence.*;
//import org.flywaydb.core.Flyway;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.CommandLineRunner;
//import org.springframework.jdbc.core.JdbcTemplate;
//import org.springframework.security.crypto.password.PasswordEncoder;
//import org.springframework.stereotype.Service;
//
//import javax.sql.DataSource;
//import java.security.NoSuchAlgorithmException;
//import java.sql.Date;
//import java.time.LocalDateTime;
//import java.util.HashMap;
//import java.util.List;
//
//@Service
//public class DBInit implements CommandLineRunner {
//
//    @Autowired
//    private UserRepository userRepository;
//    @Autowired
//    private CapsuleRepository capsuleRepository;
//    @Autowired
//    private ContentRepository contentRepository;
//    @Autowired
//    private UserService userService;
//    @Autowired
//    private CapsuleService capsuleService;
//    @Autowired
//    private ContentService contentService;
//    @Autowired
//    private PasswordEncoder passwordEncoder;
//    @Autowired
//    private JdbcTemplate jdbcTemplate;
//
//    @Autowired
//    private CapsuleMapper capsuleMapper;
//
//    @Override
//    public void run(String... args) throws NoSuchAlgorithmException {
//        clearDB();
//
//        // Create Users
//        User user1 = userService.registerUser("password1", "email1@email.com");
//        User user2 = userService.registerUser("password1", "email2@email.com");
//        User owner;
//
//        userRepository.save(user1);
//        userRepository.save(user2);
//        List<UserDto> users = List.of(
////                user1.toDto(),
////                user2.toDto()
//        );
//
//
//
//        CapsuleDto capsuleDto = new CapsuleDto( null,"kapsle1", "popiskapsle1", false, 15000L, users);
//
//        CapsuleDto capsule1 = capsuleService.createCapsule(capsuleDto, capsuleMapper. );
//        CapsuleDto capsule2 = capsuleService.createCapsule(capsuleDto);
//
//
//
////        Notification notification = new Notification("notification1Content", LocalDateTime.now(), NotificationTypes.REMINDER,capsuleRepository.getCapsuleById(capsule1.id()).get(),List.of(user1));
////        Notification notification2 = new Notification("notification2Content", LocalDateTime.now(), NotificationTypes.REMINDER,capsuleRepository.getCapsuleById(capsule2.id()).get(),List.of(user2));
//
//
//        byte[] data = new byte[10];
//
//        Date date = new Date(2021, 12, 12);
//
//
//        ContentDto contentDto = new ContentDto(DataType.PLAIN_TEXT, date, "fakecontentname","fakeurl", data);
//        contentService.uploadContent(capsule1.id(),contentDto);
//
//
//
//
//    }
//
//    private void clearDB() {
//
//        DataSource dataSource = jdbcTemplate.getDataSource();
//
//        //delete flyway history in tables
//        // mvn flyway:baseline and then this should work, maybe have to repair and clean too in commandline
//
//
//        Flyway flyway = Flyway.configure()
//                .dataSource(dataSource)
//                .locations("classpath:db/")
//                .cleanDisabled(false)
//                .load();
//
//
//        flyway.repair();
//        // Step 1: Clean the database (this removes all tables, sequences, etc.)
//        flyway.clean();
//
//        // Step 2: Reapply all migrations (this will recreate tables, schema, etc.)
//        flyway.migrate();
//
//        //delay to allow the database to be cleaned
//        try {
//            Thread.sleep(4000);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//    }
//
//}
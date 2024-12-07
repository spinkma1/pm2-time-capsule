/*
package cz.cvut.fel.pm2;

import cz.cvut.fel.pm2.service.MailService;
import jakarta.annotation.PostConstruct;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.AutoConfigureTestEntityManager;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
@AutoConfigureTestEntityManager

@TestPropertySource(locations = "classpath:application-test.properties")
@ActiveProfiles("test")

public class MailTest {


    @Autowired
    private MailService mailService;


    @Test
    public void testSendEmail() {
        mailService.sendEmail("timecapsulepm2@gmail.com", "Test", "Test");
    }



}


 */
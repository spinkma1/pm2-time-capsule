package cz.cvut.fel.pm2.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class MailService {

    private final JavaMailSender emailSender;


    /**
     * Sends an email to an address with the specified params
     * @param to the recipient adress
     * @param subject the subject of the email
     * @param text the text of the email
     */
    @Transactional
    public void sendEmail(String to, String subject, String text) {

        //todo what if address is not valid?
        // - do try catch!
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject(subject);
        message.setText(text);

        try {
            emailSender.send(message);
        } catch (MailException e) {
            log.warn("the email could not be sent. Error message: {}", e.getMessage());
        } catch (Exception e) {
            log.error("An unexpected error occurred while sending the email. Error message: {}", e.getMessage());
        }
    }

}

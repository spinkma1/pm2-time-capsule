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
     *
     * @param to      the recipient address
     * @param subject the subject of the email
     * @param text    the text of the email
     */
    @Transactional
    public void sendEmail(String to, String subject, String text) {
        if (!isValidEmail(to)) {
            log.warn("Invalid email address: {}", to);
            throw new IllegalArgumentException("The provided email address is not valid: " + to);
        }

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject(subject);
        message.setText(text);

        try {
            emailSender.send(message);
            log.info("Email successfully sent to: {}", to);
        } catch (MailException e) {
            log.warn("The email could not be sent to {}. Error: {}", to, e.getMessage());
        } catch (Exception e) {
            log.error("An unexpected error occurred while sending the email to {}. Error: {}", to, e.getMessage());
        }
    }

    /**
     * Validates an email address using a regex pattern
     *
     * @param email the email address to validate
     * @return true if valid, false otherwise
     */
    private boolean isValidEmail(String email) {
        String emailRegex = "^[\\w-\\.]+@[\\w-]+\\.[a-zA-Z]{2,}$";
        return email != null && email.matches(emailRegex);
    }
}


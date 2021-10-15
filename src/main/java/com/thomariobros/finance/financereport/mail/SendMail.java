package com.thomariobros.finance.financereport.mail;

import net.jodah.failsafe.Failsafe;
import net.jodah.failsafe.RetryPolicy;
import org.apache.commons.lang3.StringUtils;

import javax.mail.*;
import javax.mail.internet.*;
import java.time.Duration;
import java.util.Properties;
import java.util.logging.Logger;

public class SendMail {

  private static final Logger LOGGER = Logger.getLogger(SendMail.class.getName());

  public void send(final String subject, final String content) throws Exception {
    final String from = System.getenv().getOrDefault("EMAIL_FROM", "");
    final String to = System.getenv().getOrDefault("EMAIL_TO", "");
    if (StringUtils.isEmpty(from) || StringUtils.isEmpty(to)) {
      return;
    }

    LOGGER.info("Sending email...");
    final Properties props = new Properties();
    props.put("mail.smtp.host", System.getenv().getOrDefault("EMAIL_SMTP_HOST", "localhost"));
    props.put("mail.smtp.port", System.getenv().getOrDefault("EMAIL_SMTP_PORT", "25"));
    props.put("mail.smtp.ssl.enable", System.getenv().getOrDefault("EMAIL_SMTP_SSL_ENABLE", "false"));
    props.put("mail.smtp.starttls.enable", System.getenv().getOrDefault("EMAIL_SMTP_STARTTTLS_ENABLE", "false"));
    final boolean auth = Boolean.parseBoolean(System.getenv().getOrDefault("EMAIL_SMTP_AUTH", "false"));
    props.put("mail.smtp.auth", auth);
    props.put("mail.smtp.from", System.getenv().getOrDefault("EMAIL_SMTP_FROM", ""));

    Session session = null;
    if (auth) {
      session = Session.getInstance(props, new Authenticator() {
        @Override
        protected PasswordAuthentication getPasswordAuthentication() {
          return new PasswordAuthentication(
                  System.getenv().getOrDefault("EMAIL_SMTP_AUTH_USERNAME", ""),
                  System.getenv().getOrDefault("EMAIL_SMTP_AUTH_PASSWORD", "")
          );
        }
      });
    } else {
      session = Session.getInstance(props);
    }
    Message message = new MimeMessage(session);
    message.setFrom(new InternetAddress(from));
    message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to));
    message.setSubject(MimeUtility.encodeText(subject, "UTF-8", "B"));
    Multipart multipart = new MimeMultipart();
    MimeBodyPart body = new MimeBodyPart();
    body.setContent(content, "text/html;charset=\"UTF-8\"");
    multipart.addBodyPart(body);
    message.setContent(multipart);

    // send with retries
    RetryPolicy<Object> retryPolicy = new RetryPolicy<>()
      .handle(Exception.class)
      .withDelay(Duration.ofSeconds(30))
      .withMaxRetries(5);
    Failsafe.with(retryPolicy).run(() -> {
      Transport.send(message);
      LOGGER.info("Email sent!");
    });
  }

}

package com.thomariobros.finance.financereport.mail;

import net.jodah.failsafe.Failsafe;
import net.jodah.failsafe.RetryPolicy;
import org.apache.commons.lang3.StringUtils;

import javax.mail.Message;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Transport;
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
    Properties props = new Properties();
    props.put("mail.smtp.host", System.getenv().getOrDefault("EMAIL_SMTP_HOST", "localhost"));
    props.put("mail.smtp.port", System.getenv().getOrDefault("EMAIL_SMTP_PORT", "25"));

    Session session = Session.getInstance(props);
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

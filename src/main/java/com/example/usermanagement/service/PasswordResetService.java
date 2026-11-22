package com.example.usermanagement.service;
import com.example.usermanagement.entity.PasswordResetToken;
import com.example.usermanagement.repository.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.HexFormat;
@Service
public class PasswordResetService {
 private final UserRepository users;private final PasswordResetTokenRepository tokens;private final PasswordEncoder encoder;private final ObjectProvider<JavaMailSender> mail;private static final Logger log=LoggerFactory.getLogger(PasswordResetService.class);
 @Value("${app.public-url}") private String publicUrl;@Value("${spring.mail.host:}") private String mailHost;
 public PasswordResetService(UserRepository u,PasswordResetTokenRepository t,PasswordEncoder e,ObjectProvider<JavaMailSender> m){users=u;tokens=t;encoder=e;mail=m;}
 @Transactional public void request(String email){
  users.findByEmailIgnoreCase(email.trim()).ifPresent(u->{
   tokens.deleteByUserId(u.getId());byte[] b=new byte[32];new SecureRandom().nextBytes(b);String raw=HexFormat.of().formatHex(b);
   tokens.save(new PasswordResetToken(u,hash(raw),Instant.now().plus(30,ChronoUnit.MINUTES)));
   String link=publicUrl+"/reset-password?token="+raw;
   JavaMailSender sender=mail.getIfAvailable();
   if(mailHost!=null&&!mailHost.isBlank()&&sender!=null){try{SimpleMailMessage msg=new SimpleMailMessage();msg.setTo(u.getEmail());msg.setSubject("Password reset");msg.setText("Reset your password within 30 minutes. Token: "+raw+"\nLink: "+link);sender.send(msg);}catch(MailException ex){log.error("Could not send password reset email",ex);}}
   else log.warn("[DEV ONLY] Password reset for {} -> token: {}  link: {}",u.getEmail(),raw,link);
  });
 }
 @Transactional public void reset(String raw,String password){
  var t=tokens.findByTokenHash(hash(raw)).orElseThrow(()->new ResponseStatusException(HttpStatus.BAD_REQUEST,"Invalid or expired reset token"));
  if(t.getUsedAt()!=null||t.getExpiresAt().isBefore(Instant.now()))throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Invalid or expired reset token");
  t.getUser().setPasswordHash(encoder.encode(password));t.markUsed();
 }
 private String hash(String s){try{return HexFormat.of().formatHex(MessageDigest.getInstance("SHA-256").digest(s.getBytes(StandardCharsets.UTF_8)));}catch(NoSuchAlgorithmException e){throw new IllegalStateException(e);}}
}

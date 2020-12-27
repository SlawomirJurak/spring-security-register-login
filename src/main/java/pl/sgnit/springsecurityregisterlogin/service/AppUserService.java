package pl.sgnit.springsecurityregisterlogin.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import pl.sgnit.springsecurityregisterlogin.model.AppUser;
import pl.sgnit.springsecurityregisterlogin.model.VerificationToken;
import pl.sgnit.springsecurityregisterlogin.repository.AppUserRepository;
import pl.sgnit.springsecurityregisterlogin.repository.VerificationTokenRepository;

import javax.mail.MessagingException;
import javax.servlet.http.HttpServletRequest;
import java.util.Optional;
import java.util.UUID;

@Service
public class AppUserService {

    @Value("${site.admin.email}")
    private String siteAdminEmail;

    private final PasswordEncoder passwordEncoder;
    private final AppUserRepository appUserRepository;
    private final VerificationTokenRepository verificationTokenRepository;
    private final MailSenderService mailSenderService;

    public AppUserService(PasswordEncoder passwordEncoder, AppUserRepository appUserRepository, VerificationTokenRepository verificationTokenRepository, MailSenderService mailSenderService) {
        this.passwordEncoder = passwordEncoder;
        this.appUserRepository = appUserRepository;
        this.verificationTokenRepository = verificationTokenRepository;
        this.mailSenderService = mailSenderService;
    }

    public void addNewUser(AppUser user, HttpServletRequest request) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user = appUserRepository.save(user);

        String token = UUID.randomUUID().toString();
        String adminToken = UUID.randomUUID().toString();

        VerificationToken verificationToken = new VerificationToken(token, user);
        if (user.getRole().equals("admin")) {
            verificationToken.setAdminToken(adminToken);
        }
        verificationTokenRepository.save(verificationToken);

        String url = "http://" + request.getServerName() +
            ":" +
            request.getServerPort() +
            request.getContextPath() +
            "/verify-token?token=" + token;
        String urlAdmin = "http://" + request.getServerName() +
            ":" +
            request.getServerPort() +
            request.getContextPath() +
            "/verify-token-admin?token=" + adminToken;


        try {
            mailSenderService.sendMail(user.getUsername(), "Verification token", url, false);
            if (user.getRole().equals("admin")) {
                mailSenderService.sendMail(siteAdminEmail, "Verification token for admin", urlAdmin, false);
            }
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }

    public void verifyToken(String token) {
        Optional<VerificationToken> verificationToken = verificationTokenRepository.getVerificationTokenByToken(token);

        if (verificationToken.isPresent()) {
            VerificationToken activationToken = verificationToken.get();
            AppUser appUser = activationToken.getAppUser();

            appUser.setEnabled(true);
            if (appUser.isAdminVerified()) {
                verificationTokenRepository.delete(activationToken);
            } else {
                appUser.setRole("user");
            }
            appUser.setUserVerified(true);
            appUserRepository.save(appUser);
        }
    }

    public void verifyTokenAdmin(String token) {
        Optional<VerificationToken> verificationToken = verificationTokenRepository.getVerificationTokenByAdminToken(token);

        if (verificationToken.isPresent()) {
            VerificationToken activationToken = verificationToken.get();
            AppUser appUser = activationToken.getAppUser();

            appUser.setRole("admin");
            appUser.setAdminVerified(true);
            if (appUser.isUserVerified()) {
                verificationTokenRepository.delete(activationToken);
            }
            appUserRepository.save(appUser);
        }
    }
}

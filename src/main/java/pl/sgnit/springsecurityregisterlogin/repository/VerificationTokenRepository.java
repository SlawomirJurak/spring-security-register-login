package pl.sgnit.springsecurityregisterlogin.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.sgnit.springsecurityregisterlogin.model.VerificationToken;

import java.util.Optional;

@Repository
public interface VerificationTokenRepository extends JpaRepository<VerificationToken, Long> {

    Optional<VerificationToken> getVerificationTokenByToken(String token);

    Optional<VerificationToken> getVerificationTokenByAdminToken(String adminToken);
}

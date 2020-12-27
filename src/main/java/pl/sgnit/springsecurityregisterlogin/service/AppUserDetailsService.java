package pl.sgnit.springsecurityregisterlogin.service;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import pl.sgnit.springsecurityregisterlogin.model.AppUser;
import pl.sgnit.springsecurityregisterlogin.repository.AppUserRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class AppUserDetailsService implements UserDetailsService {

    private final AppUserRepository appUserRepository;

    public AppUserDetailsService(AppUserRepository appUserRepository) {
        this.appUserRepository = appUserRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<AppUser> appUser = appUserRepository.getAppUserByUsername(username);

        if (appUser.isPresent()) {
            AppUser user = appUser.get();
            List<GrantedAuthority> grantedAuthorities = new ArrayList<>();

            if (user.getRole().equals("admin")) {
                grantedAuthorities.add(new SimpleGrantedAuthority("ROLE_ADMIN"));
            } else {
                grantedAuthorities.add(new SimpleGrantedAuthority("ROLE_USER"));
            }
            user.setGrantedAuthorities(grantedAuthorities);
            return user;
        }
        return null;
    }
}

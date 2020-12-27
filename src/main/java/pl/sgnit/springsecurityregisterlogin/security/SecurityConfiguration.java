package pl.sgnit.springsecurityregisterlogin.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.rememberme.JdbcTokenRepositoryImpl;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;
import pl.sgnit.springsecurityregisterlogin.model.AppUser;
import pl.sgnit.springsecurityregisterlogin.service.AppUserDetailsService;

import javax.sql.DataSource;
import java.util.Collection;
import java.util.Collections;

@Configuration
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

    private final AppUserDetailsService appUserDetailsService;
    private final DataSource dataSource;

    public SecurityConfiguration(AppUserDetailsService appUserDetailsService, DataSource dataSource) {
        this.appUserDetailsService = appUserDetailsService;
        this.dataSource = dataSource;
    }

    @Bean
    public PasswordEncoder getPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        User user = new User("Admin",
            getPasswordEncoder().encode("admin123"),
            Collections.singleton(new SimpleGrantedAuthority("ROLE_ADMIN")));
        auth.inMemoryAuthentication().withUser(user);
        auth.userDetailsService(appUserDetailsService);
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests()
            .antMatchers("/forAdmin").hasRole("ADMIN")
            .antMatchers("/forUser").hasAnyRole("USER", "ADMIN")
            .antMatchers("/signup").permitAll()
            .antMatchers("/").authenticated()
            .and()
            .formLogin().loginPage("/login").permitAll()
            .and()
            .logout()
            .logoutUrl("/logout")
            .invalidateHttpSession(true)
            .deleteCookies("JSESSIONID")
            .and()
            .rememberMe()
            .tokenRepository(persistentTokeRepository());
    }

    private PersistentTokenRepository persistentTokeRepository() {
        JdbcTokenRepositoryImpl tokenRepository = new JdbcTokenRepositoryImpl();

        tokenRepository.setDataSource(dataSource);
        return tokenRepository;
    }
}

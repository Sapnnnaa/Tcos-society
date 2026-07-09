package com.telco.members.tcos.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
//import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
//import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.servlet.util.matcher.PathPatternRequestMatcher;
import org.springframework.web.bind.annotation.SessionAttributes;

@Configuration
@SessionAttributes("members")
@EnableWebSecurity
public class SecurityConfiguration {

  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
      http
          .authorizeHttpRequests(requests -> requests
              .requestMatchers("/dist/**", "/login", "/resetpassword", "/forgotpassword","/api/greeting", "/api/banktranstally", "/api/banktranstallydownload", "/api/download-datetime").permitAll()
              .anyRequest().authenticated())
          .formLogin(login -> login
              .loginPage("/login").loginProcessingUrl("/login")
              .defaultSuccessUrl("/members/index",true)
              .permitAll())
          .logout(logout -> logout
        		  .logoutRequestMatcher(PathPatternRequestMatcher.withDefaults().matcher("/logout"))
                  .invalidateHttpSession(true) // Invalidate session on logout
                  .clearAuthentication(true) // Clear authentication info
                  .deleteCookies("JSESSIONID")
                  .permitAll());

      return http.build();
  }

    @Bean
    BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }
}

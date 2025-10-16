package com.campusbooking.web.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

/**
 * This class configures Spring Security for the web application.
 * It defines URL access rules, the login page, and the password encoding mechanism.
 */
@Configuration
@EnableWebSecurity
public class WebSecurityConfig {

    // Inject our custom success handler, which contains the logic for role-based redirects.
    @Autowired
    private RoleBasedAuthenticationSuccessHandler successHandler;

    /**
     * Creates a bean for the password encoder. We use BCrypt, which is a strong,
     * industry-standard hashing algorithm for securely storing passwords.
     * @return A PasswordEncoder instance.
     */
    @Bean
    public static PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Configures the security filter chain that applies to all HTTP requests.
     * @param http The HttpSecurity object to configure.
     * @return The configured SecurityFilterChain.
     * @throws Exception If an error occurs during configuration.
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            // Define authorization rules for HTTP requests.
            .authorizeHttpRequests(authorize -> authorize
                // Allow public, unauthenticated access to these specific URLs.
                // This includes the root, login, registration, "no pending requests" page, and all CSS files.
                .requestMatchers("/", "/login", "/register", "/no-pending-requests", "/css/**", "/style.css").permitAll()
                // All other requests in the application require the user to be authenticated.
                .anyRequest().authenticated()
            )
            // Configure the form-based login process.
            .formLogin(form -> form
                // Specify the URL for our custom login page.
                .loginPage("/login")
                // The URL where the login form will submit its data (POST request).
                .loginProcessingUrl("/login")
                // Use our custom success handler to redirect users based on their role.
                .successHandler(successHandler)
                // Allow all users to access the login page itself.
                .permitAll()
            )
            // Configure the logout process.
            .logout(logout -> logout
                // The URL that triggers a logout.
                .logoutUrl("/logout")
                // The URL to redirect to after a successful logout.
                .logoutSuccessUrl("/login?logout")
                .permitAll()
            );
        return http.build();
    }
}


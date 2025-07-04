package Renter_Car.Security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    private CustomUserDetailService userDetailService;

    @Autowired
    public SecurityConfig(CustomUserDetailService userDetailService) {
        this.userDetailService = userDetailService;
    }

    @Bean
    public static PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // Disable CSRF (cross site request forgery)
                .csrf(csrf -> csrf.disable())

                // Configure authorization requests
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers("/authentication/**","/images/**", "/img/**", "/css/**", "/js/**", "/lib/**").permitAll()
                        .requestMatchers("/", "/register/**", "/forgot-password", "reset-password", "/update-password", "/login").permitAll()
                        .requestMatchers("/guest", "/profile/**").permitAll()
                        .requestMatchers("/customer").hasRole("CUSTOMER")
                        .requestMatchers("/products").hasRole("CAR_OWNER")
                        .anyRequest().authenticated() // Require authentication for all other requests
                )

                // Configure form login
                .formLogin(form -> form
                        .loginPage("/login")
                        .usernameParameter("email")
                        .passwordParameter("password")
                        .defaultSuccessUrl("/",true)
                        .loginProcessingUrl("/login")
                        .failureUrl("/login?error=true")
                        .permitAll()
                )

                // Configure logout
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/")
                        .permitAll()
                );

        return http.build();
    }

    public void configure(AuthenticationManagerBuilder builder) throws Exception{
        builder.userDetailsService(userDetailService).passwordEncoder(passwordEncoder());
    }
}

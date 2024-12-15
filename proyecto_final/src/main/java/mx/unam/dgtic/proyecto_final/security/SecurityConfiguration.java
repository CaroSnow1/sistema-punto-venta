package mx.unam.dgtic.proyecto_final.security;

import mx.unam.dgtic.proyecto_final.auth.service.UsuarioService;
import mx.unam.dgtic.proyecto_final.security.jwt.JWTAuthenticationFilter;
import mx.unam.dgtic.proyecto_final.security.jwt.JWTTokenProvider;
import mx.unam.dgtic.proyecto_final.security.logout.CustomLogoutSuccessHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import lombok.extern.slf4j.Slf4j;

@Configuration
@EnableWebSecurity
@Slf4j
public class SecurityConfiguration {

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private JWTTokenProvider tokenProvider;

    @Autowired
    private CustomLogoutSuccessHandler logoutSuccessHandler;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, UsuarioService usuarioService) throws Exception {
        JWTAuthenticationFilter jwtAuthenticationFilter = new JWTAuthenticationFilter(tokenProvider, usuarioService);

        //log.info("Configurando Logout");

        http
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/inicio", "/bootstrap/**", "/iconos/**", "/imagen/**", "/tema/**", "/favicon.ico", "/login", "/token").permitAll()
                        .requestMatchers("/venta/**", "/corte-caja/generar-corte").hasRole("CAJERO")
                        .requestMatchers("/producto/**", "/categoria/**").hasAnyRole("CAJERO", "GERENTE")
                        .requestMatchers("/reportes/**", "/usuario/**", "/corte-caja/consultar-corte").hasRole("GERENTE")
                        .requestMatchers("/dashboard").authenticated() // Protege el dashboard
                        .anyRequest().authenticated()
                )
                .formLogin(login -> login
                        .loginPage("/login")
                        .defaultSuccessUrl("/dashboard", true) // Redirige al dashboard después del login
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutUrl("/doLogout")
                        .logoutSuccessHandler(logoutSuccessHandler)
                        .deleteCookies("token", "JSESSIONID")
                        .clearAuthentication(false) // Desactivar limpieza antes del handler
                        .invalidateHttpSession(false) // Mantener sesión hasta después del handler
                )
                .addFilterAfter(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .csrf(AbstractHttpConfigurer::disable) // Desactiva CSRF porque estamos usando JWT
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS) // Política sin estado (JWT)
                );

        // Log para verificar el contexto antes del logout
        //log.info("Contexto de seguridad al configurar logout: {}", SecurityContextHolder.getContext().getAuthentication());

        return http.build();
    }

    @Bean
    public AuthenticationProvider authenticationProvider(PasswordEncoder passwordEncoder) {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder);
        return authProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }
}

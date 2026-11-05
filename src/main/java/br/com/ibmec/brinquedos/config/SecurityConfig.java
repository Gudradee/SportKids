package br.com.ibmec.brinquedos.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@EnableConfigurationProperties(SegurancaProperties.class)
public class SecurityConfig {
    public static final String ROLE_USER = "USER";
    public static final String ROLE_ADMIN = "ADMIN";

    @Bean
    PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    @Bean
    UserDetailsService userDetailsService(SegurancaProperties props, PasswordEncoder encoder) {
        UserDetails usuario = User.withUsername(props.usuario().username())
                .password(encoder.encode(props.usuario().senha()))
                .roles(ROLE_USER)
                .build();
        UserDetails admin = User.withUsername(props.admin().username())
                .password(encoder.encode(props.admin().senha()))
                .roles(ROLE_ADMIN, ROLE_USER)
                .build();
        return new InMemoryUserDetailsManager(usuario, admin);
    }

    @Bean
    @Order(1)
    SecurityFilterChain apiFilterChain(HttpSecurity http, ApiSecurityErrorHandler erros) throws Exception {
        http
            .securityMatcher("/brinquedos/**")
            .csrf(csrf -> csrf.disable())
            .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                .requestMatchers(HttpMethod.GET, "/brinquedos/**").hasAnyRole(ROLE_USER, ROLE_ADMIN)
                .requestMatchers(HttpMethod.POST, "/brinquedos/**").hasRole(ROLE_ADMIN)
                .requestMatchers(HttpMethod.PUT, "/brinquedos/**").hasRole(ROLE_ADMIN)
                .requestMatchers(HttpMethod.DELETE, "/brinquedos/**").hasRole(ROLE_ADMIN)
                .anyRequest().denyAll()
            )
            .httpBasic(basic -> basic.authenticationEntryPoint(erros))
            .exceptionHandling(ex -> ex
                .authenticationEntryPoint(erros)
                .accessDeniedHandler(erros)
            );
        return http.build();
    }

    @Bean
    @Order(2)
    SecurityFilterChain webFilterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/", "/login", "/error", "/css/**", "/images/**", "/favicon.ico").permitAll()
                .requestMatchers("/h2-console/**").hasRole(ROLE_ADMIN)
                .requestMatchers("/app/admin/**").hasRole(ROLE_ADMIN)
                .requestMatchers("/app/**").hasAnyRole(ROLE_USER, ROLE_ADMIN)
                .anyRequest().authenticated()
            )
            .csrf(csrf -> csrf.ignoringRequestMatchers("/h2-console/**"))
            .headers(h -> h.frameOptions(f -> f.sameOrigin()))
            .formLogin(form -> form
                .loginPage("/login")
                .defaultSuccessUrl("/app/brinquedos", true)
                .failureUrl("/login?error")
                .permitAll()
            )
            .logout(logout -> logout
                .logoutUrl("/logout")
                .logoutSuccessUrl("/login?logout")
                .invalidateHttpSession(true)
                .deleteCookies("JSESSIONID")
                .permitAll()
            );
        return http.build();
    }
}

/*
 * RESUMO: Esta classe de configuracao (@Configuration + @EnableWebSecurity) define toda a seguranca da
 * aplicacao com o Spring Security. Ela cria o PasswordEncoder (BCrypt) e os dois usuarios em memoria — "user"
 * com o perfil USER e "admin" com os perfis ADMIN e USER — lendo as senhas do SegurancaProperties. Em
 * seguida monta duas cadeias de filtros (SecurityFilterChain): a primeira (@Order(1)) protege a API REST
 * /brinquedos de forma stateless com HTTP Basic, ideal para o Postman, liberando GET para USER e ADMIN e
 * restringindo POST, PUT e DELETE ao ADMIN, com respostas 401/403 em JSON; a segunda (@Order(2)) protege o
 * site Thymeleaf com tela de login propria (/login), deixa publicas a landing page e os arquivos estaticos,
 * exige login para /app/**, reserva /app/admin/** e o console H2 ao ADMIN, mantem a protecao CSRF nos
 * formularios e configura o logout. Separar as cadeias permite aplicar a cada tipo de cliente o mecanismo
 * de autenticacao mais adequado e seguro.
 */

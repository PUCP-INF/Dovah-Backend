package pe.edu.pucp.dovah.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.authority.mapping.GrantedAuthoritiesMapper;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.DefaultOAuth2AuthorizationRequestResolver;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizationRequestResolver;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;
import org.springframework.security.oauth2.core.user.OAuth2UserAuthority;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import pe.edu.pucp.dovah.RRHH.repository.UsuarioRepository;
import pe.edu.pucp.dovah.auth.JWTFilter;
import pe.edu.pucp.dovah.auth.JWTUserDetailsService;

import javax.servlet.http.HttpServletResponse;
import java.time.Instant;
import java.util.*;
import java.util.function.Consumer;

@Configuration
@EnableMethodSecurity
public class WebSecurityConfig {
    private final JWTFilter jwtFilter;
    private final JWTUserDetailsService jwtUserDetailsService;
    private final UsuarioRepository usuarioRepository;
    private final ClientRegistrationRepository clientRegistrationRepository;
    private final Environment environment;

    public WebSecurityConfig(JWTFilter jwtFilter,
                             JWTUserDetailsService jwtUserDetailsService,
                             UsuarioRepository usuarioRepository,
                             ClientRegistrationRepository clientRegistrationRepository,
                             Environment environment) {
        this.jwtFilter = jwtFilter;
        this.jwtUserDetailsService = jwtUserDetailsService;
        this.usuarioRepository = usuarioRepository;
        this.clientRegistrationRepository = clientRegistrationRepository;
        this.environment = environment;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        if (Objects.equals(environment.getActiveProfiles()[0], "prod")) {
            http.csrf().csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse());
        } else {
            http.csrf().disable();
        }
        http
                .cors().and()
                .httpBasic().disable()
                .formLogin().disable()
                .authorizeRequests()
                    .antMatchers("/api/v1/usuario/auth/**").permitAll()
                    .antMatchers("/api/v1/documento/blob/**").permitAll().and()
                .authorizeRequests()
                    .antMatchers("/api/v1/**").hasRole("USUARIO").and()
                .authorizeRequests()
                    .anyRequest().permitAll().and()
                .userDetailsService(jwtUserDetailsService)
                .exceptionHandling()
                .authenticationEntryPoint((request, response, authException) ->
                        response.sendError(HttpServletResponse.SC_UNAUTHORIZED, authException.getMessage())
                ).and()
                .oauth2Login(oauth2 -> oauth2
                        .userInfoEndpoint(userInfo -> userInfo
                                .userAuthoritiesMapper(userAuthoritiesMapper())
                        )
                        .authorizationEndpoint(authorization -> authorization
                                .authorizationRequestResolver(authorizationRequestResolver(this.clientRegistrationRepository))
                        )
                )
                .logout(l -> l
                        .logoutUrl("/api/v1/logout")
                        .logoutSuccessUrl("/")
                        .invalidateHttpSession(true)
                        .deleteCookies("JSESSIONID")
                        .logoutSuccessHandler(logoutSuccessHandler())
                )
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED);
        http.addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    private GrantedAuthoritiesMapper userAuthoritiesMapper() {
        return (authorities) -> {
            Set<GrantedAuthority> mappedAuthorities = new HashSet<>();
            authorities.forEach(authority -> {
                if (authority instanceof OAuth2UserAuthority oauth2UserAuthority) {
                    Map<String, Object> attr = oauth2UserAuthority.getAttributes();
                    var usrDb = usuarioRepository.queryByCorreoAndActivoIsTrue(attr.get("email").toString());
                    if (usrDb.isPresent()) {
                        var usr = usrDb.get();
                        if (usr.getPicture() == null) {
                            usr.setNombre(attr.get("given_name").toString());
                            usr.setApellido(attr.get("family_name").toString());
                            usr.setPicture(attr.get("picture").toString());
                        }
                        mappedAuthorities.add(new SimpleGrantedAuthority("ROLE_USUARIO"));
                        for (var rol: usr.getListaRoles()) {
                            mappedAuthorities.add(new SimpleGrantedAuthority(String.format("ROLE_%s", rol.getNombre())));
                        }
                        usr.setLastLogin(Instant.now());
                        usuarioRepository.save(usr);
                    }
                }
            });
            return mappedAuthorities;
        };
    }

    private OAuth2AuthorizationRequestResolver authorizationRequestResolver(
            ClientRegistrationRepository clientRegistrationRepository) {

        DefaultOAuth2AuthorizationRequestResolver authorizationRequestResolver =
                new DefaultOAuth2AuthorizationRequestResolver(
                        clientRegistrationRepository, "/oauth2/authorization");
        authorizationRequestResolver.setAuthorizationRequestCustomizer(
                authorizationRequestCustomizer());

        return  authorizationRequestResolver;
    }

    private Consumer<OAuth2AuthorizationRequest.Builder> authorizationRequestCustomizer() {
        return customizer -> customizer
                .additionalParameters(params -> params.put("prompt", "consent"));
    }

    @Bean
    public LogoutSuccessHandler logoutSuccessHandler() {
        return new CustomLogoutSuccessHandler();
    }

    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowCredentials(true);
        configuration.setAllowedOrigins(List.of("http://localhost:3000"));
        configuration.setAllowedMethods(List.of("*"));
        configuration.setAllowedHeaders(List.of("*"));
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManagerBean(HttpSecurity http) throws Exception {
        AuthenticationManagerBuilder authenticationManagerBuilder = http.getSharedObject(AuthenticationManagerBuilder.class);
        authenticationManagerBuilder.userDetailsService(jwtUserDetailsService).passwordEncoder(passwordEncoder());
        return authenticationManagerBuilder.build();
    }
}

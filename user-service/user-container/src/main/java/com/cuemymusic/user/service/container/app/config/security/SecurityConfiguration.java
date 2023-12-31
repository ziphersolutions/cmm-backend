package com.cuemymusic.user.service.container.app.config.security;


import lombok.RequiredArgsConstructor;
import com.cuemymusic.user.service.authentication.filter.JwtAuthenticationFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.LogoutHandler;

import static com.cuemymusic.user.service.authentication.entities.Role.*;
import static com.cuemymusic.user.service.domain.valueobject.Permission.*;
import static org.springframework.http.HttpMethod.*;


@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
@EnableMethodSecurity
public class SecurityConfiguration {
    private final JwtAuthenticationFilter jwtAuthFilter;
    private final AuthenticationProvider authenticationProvider;
    private final LogoutHandler logoutHandler;


    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception{
        http
                .csrf()
                .disable()
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .authorizeHttpRequests()

                //////////////////////////////////////////////////////////////////
                // PUBLIC RULES
                .requestMatchers("/api/auth/**").permitAll()
                // USERS Administration RULES
                .requestMatchers("/api/admin/users/**").hasAnyRole(ADMIN.name())
                .requestMatchers(GET, "/api/admin/users/**").hasAnyAuthority(ADMIN_READ.name())
                .requestMatchers(GET, "/api/admin/users/").hasAnyAuthority(ADMIN_READ.name())
                .requestMatchers(POST, "/api/admin/users/").hasAnyAuthority(ADMIN_CREATE.name())
                .requestMatchers(PUT, "/api/admin/users/").hasAnyAuthority(ADMIN_UPDATE.name())
                .requestMatchers(DELETE, "/api/admin/users/**").hasAnyAuthority(ADMIN_DELETE.name())

                // USERS Management RULES
                .requestMatchers("/api/manager/users/**").hasAnyRole(MANAGER.name())
                .requestMatchers(PUT, "/api/manager/users/").hasAnyAuthority(MANAGER_UPDATE.name())

                // USERS RULES
                .requestMatchers("/api/profile/**").hasAnyRole(ADMIN.name(), USER.name(), MANAGER.name(), COACH.name())
                .requestMatchers(GET, "/api/profile/**").hasAnyAuthority(USER_READ.name())
                .requestMatchers(GET, "/api/profile/").hasAnyAuthority(USER_READ.name())
                .requestMatchers(PUT, "/api/profile/").hasAnyAuthority(USER_READ.name())


                /////////////////////////////////////////////////////////////////////////////////
                .anyRequest()
                .permitAll()
                .and()
                .authenticationProvider(authenticationProvider)
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
                .logout()
                .logoutUrl("/api/auth/logout")
                .addLogoutHandler(logoutHandler)
                .logoutSuccessHandler((request, response, authentication) -> SecurityContextHolder.clearContext())
        ;

        return http.build();
    }
}

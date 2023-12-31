package com.cuemymusic.club.service.container.app.config.security;


import com.cuemymusic.club.service.authentication.filter.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
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

import static com.cuemymusic.user.service.domain.valueobject.Permission.*;
import static com.cuemymusic.user.service.domain.valueobject.Role.*;
import static org.springframework.http.HttpMethod.*;


@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
@EnableMethodSecurity
public class SecurityConfiguration {
    private final JwtAuthenticationFilter jwtAuthFilter;
    private final AuthenticationProvider authenticationProvider;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception{
        http
                .csrf()
                .disable()
                .authorizeHttpRequests()

                //////////////////////////////////////////////////////////////////

                // Clubs Administration RULES
                .requestMatchers("/api/admin/clubs/**").hasAnyRole(ADMIN.name())
                .requestMatchers(GET, "/api/admin/clubs/**").hasAnyAuthority(ADMIN_READ.name())
                .requestMatchers(GET, "/api/admin/clubs/").hasAnyAuthority(ADMIN_READ.name())
                .requestMatchers(POST, "/api/admin/clubs/").hasAnyAuthority(ADMIN_CREATE.name())
                .requestMatchers(PUT, "/api/admin/clubs/").hasAnyAuthority(ADMIN_UPDATE.name())
                .requestMatchers(DELETE, "/api/admin/clubs/**").hasAnyAuthority(ADMIN_DELETE.name())

                // CLUB Management RULES
                .requestMatchers("/api/manage/clubs/**").hasAnyRole(MANAGER.name())
                .requestMatchers(PUT, "/api/manage/clubs/**").hasAnyAuthority(MANAGER_READ.name())


                // Devices Administration RULES
                .requestMatchers("/api/admin/devices/**").hasAnyRole(ADMIN.name())
                .requestMatchers(GET, "/api/admin/devices/**").hasAnyAuthority(ADMIN_READ.name())
                .requestMatchers(GET, "/api/admin/devices/").hasAnyAuthority(ADMIN_READ.name())
                .requestMatchers(POST, "/api/admin/devices/").hasAnyAuthority(ADMIN_CREATE.name())
                .requestMatchers(PUT, "/api/admin/devices/").hasAnyAuthority(ADMIN_UPDATE.name())
                .requestMatchers(DELETE, "/api/admin/devices/**").hasAnyAuthority(ADMIN_DELETE.name())

                // PUBLIC RULES
                .requestMatchers("/public/devices/**").permitAll()


                // USERS RULES
                .requestMatchers("/api/playlist/**").hasAnyRole(ADMIN.name(), USER.name(), MANAGER.name(), COACH.name())
                .requestMatchers(GET, "/api/playlist/**").hasAnyAuthority(USER_READ.name())
                .requestMatchers(GET, "/api/playlist/").hasAnyAuthority(USER_READ.name())

                /////////////////////////////////////////////////////////////////////////////////
                .anyRequest()
                .permitAll()
                .and()
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .authenticationProvider(authenticationProvider)
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)

        ;

        return http.build();
    }
}

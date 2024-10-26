package com.example.manager.securities;

import io.jsonwebtoken.io.Decoders;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;


import javax.crypto.spec.SecretKeySpec;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Value("${jwt.signerKey}")
    private String signerKey; // Khóa bí mật dùng để ký JWT

    private final String[] PUBLIC_ENDPOINTS = {"/api/v1/auth/login", "/api/v1/auth/register","/api/v1/user/register"}; // Các endpoint công khai

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {
        httpSecurity
                .authorizeRequests(request ->
                        request.requestMatchers(PUBLIC_ENDPOINTS).permitAll() // Cho phép truy cập không cần xác thực
                                .requestMatchers("/api/v1/auth/user").hasAuthority("USER")
                                .requestMatchers("/api/v1/auth/admin").hasAuthority("ADMIN")
                                .anyRequest().authenticated() // Các request còn lại cần xác thực
                )
                .oauth2ResourceServer(oauth2 ->
                        oauth2.jwt(jwtConfigurer -> jwtConfigurer
                                .decoder(jwtDecoder())
                                .jwtAuthenticationConverter(jwtAuthenticationConverter()) // Sử dụng jwtAuthenticationConverter
                        )
                )
                .csrf(AbstractHttpConfigurer::disable); // Tắt CSRF cho API

        return httpSecurity.build(); // Xây dựng SecurityFilterChain
    }


    @Bean
    public JwtDecoder jwtDecoder() {
        byte[] keyBytes = Decoders.BASE64.decode(signerKey); // Giải mã signerKey nếu nó được encode dưới dạng Base64
        SecretKeySpec secretKeySpec = new SecretKeySpec(keyBytes, "HmacSHA512");
        return NimbusJwtDecoder
                .withSecretKey(secretKeySpec)
                .macAlgorithm(MacAlgorithm.HS512)
                .build();
    }

    // Ánh xạ scope từ JWT thành authorities
    private Converter<Jwt, AbstractAuthenticationToken> jwtAuthenticationConverter() {
        JwtGrantedAuthoritiesConverter grantedAuthoritiesConverter = new JwtGrantedAuthoritiesConverter();
        grantedAuthoritiesConverter.setAuthoritiesClaimName("roles"); // Lấy thông tin từ claim "roles"
        grantedAuthoritiesConverter.setAuthorityPrefix(""); // Loại bỏ tiền tố

        JwtAuthenticationConverter authenticationConverter = new JwtAuthenticationConverter();
        authenticationConverter.setJwtGrantedAuthoritiesConverter(grantedAuthoritiesConverter);
        return authenticationConverter;
    }



    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(); // Sử dụng BCrypt để mã hóa mật khẩu
    }
}

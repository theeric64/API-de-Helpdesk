package com.helpdesk.api.config;

import com.helpdesk.api.entity.Rol;
import com.helpdesk.api.entity.Usuario;
import com.helpdesk.api.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class AdminSeeder implements CommandLineRunner {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.admin.email}")
    private String adminEmail;

    @Value("${app.admin.password}")
    private String adminPassword;

    @Override
    public void run(String... args) {
        if (usuarioRepository.existsByEmail(adminEmail)) {
            return;
        }

        Usuario admin = Usuario.builder()
                .nombre("Administrador")
                .email(adminEmail)
                .password(passwordEncoder.encode(adminPassword))
                .rol(Rol.ADMIN)
                .build();

        usuarioRepository.save(admin);

        log.info("=======================================================");
        log.info(" Usuario ADMIN sembrado -> email: {} | password: {}", adminEmail, adminPassword);
        log.info("=======================================================");
    }
}
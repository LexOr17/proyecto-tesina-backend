//package com.upemor.app_nutriologo.config;
//
//import com.upemor.app_nutriologo.administrador.entity.Administrador;
//import com.upemor.app_nutriologo.usuario.enums.Genero;
//import com.upemor.app_nutriologo.usuario.enums.Rol;
//import com.upemor.app_nutriologo.usuario.repository.UsuarioRepository;
//import lombok.RequiredArgsConstructor;
//import org.springframework.boot.CommandLineRunner;
//import org.springframework.security.crypto.password.PasswordEncoder;
//import org.springframework.stereotype.Component;
//
//import java.time.LocalDate;
//import java.time.LocalDateTime;
//
//@Component
//@RequiredArgsConstructor
//public class DataInitializer implements CommandLineRunner {
//
//    private final UsuarioRepository usuarioRepository;
//    private final PasswordEncoder passwordEncoder;
//
////    @Override
////    public void run(String... args) throws Exception {
//////        if (usuarioRepository.findByCorreo("admin@nutriologo.com") != null) {
//////            Administrador admin = new Administrador();
//////            admin.setNombre("Admin");
//////            admin.setApellidoPaterno("Principal");
//////            admin.setCorreo("admin@nutriologo.com");
//////            admin.setContrasena(passwordEncoder.encode("admin123"));
//////            admin.setRol(Rol.ADMINISTRADOR);
//////            admin.setGenero(Genero.OTRO);
//////            admin.setFechaNacimiento(LocalDate.of(1990, 1, 1));
//////            admin.setFechaRegistro(LocalDateTime.now());
//////            admin.setTelefono("0000000000");
//////            admin.setTipoAdmin("SUPER_ADMIN");
//////
//////            usuarioRepository.save(admin);
////        }
////    }
//}

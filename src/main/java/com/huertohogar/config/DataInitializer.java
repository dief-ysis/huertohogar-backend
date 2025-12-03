package com.huertohogar.config;

import com.huertohogar.entity.Producto;
import com.huertohogar.entity.Usuario;
import com.huertohogar.repository.ProductoRepository;
import com.huertohogar.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.math.BigDecimal;

@Configuration
@RequiredArgsConstructor
public class DataInitializer {

    private final UsuarioRepository usuarioRepository;
    private final ProductoRepository productoRepository;
    private final PasswordEncoder passwordEncoder;

    @Bean
    public CommandLineRunner initData() {
        return args -> {
            // 1. Crear Admin si no existe
            if (usuarioRepository.findByEmail("admin@huertohogar.cl").isEmpty()) {
                Usuario admin = Usuario.builder()
                        .nombre("Administrador")
                        .email("admin@huertohogar.cl")
                        .password(passwordEncoder.encode("admin123"))
                        .rol(Usuario.Rol.ROLE_ADMIN)
                        .telefono("999999999")
                        .activo(true)
                        .build();
                usuarioRepository.save(admin);
                System.out.println(">> ADMIN CREADO: admin@huertohogar.cl / admin123");
            }

            // 2. Crear Usuario Cliente si no existe
            if (usuarioRepository.findByEmail("cliente@huertohogar.cl").isEmpty()) {
                Usuario user = Usuario.builder()
                        .nombre("Cliente Prueba")
                        .email("cliente@huertohogar.cl")
                        .password(passwordEncoder.encode("cliente123"))
                        .rol(Usuario.Rol.ROLE_USER)
                        .telefono("888888888")
                        .direccion("Calle Falsa 123")
                        .comuna("Santiago")
                        .activo(true)
                        .build();
                usuarioRepository.save(user);
                System.out.println(">> CLIENTE CREADO: cliente@huertohogar.cl / cliente123");
            }

            // 3. Crear Productos si está vacío
            if (productoRepository.count() == 0) {
                crearProducto("Papas Orgánicas", "Papas frescas del sur", 1500, "Verduras", 100, "kg", "https://i.imgur.com/u7y7y7.jpg");
                crearProducto("Tomates Limachinos", "Tomates jugosos y rojos", 2200, "Verduras", 50, "kg", "https://i.imgur.com/x8x8x8.jpg");
                crearProducto("Miel de Ulmo", "Miel 100% natural", 8990, "Despensa", 20, "frasco", "https://i.imgur.com/z9z9z9.jpg");
                crearProducto("Lechuga Costina", "Hidropónica fresca", 1200, "Verduras", 30, "un", "https://i.imgur.com/a1a1a1.jpg");
                System.out.println(">> PRODUCTOS INICIALES CREADOS");
            }
        };
    }

    private void crearProducto(String nombre, String desc, double precio, String cat, int stock, String unidad, String img) {
        Producto p = Producto.builder()
                .nombre(nombre)
                .descripcion(desc)
                .precio(BigDecimal.valueOf(precio))
                .categoria(cat)
                .stock(stock)
                .unidad(unidad)
                .imagen(img)
                .activo(true)
                .destacado(true)
                .build();
        productoRepository.save(p);
    }
}
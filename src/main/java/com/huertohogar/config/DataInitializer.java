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
                // PRODUCTOS NORMALES (Descuento 0, Destacado false)
                crearProducto("Papas Orgánicas", "Papas frescas del sur", 1500, 0, "VERDURAS", 100, "kg", "https://ecomercioagrario.com/wp-content/uploads/2015/08/peru-duplica-produccion-papa-organica-gracias-semilla-certificada-ecomercioagrario.jpg", false);
                crearProducto("Lechuga Costina", "Hidropónica fresca", 1200, 0, "VERDURAS", 30, "un", "https://chilehuerta.cl/wp-content/uploads/2022/03/Lechuga-Costina.jpg", false);
                crearProducto("Miel de Ulmo", "Miel 100% natural", 8990, 0, "ORGANICOS", 20, "frasco", "https://adagio.cl/cdn/shop/files/miel-organica-ulmo.jpg?v=1701198006&width=1280", true);

                // OFERTAS (Con descuento, Destacado true)
                crearProducto("Pack Frutas Estación", "Mix de manzanas, peras y naranjas", 5000, 20, "FRUTAS", 50, "malla", "https://images.unsplash.com/photo-1610832958506-aa56368176cf?auto=format&fit=crop&w=500&q=60", true);
                crearProducto("Tomates Limachinos", "Tomates jugosos y rojos", 2200, 15, "VERDURAS", 50, "kg", "https://images.unsplash.com/photo-1592924357228-91a4daadcfea?auto=format&fit=crop&w=500&q=60", true);
                crearProducto("Aceite de Oliva", "Prensado en frío 1L", 12990, 30, "ORGANICOS", 15, "botella", "https://images.unsplash.com/photo-1474979266404-7eaacbcd87c5?auto=format&fit=crop&w=500&q=60", true);
                
                System.out.println(">> PRODUCTOS INICIALES CREADOS");
            }
        };
    }

    private void crearProducto(String nombre, String desc, double precio, double descuento, String cat, int stock, String unidad, String img, boolean destacado) {
        Producto p = Producto.builder()
                .nombre(nombre)
                .descripcion(desc)
                .precio(BigDecimal.valueOf(precio))
                .descuento(BigDecimal.valueOf(descuento))
                .categoria(cat)
                .stock(stock)
                .unidad(unidad)
                .imagen(img)
                .activo(true)
                .destacado(destacado)
                .rating(BigDecimal.valueOf(4.5)) // Valor por defecto
                .build();
        productoRepository.save(p);
    }
}
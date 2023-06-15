package com.mx.banorte.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.transaction.Transactional;

import org.apache.camel.CamelContext;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.builder.RouteBuilder;
import org.junit.jupiter.api.Test;

import com.mx.banorte.services.vo.Usuario;

import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
public class JuntiTestUsuario  {
    
    @Inject
    CamelContext camelContext;

    @Inject
    ProducerTemplate producerTemplate;

    @Inject
    EntityManager entityManager;

    @Test
        public void testMyRoute() throws Exception {
            camelContext.addRoutes(new RouteBuilder() {
                @Override
                public void configure() throws Exception {
                    from("direct:crearUsuario")
                            .routeId("crearUsuario")
                            .log("Creando un usuario nuevo: ${body}")
                            .toD("jpa://" + Usuario.class.getName());
                }
            });

            Usuario usuario = new Usuario();
            usuario.setNombre("Gerald");
            usuario.setApellido("Pasten");

            producerTemplate.sendBody("direct:crearUsuario", usuario);

            Usuario result = entityManager.find(Usuario.class, usuario.getId());
            assertNotNull(result);
        }
    
    @Transactional
    @Test
        public void testActualizarUsuario() throws Exception {
            // Datos del usuario a actualizar
            Usuario usuario = new Usuario();
            usuario.setNombre("John");
            usuario.setApellido("Doe");

            // Persistir el usuario en la base de datos
            entityManager.persist(usuario);

            // Configuración de la ruta
            camelContext.addRoutes(new RouteBuilder() {
                @Override
                public void configure() throws Exception {
                    from("direct:actualizarUsuario")
                            .routeId("actualizarUsuario")
                            .log("Actualizando el usuario: ${body}")
                            .toD("jpa://" + Usuario.class.getName());
                }
            });

            // Actualización del usuario
            usuario.setNombre("Gerald");
            usuario.setApellido("Pasten");
            producerTemplate.sendBody("direct:actualizarUsuario", usuario);

            // Verificación de que el usuario fue actualizado exitosamente
            Usuario result = entityManager.find(Usuario.class, usuario.getId());
            assertNotNull(result);
            assertEquals("Gerald", result.getNombre());
            assertEquals("Pasten", result.getApellido());
        }
    @Transactional
    @Test
        public void testActualizarUsuario_DatosErroneos() throws Exception {
            // Datos del usuario a actualizar
            Usuario usuario = new Usuario();
            usuario.setNombre("John");
            usuario.setApellido("Doe");

            // Persistir el usuario en la base de datos
            entityManager.persist(usuario);

            // Configuración de la ruta
            camelContext.addRoutes(new RouteBuilder() {
                @Override
                public void configure() throws Exception {
                    from("direct:actualizarUsuario")
                            .routeId("actualizarUsuario")
                            .log("Actualizando el usuario: ${body}")
                            .toD("jpa://" + Usuario.class.getName());
                }
            });

            // Actualización del usuario con datos erroneos
            usuario.setNombre(null);
            usuario.setApellido(null);
            producerTemplate.sendBody("direct:actualizarUsuario", usuario);

            // Verificación de que se haya producido una respuesta de error
            assertNull(entityManager.find(Usuario.class, usuario.getId()));
        }
    

}

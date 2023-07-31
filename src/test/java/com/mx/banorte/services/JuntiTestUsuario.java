package com.mx.banorte.services;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;


import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.transaction.Transactional;

import org.apache.camel.CamelContext;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.builder.RouteBuilder;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import com.mx.banorte.services.vo.Usuario;

import io.quarkus.test.junit.QuarkusTest;

/**
 * Clase de pruebas unitarias escrita en Java utilizando el framework QuarkusTest 
 * prueba el funcionamiento de diferentes funcionalidades relacionadas con la creación y actualización de usuarios.
 * 
 * La anotación @QuarkusTest indica que la clase es una prueba de Quarkus y debe ser ejecutada como tal.
 * Las anotaciones @Inject se utilizan para inyectar instancias de diferentes objetos, como CamelContext, ProducerTemplate y EntityManager, en la clase de prueba.
 * La anotación @Test se utiliza en cada uno de los métodos de prueba en la clase para indicar que son métodos de prueba unitaria. 
 * La anotación @Transactional se utiliza en pruebas unitarias para indicar que las operaciones realizadas dentro del método de prueba deben ser ejecutadas en una transacción.
 * @author RedHat
 * 
 */

@QuarkusTest
@Tag("integration")
public class JuntiTestUsuario  {
    
    @Inject
    CamelContext camelContext;

    @Inject
    ProducerTemplate producerTemplate;

    @Inject
    EntityManager entityManager;

    @Test
        public void testMyRoute() throws Exception {
            // Configuración de la ruta
            camelContext.addRoutes(new RouteBuilder() {
                @Override
                public void configure() throws Exception {
                    from("direct:crearUsuario")
                        .routeId("crearUsuario")
                        .log("Creando un usuario nuevo: ${body}")
                        .toD("jpa://" + Usuario.class.getName());
                }
            });

            // Crear un nuevo usuario
            Usuario usuario = new Usuario();
            usuario.setId(1);
            usuario.setNombre("Gerald");
            usuario.setApellido("Pasten");

            // Enviar el objeto Usuario a través de la ruta "direct:crearUsuario"
            producerTemplate.sendBody("direct:crearUsuario", usuario);

            // Verificar que el usuario fue creado exitosamente
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
    public void testObeteneUsuario() throws Exception {
          // Enviar un mensaje vacío a la ruta "direct:getUser"
        // Como este punto no requiere un body, podemos enviar un body vacío.
        // No estamos esperando una respuesta, ya que solo queremos verificar que la ruta se ejecuta sin errores.
        producerTemplate.sendBody("direct:getUser", null);

        // No necesitamos esperar una respuesta específica, ya que el punto no tiene un cuerpo (body).
        // Simplemente verificamos que la ruta se ejecutó correctamente sin errores.

        assertDoesNotThrow(() -> producerTemplate.sendBody("direct:getUser", null));
     }


    
}



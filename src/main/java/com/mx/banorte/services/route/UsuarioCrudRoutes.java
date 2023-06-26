package com.mx.banorte.services.route;

import java.util.ArrayList;
import java.util.List;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.PersistenceException;
import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.rest.RestBindingMode;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.hibernate.HibernateException;

import com.mx.banorte.services.vo.Usuario;

/**
 * Clase que define las rutas y configuraciones de la aplicación que utiliza Quarkus, Camel, JPA, Hibernate y rutas REST.
 * 
 * En donde realiza un CRUD para las entidades PerfilUsuario Y Usuario que se manejan como entidades persistentes
 * Esta anotación @ApplicationScoped marca la clase como un componente de alcance de aplicación. Indica que la instancia de esta clase será creada una vez por la aplicación y compartida entre múltiples hilos de ejecución.
 * Esta anotación @Inject se utiliza para realizar la inyección de dependencias en los campos de la clase. Se utiliza para obtener instancias de otros componentes y recursos necesarios para el funcionamiento de la clase.
 * Esta anotación @ConfigProperty se utiliza para inyectar valores de propiedades de configuración en los campos de la clase.
 * 
 * @author RedHat
 */
@ApplicationScoped
public class UsuarioCrudRoutes extends RouteBuilder {

        @Inject
        @ConfigProperty(name = "select.usuarios")
        private String query;

        @Inject
        @ConfigProperty(name = "delete.usuarios")
        private String delete;

        @Override
        public void configure() throws Exception {

                //Configuracion de Rest BindingMode para recibir y enviar respuesta JSON
                restConfiguration().bindingMode(RestBindingMode.json);  

                // Definicion del punto REST
                rest("/usuario")
                        
                        //Creacion de la ruta Camel getUser
                        .get("obtener")
                        .description("Details of an user by id")
                        .outType(Usuario.class)
                        .to("direct:getUser")

                        
                        //Creacion de la ruta Camel crearUsuario
                        .put("create")
                        .description("Crear un nuevo usuario")
                        .type(Usuario.class)
                        .to("direct:crearUsuario")

                        //Creacion de la ruta Camel updateUsuario
                        .put("update")
                        .description("Actualizar un usuario")
                        .type(Usuario.class)
                        .to("direct:updateUsuario")

                        //Creacion de la ruta Camel deleteUsuario
                        .delete("/{id}")
                        .consumes("application/json")
                        .description("Eliminar un usuario")
                        .type(Usuario.class)
                        .to("direct:deleteUsuario");

                        from("direct:getUser")
                        .routeId("getUserPerRoute")
                        .log("Obteniendo Usuarios mediante query ...")
                        .doTry()
                        .toD("jpa://" + Usuario.class.getName()+ "?query="+query)
                        .process(exchange -> {
                                @SuppressWarnings("unchecked")
                                List<String[]> resultados = exchange.getIn().getBody(ArrayList.class);
                                List<Usuario> usuarios = new ArrayList<>();          
                                for (Object[] resultado :  resultados) {    
                                Usuario usuario = new Usuario();
                                usuario.setId(0);
                                usuario.setNombre("Su nombre completo es: "+resultado[0].toString() + " " +resultado[1].toString());
                                usuario.setApellido("Su apellido modificado es: "+resultado[1].toString() +" Cabrera");
                                usuarios.add(usuario);
                                }
                                exchange.getIn().setBody(usuarios);
                        })
                        .doCatch(HibernateException.class, PersistenceException.class)
                        .log("Error al Obtener usuarios: ${exception.message}")
                        .setBody(constant("Error al obtener usuario"))
                        .setHeader(Exchange.HTTP_RESPONSE_CODE, constant(204))
                        .end();

                        from("direct:crearUsuario")
                        .routeId("crearUsuario")
                        .log("Creando un usuario nuevo: ${body}")
                        .doTry()
                        .toD("jpa://" + Usuario.class.getName())
                        .doCatch(HibernateException.class, PersistenceException.class)
                        .log("Error al Obtener usuarios: ${exception.message}")
                        .setBody(constant("Error al crear usuario"))
                        .setHeader(Exchange.HTTP_RESPONSE_CODE, constant(500))
                        .end();

                        from("direct:updateUsuario")
                        .routeId("updateUsuario").log("Probando actualizar usuario")
                        .doTry()
                        .to("jpa://"+ Usuario.class.getName() +"?useExecuteUpdate=true")
                        .log("Error al Obtener usuarios: ${exception.message}")
                        .setBody(constant("Error al actualizar usuario"))
                        .setHeader(Exchange.HTTP_RESPONSE_CODE, constant(500))
                        .end();

                        from("direct:deleteUsuario")
                        .doTry()
                        .toD("jpa://"+Usuario.class.getName()+"?query="+delete +"="+"${header.id}&useExecuteUpdate=true")
                        .doCatch(HibernateException.class, PersistenceException.class)
                        .log("Error al Obtener usuarios: ${exception.message}")
                        .setBody(constant("Error al eliminar usuario"))
                        .setHeader(Exchange.HTTP_RESPONSE_CODE, constant(500))
                        .end();

                        

        }  
    
}

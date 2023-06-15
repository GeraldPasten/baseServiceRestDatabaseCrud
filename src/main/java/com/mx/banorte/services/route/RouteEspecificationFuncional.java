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
import com.mx.banorte.services.vo.PerfilUsuario;
import com.mx.banorte.services.vo.Usuario;


@ApplicationScoped
public class RouteEspecificationFuncional extends RouteBuilder {

        @Inject
        @ConfigProperty(name = "select.usuarios")
        private String query;

        @Inject
        @ConfigProperty(name = "delete.usuarios")
        private String delete;

        @Inject
        @ConfigProperty(name = "select.perfiles.usuario")
        private String perfiles;

        

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
                        .to("direct:deleteUsuario")

                        //Creacion de la ruta Camel getUser
                        .get("perfiles")
                        .description("Obtener Usuario y su perfil")
                        .outType(PerfilUsuario.class)
                        .to("direct:getUserProfile")

                        //Creacion de la ruta Camel CreatePerfil
                        .put("perfiles/create")
                        .description("Crear perfil asociado a un usuario")
                        .type(PerfilUsuario.class)
                        .to("direct:createPerfil");

                        from("direct:createPerfil")
                        .routeId("createUserPerfil")
                        .log("Creando Perfil asociado a id de usuario: ${body}")
                        .doTry()
                        .toD("jpa://" + PerfilUsuario.class.getName())
                        .doCatch(HibernateException.class, PersistenceException.class)
                        .log("Error al crear el perfil: ${exception.message}")
                        .setBody(constant("Error al crear el perfil"))
                        .setHeader(Exchange.HTTP_RESPONSE_CODE, constant(500))
                        .end();
                        
                        from("direct:getUserProfile")
                        .routeId("getUserPerfil")
                        .log("Obteniendo Usuarios mediante query ...")
                        .doTry()
                        .toD("jpa://" + PerfilUsuario.class.getName() + "?query="+perfiles)
                        .doCatch(HibernateException.class, PersistenceException.class)
                        .log("Error al Obtener perfiles de usuario: ${exception.message}")
                        .setBody(constant("Error al Obtener perfiles de usuario"))
                        .setHeader(Exchange.HTTP_RESPONSE_CODE, constant(500))
                        .end();

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
                        .setHeader(Exchange.HTTP_RESPONSE_CODE, constant(500))
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

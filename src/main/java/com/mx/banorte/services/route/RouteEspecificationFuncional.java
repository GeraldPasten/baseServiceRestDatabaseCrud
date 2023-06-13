package com.mx.banorte.services.route;

import java.util.ArrayList;
import java.util.List;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.rest.RestBindingMode;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import com.mx.banorte.services.vo.Usuario;

@ApplicationScoped
public class RouteEspecificationFuncional extends RouteBuilder {

        @Inject
        @ConfigProperty(name = "select.usuarios")
        private String query;

        @Inject
        @ConfigProperty(name = "delete.usuarios")
        private String delete;

        @Override
        public void configure() throws Exception {

                //Configuracion de Resr BindingMode para recibir y enviar respuesta JSON
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
                        .delete("a/{id}")
                        .consumes("application/json")
                        .description("Eliminar un usuario")
                        .type(Usuario.class)
                        .to("direct:deleteUsuario");

                        from("direct:getUserPer")
                        .routeId("getUserPerRoute")
                        .log("Obteniendo Usuarios mediante query ...")
                        .toD("jpa://" + Usuario.class.getName()+ "?query="+query)
                        .process(exchange -> {
                                List<String[]> resultados = exchange.getIn().getBody(ArrayList.class);
                                List<Usuario> usuarios = new ArrayList<>();          
                                for (Object[] resultado :  resultados) {    
                                Usuario usuario = new Usuario();
                                usuario.setId(1);
                                usuario.setNombre("Su nombre completo es: "+resultado[0].toString() + " " +resultado[1].toString());
                                usuario.setApellido("Su apellido es: "+resultado[1].toString());
                                usuarios.add(usuario);
                                }
                                exchange.getIn().setBody(usuarios);
                        });

                        from("direct:crearUsuario")
                        .routeId("crearUsuario")
                        .log("Creando un usuario nuevo: ${body}")
                        .toD("jpa://" + Usuario.class.getName());

                        from("direct:updateUsuario")
                        .routeId("updateUsuario").log("Probando actualizar usuario")
                        .to("jpa://"+ Usuario.class.getName() +"?useExecuteUpdate=true");

                        from("direct:deleteUsuario")
                        .toD("jpa://"+Usuario.class.getName()+"?query="+delete +"="+"${header.id}&useExecuteUpdate=true")
                        .log("Deleted usuario: ${body} , ${header.id}");


        }  
    
}

package com.mx.banorte.services.route;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.PersistenceException;

import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.rest.RestBindingMode;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.hibernate.HibernateException;

import com.mx.banorte.services.vo.PerfilUsuario;


@ApplicationScoped
public class PerfilesUsuarioRoutes extends RouteBuilder{

    @Inject
        @ConfigProperty(name = "select.perfiles.usuario")
        private String perfiles;

        @Override
        public void configure() throws Exception {

        //Configuracion de Rest BindingMode para recibir y enviar respuesta JSON
        restConfiguration().bindingMode(RestBindingMode.json);

        rest("/perfil")
                        .get("perfiles")
                        .description("Obtener Usuario y su perfil")
                        .outType(PerfilUsuario.class)
                        .to("direct:getUserProfile")

                         //Creacion de la ruta Camel CreatePerfil
                        .put("perfiles/create")
                        .description("Crear perfil asociado a un usuario")
                        .type(PerfilUsuario.class)
                        .to("direct:createPerfil");

        from("direct:getUserProfile")
                        .routeId("getUserPerfil")
                        .log("Obteniendo Usuarios mediante query ...")
                        .doTry()
                        .toD("jpa://" + PerfilUsuario.class.getName() + "?query="+perfiles)
                        .doCatch(HibernateException.class, PersistenceException.class)
                        .log("Error al Obtener perfiles de usuario: ${exception.message}")
                        .setBody(constant("Error al Obtener perfiles de usuario"))
                        .setHeader(Exchange.HTTP_RESPONSE_CODE, constant(204))
                        .end();

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

        }



}

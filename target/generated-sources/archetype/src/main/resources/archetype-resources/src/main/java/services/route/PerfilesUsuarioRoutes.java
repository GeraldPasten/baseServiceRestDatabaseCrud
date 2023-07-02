#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${package}.services.route;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.PersistenceException;
import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.rest.RestBindingMode;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.hibernate.HibernateException;
import ${package}.services.vo.PerfilUsuario;

/*
 * Clase que define las rutas y configuraciones para levantar endpoints rest con Java DSL 
 * Anotaciones utilizadas: 
 * Esta anotación @ApplicationScoped marca la clase como un componente de alcance de aplicación. Indica que la instancia de esta clase será creada una vez por la aplicación y compartida entre múltiples hilos de ejecución.
 * Esta anotación @Inject se utiliza para realizar la inyección de dependencias en los campos de la clase. Se utiliza para obtener instancias de otros componentes y recursos necesarios para el funcionamiento de la clase.
 * Esta anotación @ConfigProperty se utiliza para inyectar valores de propiedades de configuración en los campos de la clase.
 * Esta anotación @Override se utiliza en Java para indicar que un método en una clase está siendo sobrescrito de una clase padre o de una interfaz.
 * 
 * Nuestra clase PerfilesUsuarioRoutes debe extenderse de La clase RouteBuilder ya que es una clase proporcionada por Apache Camel que se utiliza para construir y configurar rutas.
 * Al hacerlo, se debe proporcionar una implementación del método configure(). Dentro de este método, puedes definir tus rutas Camel utilizando el lenguaje de dominio específico de Camel.
 * 
 * restConfiguration() Como configurar el modo de enlace JSON en la ruta. Esto indica que los datos se convertirán automáticamente entre formato JSON y objetos Java al utilizar la ruta REST.
 * rest() Define la ruta base para todas las rutas REST. Todas las rutas definidas después de esto estarán anidadas bajo lo indicado.
 * get()  Configura endpoint GET en la ruta Este endpoint responderá a las solicitudes GET.
 * to()   Redirige la solicitud a una ruta, esto significa que cuando se reciba una solicitud GET en el endpoint se activara la ruta a la que se redirige.
 * 
 * 
 * @Author Red Hat 
 */

@ApplicationScoped
public class PerfilesUsuarioRoutes extends RouteBuilder{

    @Inject
        @ConfigProperty(name = "select.perfiles.usuario")
        private String perfiles;

        @Override
        public void configure() throws Exception {

        //Configuracion de Rest BindingMode para recibir y enviar respuesta JSON
        restConfiguration().bindingMode(RestBindingMode.json);

        rest("/perfil") // Define la ruta base para todas las rutas REST relacionadas con "perfil". Todas las rutas definidas después de esto estarán anidadas bajo "/perfil".
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
                        .log("Error al Obtener perfiles de usuario: ${symbol_dollar}{exception.message}")
                        .setBody(constant("Error al Obtener perfiles de usuario"))
                        .setHeader(Exchange.HTTP_RESPONSE_CODE, constant(204))
                        .end();

        from("direct:createPerfil")
                        .routeId("createUserPerfil")
                        .log("Creando Perfil asociado a id de usuario: ${symbol_dollar}{body}")
                        .doTry()
                        .toD("jpa://" + PerfilUsuario.class.getName())
                        .doCatch(HibernateException.class, PersistenceException.class)
                        .log("Error al crear el perfil: ${symbol_dollar}{exception.message}")
                        .setBody(constant("Error al crear el perfil"))
                        .setHeader(Exchange.HTTP_RESPONSE_CODE, constant(500))
                        .end();

        }



}

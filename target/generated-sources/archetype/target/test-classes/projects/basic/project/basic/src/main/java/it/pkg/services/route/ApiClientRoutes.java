package it.pkg.services.route;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.rest.RestBindingMode;
import org.eclipse.microprofile.config.inject.ConfigProperty;




@ApplicationScoped
public class ApiClientRoutes extends RouteBuilder {

@Inject
@ConfigProperty(name = "basicAuth.credencials")
private String credencials;

@Override
public void configure() throws Exception {
        //Configuracion de Rest BindingMode para recibir y enviar respuesta JSON
                restConfiguration().bindingMode(RestBindingMode.json);

                rest("/extension")
                
                .get("llamar")
                .description("Llamando a otra api")
                .to("direct:llamada");
  
                from("direct:llamada")
                .routeId("callService")
                .log("Llamando a un api con seguirdad, Autenticacion Basica y certificado TLS")
                .setHeader("Authorization", constant(credencials)) 
                .doTry()
                .toD("http://localhost:8081/admin?bridgeEndpoint=true&httpMethod=GET")
                .log("Body de llamada: ${body}")
                .convertBodyTo(String.class)
                .setBody(simple("${body}"))
                .doCatch(Exception.class)
                .log("Error llamar api externa ${exception.message}")
                .setBody(constant("Error llamar api externa "))
                .end();

    }
    


}

# baseservice-rest-databese-crud

Aplicación desarrollada utilizando Quarkus, Camel 3, JPA, Hibernate y rutas REST. La aplicación realiza operaciones CRUD en una base de datos utilizando JPA, y utiliza Camel para exponer puntos de acceso REST. También se proporcionan ejemplos de cómo manejar logs en Camel y configuraciones de Quarkus, cómo manejar excepciones, procesar datos de consultas y aplicar lógica de negocios. Además, se incluyen pruebas JUnit 5 con Camel y una carpeta "openshift" con configuraciones de rutas para OpenShift.


# Contenido
- [x] Endpoints Rest Camel
- [x] Recibir y producir JSON
- [x] Despliegue en ocp / Comandos OC
- [x] Carpeta Openshift
- [x] Parametrizacion y Configuracion
- [x] CRUD en JPA mediante CAMEL
- [x] Logica de negocios en rutas Camel
- [x] Configuracion de Logs 
- [x] Manejo de excepciones
- [x] Junit Test con Camel

# Tecnologías utilizadas
Quarkus: Un framework de Java diseñado para aplicaciones nativas de la nube.
Camel 3: Un framework de integración de código abierto que permite la implementación de patrones de integración empresarial.
JPA (Java Persistence API): Una API estándar de Java para mapear objetos a bases de datos relacionales.
Hibernate: Una implementación de JPA que proporciona una capa de persistencia para el acceso a bases de datos.
REST: Un estilo arquitectónico para construir servicios web escalables y fáciles de consumir.

# Configuración
La configuración de la aplicación se encuentra en el archivo application.properties en la carpeta resources. Aquí se pueden ajustar diversos aspectos, como la configuración de la base de datos, los logs y otras propiedades específicas de Quarkus.

# Uso de Camel para rutas REST
La clase UsuarioResource utiliza Camel para definir rutas REST que se encargan de manejar las operaciones CRUD de la entidad Usuario. Se pueden agregar más rutas o modificar las existentes según sea necesario para adaptarse a los requisitos específicos del proyecto.

# Manejo de logs
La configuración de logs se encuentra en el archivo application.properties. Aquí se pueden ajustar los niveles de log para diferentes paquetes y clases, así como configurar la salida de los logs.

Además, Camel proporciona su propio mecanismo de logging que se puede configurar mediante opciones específicas en las rutas.

# Manejo de excepciones
La aplicación muestra ejemplos de cómo manejar excepciones utilizando Camel. Se pueden agregar manejadores de excepciones personalizados en las rutas para capturar y manejar excepciones específicas según sea necesario.

# Consultas y lógica de negocios
La clase UsuarioService implementa la lógica de negocio relacionada con el manejo de usuarios. Aquí se pueden realizar consultas a la base de datos utilizando JPA y aplicar lógica de negocio adicional según sea necesario.

# Pruebas JUnit 5 con Camel
La clase UsuarioServiceTest en el directorio test contiene pruebas unitarias utilizando JUnit 5 y Camel. Estas pruebas permiten verificar el comportamiento esperado de los diferentes métodos de la clase UsuarioService.

# Configuraciones de rutas para OpenShift
La carpeta openshift contiene el archivo route-config.yaml, que proporciona configuraciones de rutas para OpenShift. Estas configuraciones se pueden utilizar para exponer los puntos de acceso REST de la aplicación en un clúster de OpenShift.

# Comandos
A continuación se presentan algunos comandos útiles para utilizar la aplicación:

#### Ejecutar la aplicación en modo de desarrollo:

./mvnw compile quarkus:dev
Empaquetar y ejecutar la aplicación:

./mvnw package
java -jar target/quarkus-app/quarkus-run.jar
Crear un ejecutable nativo:

./mvnw package -Pnative
Ejecutar el ejecutable nativo en un contenedor:

./mvnw package -Pnative -Dquarkus.native.container-build=true



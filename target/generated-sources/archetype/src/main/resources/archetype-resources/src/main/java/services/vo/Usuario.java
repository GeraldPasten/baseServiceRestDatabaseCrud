#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${package}.services.vo;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import com.fasterxml.jackson.annotation.JsonProperty;

/* Esta clase del paquete vo se utiliza para declarar un entity para JPA en donde podre manipular la base de datos mediante este objeto,
 * estos objetos son conocidos como Entity, las cuales son clases comunes y corrientes también llamada POJO’s.
 * Esta clase tiene la particularidad de que esta mapeadas contra una tabla de la base de datos, dicho mapeo se lleva a cabo generalmente mediante Anotaciones:
 * @Entity @Table @Id @GeneratedValue @Column @ManyToOne @JoinColumn.
 * Dichas anotaciones brindan los suficientes metadatos como para poder por relacionar las clases contra las tablas y las propiedades contra las columnas.
 * Es de esta forma que JPA es capaz de interactuar con la base de datos a través de las clases.
 * 
 * @Author Red Hat 
 */

@Entity()                                               // Se declara como una entidad JPA
@Table(name = "usuario")                                // Se declara como tabla llamada usuario

public class Usuario {

    @JsonProperty                                       // Se declara como una propiedad Json.
    @Id                                                 // Se declara como id primario de la tabla.
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Especificamos com seberia generarse nuestro id.
    private int id;

    @JsonProperty
    @Column
    private String nombre;

    @JsonProperty
    @Column
    private String apellido;

    public Usuario() {

    }

    // Getters and setters de esta clase 

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApellido() {
        return apellido;
    }

    public void setApellido(String apellido) {
        this.apellido = apellido;
    } 
}
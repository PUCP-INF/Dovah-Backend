/*
 * Nombre del archivo: Semestre.java
 * Fecha de creacion: 20-09-2022
 * Autor: Victor Avalos
 * Descripción: Definición de la clase Semestre
 */
package pe.edu.pucp.dovah.Gestion.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter @Setter @NoArgsConstructor
public class Semestre {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Setter(AccessLevel.PROTECTED)
    private int idSemestre;
    private String anhoAcademico;
    private String periodo;
    private String fechaInicio;
    private String fechaFin;
    private boolean activo;

    @JsonIgnoreProperties("semestre")
    @OneToMany (mappedBy = "semestre", cascade = CascadeType.ALL)
    private List<Curso> cursos = new ArrayList<>();

    public Semestre(String anhoAcademico, String periodo,String fechaInicio,String fechaFin){
        this.anhoAcademico = anhoAcademico;
        this.periodo = periodo;
        this.fechaInicio = fechaInicio;
        this.fechaFin = fechaFin;
        this.activo = true;
    }
    @Override
    public String toString() {
        return String.format("Semestre(anhoAcademico='%s' periodo='%s')",
                this.getAnhoAcademico(),this.getPeriodo());
    }


}

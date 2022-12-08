/*
 * Nombre del archivo: CursoRepository.java
 * Fecha de creacion: 20-09-2022
 * Autor: Victor Avalos
 * Descripción: Interfaz de repositorio para la clase Curso
 */
package pe.edu.pucp.dovah.Gestion.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import pe.edu.pucp.dovah.Gestion.model.Curso;

import java.util.List;
import java.util.Optional;

public interface CursoRepository extends JpaRepository<Curso, Integer> {

    List<Curso> queryAllByActivoIsTrue();

    @Query("""
            select c
            from Curso c
            left join fetch c.semestre
            where c.activo = true
            and c.idCurso = :id
""")
    Optional<Curso> findByIdCursoAndActivoIsTrue(int id);

    List<Curso> findByEspecialidad_IdEspecialidadAndActivoIsTrue(int id);

    List<Curso> findBySemestre_IdSemestreAndActivoIsTrue(int id);

    List<Curso> findByEspecialidad_IdEspecialidadAndSemestre_IdSemestreAndActivoIsTrue(int idEspecialidad, int idSemestre);
    @Query("select distinct c from Curso c left join fetch c.documentosGenerales where c.activo = true and c.idCurso = :id")
    Optional<Curso>queryCursoWithDocumentos(int id);

    @Query("""
            select c
            from Curso c
            left join fetch c.alumnos al
            where c.activo = true
            and al.activo = true
            and c.idCurso = :id
""")
    Optional<Curso> queryByIdCursoWithAlumnos(int id);

    @Query("""
            select c
            from Curso c
            left join fetch c.profesores pr
            left join fetch pr.roles
            where c.activo = true
            and pr.activo = true
            and c.idCurso = :id
""")
    Optional<Curso> queryByIdCursoWithProfesores(int id);

    @Query("""
            select distinct concat(c.clave,'-',c.nombre)
            from Curso c
            where c.activo = true
            and c.especialidad.idEspecialidad = :idEspecialidad
""")
    List<String> getCursosPasados(int idEspecialidad);
}

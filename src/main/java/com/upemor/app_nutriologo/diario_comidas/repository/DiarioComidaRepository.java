package com.upemor.app_nutriologo.diario_comidas.repository;

import com.upemor.app_nutriologo.diario_comidas.entity.DiarioComida;
import com.upemor.app_nutriologo.plantillas.enums.TipoComida;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Repositorio Spring Data JPA para la entidad {@link DiarioComida}.
 * <p>
 * Proporciona métodos para consultar los registros diarios de comidas de un paciente
 * utilizando grafos de entidades (EntityGraph) para evitar el problema de consultas N+1
 * al cargar las relaciones de comidas programadas y plantillas.
 * </p>
 *
 * @author Sistema Nutriólogo
 * @version 1.0
 */
public interface DiarioComidaRepository extends JpaRepository<DiarioComida, Long> {

    /**
     * Recupera todos los registros de diario de comidas de un paciente ordenados por fecha descendente.
     *
     * @param pacienteId Identificador único del paciente.
     * @return Lista de registros de comida ordenados de más reciente a más antiguo.
     */
    @EntityGraph(attributePaths = { "comidaProgramada", "comidaProgramada.plantilla" })
    List<DiarioComida> findByPacienteIdOrderByFechaDesc(Long pacienteId);

    /**
     * Recupera los registros de comida de un paciente dentro de un rango de fechas especificado.
     *
     * @param pacienteId Identificador único del paciente.
     * @param inicio     Fecha de inicio del rango.
     * @param fin        Fecha final del rango.
     * @return Lista de registros de comida en el período indicado.
     */
    @EntityGraph(attributePaths = { "comidaProgramada", "comidaProgramada.plantilla" })
    List<DiarioComida> findByPacienteIdAndFechaBetween(Long pacienteId, LocalDate inicio, LocalDate fin);

    /**
     * Recupera los registros de comida de un paciente en un rango de fechas ordenados cronológicamente de forma ascendente.
     *
     * @param pacienteId Identificador único del paciente.
     * @param inicio     Fecha inicial del rango.
     * @param fin        Fecha final del rango.
     * @return Lista de registros de comida ordenados cronológicamente.
     */
    @EntityGraph(attributePaths = { "comidaProgramada", "comidaProgramada.plantilla" })
    List<DiarioComida> findByPacienteIdAndFechaBetweenOrderByFechaAsc(Long pacienteId, LocalDate inicio, LocalDate fin);

    /**
     * Busca un registro de comida de un paciente para una fecha y momento del día específicos.
     *
     * @param pacienteId Identificador único del paciente.
     * @param fecha      Fecha del registro.
     * @param horario    Momento o tipo de comida (desayuno, almuerzo, etc.).
     * @return {@link Optional} con el registro coincidente si existe.
     */
    Optional<DiarioComida> findByPacienteIdAndFechaAndHorario(Long pacienteId, LocalDate fecha, TipoComida horario);

    /**
     * Recupera todos los registros de comida de un paciente correspondientes a una fecha específica.
     *
     * @param pacienteId Identificador único del paciente.
     * @param fecha      Fecha de consulta.
     * @return Lista de registros de comida consumidos en la fecha indicada.
     */
    @EntityGraph(attributePaths = { "comidaProgramada", "comidaProgramada.plantilla" })
    List<DiarioComida> findByPacienteIdAndFecha(Long pacienteId, LocalDate fecha);
}
package com.upemor.app_nutriologo.common.service;

import com.upemor.app_nutriologo.common.exceptions.BadRequestException;
import com.upemor.app_nutriologo.common.exceptions.InvalidOperationException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * Componente compartido que centraliza la validación de la ventana de registro
 * de los diarios (comidas y extendido).
 * <p>
 * Regla: se puede registrar o modificar hasta el fin del día de la fecha
 * más los días/horas de gracia configurados.
 */
@Component
public class DiarioVentanaValidator {

    @Value("${app.diario.max-dias-gracia:0}")
    private int maxDiasGracia;

    @Value("${app.diario.horas-gracia-extra:12}")
    private int horasGraciaExtra;

    /**
     * Valida que la fecha indicada sea pasada o presente y que todavía
     * esté dentro de la ventana de registro permitida.
     *
     * @param fecha fecha del registro a validar
     * @throws BadRequestException       si la fecha es futura
     * @throws InvalidOperationException si la ventana de registro ha vencido
     */
    public void validar(LocalDate fecha) {
        LocalDateTime ahora = LocalDateTime.now();

        if (fecha.isAfter(ahora.toLocalDate())) {
            throw new BadRequestException("No puedes registrar datos para una fecha futura.");
        }

        LocalDateTime limite = fecha.atTime(LocalTime.MAX)
                .plusDays(maxDiasGracia)
                .plusHours(horasGraciaExtra);

        if (ahora.isAfter(limite)) {
            throw new InvalidOperationException(
                    "El plazo para registrar o modificar el registro del " + fecha + " ha vencido.");
        }
    }
}


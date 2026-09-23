package com.upemor.app_nutriologo.calendario.service;

import com.upemor.app_nutriologo.calendario.dto.CalendarioDiaNutriologoResponse;
import com.upemor.app_nutriologo.calendario.dto.CalendarioDiaPacienteResponse;
import com.upemor.app_nutriologo.calendario.dto.CalendarioMesNutriologoResponse;
import com.upemor.app_nutriologo.calendario.dto.CalendarioMesPacienteResponse;

import java.time.LocalDate;

public interface CalendarioService {

    /**
     * Resumen mensual para el nutriólogo autenticado.
     * Devuelve un indicador por cada día del mes para poblar el grid.
     */
    CalendarioMesNutriologoResponse getMesNutriologo(int year, int month);

    /**
     * Detalle completo de un día para el nutriólogo autenticado:
     * citas, horas libres y pacientes sin diario.
     */
    CalendarioDiaNutriologoResponse getDiaNutriologo(LocalDate fecha);

    /**
     * Resumen mensual para el paciente autenticado.
     * Devuelve un indicador por cada día del mes para poblar el grid.
     */
    CalendarioMesPacienteResponse getMesPaciente(int year, int month);

    /**
     * Detalle completo de un día para el paciente autenticado:
     * citas, comidas programadas, registros del diario y bienestar.
     */
    CalendarioDiaPacienteResponse getDiaPaciente(LocalDate fecha);
}

package com.upemor.app_nutriologo.nutriologo.dto;

/**
 * DTO inmutable (record) ligero utilizado en selectores o menús desplegables de la interfaz de usuario.
 * <p>
 * Proporciona el identificador y nombre abreviado de un nutriólogo para asignaciones rápidas.
 * </p>
 *
 * @param id identificador primario del nutriólogo.
 * @param nombre nombre(s) de pila del nutriólogo.
 * @param apellido primer apellido del nutriólogo.
 *
 * @author Sistema Nutriólogo
 * @version 1.0
 */
public record NutriologoSelectorDTO(Long id, String nombre, String apellido) { }

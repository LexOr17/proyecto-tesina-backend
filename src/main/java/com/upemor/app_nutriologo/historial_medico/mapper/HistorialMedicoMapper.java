package com.upemor.app_nutriologo.historial_medico.mapper;

import com.upemor.app_nutriologo.historial_medico.dto.HistorialMedicoResponseDTO;
import com.upemor.app_nutriologo.historial_medico.entity.HistorialMedico;
import com.upemor.app_nutriologo.medidas_antropometricas.mapper.MedidaAntropometricaMapper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {MedidaAntropometricaMapper.class})
public interface HistorialMedicoMapper {

    @Mapping(source = "paciente.id", target = "pacienteId")
    @Mapping(source = "medidaAntropometrica", target = "medidaAntropometrica")
    @Mapping(source = "enfermedades", target = "enfermedades")
    @Mapping(source = "sintomas", target = "sintomas")
    HistorialMedicoResponseDTO toDto(HistorialMedico historial);
}

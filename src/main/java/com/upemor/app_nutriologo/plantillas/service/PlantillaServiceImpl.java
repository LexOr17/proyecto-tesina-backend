package com.upemor.app_nutriologo.plantillas.service;

import com.upemor.app_nutriologo.alimento.entity.Alimento;
import com.upemor.app_nutriologo.alimento.repository.AlimentoRepository;
import com.upemor.app_nutriologo.plantillas.dto.PlantillaRequestDTO;
import com.upemor.app_nutriologo.plantillas.dto.PlantillaResponseDTO;
import com.upemor.app_nutriologo.plantillas.entity.Plantilla;
import com.upemor.app_nutriologo.plantillas.mapper.PlantillaMapper;
import com.upemor.app_nutriologo.plantillas.repository.PlantillaRepository;
import com.upemor.app_nutriologo.usuario.entity.Usuario;
import com.upemor.app_nutriologo.usuario.repository.UsuarioRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class PlantillaServiceImpl implements PlantillaService {

    private final PlantillaRepository plantillaRepository;
    private final UsuarioRepository usuarioRepository;
    private final AlimentoRepository alimentoRepository;
    private final PlantillaMapper plantillaMapper;

    public PlantillaServiceImpl(PlantillaRepository plantillaRepository,
                            UsuarioRepository usuarioRepository,
                            AlimentoRepository alimentoRepository,
                            PlantillaMapper plantillaMapper) {
        this.plantillaRepository = plantillaRepository;
        this.usuarioRepository = usuarioRepository;
        this.alimentoRepository = alimentoRepository;
        this.plantillaMapper = plantillaMapper;
    }

    @Override
    @Transactional
    public PlantillaResponseDTO crearPlantilla(PlantillaRequestDTO requestDTO) {
        Plantilla plantilla = plantillaMapper.toEntity(requestDTO);

        Usuario usuario = usuarioRepository.findById(requestDTO.usuarioId())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con id: " + requestDTO.usuarioId()));
        plantilla.setUsuario(usuario);

        List<Alimento> alimentos = alimentoRepository.findAllById(requestDTO.alimentoIds());
        if (alimentos.size() != requestDTO.alimentoIds().size()) {
            throw new RuntimeException("Uno o más alimentos no fueron encontrados.");
        }
        plantilla.setAlimentos(alimentos);

        Plantilla savedPlantilla = plantillaRepository.save(plantilla);

        return plantillaMapper.toDto(savedPlantilla);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<PlantillaResponseDTO> obtenerPlantillas(Pageable pageable) {
        return plantillaRepository.findAll(pageable)
                .map(plantillaMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public PlantillaResponseDTO buscarPlantillaPorId(Long id) {
        Plantilla plantilla = plantillaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Plantilla no encontrada con id: " + id));
        return plantillaMapper.toDto(plantilla);
    }

    @Override
    @Transactional
    public PlantillaResponseDTO actualizarPlantilla(Long id, PlantillaRequestDTO requestDTO) {
        Plantilla plantillaExistente = plantillaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Plantilla no encontrada con id: " + id));

        plantillaExistente.setNombre(requestDTO.nombre());
        plantillaExistente.setTipoComida(requestDTO.tipoComida());

        Usuario usuario = usuarioRepository.findById(requestDTO.usuarioId())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con id: " + requestDTO.usuarioId()));
        plantillaExistente.setUsuario(usuario);

        List<Alimento> alimentos = alimentoRepository.findAllById(requestDTO.alimentoIds());
        if (alimentos.size() != requestDTO.alimentoIds().size()) {
            throw new RuntimeException("Uno o más alimentos no fueron encontrados.");
        }
        plantillaExistente.getAlimentos().clear();
        plantillaExistente.getAlimentos().addAll(alimentos);

        Plantilla updatedPlantilla = plantillaRepository.save(plantillaExistente);
        return plantillaMapper.toDto(updatedPlantilla);
    }

    @Override
    @Transactional
    public void eliminarPlantilla(Long id) {
        if (!plantillaRepository.existsById(id)) {
            throw new RuntimeException("Plantilla no encontrada con id: " + id);
        }
        plantillaRepository.deleteById(id);
    }
}

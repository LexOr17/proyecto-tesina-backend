package com.upemor.app_nutriologo.alimento.service;

import com.upemor.app_nutriologo.alimento.dto.AlimentoRequestDTO;
import com.upemor.app_nutriologo.alimento.dto.AlimentoResponseDTO;
import com.upemor.app_nutriologo.alimento.entity.Alimento;
import com.upemor.app_nutriologo.alimento.mapper.AlimentoMapper;
import com.upemor.app_nutriologo.alimento.repository.AlimentoRepository;
import com.upemor.app_nutriologo.common.service.FileStorageService;
import com.upemor.app_nutriologo.common.exceptions.DuplicateResourceException;
import com.upemor.app_nutriologo.common.exceptions.ResourceNotFoundException;
import com.upemor.app_nutriologo.usuario.entity.Usuario;
import com.upemor.app_nutriologo.usuario.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AlimentoServiceImpl implements AlimentoService {

    private final AlimentoRepository alimentoRepository;
    private final UsuarioRepository usuarioRepository;
    private final AlimentoMapper alimentoMapper;
    private final FileStorageService fileStorageService;

    @Override
    @Transactional
    public AlimentoResponseDTO registrarAlimento(AlimentoRequestDTO requestDTO, MultipartFile imagen, String correoUsuario) throws IOException {
        if (alimentoRepository.existsByNombre(requestDTO.getNombre())) {
            throw new DuplicateResourceException("El alimento con nombre '" + requestDTO.getNombre() + "' ya existe.");
        }

        Usuario usuario = usuarioRepository.findByCorreo(correoUsuario)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con correo: " + correoUsuario));

        Alimento alimento = alimentoMapper.toEntity(requestDTO);
        alimento.setRegistradoPor(usuario);

        if (imagen != null && !imagen.isEmpty()) {
            String fileName = fileStorageService.storeFile(imagen);
            alimento.setImagenUrl(fileName);
        }

        Alimento guardado = alimentoRepository.save(alimento);

        return alimentoMapper.toResponseDto(guardado);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<AlimentoResponseDTO> listarAlimentos(Pageable pageable) {
        return alimentoRepository.findAll(pageable)
                .map(alimentoMapper::toResponseDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<AlimentoResponseDTO> buscarAlimentosPorPalabraClave(String keyword, Pageable pageable) {
        return alimentoRepository.searchByKeyword(keyword, pageable)
                .map(alimentoMapper::toResponseDto);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AlimentoResponseDTO> buscarPorCategoria(String categoria) {
        return alimentoRepository.findByCategoria(categoria).stream()
                .map(alimentoMapper::toResponseDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public AlimentoResponseDTO buscarPorId(Long id) {
        return alimentoRepository.findById(id)
                .map(alimentoMapper::toResponseDto)
                .orElseThrow(() -> new ResourceNotFoundException("Alimento no encontrado con id: " + id));
    }

    @Override
    @Transactional
    public AlimentoResponseDTO actualizarAlimento(Long id, AlimentoRequestDTO requestDTO, MultipartFile imagen) throws IOException {
        Alimento alimento = alimentoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Alimento no encontrado con id: " + id));

        // Validar si el nombre cambió y si ya existe otro con ese nombre
        if (!alimento.getNombre().equals(requestDTO.getNombre()) &&
                alimentoRepository.existsByNombre(requestDTO.getNombre())) {
            throw new DuplicateResourceException("El alimento con nombre '" + requestDTO.getNombre() + "' ya existe.");
        }

        alimentoMapper.updateEntityFromDto(requestDTO, alimento);

        if (imagen != null && !imagen.isEmpty()) {
            // Eliminar la imagen anterior si existe
            if (alimento.getImagenUrl() != null) {
                fileStorageService.deleteFile(alimento.getImagenUrl());
            }
            String fileName = fileStorageService.storeFile(imagen);
            alimento.setImagenUrl(fileName);
        }

        Alimento actualizado = alimentoRepository.save(alimento);

        return alimentoMapper.toResponseDto(actualizado);
    }

    @Override
    @Transactional
    public void eliminarAlimento(Long id) {
        Alimento alimento = alimentoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Alimento no encontrado con id: " + id));

        if (alimento.getImagenUrl() != null) {
            fileStorageService.deleteFile(alimento.getImagenUrl());
        }

        alimentoRepository.deleteById(id);
    }
}

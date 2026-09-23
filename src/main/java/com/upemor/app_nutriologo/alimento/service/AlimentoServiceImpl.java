package com.upemor.app_nutriologo.alimento.service;

import com.upemor.app_nutriologo.alimento.dto.AlimentoRequestDTO;
import com.upemor.app_nutriologo.alimento.dto.AlimentoResponseDTO;
import com.upemor.app_nutriologo.alimento.entity.Alimento;
import com.upemor.app_nutriologo.alimento.mapper.AlimentoMapper;
import com.upemor.app_nutriologo.alimento.repository.AlimentoRepository;
import com.upemor.app_nutriologo.common.service.FileStorageService;
import com.upemor.app_nutriologo.common.exceptions.DuplicateResourceException;
import com.upemor.app_nutriologo.common.exceptions.ResourceNotFoundException;
import com.upemor.app_nutriologo.notificaciones.enums.TipoNotificacion;
import com.upemor.app_nutriologo.notificaciones.service.NotificacionService;
import com.upemor.app_nutriologo.usuario.entity.Usuario;
import com.upemor.app_nutriologo.usuario.enums.Rol;
import com.upemor.app_nutriologo.usuario.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

/**
 * Implementación de la capa de servicios para la administración del catálogo de alimentos.
 * <p>
 * Gestiona el ciclo de vida de los alimentos (alta, consulta, modificación y baja),
 * la validación de nombres únicos, el almacenamiento físico y borrado de imágenes multimedia,
 * y la emisión de notificaciones globales al incorporar nuevos productos al catálogo.
 * </p>
 *
 * @author Sistema Nutriólogo
 * @version 1.0
 */
@Service
@RequiredArgsConstructor
public class AlimentoServiceImpl implements AlimentoService {

    private final AlimentoRepository alimentoRepository;
    private final UsuarioRepository usuarioRepository;
    private final AlimentoMapper alimentoMapper;
    private final FileStorageService fileStorageService;
    private final NotificacionService notificacionService;

    /**
     * Registra un nuevo alimento en la base de datos, almacena su imagen si fue suministrada,
     * asocia al usuario autenticado como creador y despacha una notificación a los roles correspondientes.
     *
     * @param requestDTO datos nutricionales del alimento.
     * @param imagen archivo de imagen opcional.
     * @param correoUsuario correo del usuario que ejecuta el registro.
     * @return {@link AlimentoResponseDTO} con los datos del alimento creado.
     * @throws DuplicateResourceException si ya existe un alimento con el mismo nombre.
     * @throws ResourceNotFoundException si no se localiza al usuario con el correo provisto.
     * @throws IOException si ocurre un fallo al guardar el archivo físico en el almacenamiento.
     */
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

        // Notificar a nutriólogos y pacientes sobre el nuevo alimento
        notificacionService.enviarARoles(
                List.of(Rol.NUTRIOLOGO, Rol.PACIENTE),
                TipoNotificacion.NUEVO_ALIMENTO,
                "Nuevo alimento disponible",
                "Se ha agregado '" + guardado.getNombre() + "' al catálogo de alimentos.",
                "/alimentos"
            );

        return alimentoMapper.toResponseDto(guardado);
    }

    /**
     * Obtiene una página con todos los alimentos del catálogo.
     *
     * @param pageable configuración de paginación y ordenamiento.
     * @return página {@link Page} de {@link AlimentoResponseDTO}.
     */
    @Override
    @Transactional(readOnly = true)
    public Page<AlimentoResponseDTO> listarAlimentos(Pageable pageable) {
        return alimentoRepository.findAll(pageable)
                .map(alimentoMapper::toResponseDto);
    }

    /**
     * Busca alimentos cuyo nombre o categoría coincidan con el término indicado.
     *
     * @param keyword texto de búsqueda.
     * @param pageable configuración de paginación y ordenamiento.
     * @return página {@link Page} de {@link AlimentoResponseDTO} coincidentes.
     */
    @Override
    @Transactional(readOnly = true)
    public Page<AlimentoResponseDTO> buscarAlimentosPorPalabraClave(String keyword, Pageable pageable) {
        return alimentoRepository.searchByKeyword(keyword, pageable)
                .map(alimentoMapper::toResponseDto);
    }

    /**
     * Obtiene la lista de alimentos que pertenecen a una categoría nutricional determinada.
     *
     * @param categoria nombre de la categoría.
     * @return lista de {@link AlimentoResponseDTO}.
     */
    @Override
    @Transactional(readOnly = true)
    public List<AlimentoResponseDTO> buscarPorCategoria(String categoria) {
        return alimentoRepository.findByCategoria(categoria).stream()
                .map(alimentoMapper::toResponseDto)
                .toList();
    }

    /**
     * Localiza un alimento a partir de su identificador primario.
     *
     * @param id identificador primario del alimento.
     * @return {@link AlimentoResponseDTO} con los datos del alimento.
     * @throws ResourceNotFoundException si el alimento no existe.
     */
    @Override
    @Transactional(readOnly = true)
    public AlimentoResponseDTO buscarPorId(Long id) {
        return alimentoRepository.findById(id)
                .map(alimentoMapper::toResponseDto)
                .orElseThrow(() -> new ResourceNotFoundException("Alimento no encontrado con id: " + id));
    }

    /**
     * Actualiza los datos de un alimento, valida que el nuevo nombre no esté duplicado
     * y reemplaza el archivo de imagen anterior si se carga uno nuevo.
     *
     * @param id identificador primario del alimento a modificar.
     * @param requestDTO datos actualizados del alimento.
     * @param imagen nuevo archivo de imagen opcional.
     * @return {@link AlimentoResponseDTO} con los datos actualizados.
     * @throws ResourceNotFoundException si el alimento no existe.
     * @throws DuplicateResourceException si el nuevo nombre ya pertenece a otro alimento existente.
     * @throws IOException si falla la manipulación del archivo físico de la imagen.
     */
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

    /**
     * Elimina un alimento del catálogo y remueve su archivo de imagen asociado del disco si existe.
     *
     * @param id identificador primario del alimento a eliminar.
     * @throws ResourceNotFoundException si el alimento no existe.
     */
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

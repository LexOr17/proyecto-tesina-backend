package com.upemor.app_nutriologo.plantillas.service;

import com.upemor.app_nutriologo.alimento.entity.Alimento;
import com.upemor.app_nutriologo.alimento.repository.AlimentoRepository;
import com.upemor.app_nutriologo.common.exceptions.ResourceNotFoundException;
import com.upemor.app_nutriologo.notificaciones.enums.TipoNotificacion;
import com.upemor.app_nutriologo.notificaciones.service.NotificacionService;
import com.upemor.app_nutriologo.plantillas.dto.PlantillaAlimentoRequestDTO;
import com.upemor.app_nutriologo.plantillas.dto.PlantillaRequestDTO;
import com.upemor.app_nutriologo.plantillas.dto.PlantillaResponseDTO;
import com.upemor.app_nutriologo.plantillas.entity.Plantilla;
import com.upemor.app_nutriologo.plantillas.mapper.PlantillaMapper;
import com.upemor.app_nutriologo.plantillas.repository.PlantillaRepository;
import com.upemor.app_nutriologo.security.UserContextService;
import com.upemor.app_nutriologo.usuario.entity.Usuario;
import com.upemor.app_nutriologo.usuario.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Implementación del servicio de gestión de plantillas de comidas nutricionales.
 * <p>
 * Proporciona la lógica de negocio para la creación, búsqueda paginada,
 * consulta, actualización y eliminación de plantillas, validando la existencia de alimentos,
 * la pertenencia de las plantillas al nutriólogo autenticado o permisos administrativos,
 * y emitiendo notificaciones globales ante nuevas plantillas.
 * </p>
 *
 * @author Sistema Nutriólogo
 * @version 1.0
 */
@Service
@RequiredArgsConstructor // Genera el constructor automáticamente para los campos final
public class PlantillaServiceImpl implements PlantillaService {

    private final PlantillaRepository plantillaRepository;
    private final UsuarioRepository usuarioRepository;
    private final AlimentoRepository alimentoRepository;
    private final PlantillaMapper plantillaMapper;
    private final UserContextService userContextService;
    private final NotificacionService notificacionService;

    /**
     * Crea una nueva plantilla de comida asociándola al nutriólogo autenticado.
     * <p>
     * Valida la existencia de todos los alimentos indicados, calcula los totales nutricionales
     * y emite una notificación a todos los usuarios del sistema informando sobre la nueva plantilla.
     * </p>
     *
     * @param requestDTO Datos de la plantilla y lista de alimentos con sus cantidades.
     * @return DTO con la información de la plantilla creada.
     * @throws ResourceNotFoundException Si el usuario autenticado o algún alimento no existe.
     */
    @Override
    @Transactional
    public PlantillaResponseDTO crearPlantilla(PlantillaRequestDTO requestDTO) {
        // 1. Obtener el ID del Nutriólogo directamente del TOKEN (Seguridad)
        Long usuarioLogueadoId = userContextService.getCurrentUserId();

        // 2. Mapeo inicial
        Plantilla plantilla = plantillaMapper.toEntity(requestDTO);

        // 3. Buscar y asignar el Usuario (Nutriólogo)
        Usuario usuario = usuarioRepository.findById(usuarioLogueadoId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con id: " + usuarioLogueadoId));
        plantilla.setUsuario(usuario);

        // 4. Procesar Alimentos y Cantidades
        vincularAlimentosConCantidades(plantilla, requestDTO.alimentos());

        PlantillaResponseDTO plantillaGuardada = plantillaMapper.toDto(plantillaRepository.save(plantilla));

        // Notificar a todos los usuarios (excepto quien la creó) sobre la nueva plantilla
        notificacionService.enviarATodos(
                TipoNotificacion.NUEVA_PLANTILLA,
                "Nueva plantilla disponible",
                "Se ha creado la plantilla '" + plantillaGuardada.nombre() + "'. \u00a1Explora su contenido!",
                "/plantillas/" + plantillaGuardada.id()
        );

        return plantillaGuardada;
    }

    /**
     * Obtiene el listado paginado de todas las plantillas registradas en el sistema.
     *
     * @param pageable Configuración de paginación y ordenamiento.
     * @return Página de DTOs con las plantillas encontradas.
     */
    @Override
    @Transactional(readOnly = true)
    public Page<PlantillaResponseDTO> obtenerPlantillas(Pageable pageable) {
        return plantillaRepository.findAll(pageable)
                .map(plantillaMapper::toDto);
    }

    /**
     * Busca plantillas de comida mediante filtros de coincidencia de texto y tipo de comida opcional.
     *
     * @param keyword   Palabra clave a buscar en nombre, tipo o ingrediente.
     * @param categoria Nombre o representación textual del tipo de comida por el cual filtrar.
     * @param pageable  Configuración de paginación y ordenamiento.
     * @return Página de DTOs con las plantillas filtradas.
     */
    @Override
    @Transactional(readOnly = true)
    public Page<PlantillaResponseDTO> buscarPlantillas(String keyword, String categoria, Pageable pageable) {
        com.upemor.app_nutriologo.plantillas.enums.TipoComida tipo = null;
        if (categoria != null && !categoria.trim().isEmpty() && !categoria.equalsIgnoreCase("TODAS")
                && !categoria.equalsIgnoreCase("null")) {
            try {
                tipo = com.upemor.app_nutriologo.plantillas.enums.TipoComida.valueOf(categoria.toUpperCase());
            } catch (IllegalArgumentException e) {
                // Ignore invalid category, will filter by keyword only
            }
        }

        String searchKeyword = (keyword != null) ? keyword.trim() : "";

        return plantillaRepository.buscarPorNombreYTipoComida(searchKeyword, tipo, pageable)
                .map(plantillaMapper::toDto);
    }

    /**
     * Busca y retorna los detalles de una plantilla por su identificador único.
     *
     * @param id Identificador único de la plantilla.
     * @return DTO con los detalles y cálculos nutricionales de la plantilla.
     * @throws ResourceNotFoundException Si la plantilla no existe en el sistema.
     */
    @Override
    @Transactional(readOnly = true)
    public PlantillaResponseDTO buscarPlantillaPorId(Long id) {
        return plantillaRepository.findById(id)
                .map(plantillaMapper::toDto)
                .orElseThrow(() -> new ResourceNotFoundException("Plantilla no encontrada"));
    }

    /**
     * Actualiza los datos y alimentos de una plantilla existente.
     * <p>
     * Valida que el usuario autenticado sea el propietario de la plantilla o un administrador.
     * Reemplaza la lista previa de alimentos por los nuevos elementos proporcionados.
     * </p>
     *
     * @param id         Identificador único de la plantilla a modificar.
     * @param requestDTO Nuevos datos y lista de alimentos para la plantilla.
     * @return DTO con la plantilla actualizada y cálculos recalculados.
     * @throws ResourceNotFoundException Si la plantilla o alguno de los alimentos no existe.
     * @throws AccessDeniedException Si el usuario no tiene permisos para modificar la plantilla.
     */
    @Override
    @Transactional
    public PlantillaResponseDTO actualizarPlantilla(Long id, PlantillaRequestDTO requestDTO) {
        // 1. Buscar la plantilla existente
        Plantilla plantillaExistente = plantillaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Plantilla no encontrada con id: " + id));

        // 2. Obtener datos del usuario logueado desde el contexto
        Long usuarioLogueadoId = userContextService.getCurrentUserId();
        boolean esAdmin = userContextService.isAdmin();

        // 3. VALIDACIÓN DE SEGURIDAD
        // ¿Es el dueño original? ¿O es un administrador con superpoderes?
        boolean esDuenio = plantillaExistente.getUsuario().getId().equals(usuarioLogueadoId);

        if (!esDuenio && !esAdmin) {
            throw new AccessDeniedException(
                    "No tienes permiso para modificar esta plantilla porque pertenece a otro nutriólogo.");
        }

        // 4. Actualizar datos básicos (Nombre y tipo de comida)
        plantillaExistente.setNombre(requestDTO.nombre());
        plantillaExistente.setTipoComida(requestDTO.tipoComida());

        // 5. Gestión de Alimentos:
        // Limpiamos la lista actual para que JPA/Hibernate maneje el reemplazo
        // automático
        // (Gracias al orphanRemoval = true en la Entidad)
        plantillaExistente.getDetalles().clear();

        // 6. Volver a vincular los nuevos alimentos con sus cantidades (Optimizado con
        // Map)
        vincularAlimentosConCantidades(plantillaExistente, requestDTO.alimentos());

        // 7. Guardar y retornar el DTO calculado
        return plantillaMapper.toDto(plantillaRepository.save(plantillaExistente));
    }

    /**
     * Elimina una plantilla del sistema por su identificador.
     *
     * @param id Identificador único de la plantilla a eliminar.
     * @throws ResourceNotFoundException Si la plantilla no existe en el sistema.
     */
    @Override
    @Transactional
    public void eliminarPlantilla(Long id) {
        if (!plantillaRepository.existsById(id)) {
            throw new ResourceNotFoundException("Plantilla no encontrada");
        }
        plantillaRepository.deleteById(id);
    }

    /**
     * Método auxiliar para optimizar la búsqueda de alimentos en lote y asociarlos a la plantilla.
     *
     * @param plantilla    Entidad de la plantilla a la que se vincularán los alimentos.
     * @param alimentoDtos Lista de DTOs con los identificadores de alimentos y cantidades.
     * @throws ResourceNotFoundException Si uno o más alimentos no existen en el catálogo.
     */
    private void vincularAlimentosConCantidades(Plantilla plantilla, List<PlantillaAlimentoRequestDTO> alimentoDtos) {
        // Extraemos todos los IDs de los alimentos para buscarlos en una sola consulta
        Set<Long> ids = alimentoDtos.stream()
                .map(dto -> dto.alimentoId())
                .collect(Collectors.toSet());

        // Buscamos todos los alimentos de golpe y los metemos a un Map para acceso
        // rápido
        Map<Long, Alimento> alimentosMap = alimentoRepository.findAllById(ids).stream()
                .collect(Collectors.toMap(dto -> dto.getId(), a -> a));

        if (alimentosMap.size() != ids.size()) {
            throw new ResourceNotFoundException("Uno o más alimentos no existen en el catálogo.");
        }

        // Creamos la relación intermedia con la cantidad específica
        for (PlantillaAlimentoRequestDTO dto : alimentoDtos) {
            Alimento alimento = alimentosMap.get(dto.alimentoId());
            // Usamos el método Helper 'addAlimento' que creamos en la Entidad Plantilla
            plantilla.addAlimento(alimento, dto.cantidad());
        }
    }
}
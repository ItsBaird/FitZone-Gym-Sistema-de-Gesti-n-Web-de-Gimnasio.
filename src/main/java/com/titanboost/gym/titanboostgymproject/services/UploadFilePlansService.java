package com.titanboost.gym.titanboostgymproject.services;

import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;

import org.springframework.validation.BindingResult;
import org.springframework.web.multipart.MultipartFile;

/**
 * Servicio encargado de la gestión de archivos relacionados con planes de membresía.
 * Implementa la interfaz {@link IUploadFileService} y proporciona métodos para cargar, copiar y eliminar archivos,
 * así como la validación de imágenes subidas.
 * <p>
 * Este servicio maneja archivos dentro de la carpeta "uploadsPlans" y permite realizar operaciones sobre ellos,
 * además de realizar validaciones sobre el tipo de archivo, su extensión y su tamaño.
 * </p>
 */
@Service
public class UploadFilePlansService implements IUploadFileService {

    // Carpeta donde se almacenan los archivos subidos
    private final static String UPLOADS_FOLDER = "uploadsPlans";

    /**
     * Carga un archivo desde el sistema de archivos.
     *
     * @param filename El nombre del archivo que se desea cargar.
     * @return Un objeto {@link Resource} que representa el archivo cargado.
     * @throws MalformedURLException Si la URL generada para el archivo es inválida.
     * @throws RuntimeException      Si el archivo no existe o no se puede leer.
     */
    @Override
    public Resource load(String filename) throws MalformedURLException {
        Path path = getPath(filename);
        Resource resource = new UrlResource(path.toUri());

        if (!resource.exists() || !resource.isReadable()) {
            throw new RuntimeException("Error in path: " + path.toString());
        }
        return resource;
    }

    /**
     * Copia un archivo recibido en una ubicación determinada dentro del sistema de archivos.
     * El archivo se renombra con un nombre único generado aleatoriamente.
     *
     * @param file El archivo que se desea copiar.
     * @return El nombre único del archivo copiado.
     * @throws IOException Si ocurre un error al copiar el archivo.
     */
    @Override
    public String copy(MultipartFile file) throws IOException {
        String uniqueFilename = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
        Path rootPath = getPath(uniqueFilename);
        Files.copy(file.getInputStream(), rootPath);
        return uniqueFilename;
    }

    /**
     * Elimina un archivo del sistema de archivos.
     *
     * @param filename El nombre del archivo que se desea eliminar.
     * @return {@code true} si el archivo fue eliminado exitosamente, {@code false} en caso contrario.
     */

    @Override
    public boolean delete(String filename) {
        Path rootPath = getPath(filename);
        File file = rootPath.toFile();
        if (file.exists() && file.canRead()) {
            if (file.delete()) {
                return true;
            }
        }
        return false;
    }

    /**
     * Obtiene la ruta absoluta de un archivo en la carpeta de carga.
     *
     * @param filename El nombre del archivo cuya ruta se desea obtener.
     * @return Un objeto {@link Path} que representa la ruta absoluta del archivo.
     */
    public Path getPath(String filename) {

        return Paths.get(UPLOADS_FOLDER).resolve(filename).toAbsolutePath();
    }

    /**
     * Valida un archivo de imagen asegurándose de que sea del tipo correcto, tenga una extensión permitida
     * y no exceda el tamaño máximo de 5MB.
     *
     * @param image El archivo de imagen que se desea validar.
     * @param result El objeto {@link BindingResult} que almacena los errores de validación.
     */
    // Validar imagen
    public void validateImage(MultipartFile image, BindingResult result) {
        // Validar tipo de archivo
        if (!image.getContentType().startsWith("image/")) {
            result.rejectValue("url_image", "invalid", "El archivo debe ser una imagen.");
            return;
        }

        // Validar extensión
        String originalFilename = image.getOriginalFilename();
        String extension = originalFilename != null
                ? originalFilename.substring(originalFilename.lastIndexOf(".") + 1).toLowerCase()
                : "";
        List<String> allowedExtensions = Arrays.asList("jpg", "jpeg", "png", "gif");
        if (!allowedExtensions.contains(extension)) {
            result.rejectValue("url_image", "invalid", "Solo se permiten archivos de imagen (jpg, jpeg, png, gif).");
            return;
        }

        // Validar tamaño (máximo 5MB)
        if (image.getSize() > 5 * 1024 * 1024) {
            result.rejectValue("url_image", "size", "El archivo no debe superar los 5MB.");
        }
    }

}

package com.titanboost.gym.titanboostgymproject.services;

import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

/**
 * Servicio encargado de la gestión de archivos relacionados con noticias.
 * Implementa la interfaz {@link IUploadFileService} y proporciona métodos para cargar, copiar y eliminar archivos.
 * <p>
 * Este servicio maneja archivos dentro de la carpeta "uploadsNews" y permite realizar operaciones sobre ellos.
 * </p>
 */
@Service
public class UploadFileNewsService implements IUploadFileService{

    // Carpeta donde se almacenan los archivos subidos
    private final static String UPLOADS_FOLDER = "uploadsNews";

    /**
     * Carga un archivo desde el sistema de archivos.
     *
     * @param filename El nombre del archivo que se desea cargar.
     * @return Un objeto {@link Resource} que representa el archivo cargado.
     * @throws MalformedURLException Si la URL generada para el archivo es inválida.
     * @throws RuntimeException Si el archivo no existe o no se puede leer.
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
        if(file.exists() && file.canRead()) {
            if(file.delete()) {
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
}

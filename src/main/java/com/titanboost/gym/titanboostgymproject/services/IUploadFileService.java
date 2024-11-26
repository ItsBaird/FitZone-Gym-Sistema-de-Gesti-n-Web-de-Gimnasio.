package com.titanboost.gym.titanboostgymproject.services;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.net.MalformedURLException;

/**
 * Servicio encargado de manejar las operaciones relacionadas con la carga, eliminación y obtención de archivos.
 * <p>
 * Este servicio define los métodos necesarios para cargar un archivo, copiar un archivo recibido como
 * parte de una solicitud HTTP, y eliminar archivos del sistema.
 * </p>
 */
public interface IUploadFileService {

    /**
     * Carga un archivo desde el sistema de archivos local.
     *
     * @param filename El nombre del archivo a cargar.
     * @return Un objeto {@link Resource} que representa el archivo cargado.
     * @throws MalformedURLException Si la URL del archivo no es válida.
     */
    public Resource load(String filename) throws MalformedURLException;

    /**
     * Copia un archivo recibido como parte de una solicitud HTTP al sistema de archivos local.
     *
     * @param file El archivo que se desea copiar.
     * @return Una cadena de texto que representa la ruta o el nombre del archivo copiado.
     * @throws IOException Si ocurre un error al intentar copiar el archivo.
     */
    public String copy(MultipartFile file) throws IOException;

    /**
     * Elimina un archivo del sistema de archivos local.
     *
     * @param filename El nombre del archivo a eliminar.
     * @return {@code true} si el archivo fue eliminado correctamente, {@code false} en caso contrario.
     */
    public boolean delete(String filename);
}

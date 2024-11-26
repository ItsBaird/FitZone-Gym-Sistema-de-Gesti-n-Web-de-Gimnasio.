function validateForm(event) {
    let isValid = true;
    const errorMessages = [];

    // Validación: Título
    const title = document.getElementById("title").value.trim();
    if (!title) {
        isValid = false;
        errorMessages.push("El título es obligatorio.");
    } else if (title.length < 5 || title.length > 100) {
        isValid = false;
        errorMessages.push("El título debe tener entre 5 y 100 caracteres.");
    }

    // Validación: Contenido
    const content = document.getElementById("content").value.trim();
    if (!content) {
        isValid = false;
        errorMessages.push("El contenido es obligatorio.");
    } else if (content.length < 10) {
        isValid = false;
        errorMessages.push("El contenido debe tener al menos 10 caracteres.");
    }

    // Validación: Imagen
    const fileInput = document.getElementById("file");
    const file = fileInput.files[0];
    if (!file) {
        isValid = false;
        errorMessages.push("Debe cargar una imagen.");
    } else {
        const validExtensions = ["image/jpeg", "image/png", "image/gif" , "image/webp"];
        if (!validExtensions.includes(file.type)) {
            isValid = false;
            errorMessages.push("El archivo debe ser una imagen válida (JPEG, PNG o GIF).");
        }
    }

    // Si hay errores, mostrar alertas y prevenir el envío
    if (!isValid) {
        event.preventDefault(); // Prevenir el envío del formulario
        showAlert(errorMessages); // Mostrar los errores con alertas de Bootstrap
        return false;  // Evita que el formulario se envíe
    }

    return true;  // Si es válido, permite que el formulario se envíe
}

// Mostrar los errores en alertas de Bootstrap
function showAlert(messages) {
    // Eliminar las alertas anteriores si existen
    const existingAlerts = document.querySelectorAll('.alert');
    existingAlerts.forEach(alert => alert.remove());

    // Crear las alertas
    const alertContainer = document.getElementById("alert-container");
    const alertElement = document.createElement("div");
    alertElement.className = "alert alert-danger alert-dismissible fade show w-50 mx-auto"; // w-50 para limitar el ancho y mx-auto para centrarla
    alertElement.role = "alert";

    // Añadir los mensajes al contenedor de alertas
    messages.forEach(msg => {
        const message = document.createElement("p");
        message.textContent = msg;
        alertElement.appendChild(message);
    });

    // Botón de cierre de la alerta
    const closeButton = document.createElement("button");
    closeButton.type = "button";
    closeButton.className = "close";
    closeButton.setAttribute("data-bs-dismiss", "alert");
    closeButton.setAttribute("aria-label", "Close");
    const closeSpan = document.createElement("span");
    closeSpan.setAttribute("aria-hidden", "true");
    closeSpan.textContent = "×";
    closeButton.appendChild(closeSpan);
    alertElement.appendChild(closeButton);

    // Insertar las alertas debajo del título
    alertContainer.appendChild(alertElement);
}
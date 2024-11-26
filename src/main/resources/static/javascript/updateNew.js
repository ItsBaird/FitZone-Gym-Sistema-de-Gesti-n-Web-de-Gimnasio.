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

    // Validación: Imagen (opcional si ya existe una imagen)
    const fileInput = document.getElementById("imageFile");
    const file = fileInput.files[0];
    const existingImage = document.querySelector("img");
    if (!file && !existingImage) {
        isValid = false;
        errorMessages.push("Debe cargar una imagen.");
    } else if (file) {
        const validExtensions = ["image/jpeg", "image/png", "image/gif", "image/webp"];
        if (!validExtensions.includes(file.type)) {
            isValid = false;
            errorMessages.push("El archivo debe ser una imagen válida (JPEG, PNG, GIF o WEBP).");
        }
    }

    // Validación: Estado
    const enabled = document.getElementById("enabled").value;
    if (enabled !== "true" && enabled !== "false") {
        isValid = false;
        errorMessages.push("Debe seleccionar un estado válido.");
    }

    // Si hay errores, mostrar alertas y prevenir el envío
    if (!isValid) {
        event.preventDefault(); // Prevenir el envío del formulario
        showAlert(errorMessages); // Mostrar los errores con alertas de Bootstrap
        return false; // Evita que el formulario se envíe
    }

    return true; // Si es válido, permite que el formulario se envíe
}

// Mostrar los errores en alertas de Bootstrap justo debajo del título
function showAlert(messages) {
    // Eliminar las alertas anteriores si existen
    const existingAlerts = document.querySelectorAll(".alert");
    existingAlerts.forEach(alert => alert.remove());

    // Crear el contenedor de alerta
    const alertContainer = document.createElement("div");
    alertContainer.className = "my-3"; // Espaciado para separar la alerta del título

    const alertElement = document.createElement("div");
    alertElement.className = "alert alert-danger alert-dismissible fade show alert-custom";
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
    closeButton.className = "btn-close";
    closeButton.setAttribute("data-bs-dismiss", "alert");
    closeButton.setAttribute("aria-label", "Close");

    alertElement.appendChild(closeButton);
    alertContainer.appendChild(alertElement);

    // Insertar las alertas justo debajo del h1
    const header = document.querySelector("h1");
    header.parentNode.insertBefore(alertContainer, header.nextSibling);
}

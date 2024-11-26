function validateForm(event) {
    const errors = [];

    // Obtener valores de los campos
    const name = document.getElementById("name").value.trim();
    const lastName = document.getElementById("last_name").value.trim();
    const email = document.getElementById("email") ? document.getElementById("email").value.trim() : null;
    const phone = document.getElementById("phone").value.trim();
    const documentNum = document.getElementById("documentNum").value.trim();
    const identificationType = document.getElementById("identification_type_id").value;

    // Validaciones
    if (!name.match(/^[a-zA-ZáéíóúÁÉÍÓÚñÑ\s]{2,100}$/)) {
        errors.push("El nombre debe contener solo letras y espacios, entre 2 y 100 caracteres.");
    }

    if (!lastName.match(/^[a-zA-ZáéíóúÁÉÍÓÚñÑ\s]{2,100}$/)) {
        errors.push("El apellido debe contener solo letras y espacios, entre 2 y 100 caracteres.");
    }

    if (email && !email.match(/^[^\s@]+@[^\s@]+\.[^\s@]+$/)) {
        errors.push("Formato de correo electrónico inválido.");
    }

    if (!phone.match(/^[0-9]{10}$/)) {
        errors.push("El número de teléfono debe contener 10 dígitos.");
    }

    if (!documentNum.match(/^[0-9]{6,10}$/)) {
        errors.push("El número de documento debe tener entre 6 y 10 dígitos.");
    }

    if (!identificationType) {
        errors.push("Debes seleccionar un tipo de identificación.");
    }

    // Si hay errores, mostrar el alert y prevenir el envío del formulario
    if (errors.length > 0) {
        event.preventDefault(); // Prevenir el envío

        // Crear y mostrar el alert
        let alertDiv = document.querySelector("#formErrorsAlert");
        if (!alertDiv) {
            // Si no existe el div del alert, crearlo
            alertDiv = document.createElement("div");
            alertDiv.id = "formErrorsAlert";
            alertDiv.className = "alert alert-danger mt-3 mx-auto";
            alertDiv.style.maxWidth = "600px";
            alertDiv.role = "alert";

            // Insertar el alert justo debajo del título
            const title = document.querySelector("h1");
            title.parentNode.insertBefore(alertDiv, title.nextSibling);
        }

        // Agregar errores al alert
        alertDiv.innerHTML = `
            <strong>Errores en el formulario:</strong>
            <ul>${errors.map(err => `<li>${err}</li>`).join("")}</ul>
        `;
        return false; // Evitar el envío del formulario
    }

    return true; // Permitir el envío del formulario si no hay errores
}
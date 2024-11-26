function validateForm(event) {
    const errors = [];

    // Obtener valores de los campos
    const name = document.getElementById("name").value.trim();
    const lastName = document.getElementById("last_name").value.trim();
    const email = document.getElementById("email").value.trim();
    const phone = document.getElementById("phone").value.trim();
    const password = document.getElementById("password").value;
    const passwordConfirm = document.getElementById("passwordConfirm").value;
    const documentNum = document.getElementById("documentNum").value.trim();
    const identificationType = document.getElementById("identification_type_id").value;

    // Validaciones
    if (!name.match(/^[a-zA-ZáéíóúÁÉÍÓÚñÑ\s]{2,100}$/)) {
        errors.push("El nombre debe contener solo letras y espacios, entre 2 y 100 caracteres.");
    }

    if (!lastName.match(/^[a-zA-ZáéíóúÁÉÍÓÚñÑ\s]{2,100}$/)) {
        errors.push("El apellido debe contener solo letras y espacios, entre 2 y 100 caracteres.");
    }

    if (!email.match(/^[^\s@]+@[^\s@]+\.[^\s@]+$/)) {
        errors.push("Formato de correo electrónico inválido.");
    }

    if (!phone.match(/^[0-9]{10}$/)) {
        errors.push("El número de teléfono debe contener 10 dígitos.");
    }

    if (!password.match(/^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\S+$).{8,}$/)) {
        errors.push("La contraseña debe tener al menos 8 caracteres, incluir mayúsculas, minúsculas, números y caracteres especiales.");
    }

    if (password !== passwordConfirm) {
        errors.push("Las contraseñas no coinciden.");
    }

    if (!documentNum.match(/^[0-9]{6,12}$/)) {
        errors.push("El número de documento debe tener entre 6 y 12 dígitos.");
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
            alertDiv.style.maxWidth = "500px";
            alertDiv.role = "alert";

            // Insertar el alert debajo del título "Registro"
            const header = document.querySelector("header");
            header.insertAdjacentElement("afterend", alertDiv);
        }

        // Agregar errores al alert
        alertDiv.innerHTML = `
            <strong>Errores en el formulario:</strong>
            <ul>${errors.map(err => `<li>${err}</li>`).join("")}</ul>
        `;
        return false;
    }

    return true;
}
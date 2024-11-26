function validateForm(event) {
    // Evita el envío del formulario
    event.preventDefault();

    // Limpiar alertas previas
    const alertDiv = document.getElementById('alert-messages');
    if (alertDiv) {
        alertDiv.remove();
    }

    // Arreglo para los errores
    let errors = [];

    // Validación de Nombre
    const name = document.getElementById('name').value;
    if (!name) {
        errors.push('El nombre es obligatorio.');
    }

    // Validación de Descripción (al menos 10 caracteres)
    const description = document.getElementById('description').value;
    if (!description) {
        errors.push('La descripción es obligatoria.');
    } else if (description.length < 10) {
        errors.push('La descripción debe tener al menos 10 caracteres.');
    }

    // Validación de Precio
    const price = document.getElementById('price').value;
    if (!price || isNaN(price) || price <= 0) {
        errors.push('El precio debe ser un número válido y mayor que 0.');
    }

    // Validación de Duración
    const duration = document.getElementById('duration').value;
    if (!duration || isNaN(duration) || duration <= 0) {
        errors.push('La duración debe ser un número válido y mayor que 0.');
    }

    // Validación de Sede
    const branch = document.getElementById('branch_id').value;
    if (!branch) {
        errors.push('Debe seleccionar una sede.');
    }

    // Validación de Habilitado
    const enabled = document.getElementById('enabled').value;
    if (enabled === "") {
        errors.push('Debe seleccionar un estado de habilitación.');
    }

    // Si hay errores, mostrar alerta y no enviar el formulario
    if (errors.length > 0) {
        // Crear el alert de Bootstrap
        const alertHtml = `
            <div class="alert alert-danger" id="alert-messages">
                <ul>
                    ${errors.map(error => `<li>${error}</li>`).join('')}
                </ul>
            </div>
        `;

        // Insertar el alert después del título
        const formHeader = document.querySelector('.card-header');
        formHeader.insertAdjacentHTML('afterend', alertHtml);

        return false; // No enviar el formulario
    }

    // Si no hay errores, permitir el envío del formulario
    event.target.submit();
}
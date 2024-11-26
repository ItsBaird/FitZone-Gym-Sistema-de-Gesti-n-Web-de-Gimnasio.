function validateForm(event) {
    event.preventDefault(); // Previene el envío del formulario si hay errores
    let errors = [];
    let errorMessages = document.getElementById('error-messages');
    errorMessages.innerHTML = ''; // Limpia los mensajes de error previos

    // Validación de nombre
    let name = document.getElementById('name').value;
    if (name.trim() === '') {
        errors.push('El nombre no puede ser nulo.');
    } else if (name.length > 255) {
        errors.push('El nombre no debe exceder los 255 caracteres.');
    }

    // Validación de descripción
    let description = document.getElementById('description').value;
    if (description.trim() === '') {
        errors.push('La descripción no puede ser nula.');
    } else if (description.length < 10) {
        errors.push('La descripción debe tener al menos 10 caracteres.');
    }

    // Validación de precio
    let price = document.getElementById('price').value;
    if (price === '' || isNaN(price) || parseFloat(price) <= 0) {
        errors.push('El precio debe ser un número positivo.');
    }

    // Validación de duración
    let duration = document.getElementById('duration').value;
    if (duration === '' || duration < 1) {
        errors.push('La duración debe ser al menos 1 día.');
    }

    // Validación de sede
    let branchId = document.getElementById('branch_id').value;
    if (branchId === '') {
        errors.push('Debe seleccionar una sede.');
    }

    // Validación de imagen
    let file = document.getElementById('file').files[0];
    if (!file) {
        errors.push('Debe seleccionar una imagen.');
    } else {
        let validImageTypes = ['image/jpeg', 'image/png', 'image/gif'];
        if (!validImageTypes.includes(file.type)) {
            errors.push('El archivo debe ser una imagen válida (JPG, PNG, GIF).');
        }
    }

    // Si hay errores, mostrar el mensaje en el showalert
    if (errors.length > 0) {
        let alertDiv = document.createElement('div');
        alertDiv.classList.add('alert', 'alert-danger', 'alert-custom');
        alertDiv.setAttribute('role', 'alert');
        alertDiv.innerHTML = '<ul>' + errors.map(error => `<li>${error}</li>`).join('') + '</ul>';
        errorMessages.appendChild(alertDiv);
        return false; // No enviar el formulario
    }

    // Si no hay errores, enviar el formulario
    event.target.submit();
}
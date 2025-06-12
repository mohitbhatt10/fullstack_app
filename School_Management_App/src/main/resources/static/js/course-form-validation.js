document.addEventListener('DOMContentLoaded', function() {
    const courseForm = document.getElementById('courseForm');
    const clearFormBtn = document.getElementById('clearForm');
    const codeInput = document.getElementById('code');
    const nameInput = document.getElementById('name');
    const teacherSelect = document.getElementById('teacherIds');

    // Form validation
    if (courseForm) {
        courseForm.addEventListener('submit', function(event) {
            // Validate teacher selection
            if (teacherSelect && teacherSelect.selectedOptions.length === 0) {
                event.preventDefault();
                event.stopPropagation();
                teacherSelect.setCustomValidity('Please select at least one teacher');
            } else if (teacherSelect) {
                teacherSelect.setCustomValidity('');
            }
            
            if (!this.checkValidity()) {
                event.preventDefault();
                event.stopPropagation();
            }
            this.classList.add('was-validated');
        });
    }

    // Teacher selection validation
    if (teacherSelect) {
        teacherSelect.addEventListener('change', function() {
            if (this.selectedOptions.length === 0) {
                this.setCustomValidity('Please select at least one teacher');
            } else {
                this.setCustomValidity('');
            }
        });
    }

    // Course code validation
    if (codeInput) {
        codeInput.addEventListener('input', function() {
            this.value = this.value.toUpperCase();
            if (!this.value.match(/^[A-Z]{2,4}[0-9]{3,4}$/)) {
                this.setCustomValidity('Course code must be 2-4 uppercase letters followed by 3-4 numbers');
            } else {
                this.setCustomValidity('');
            }
        });
    }

    // Course name validation
    if (nameInput) {
        nameInput.addEventListener('input', function() {
            if (this.value.length < 5) {
                this.setCustomValidity('Course name must be at least 5 characters long');
            } else if (this.value.length > 100) {
                this.setCustomValidity('Course name cannot exceed 100 characters');
            } else {
                this.setCustomValidity('');
            }
        });
    }

    // Clear form
    if (clearFormBtn && courseForm) {
        clearFormBtn.addEventListener('click', function() {
            if (confirm('Are you sure you want to clear the form?')) {
                const inputs = courseForm.querySelectorAll('input:not([type="hidden"]), select');
                inputs.forEach(input => {
                    if (input.multiple) {
                        // Clear multiple select
                        Array.from(input.options).forEach(option => option.selected = false);
                    } else {
                        input.value = '';
                    }
                });
                courseForm.classList.remove('was-validated');
            }
        });
    }
});

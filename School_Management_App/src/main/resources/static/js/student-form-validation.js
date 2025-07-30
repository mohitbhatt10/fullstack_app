document.addEventListener('DOMContentLoaded', function() {
    const form = document.getElementById('studentForm');
    const firstNameInput = document.getElementById('firstName');
    const lastNameInput = document.getElementById('lastName');
    const emailInput = document.getElementById('email');
    const phoneNumberInput = document.getElementById('phoneNumber');
    const usernameInput = document.getElementById('username');
    const rollNumberInput = document.getElementById('rollNumber');
    const departmentInput = document.getElementById('department');
    const semesterInput = document.getElementById('semester');
    const passwordInput = document.getElementById('password');
    const passwordToggle = document.getElementById('password-toggle');
    const strengthBar = document.querySelector('.password-strength .progress-bar');
    const strengthText = document.querySelector('.password-strength .strength-text');

    // Show error message
    function showError(input, message) {
        const formGroup = input.closest('.col-md-6');
        let errorDiv = formGroup.querySelector('.invalid-feedback');
        if (!errorDiv) {
            errorDiv = document.createElement('div');
            errorDiv.className = 'invalid-feedback';
            input.parentNode.appendChild(errorDiv);
        }
        errorDiv.textContent = message;
        input.classList.add('is-invalid');
        input.classList.remove('is-valid');
    }

    // Clear error message
    function clearError(input) {
        const formGroup = input.closest('.col-md-6');
        const errorDiv = formGroup.querySelector('.invalid-feedback');
        if (errorDiv) {
            errorDiv.textContent = '';
        }
        input.classList.remove('is-invalid');
        input.classList.add('is-valid');
    }

    // Validate name fields
    function validateName(input) {
        const namePattern = /^[A-Za-z]{2,50}$/;
        if (!namePattern.test(input.value)) {
            showError(input, 'Name must be between 2 and 50 characters, letters only');
            return false;
        }
        clearError(input);
        return true;
    }

    // Validate email
    function validateEmail() {
        const emailPattern = /^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$/;
        if (!emailPattern.test(emailInput.value)) {
            showError(emailInput, 'Please enter a valid email address');
            return false;
        }
        clearError(emailInput);
        return true;
    }

    // Validate phone number
    function validatePhoneNumber() {
        const phonePattern = /^(\+?[1-9]\d{1,14}|\d{10,15})$/;
        if (!phonePattern.test(phoneNumberInput.value)) {
            showError(phoneNumberInput, 'Please enter a valid phone number (10-15 digits, optional country code)');
            return false;
        }
        clearError(phoneNumberInput);
        return true;
    }

    // Validate username
    function validateUsername() {
        const usernamePattern = /^[a-zA-Z0-9_]{4,20}$/;
        if (!usernamePattern.test(usernameInput.value)) {
            showError(usernameInput, 'Username must be 4-20 characters and can contain letters, numbers and underscore');
            return false;
        }
        clearError(usernameInput);
        return true;
    }

    // Validate roll number
    function validateRollNumber() {
        const rollNumberPattern = /^[A-Z0-9]+$/;
        if (!rollNumberPattern.test(rollNumberInput.value)) {
            showError(rollNumberInput, 'Roll number must contain only uppercase letters and numbers');
            return false;
        }
        clearError(rollNumberInput);
        return true;
    }

    // Validate department
    function validateDepartment() {
        const departmentPattern = /^[A-Z]{2,5}$/;
        if (!departmentPattern.test(departmentInput.value)) {
            showError(departmentInput, 'Department must be 2-5 uppercase letters (e.g., CSE, ECE)');
            return false;
        }
        clearError(departmentInput);
        return true;
    }

    // Validate semester
    function validateSemester() {
        const semester = parseInt(semesterInput.value);
        if (isNaN(semester) || semester < 1 || semester > 8) {
            showError(semesterInput, 'Semester must be between 1 and 8');
            return false;
        }
        clearError(semesterInput);
        return true;
    }

    // Check password strength
    function checkPasswordStrength(password) {
        let strength = 0;
        const strengthIndicator = document.querySelector('.password-strength');

        if (!password) {
            strengthIndicator.style.display = 'none';
            return;
        }

        strengthIndicator.style.display = 'block';

        if (password.length >= 6) strength += 20;
        if (password.match(/[A-Z]/)) strength += 20;
        if (password.match(/[a-z]/)) strength += 20;
        if (password.match(/[0-9]/)) strength += 20;
        if (password.match(/[!@#$%^&*]/)) strength += 20;

        strengthBar.style.width = strength + '%';
        
        if (strength <= 20) {
            strengthBar.className = 'progress-bar bg-danger';
            strengthText.textContent = 'Very Weak';
        } else if (strength <= 40) {
            strengthBar.className = 'progress-bar bg-warning';
            strengthText.textContent = 'Weak';
        } else if (strength <= 60) {
            strengthBar.className = 'progress-bar bg-info';
            strengthText.textContent = 'Medium';
        } else if (strength <= 80) {
            strengthBar.className = 'progress-bar bg-primary';
            strengthText.textContent = 'Strong';
        } else {
            strengthBar.className = 'progress-bar bg-success';
            strengthText.textContent = 'Very Strong';
        }
    }

    // Validate password
    function validatePassword() {
        if (!passwordInput) return true; // Skip validation if password field doesn't exist (edit mode)
        
        if (passwordInput.value.length < 6) {
            showError(passwordInput, 'Password must be at least 6 characters long');
            return false;
        }

        const hasUpperCase = /[A-Z]/.test(passwordInput.value);
        const hasLowerCase = /[a-z]/.test(passwordInput.value);
        const hasNumbers = /\d/.test(passwordInput.value);
        const hasSpecialChar = /[!@#$%^&*]/.test(passwordInput.value);

        if (!(hasUpperCase && hasLowerCase && hasNumbers && hasSpecialChar)) {
            showError(passwordInput, 'Password must contain uppercase, lowercase, numbers and special characters');
            return false;
        }

        clearError(passwordInput);
        return true;
    }    // Generate a strong password
    function generateStrongPassword() {
        const uppercase = 'ABCDEFGHIJKLMNOPQRSTUVWXYZ';
        const lowercase = 'abcdefghijklmnopqrstuvwxyz';
        const numbers = '0123456789';
        const specialChars = '!@#$%^&*';
        
        let password = '';
        
        // Ensure at least one character from each category
        password += uppercase[Math.floor(Math.random() * uppercase.length)];
        password += lowercase[Math.floor(Math.random() * lowercase.length)];
        password += numbers[Math.floor(Math.random() * numbers.length)];
        password += specialChars[Math.floor(Math.random() * specialChars.length)];
        
        // Add more random characters
        const allChars = uppercase + lowercase + numbers + specialChars;
        for (let i = 0; i < 4; i++) {
            password += allChars[Math.floor(Math.random() * allChars.length)];
        }
        
        // Shuffle the password
        return password.split('').sort(() => Math.random() - 0.5).join('');
    }

    // Toggle password visibility
    if (passwordToggle) {
        passwordToggle.addEventListener('click', function() {
            const type = passwordInput.getAttribute('type');
            passwordInput.setAttribute('type', type === 'password' ? 'text' : 'password');
            const icon = this.querySelector('i');
            icon.className = type === 'password' ? 'fas fa-eye-slash' : 'fas fa-eye';
        });
    }

    // Generate password button
    const generatePasswordBtn = document.getElementById('generate-password');
    if (generatePasswordBtn && passwordInput) {
        generatePasswordBtn.addEventListener('click', function() {
            const newPassword = generateStrongPassword();
            passwordInput.value = newPassword;
            passwordInput.setAttribute('type', 'text');
            const icon = passwordToggle.querySelector('i');
            icon.className = 'fas fa-eye-slash';
            
            // Trigger password validation and strength check
            const event = new Event('input');
            passwordInput.dispatchEvent(event);

            // Select the password for easy copying
            passwordInput.select();
            
            // Add a temporary tooltip
            generatePasswordBtn.setAttribute('data-original-title', 'Password generated! Click to copy.');
            setTimeout(() => {
                passwordInput.setAttribute('type', 'password');
                icon.className = 'fas fa-eye';
            }, 3000);
        });
    }

    // Add input event listeners for real-time validation
    if (firstNameInput) firstNameInput.addEventListener('input', () => validateName(firstNameInput));
    if (lastNameInput) lastNameInput.addEventListener('input', () => validateName(lastNameInput));
    if (emailInput) emailInput.addEventListener('input', validateEmail);
    if (usernameInput) usernameInput.addEventListener('input', validateUsername);
    if (rollNumberInput) rollNumberInput.addEventListener('input', validateRollNumber);
    if (departmentInput) departmentInput.addEventListener('input', validateDepartment);
    if (semesterInput) semesterInput.addEventListener('input', validateSemester);
    if (passwordInput) {
        passwordInput.addEventListener('input', function() {
            validatePassword();
            checkPasswordStrength(this.value);
        });
    }    // Handle email domain selection
    const emailDomainSelect = document.getElementById('emailDomainSelect');
    if (emailDomainSelect && emailInput) {
        emailDomainSelect.addEventListener('change', function() {
            const domain = this.value;
            if (domain) {
                const currentEmail = emailInput.value;
                const username = currentEmail.split('@')[0] || currentEmail;
                emailInput.value = username + domain;
                validateEmail();
            }
        });

        // Update domain select when email is typed
        emailInput.addEventListener('input', function() {
            const domain = this.value.split('@')[1];
            if (domain) {
                const options = emailDomainSelect.options;
                for (let i = 0; i < options.length; i++) {
                    if (options[i].value === '@' + domain) {
                        emailDomainSelect.selectedIndex = i;
                        return;
                    }
                }
                emailDomainSelect.selectedIndex = 0;
            }
        });
    }

    // Clear form functionality
    const clearFormBtn = document.getElementById('clearForm');
    if (clearFormBtn) {
        clearFormBtn.addEventListener('click', function() {
            if (confirm('Are you sure you want to clear all fields?')) {
                form.reset();
                const allInputs = form.querySelectorAll('input:not([type="hidden"])');
                allInputs.forEach(input => {
                    input.classList.remove('is-valid', 'is-invalid');
                });
                if (passwordInput) {
                    document.querySelector('.password-strength').style.display = 'none';
                }
            }
        });
    }

    // Preview functionality
    const previewBtn = document.getElementById('previewBtn');
    if (previewBtn) {
        previewBtn.addEventListener('click', function() {
            document.getElementById('previewFullName').textContent = 
                `${firstNameInput.value} ${lastNameInput.value}`;
            document.getElementById('previewEmail').textContent = emailInput.value;
            document.getElementById('previewPhoneNumber').textContent = phoneNumberInput.value;
            document.getElementById('previewUsername').textContent = usernameInput.value;
            document.getElementById('previewRollNumber').textContent = rollNumberInput.value;
            document.getElementById('previewDepartment').textContent = departmentInput.value;
            document.getElementById('previewSemester').textContent = semesterInput.value;
        });
    }

    // Form submission validation
    form.addEventListener('submit', function(event) {
        let isValid = true;

        if (firstNameInput) isValid = validateName(firstNameInput) && isValid;
        if (lastNameInput) isValid = validateName(lastNameInput) && isValid;
        if (emailInput) isValid = validateEmail() && isValid;
        if (phoneNumberInput) isValid = validatePhoneNumber() && isValid;
        if (usernameInput) isValid = validateUsername() && isValid;
        if (rollNumberInput) isValid = validateRollNumber() && isValid;
        if (departmentInput) isValid = validateDepartment() && isValid;
        if (semesterInput) isValid = validateSemester() && isValid;
        if (passwordInput) isValid = validatePassword() && isValid;

        if (!isValid) {
            event.preventDefault();
            // Scroll to the first error
            const firstError = form.querySelector('.is-invalid');
            if (firstError) {
                firstError.scrollIntoView({ behavior: 'smooth', block: 'center' });
                firstError.focus();
            }
        }
    });
});

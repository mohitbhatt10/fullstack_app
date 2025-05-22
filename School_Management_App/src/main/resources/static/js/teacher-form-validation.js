// Teacher Form Validation and Enhancement
document.addEventListener('DOMContentLoaded', function() {
    const teacherForm = document.getElementById('teacherForm');
    const emailInput = document.getElementById('email');
    const emailDomainSelect = document.getElementById('emailDomainSelect');
    const passwordInput = document.getElementById('password');
    const generatePasswordBtn = document.getElementById('generate-password');
    const passwordToggleBtn = document.getElementById('password-toggle');
    const clearFormBtn = document.getElementById('clearForm');
    const previewBtn = document.getElementById('previewBtn');

    // Email domain selection
    if (emailDomainSelect && emailInput) {
        emailDomainSelect.addEventListener('change', function() {
            const selectedDomain = this.value;
            if (selectedDomain) {
                const currentEmail = emailInput.value;
                const atIndex = currentEmail.indexOf('@');
                emailInput.value = atIndex >= 0 
                    ? currentEmail.substring(0, atIndex) + selectedDomain
                    : currentEmail + selectedDomain;
            }
        });
    }

    // Password visibility toggle
    if (passwordToggleBtn && passwordInput) {
        passwordToggleBtn.addEventListener('click', function() {
            const type = passwordInput.type === 'password' ? 'text' : 'password';
            passwordInput.type = type;
            this.querySelector('i').classList.toggle('fa-eye');
            this.querySelector('i').classList.toggle('fa-eye-slash');
        });
    }

    // Generate strong password
    function generateStrongPassword() {
        const length = 12;
        const charset = 'abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789!@#$%^&*';
        let password = '';
        
        // Ensure at least one of each required character type
        password += 'ABCDEFGHIJKLMNOPQRSTUVWXYZ'[Math.floor(Math.random() * 26)];
        password += 'abcdefghijklmnopqrstuvwxyz'[Math.floor(Math.random() * 26)];
        password += '0123456789'[Math.floor(Math.random() * 10)];
        password += '!@#$%^&*'[Math.floor(Math.random() * 8)];
        
        // Fill the rest with random characters
        for (let i = password.length; i < length; i++) {
            password += charset[Math.floor(Math.random() * charset.length)];
        }
        
        // Shuffle the password
        return password.split('').sort(() => Math.random() - 0.5).join('');
    }

    if (generatePasswordBtn && passwordInput) {
        generatePasswordBtn.addEventListener('click', function() {
            const password = generateStrongPassword();
            passwordInput.value = password;
            // Trigger password strength check
            passwordInput.dispatchEvent(new Event('input'));
        });
    }

    // Password strength indicator
    if (passwordInput) {
        const strengthBar = document.querySelector('.password-strength .progress-bar');
        const strengthText = document.querySelector('.password-strength .strength-text');
        
        function checkPasswordStrength(password) {
            let strength = 0;
            const indicators = document.querySelector('.password-strength');
            
            if (indicators) {
                indicators.style.display = password ? 'block' : 'none';
            }
            
            if (!password) return;
            
            // Length check
            if (password.length >= 8) strength += 20;
            if (password.length >= 12) strength += 10;
            
            // Character type checks
            if (/[A-Z]/.test(password)) strength += 20;
            if (/[a-z]/.test(password)) strength += 20;
            if (/[0-9]/.test(password)) strength += 20;
            if (/[^A-Za-z0-9]/.test(password)) strength += 20;
            
            if (strengthBar) {
                strengthBar.style.width = strength + '%';
                strengthBar.className = 'progress-bar';
                
                if (strength > 80) {
                    strengthBar.classList.add('bg-success');
                    if (strengthText) strengthText.textContent = 'Strong';
                } else if (strength > 50) {
                    strengthBar.classList.add('bg-warning');
                    if (strengthText) strengthText.textContent = 'Medium';
                } else {
                    strengthBar.classList.add('bg-danger');
                    if (strengthText) strengthText.textContent = 'Weak';
                }
            }
        }

        passwordInput.addEventListener('input', function() {
            checkPasswordStrength(this.value);
        });
    }

    // Clear form
    if (clearFormBtn && teacherForm) {
        clearFormBtn.addEventListener('click', function() {
            if (confirm('Are you sure you want to clear the form?')) {
                const inputs = teacherForm.querySelectorAll('input:not([type="hidden"]), select');
                inputs.forEach(input => {
                    input.value = '';
                });
                teacherForm.classList.remove('was-validated');
                if (document.querySelector('.password-strength')) {
                    document.querySelector('.password-strength').style.display = 'none';
                }
            }
        });
    }

    // Preview functionality
    if (previewBtn) {
        previewBtn.addEventListener('click', function() {
            const firstName = document.getElementById('firstName').value;
            const lastName = document.getElementById('lastName').value;
            const email = document.getElementById('email').value;
            const username = document.getElementById('username').value;
            const department = document.getElementById('department').value;
            const designation = document.getElementById('designation').value;
            const specialization = document.getElementById('specialization').value;

            document.getElementById('previewFullName').textContent = `${firstName} ${lastName}`;
            document.getElementById('previewEmail').textContent = email;
            document.getElementById('previewUsername').textContent = username;
            document.getElementById('previewDepartment').textContent = department;
            document.getElementById('previewDesignation').textContent = designation;
            document.getElementById('previewSpecialization').textContent = specialization;
        });
    }

    // Form validation
    if (teacherForm) {
        teacherForm.addEventListener('submit', function(event) {
            if (!this.checkValidity()) {
                event.preventDefault();
                event.stopPropagation();
            }
            this.classList.add('was-validated');
        });

        // Custom validation for email
        if (emailInput) {
            emailInput.addEventListener('input', function() {
                if (this.value && !this.value.match(/^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$/)) {
                    this.setCustomValidity('Please enter a valid email address');
                } else {
                    this.setCustomValidity('');
                }
            });
        }
    }
});

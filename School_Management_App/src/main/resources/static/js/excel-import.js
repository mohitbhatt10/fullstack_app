/**
 * Excel import validation script
 */
document.addEventListener('DOMContentLoaded', function() {
    const fileInput = document.getElementById('file');
    const importForm = document.getElementById('importForm');
    
    if (importForm) {
        importForm.addEventListener('submit', function(e) {
            if (!fileInput.files || fileInput.files.length === 0) {
                e.preventDefault();
                showError('Please select a file to upload');
                return;
            }
            
            const file = fileInput.files[0];
            const fileName = file.name.toLowerCase();
            
            // Check file extension
            if (!fileName.endsWith('.xlsx') && !fileName.endsWith('.xls')) {
                e.preventDefault();
                showError('Please upload an Excel file (.xlsx or .xls)');
                return;
            }
            
            // Check file size (max 5MB)
            const maxSize = 5 * 1024 * 1024; // 5MB
            if (file.size > maxSize) {
                e.preventDefault();
                showError('File size exceeds 5MB limit');
                return;
            }
        });
    }
    
    function showError(message) {
        const errorDiv = document.getElementById('errorMessage');
        if (errorDiv) {
            errorDiv.textContent = message;
            errorDiv.classList.remove('d-none');
        } else {
            alert(message);
        }
    }
});
/**
 * Excel import validation script
 */
document.addEventListener('DOMContentLoaded', function() {
    const fileInput = document.getElementById('file');
    const importForm = document.getElementById('importForm');
    
    if (importForm) {
        importForm.addEventListener('submit', function(e) {
            if (!fileInput.files || fileInput.files.length === 0) {
                e.preventDefault();
                showError('Please select a file to upload');
                return;
            }
            
            const file = fileInput.files[0];
            const fileName = file.name.toLowerCase();
            
            // Check file extension
            if (!fileName.endsWith('.xlsx') && !fileName.endsWith('.xls')) {
                e.preventDefault();
                showError('Please upload an Excel file (.xlsx or .xls)');
                return;
            }
            
            // Check file size (max 5MB)
            const maxSize = 5 * 1024 * 1024; // 5MB
            if (file.size > maxSize) {
                e.preventDefault();
                showError('File size exceeds 5MB limit');
                return;
            }
        });
    }
    
    function showError(message) {
        const errorDiv = document.getElementById('errorMessage');
        if (errorDiv) {
            errorDiv.textContent = message;
            errorDiv.classList.remove('d-none');
        } else {
            alert(message);
        }
    }
});
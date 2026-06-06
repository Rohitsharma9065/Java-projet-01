/**
 * validation.js
 * --------------
 * Frontend form validation functions.
 * Used across login, register, and admin forms.
 *
 * Author: Rohit Sharma
 */

// -------------------------------------------------------
// Show an error message below a field
// -------------------------------------------------------
function showError(fieldId, message) {
    const field = document.getElementById(fieldId);
    if (!field) return;

    field.classList.add('error');

    // Find or create error message element
    let errMsg = field.parentElement.querySelector('.error-msg');
    if (!errMsg) {
        errMsg = document.createElement('span');
        errMsg.className = 'error-msg';
        field.parentElement.appendChild(errMsg);
    }
    errMsg.textContent = message;
}

// -------------------------------------------------------
// Clear error from a field
// -------------------------------------------------------
function clearError(fieldId) {
    const field = document.getElementById(fieldId);
    if (!field) return;

    field.classList.remove('error');
    const errMsg = field.parentElement.querySelector('.error-msg');
    if (errMsg) errMsg.textContent = '';
}

// -------------------------------------------------------
// Clear ALL errors on the page
// -------------------------------------------------------
function clearAllErrors() {
    document.querySelectorAll('.error').forEach(el => el.classList.remove('error'));
    document.querySelectorAll('.error-msg').forEach(el => el.textContent = '');
}

// -------------------------------------------------------
// Validate Registration Form
// Returns true if valid, false otherwise
// -------------------------------------------------------
function validateRegisterForm() {
    clearAllErrors();
    let isValid = true;

    const name     = document.getElementById('regName');
    const email    = document.getElementById('regEmail');
    const password = document.getElementById('regPassword');
    const confirm  = document.getElementById('regConfirm');

    // Name validation
    if (!name || name.value.trim() === '') {
        showError('regName', 'Full name is required.');
        isValid = false;
    } else if (name.value.trim().length < 2) {
        showError('regName', 'Name must be at least 2 characters.');
        isValid = false;
    }

    // Email validation
    const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    if (!email || email.value.trim() === '') {
        showError('regEmail', 'Email address is required.');
        isValid = false;
    } else if (!emailRegex.test(email.value.trim())) {
        showError('regEmail', 'Please enter a valid email address.');
        isValid = false;
    }

    // Password validation
    if (!password || password.value === '') {
        showError('regPassword', 'Password is required.');
        isValid = false;
    } else if (password.value.length < 6) {
        showError('regPassword', 'Password must be at least 6 characters.');
        isValid = false;
    }

    // Confirm password
    if (!confirm || confirm.value === '') {
        showError('regConfirm', 'Please confirm your password.');
        isValid = false;
    } else if (password && password.value !== confirm.value) {
        showError('regConfirm', 'Passwords do not match.');
        isValid = false;
    }

    return isValid;
}

// -------------------------------------------------------
// Validate Login Form
// -------------------------------------------------------
function validateLoginForm() {
    clearAllErrors();
    let isValid = true;

    const email    = document.getElementById('loginEmail');
    const password = document.getElementById('loginPassword');

    if (!email || email.value.trim() === '') {
        showError('loginEmail', 'Email address is required.');
        isValid = false;
    }

    if (!password || password.value === '') {
        showError('loginPassword', 'Password is required.');
        isValid = false;
    }

    return isValid;
}

// -------------------------------------------------------
// Validate Conversion Form
// -------------------------------------------------------
function validateConversionForm() {
    clearAllErrors();
    let isValid = true;

    const amount   = document.getElementById('amount');
    const fromCurr = document.getElementById('fromCurrency');
    const toCurr   = document.getElementById('toCurrency');

    if (!amount || amount.value.trim() === '' || isNaN(amount.value)) {
        showError('amount', 'Please enter a valid number.');
        isValid = false;
    } else if (parseFloat(amount.value) <= 0) {
        showError('amount', 'Amount must be greater than zero.');
        isValid = false;
    }

    if (!fromCurr || fromCurr.value === '') {
        showError('fromCurrency', 'Please select a source currency.');
        isValid = false;
    }

    if (!toCurr || toCurr.value === '') {
        showError('toCurrency', 'Please select a target currency.');
        isValid = false;
    }

    if (fromCurr && toCurr && fromCurr.value !== '' && toCurr.value !== ''
            && fromCurr.value === toCurr.value) {
        showError('toCurrency', 'Please select different currencies.');
        isValid = false;
    }

    return isValid;
}

// -------------------------------------------------------
// Validate Add Currency Form (Admin)
// -------------------------------------------------------
function validateCurrencyForm() {
    clearAllErrors();
    let isValid = true;

    const code = document.getElementById('currencyCode');
    const name = document.getElementById('currencyName');
    const rate = document.getElementById('exchangeRate');

    if (!code || code.value.trim() === '') {
        showError('currencyCode', 'Currency code is required (e.g. USD).');
        isValid = false;
    } else if (code.value.trim().length > 5) {
        showError('currencyCode', 'Code must be 2–5 characters.');
        isValid = false;
    }

    if (!name || name.value.trim() === '') {
        showError('currencyName', 'Currency name is required.');
        isValid = false;
    }

    if (!rate || rate.value === '' || isNaN(rate.value)) {
        showError('exchangeRate', 'Please enter a valid exchange rate.');
        isValid = false;
    } else if (parseFloat(rate.value) <= 0) {
        showError('exchangeRate', 'Exchange rate must be positive.');
        isValid = false;
    }

    return isValid;
}

// -------------------------------------------------------
// Real-time input validation: clear error when user types
// -------------------------------------------------------
document.addEventListener('DOMContentLoaded', function () {
    const inputs = document.querySelectorAll('input, select');
    inputs.forEach(function (input) {
        input.addEventListener('input', function () {
            clearError(this.id);
        });
    });
});

// -------------------------------------------------------
// Password strength indicator
// -------------------------------------------------------
function checkPasswordStrength(password) {
    if (password.length < 6)  return { level: 'Weak',   color: '#ef4444', width: '30%' };
    if (password.length < 10) return { level: 'Medium', color: '#f59e0b', width: '65%' };
    return                            { level: 'Strong', color: '#10b981', width: '100%' };
}

// Attach to password field if it exists
document.addEventListener('DOMContentLoaded', function () {
    const pwdField = document.getElementById('regPassword');
    const strengthBar = document.getElementById('passwordStrength');
    const strengthText = document.getElementById('strengthText');

    if (pwdField && strengthBar) {
        pwdField.addEventListener('input', function () {
            const result = checkPasswordStrength(this.value);
            strengthBar.style.width = result.width;
            strengthBar.style.background = result.color;
            if (strengthText) strengthText.textContent = result.level;
        });
    }
});

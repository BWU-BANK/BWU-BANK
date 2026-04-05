document.addEventListener('DOMContentLoaded', () => {
    const loginForm = document.getElementById('loginForm');
    const pinInput = document.getElementById('pin');
    const errorMessage = document.getElementById('error-message');

    if (loginForm) {
        loginForm.addEventListener('submit', (e) => {
            e.preventDefault();
            const pinValue = pinInput.value;
            const accValue = document.getElementById('accountNumber').value.trim();

            const storedUserRaw = localStorage.getItem('bankUser');
            // If the user hasn't registered yet, assume sandbox mode for testing
            let validAcc = 'ACC12345678';
            let validPin = '1234';

            if (storedUserRaw) {
                const storedUser = JSON.parse(storedUserRaw);
                validAcc = storedUser.accountNumber;
                validPin = storedUser.pin;
            } else {
                // Flash message encouraging them to register if no memory cache
                errorMessage.innerHTML = "No account found in memory. Please <a href='open-account.html'>Register</a> first.";
                errorMessage.style.display = 'block';
                return;
            }

            if (pinValue === validPin && accValue === validAcc) {
                // Success - redirect to dashboard
                window.location.href = 'dashboard.html';
            } else {
                // Error - shake animation
                errorMessage.innerHTML = "Invalid Account Number or PIN. Please try again.";
                errorMessage.style.display = 'block';
                pinInput.classList.add('error');
                pinInput.classList.add('shake');
                
                // Remove shake class after animation completes to allow it to happen again
                setTimeout(() => {
                    pinInput.classList.remove('shake');
                }, 300);
            }
        });

        // Clear error on generic input typing
        pinInput.addEventListener('input', () => {
            pinInput.classList.remove('error');
            errorMessage.style.display = 'none';
        });
    }
});

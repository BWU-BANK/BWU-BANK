/**
 * Transfer specific interactions.
 */
document.addEventListener('DOMContentLoaded', () => {
    const transferForm = document.getElementById('transferForm');
    const targetAccountInput = document.getElementById('targetAccount');
    const amountInput = document.getElementById('amount');
    const btnReview = document.getElementById('btnReview');
    
    const confirmModal = document.getElementById('confirmModal');
    const successModal = document.getElementById('successModal');
    const modalSummary = document.getElementById('modalSummary');
    
    const btnCancel = document.getElementById('btnCancel');
    const btnConfirm = document.getElementById('btnConfirm');

    // Basic live validation
    const validateForm = () => {
        const target = targetAccountInput.value.trim();
        const amt = parseFloat(amountInput.value);
        return target.length > 0 && amt > 0;
    };

    targetAccountInput.addEventListener('input', () => {
        btnReview.style.opacity = validateForm() ? '1' : '0.5';
    });
    
    amountInput.addEventListener('input', () => {
        btnReview.style.opacity = validateForm() ? '1' : '0.5';
    });

    // Initial state
    btnReview.style.opacity = '0.5';

    // Review logic
    btnReview.addEventListener('click', () => {
        if (!validateForm()) {
            targetAccountInput.classList.add('error');
            setTimeout(() => targetAccountInput.classList.remove('error'), 1000);
            return;
        }

        const target = targetAccountInput.value.trim();
        const amt = parseFloat(amountInput.value).toFixed(2);
        
        modalSummary.innerHTML = `Transferring <strong>₹${amt}</strong><br>to account <strong>${target}</strong>`;
        confirmModal.classList.add('active');
    });

    // Cancel modal
    btnCancel.addEventListener('click', () => {
        confirmModal.classList.remove('active');
    });

    // Confirm modal
    btnConfirm.addEventListener('click', () => {
        confirmModal.classList.remove('active');
        
        // Show success state modal
        setTimeout(() => {
            successModal.classList.add('active');
        }, 300);
    });
});

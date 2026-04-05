/**
 * Dashboard specific interactions.
 */
document.addEventListener('DOMContentLoaded', () => {
    // 1. Storage Integration
    const storedBalance = localStorage.getItem('bankBalance');
    const storedTransactions = localStorage.getItem('bankTransactions');
    const storedUserRaw = localStorage.getItem('bankUser');
    
    let currentBalance = storedBalance ? parseFloat(storedBalance) : 0;
    let transactionsList = storedTransactions ? JSON.parse(storedTransactions) : [];
    let bankUser = storedUserRaw ? JSON.parse(storedUserRaw) : null;

    if (bankUser) {
        // Update user display header
        const nameSpan = document.querySelector('.user-section span.text-secondary');
        if (nameSpan) nameSpan.textContent = bankUser.name;
        
        const avatarBox = document.querySelector('.user-section .avatar');
        if (avatarBox) {
            const parts = bankUser.name.split(' ');
            const initials = parts.length > 1 
                ? (parts[0][0] + parts[1][0]).toUpperCase() 
                : bankUser.name.substring(0, 2).toUpperCase();
            avatarBox.textContent = initials;
        }

        const navAccNumber = document.getElementById('navAccNumber');
        if (navAccNumber) navAccNumber.textContent = bankUser.accountNumber;
    }

    // 1b. Header Interactions
    window.addEventListener('click', function(e) {
        const menu = document.getElementById('userMenu');
        if (menu && menu.classList.contains('show') && !e.target.closest('.user-section')) {
            menu.classList.remove('show');
        }
    });

    // 2. Balance Count-up Animation
    const balanceDisplay = document.getElementById('balance-display');
    if (balanceDisplay) {
        setTimeout(() => {
            if(window.animateValue) {
                window.animateValue(balanceDisplay, 0, currentBalance, 1500);
            } else {
                balanceDisplay.innerHTML = '₹' + currentBalance.toLocaleString('en-IN', { minimumFractionDigits: 2 });
            }
        }, 300);
    }

    // 3. Populate Recent Transactions
    const tableBody = document.getElementById('transactionList');
    const lastAmtEl = document.getElementById('last-transaction-amt');
    const lastDescEl = document.getElementById('last-transaction-desc');

    if (transactionsList.length > 0) {
        // Output last transaction
        const last = transactionsList[transactionsList.length - 1]; // Assume last is the newest based on array push
        lastAmtEl.innerHTML = last.amount;
        lastDescEl.innerHTML = `<span style="color: ${last.amtColor};">${last.type}</span> • ${last.date}`;

        // Populate table from newest to oldest
        tableBody.innerHTML = ''; // Ensure clear
        transactionsList.slice().reverse().forEach((tx, idx) => {
            const tr = document.createElement('tr');
            tr.className = tx.cssClass;
            tr.innerHTML = `
                <td>${tx.date}</td>
                <td>${tx.type}</td>
                <td style="color: ${tx.amtColor};">${tx.amount}</td>
                <td>${tx.balance}</td>
                <td>${tx.note}</td>
            `;

            // animation properties similar to animations.js expectations natively
            tr.style.opacity = '0';
            tr.style.transform = 'translateX(-20px)';
            tr.classList.add('fade-row');
            tr.style.transition = `opacity 0.4s ease ${idx * 0.1}s, transform 0.4s ease ${idx * 0.1}s`;

            tableBody.appendChild(tr);
            
            // Trigger animation
            setTimeout(() => {
                tr.style.opacity = '1';
                tr.style.transform = 'translateX(0)';
            }, 100);
        });
    }
});

// --- Modal & Action Handlers ---

function openModal(id) {
    document.getElementById(id).classList.add('active');
}

function closeModal(id) {
    document.getElementById(id).classList.remove('active');
}

function toggleUPIField() {
    const mode = document.getElementById('depositMode').value;
    const upiField = document.getElementById('upiField');
    if (mode === 'UPI') {
        upiField.style.display = 'block';
    } else {
        upiField.style.display = 'none';
        document.getElementById('upiId').value = '';
    }
}

function handleDeposit(e) {
    e.preventDefault();
    const amt = parseFloat(document.getElementById('depositAmount').value);
    if(isNaN(amt) || amt <= 0) return alert('Enter valid amount');

    const depositPin = document.getElementById('depositPin').value;
    const storedUserRaw = localStorage.getItem('bankUser');
    const validPin = storedUserRaw ? JSON.parse(storedUserRaw).pin : '1234';

    if (depositPin !== validPin) {
        return alert('Incorrect PIN. Transaction denied.');
    }

    const mode = document.getElementById('depositMode').value;
    const upiId = document.getElementById('upiId').value;
    let note = mode === 'UPI' ? 'UPI Deposit (' + (upiId || 'Scanner') + ')' : 'Bank Deposit';

    updateBalanceAndTransactions(amt, 'Deposit', `+₹${amt.toFixed(2)}`, 'var(--green)', 'row-deposit', note);
}

function handleWithdraw(e) {
    e.preventDefault();
    const amt = parseFloat(document.getElementById('withdrawAmount').value);
    if(isNaN(amt) || amt <= 0) return alert('Enter valid amount');

    const withdrawPin = document.getElementById('withdrawPin').value;
    const storedUserRaw = localStorage.getItem('bankUser');
    const validPin = storedUserRaw ? JSON.parse(storedUserRaw).pin : '1234';

    if (withdrawPin !== validPin) {
        return alert('Incorrect PIN. Transaction denied.');
    }

    let currentBalance = parseFloat(localStorage.getItem('bankBalance')) || 0;
    if(amt > currentBalance) {
        return alert("Error: Insufficient funds. You cannot withdraw more than your balance.");
    }

    updateBalanceAndTransactions(-amt, 'Withdrawal', `-₹${amt.toFixed(2)}`, 'var(--red)', 'row-withdrawal', 'ATM / Bank Transfer');
}

function toggleDropdown(e) {
    if (e) e.stopPropagation();
    const menu = document.getElementById('userMenu');
    if (menu) menu.classList.toggle('show');
}

function handleUpdatePin(e) {
    e.preventDefault();
    const currentPin = document.getElementById('currentPin').value;
    const newPin = document.getElementById('newPin').value;
    const confirmNewPin = document.getElementById('confirmNewPin').value;

    const storedUserRaw = localStorage.getItem('bankUser');
    if(!storedUserRaw) return alert('Error: User profile missing from memory.');
    
    let activeUser = JSON.parse(storedUserRaw);

    if (currentPin !== activeUser.pin) {
        return alert('Current PIN is incorrect. Request denied.');
    }

    if (newPin !== confirmNewPin) {
        return alert('New PIN fields do not match!');
    }

    // Success Update
    activeUser.pin = newPin;
    localStorage.setItem('bankUser', JSON.stringify(activeUser));
    
    alert('PIN Updated Successfully! You will use this for future transactions and logins.');
    closeModal('updatePinModal');
    document.getElementById('updatePinForm').reset();
}

function updateBalanceAndTransactions(amtChange, type, amtString, color, cssClass, note) {
    let currentBalance = parseFloat(localStorage.getItem('bankBalance')) || 0;
    currentBalance += amtChange;
    localStorage.setItem('bankBalance', currentBalance.toFixed(2));

    let txList = JSON.parse(localStorage.getItem('bankTransactions')) || [];
    txList.push({
        date: new Date().toLocaleDateString('en-US', { month: 'short', day: 'numeric', year: 'numeric' }),
        type: type,
        amount: amtString,
        balance: `₹${currentBalance.toFixed(2)}`,
        note: note,
        cssClass: cssClass,
        amtColor: color
    });
    localStorage.setItem('bankTransactions', JSON.stringify(txList));
    
    // Refresh UI
    window.location.reload();
}

function generatePDF() {
    if (!window.jspdf || !window.jspdf.jsPDF) {
        alert("PDF generator not loaded properly. Check network connection.");
        return;
    }
    const { jsPDF } = window.jspdf;
    const doc = new jsPDF();
    
    let txList = JSON.parse(localStorage.getItem('bankTransactions')) || [];
    let currentBalance = localStorage.getItem('bankBalance') || "0.00";
    let storedUserRaw = localStorage.getItem('bankUser');
    let bankUser = storedUserRaw ? JSON.parse(storedUserRaw) : { name: "N/A", accountNumber: "N/A" };

    doc.setFont("helvetica");
    doc.setFontSize(20);
    doc.text("BWU-BANK Account Statement", 14, 22);
    
    doc.setFontSize(12);
    doc.text("Account Holder: " + bankUser.name, 14, 32);
    doc.text("Account Number: " + bankUser.accountNumber, 14, 38);
    doc.text("Total Balance: INR " + parseFloat(currentBalance).toFixed(2), 14, 44);
    doc.text("Generated: " + new Date().toLocaleString(), 14, 50);

    if (txList.length === 0) {
        doc.setFontSize(12);
        doc.text("No transactions available to display.", 14, 60);
    } else {
        const tableColumn = ["Date", "Type", "Amount", "Balance After", "Note"];
        const tableRows = [];

        txList.forEach(tx => {
            // strip unicode ₹ for safe PDF encoding rendering using standard signs just in case
            const safeAmount = tx.amount.replace('₹', 'INR ');
            const safeBal = tx.balance.replace('₹', 'INR ');
            const rowData = [
                tx.date,
                tx.type,
                safeAmount,
                safeBal,
                tx.note
            ];
            tableRows.push(rowData);
        });

        doc.autoTable({
            head: [tableColumn],
            body: tableRows,
            startY: 55,
            theme: 'striped',
            headStyles: { fillColor: [10, 22, 40] }
        });
    }

    doc.save("BWU-BANK_Statement.pdf");
}

window.onclick = function(event) {
    if (event.target.classList.contains('modal')) {
        event.target.classList.remove('active');
    }
}

/**
 * app.js
 * -------
 * Main JavaScript for Global Currency Converter.
 * Communicates with the Java Servlet backend via fetch() API.
 *
 * API Base URL auto-detects whether running on Tomcat or opened as a file.
 *
 * Author: Rohit Sharma
 */

// ============================================================
// API BASE URL
// IS_SERVER = true ONLY when running on Tomcat (port 8080).
// On our simple Java file server (port 5500) or file:// mode,
// we use localStorage demo mode — no backend calls at all.
// ============================================================
const IS_SERVER = window.location.port === '8080';
const API_BASE  = '';   // same origin when on Tomcat

// ============================================================
// DEFAULT CURRENCY DATA (used as fallback / for dropdowns)
// Will be overwritten by live data from /api/currency/list
// ============================================================
let CURRENCIES = [
    { code: 'USD', name: 'US Dollar',          rate: 1.000000,  flag: '🇺🇸' },
    { code: 'INR', name: 'Indian Rupee',        rate: 83.500000, flag: '🇮🇳' },
    { code: 'EUR', name: 'Euro',                rate: 0.920000,  flag: '🇪🇺' },
    { code: 'GBP', name: 'British Pound',       rate: 0.790000,  flag: '🇬🇧' },
    { code: 'JPY', name: 'Japanese Yen',        rate: 157.50000, flag: '🇯🇵' },
    { code: 'AUD', name: 'Australian Dollar',   rate: 1.540000,  flag: '🇦🇺' },
    { code: 'CAD', name: 'Canadian Dollar',     rate: 1.370000,  flag: '🇨🇦' },
    { code: 'CHF', name: 'Swiss Franc',         rate: 0.900000,  flag: '🇨🇭' },
    { code: 'CNY', name: 'Chinese Yuan',        rate: 7.250000,  flag: '🇨🇳' },
    { code: 'AED', name: 'UAE Dirham',          rate: 3.670000,  flag: '🇦🇪' },
    { code: 'SGD', name: 'Singapore Dollar',    rate: 1.350000,  flag: '🇸🇬' },
    { code: 'MYR', name: 'Malaysian Ringgit',   rate: 4.720000,  flag: '🇲🇾' },
];

// ============================================================
// SESSION MANAGEMENT (localStorage)
// ============================================================
const Session = {
    save(user)      { localStorage.setItem('currentUser', JSON.stringify(user)); },
    get()           { const d = localStorage.getItem('currentUser'); return d ? JSON.parse(d) : null; },
    clear()         { localStorage.removeItem('currentUser'); },
    isLoggedIn()    { return this.get() !== null; },
    isAdmin()       { const u = this.get(); return u && u.role === 'admin'; }
};

// ============================================================
// API HELPER
// ============================================================
async function apiPost(url, data) {
    const resp = await fetch(API_BASE + url, {
        method:  'POST',
        headers: { 'Content-Type': 'application/json' },
        body:    JSON.stringify(data),
        credentials: 'include'    // send session cookie
    });
    return resp.json();
}

async function apiGet(url) {
    const resp = await fetch(API_BASE + url, {
        method:      'GET',
        credentials: 'include'
    });
    return resp.json();
}

// ============================================================
// LOAD CURRENCIES FROM SERVER (or use fallback)
// ============================================================
async function loadCurrenciesFromServer() {
    if (!IS_SERVER) return;   // file:// mode — use defaults
    try {
        const data = await apiGet('/api/currency/list');
        if (data.success && data.currencies.length > 0) {
            // Merge server rates into CURRENCIES (keep flags)
            data.currencies.forEach(srv => {
                const local = CURRENCIES.find(c => c.code === srv.code);
                if (local) {
                    local.rate = srv.rate;
                    local.name = srv.name;
                } else {
                    CURRENCIES.push({ code: srv.code, name: srv.name, rate: srv.rate, flag: '💱' });
                }
            });
        }
    } catch (e) {
        console.warn('Could not fetch rates from server, using defaults.', e);
    }
}

// ============================================================
// TOAST NOTIFICATION
// ============================================================
function showToast(message, type = 'info', duration = 3500) {
    const existing = document.querySelector('.toast');
    if (existing) existing.remove();

    const toast = document.createElement('div');
    toast.className = `toast ${type}`;
    const icons = { success: '✅', error: '❌', info: 'ℹ️' };
    toast.innerHTML = `${icons[type] || ''} ${message}`;
    document.body.appendChild(toast);

    setTimeout(() => {
        toast.style.animation = 'fadeOut 0.4s ease forwards';
        setTimeout(() => toast.remove(), 400);
    }, duration);
}

// ============================================================
// DARK MODE
// ============================================================
function initDarkMode() {
    if (localStorage.getItem('darkMode') === 'true') {
        document.body.classList.add('dark');
        updateDarkIcon(true);
    }
}
function toggleDarkMode() {
    const isDark = document.body.classList.toggle('dark');
    localStorage.setItem('darkMode', isDark);
    updateDarkIcon(isDark);
}
function updateDarkIcon(isDark) {
    const btn = document.getElementById('darkToggle');
    if (btn) btn.textContent = isDark ? '☀️' : '🌙';
}

// ============================================================
// NAVBAR
// ============================================================
function initNavbar() {
    const hamburger = document.getElementById('hamburger');
    const navLinks  = document.getElementById('navLinks');
    if (hamburger && navLinks) {
        hamburger.addEventListener('click', () => navLinks.classList.toggle('open'));
        navLinks.querySelectorAll('a').forEach(a => {
            a.addEventListener('click', () => navLinks.classList.remove('open'));
        });
    }
    updateNavForUser();
}

function updateNavForUser() {
    const user      = Session.get();
    const logoutBtn = document.getElementById('logoutBtn');
    const adminLink = document.getElementById('adminLink');
    const userGreet = document.getElementById('userGreet');

    if (logoutBtn) logoutBtn.style.display = user ? 'inline-flex' : 'none';
    if (adminLink) adminLink.style.display = (user && user.role === 'admin') ? 'inline' : 'none';
    if (userGreet && user) userGreet.textContent = `Hi, ${user.name.split(' ')[0]}`;
}

// ============================================================
// LOGOUT
// ============================================================
async function logout() {
    if (IS_SERVER) {
        try { await apiGet('/api/auth/logout'); } catch (e) { /* ignore */ }
    }
    Session.clear();
    showToast('Logged out successfully.', 'info', 1800);
    setTimeout(() => window.location.href = 'index.html', 1500);
}

// ============================================================
// POPULATE CURRENCY DROPDOWNS
// ============================================================
function populateCurrencyDropdowns(fromId, toId, defaultFrom = 'INR', defaultTo = 'USD') {
    const fromSel = document.getElementById(fromId);
    const toSel   = document.getElementById(toId);
    if (!fromSel || !toSel) return;

    fromSel.innerHTML = '';
    toSel.innerHTML   = '';

    CURRENCIES.forEach(c => {
        fromSel.add(new Option(`${c.flag} ${c.code} — ${c.name}`, c.code));
        toSel.add(new Option(`${c.flag} ${c.code} — ${c.name}`, c.code));
    });

    fromSel.value = defaultFrom;
    toSel.value   = defaultTo;
}

// ============================================================
// CLIENT-SIDE CONVERSION (instant preview, no server round-trip)
// Formula: amount / fromRate × toRate
// ============================================================
function convertCurrencyLocal(amount, fromCode, toCode) {
    const from = CURRENCIES.find(c => c.code === fromCode);
    const to   = CURRENCIES.find(c => c.code === toCode);
    if (!from || !to) return null;
    const amountInUSD = amount / from.rate;
    const result      = amountInUSD * to.rate;
    return Math.round(result * 10000) / 10000;
}

// ============================================================
// PERFORM CONVERSION (called on form submit)
// Saves to DB via Java Servlet when on server; local fallback otherwise
// ============================================================
async function performConversion(event) {
    event.preventDefault();
    if (!validateConversionForm()) return;

    const amount   = parseFloat(document.getElementById('amount').value);
    const fromCode = document.getElementById('fromCurrency').value;
    const toCode   = document.getElementById('toCurrency').value;

    let result, message;

    if (IS_SERVER && Session.isLoggedIn()) {
        // Call Java backend
        try {
            const data = await apiPost('/api/currency/convert', {
                amount, fromCurrency: fromCode, toCurrency: toCode
            });
            if (data.success) {
                result  = data.convertedAmount;
                message = null;
            } else {
                showToast(data.message, 'error');
                return;
            }
        } catch (e) {
            // Fall back to local calculation if server is unreachable
            result = convertCurrencyLocal(amount, fromCode, toCode);
        }
    } else {
        // File mode or not logged in — local calculation + localStorage history
        result = convertCurrencyLocal(amount, fromCode, toCode);
        const user = Session.get();
        if (user) {
            History.add({ userId: user.id, userName: user.name,
                          fromCurrency: fromCode, toCurrency: toCode,
                          amount, convertedAmount: result });
        }
    }

    if (result === null) { showToast('Conversion failed. Check currencies.', 'error'); return; }

    // Show result on screen
    const resultBox    = document.getElementById('resultBox');
    const resultAmount = document.getElementById('resultAmount');
    const resultLabel  = document.getElementById('resultLabel');
    const rateInfo     = document.getElementById('rateInfo');

    if (resultBox)    resultBox.classList.remove('hidden');
    if (resultAmount) resultAmount.textContent = `${result.toLocaleString()} ${toCode}`;
    if (resultLabel)  resultLabel.textContent  = `${amount.toLocaleString()} ${fromCode} equals`;

    const from = CURRENCIES.find(c => c.code === fromCode);
    const to   = CURRENCIES.find(c => c.code === toCode);
    if (rateInfo && from && to) {
        const rate = Math.round((to.rate / from.rate) * 10000) / 10000;
        rateInfo.textContent = `1 ${fromCode} = ${rate} ${toCode}`;
    }

    showToast(`${amount} ${fromCode} = ${result} ${toCode}`, 'success');
}

// ============================================================
// SWAP CURRENCIES
// ============================================================
function swapCurrencies() {
    const from = document.getElementById('fromCurrency');
    const to   = document.getElementById('toCurrency');
    if (!from || !to) return;
    [from.value, to.value] = [to.value, from.value];
    const amount = document.getElementById('amount');
    if (amount && amount.value) {
        document.getElementById('convertForm')?.dispatchEvent(new Event('submit'));
    }
}

// ============================================================
// CURRENCY GRID (Home page)
// ============================================================
function populateCurrencyGrid(containerId, filterText) {
    const container = document.getElementById(containerId);
    if (!container) return;

    const filtered = filterText
        ? CURRENCIES.filter(c =>
            c.code.toLowerCase().includes(filterText.toLowerCase()) ||
            c.name.toLowerCase().includes(filterText.toLowerCase()))
        : CURRENCIES;

    container.innerHTML = filtered.map(c => `
        <div class="currency-item" onclick="selectCurrency('${c.code}')">
            <div class="flag">${c.flag}</div>
            <div class="code">${c.code}</div>
            <div class="rate">1 USD = ${c.rate} ${c.code}</div>
        </div>
    `).join('');
}

function selectCurrency(code) {
    const fromSel = document.getElementById('fromCurrency');
    if (fromSel) { fromSel.value = code; showToast(`${code} selected.`, 'info', 1800); }
}

// ============================================================
// LOCAL HISTORY (localStorage fallback)
// ============================================================
const History = {
    KEY: 'conversionHistory',
    add(entry) {
        const all = this.getAll();
        entry.id   = Date.now();
        entry.date = new Date().toLocaleString();
        all.unshift(entry);
        if (all.length > 100) all.pop();
        localStorage.setItem(this.KEY, JSON.stringify(all));
    },
    getAll()          { const d = localStorage.getItem(this.KEY); return d ? JSON.parse(d) : []; },
    getByUser(userId) { return this.getAll().filter(h => h.userId === userId); },
    clear()           { localStorage.removeItem(this.KEY); }
};

// ============================================================
// DEMO LOGIN HELPER (used in file/5500 mode)
// ============================================================
function demoLogin(email, password, errDiv) {
    // Built-in demo accounts
    const demo = [
        { id: 1, name: 'Admin',        email: 'admin@currency.com', password: 'admin123', role: 'admin' },
        { id: 2, name: 'Rohit Sharma', email: 'rohit@test.com',     password: 'pass123',  role: 'user'  },
    ];

    // Also check localStorage registered users
    const stored     = localStorage.getItem('registeredUsers');
    const localUsers = stored ? JSON.parse(stored) : [];
    const allUsers   = [...demo, ...localUsers];

    const found = allUsers.find(u => u.email === email && u.password === password);

    if (found) {
        found.lastLogin = new Date().toLocaleString();
        Session.save(found);
        showToast(`Welcome, ${found.name}! 🎉`, 'success');
        setTimeout(() => {
            window.location.href = found.role === 'admin' ? 'admin.html' : 'dashboard.html';
        }, 1200);
    } else {
        if (errDiv) {
            errDiv.className     = 'alert alert-error';
            errDiv.textContent   = '❌ Invalid email or password. Try: rohit@test.com / pass123';
            errDiv.style.display = 'flex';
        }
        showToast('Invalid email or password.', 'error');
    }
}

// ============================================================
// HANDLE LOGIN (calls Java backend when on Tomcat port 8080)
// ============================================================
async function handleLogin(event) {
    event.preventDefault();
    if (!validateLoginForm()) return;

    const email    = document.getElementById('loginEmail').value.trim().toLowerCase();
    const password = document.getElementById('loginPassword').value;
    const errDiv   = document.getElementById('loginError');

    if (errDiv) errDiv.style.display = 'none';

    if (IS_SERVER) {
        // Running on Tomcat (port 8080) — call real Java backend
        try {
            const data = await apiPost('/api/auth/login', { email, password });
            if (data.success) {
                Session.save(data.user);
                showToast(`Welcome back, ${data.user.name}!`, 'success');
                setTimeout(() => {
                    window.location.href = data.user.role === 'admin' ? 'admin.html' : 'dashboard.html';
                }, 1200);
            } else {
                if (errDiv) {
                    errDiv.className     = 'alert alert-error';
                    errDiv.textContent   = '❌ ' + data.message;
                    errDiv.style.display = 'flex';
                }
                showToast(data.message, 'error');
            }
        } catch (e) {
            showToast('Backend error. Falling back to demo mode.', 'info');
            demoLogin(email, password, errDiv);
        }
    } else {
        // Demo mode (port 5500 file server or file://)
        demoLogin(email, password, errDiv);
    }
}

// ============================================================
// LOCAL REGISTER HELPER
// ============================================================
function localRegister(name, email, password) {
    const stored = localStorage.getItem('registeredUsers');
    const users  = stored ? JSON.parse(stored) : [];

    if (users.find(u => u.email === email)) {
        showError('regEmail', 'This email is already registered.');
        showToast('Email already registered.', 'error');
        return;
    }

    users.push({ id: Date.now(), name, email, password, role: 'user',
                 createdAt: new Date().toLocaleString() });
    localStorage.setItem('registeredUsers', JSON.stringify(users));
    showToast('Registration successful! Redirecting to login...', 'success');
    setTimeout(() => window.location.href = 'login.html', 1800);
}

// ============================================================
// HANDLE REGISTER
// ============================================================
async function handleRegister(event) {
    event.preventDefault();
    if (!validateRegisterForm()) return;

    const name            = document.getElementById('regName').value.trim();
    const email           = document.getElementById('regEmail').value.trim().toLowerCase();
    const password        = document.getElementById('regPassword').value;
    const confirmPassword = document.getElementById('regConfirm').value;

    if (IS_SERVER) {
        try {
            const data = await apiPost('/api/auth/register', { name, email, password, confirmPassword });
            if (data.success) {
                showToast(data.message, 'success');
                setTimeout(() => window.location.href = 'login.html', 1800);
            } else {
                showToast(data.message, 'error');
                if (data.message.toLowerCase().includes('email')) {
                    showError('regEmail', data.message);
                }
            }
        } catch (e) {
            // Fall back to local register
            localRegister(name, email, password);
        }
    } else {
        localRegister(name, email, password);
    }
}

// ============================================================
// LOAD HISTORY FROM SERVER (history.html)
// ============================================================
async function loadServerHistory() {
    if (!IS_SERVER) return null;
    try {
        const data = await apiGet('/api/history/user');
        return data.success ? data.history : null;
    } catch (e) {
        return null;
    }
}

// ============================================================
// EXPORT TO CSV
// ============================================================
function exportToCSV(records) {
    if (!records) {
        const user = Session.get();
        records = user ? History.getByUser(user.id) : [];
    }
    if (records.length === 0) { showToast('No history to export.', 'info'); return; }

    const headers = ['ID', 'From', 'To', 'Amount', 'Converted Amount', 'Date'];
    const rows    = records.map(h =>
        [h.id, h.fromCurrency, h.toCurrency, h.amount, h.convertedAmount, h.date || h.conversionDate].join(',')
    );
    const csv  = [headers.join(','), ...rows].join('\n');
    const blob = new Blob([csv], { type: 'text/csv;charset=utf-8;' });
    const url  = URL.createObjectURL(blob);
    const link = document.createElement('a');
    link.href  = url;
    link.download = `history_${Date.now()}.csv`;
    link.click();
    URL.revokeObjectURL(url);
    showToast('Exported to CSV!', 'success');
}

// ============================================================
// FOOTER YEAR
// ============================================================
function setFooterYear() {
    const el = document.getElementById('footerYear');
    if (el) el.textContent = new Date().getFullYear();
}

// ============================================================
// INIT — runs when DOM is ready
// ============================================================
document.addEventListener('DOMContentLoaded', async function () {
    initDarkMode();
    initNavbar();
    setFooterYear();

    // Load live rates from server (non-blocking)
    await loadCurrenciesFromServer();

    // Wire up dark toggle
    document.getElementById('darkToggle')?.addEventListener('click', toggleDarkMode);

    // Wire up logout
    document.getElementById('logoutBtn')?.addEventListener('click', logout);

    // Wire up swap
    document.getElementById('swapBtn')?.addEventListener('click', swapCurrencies);

    // Wire up conversion form
    document.getElementById('convertForm')?.addEventListener('submit', performConversion);

    // Wire up login form
    document.getElementById('loginForm')?.addEventListener('submit', handleLogin);

    // Wire up register form
    document.getElementById('registerForm')?.addEventListener('submit', handleRegister);

    // Currency search on home page
    const currSearch = document.getElementById('currencySearch');
    if (currSearch) {
        populateCurrencyGrid('currencyGrid', '');
        currSearch.addEventListener('input', () => populateCurrencyGrid('currencyGrid', currSearch.value));
    }

    // Populate quick converter on home page
    if (document.getElementById('fromCurrency')) {
        populateCurrencyDropdowns('fromCurrency', 'toCurrency', 'INR', 'USD');
    }
});

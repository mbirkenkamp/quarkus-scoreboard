const addUser = function () {
    if (this.newUsername === "") {
        notie.alert({
            type: 'error',
            text: 'Spielername ist leer!'
        });
        return;
    }

    const options = {
        method: 'POST'
    }

    fetch('/scoreboard/user/' + encodeURI(this.newUsername), options)
        .then(response => {
            if (response.status === 200) {
                this.loadUsers();
                this.newUserPanelOpen = false;
                notie.alert({
                    type: 'success',
                    text: 'Spieler angelegt!'
                });
            } else if (response.status === 409) {
                notie.alert({
                    type: 'error',
                    text: 'Spieler existiert schon!'
                });
            } else {
                notie.alert({
                    type: 'error',
                    text: 'Spieler konnte nicht angelegt werden!'
                });
            }
        })
}

const loadUsers = function () {
    fetch('/scoreboard/user')
        .then(response => response.json())
        .then(users => {
            this.users = users;
        });
}



function setDarkMode(enabled) {
    document.documentElement.setAttribute('data-theme', enabled ? 'dark' : 'light');
    localStorage.setItem('darkmode', enabled ? 'on' : 'off');
    if (enabled) {
        document.getElementById('colorswitch').classList.add('fa-sun');
        document.getElementById('colorswitch').classList.remove('fa-moon');
    } else {
        document.getElementById('colorswitch').classList.add('fa-moon');
        document.getElementById('colorswitch').classList.remove('fa-sun');
    }
}

function initTheme() {
    const saved = localStorage.getItem('darkmode');

    let dark;
    if (saved === 'on') {
        dark = true;
    } else if (saved === 'off') {
        dark = false;
    } else {
        dark = window.matchMedia && window.matchMedia('(prefers-color-scheme: dark)').matches;
    }
    setDarkMode(dark);
}

function toggleColor() {
    const currentlyDark = document.documentElement.getAttribute('data-theme') === 'dark';
    setDarkMode(!currentlyDark);
}

document.addEventListener('DOMContentLoaded', () => {
    initTheme();
});

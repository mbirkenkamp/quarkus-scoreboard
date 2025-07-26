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

const initDarkMode = function () {
    let savedMode = localStorage.getItem('darkmode');
    if (savedMode === 'on') {
        document.querySelector("#darkmode").disabled = "";
        this.ui.colorScheme = "fa-sun";
    } else {
        document.querySelector("#darkmode").disabled = "disabled";
        this.ui.colorScheme = "fa-moon";
    }
}

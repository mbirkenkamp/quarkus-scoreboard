"use strict";

function getYear(dateString) {
    return dateString.substring(0, 4);
}

function getMonth(dateString) {
    return dateString.substring(5, 7);
}

function getDay(dateString) {
    return dateString.substring(8, dateString.length);
}

const boardgame = Vue.createApp({
    data() {
        return {
            users: [],
            boardgames: [],

            newBoardgamePanelOpen: false,
            newBoardgameName: "",
            newBoardgameDescription: "",

            newUserPanelOpen: false,
            newUsername: "",

            tableEntries: [],
            filterDateFrom: '',
            filterBoardgameId: '',

            newSessionPanelOpen: false,
            newSessionBoardgameId: "",
            newSessionParticipants: [],

            ui: {
                colorScheme: "fa-moon"
            }
        }
    },
    created: function () {
        this.loadBoardGames();
        this.loadUsers();

        this.resetFilter();
        this.loadGameSessions();

        this.expandNewSessionParticipants();

        let savedMode = localStorage.getItem('darkmode');
        if (savedMode === 'on') {
            document.querySelector("#darkmode").disabled = "";
            this.ui.colorScheme = "fa-sun";
        } else {
            document.querySelector("#darkmode").disabled = "disabled";
            this.ui.colorScheme = "fa-moon";
        }
    },
    computed: {
        getParticipantScores() {
            const scores = {};
            for (const entry of this.tableEntries) {
                for (const participant of entry.participants) {
                    const name = participant.name;
                    if (!scores[name]) {
                        scores[name] = {
                            name: name,
                            wins: 0,
                            losses: 0,
                            score: 0
                        };
                    }
                    if (participant.hasWon) {
                        scores[name].wins += 1;
                        scores[name].score += 1;
                    }
                    if (participant.hasLost) {
                        scores[name].losses += 1;
                        scores[name].score -= 1;
                    }
                }
            }
            return Object.values(scores).sort((a, b) => b.score - a.score);
        },
        selectedBoardgame() {
            return this.boardgames.find(b => String(b.id) === String(this.newSessionBoardgameId));
        },
        selectedBoardgameDescription() {
            return this.selectedBoardgame?.description || "";
        }
    },
    methods: {
        loadUsers: function () {
            fetch('/scoreboard/user')
                .then(response => response.json())
                .then(users => {
                    this.users = users;
                });
        },
        loadBoardGames: function () {
            fetch('/scoreboard/boardgames/')
                .then(response => response.json())
                .then(games => {
                    this.boardgames = games;
                });
        },
        loadGameSessions: function () {
            fetch('/scoreboard/boardgames/sessions'
                + `?boardgameId=${this.filterBoardgameId}&date-from=${this.filterDateFrom}`)
                .then(response => response.json())
                .then(games => {
                    this.tableEntries = games;
                });
        },
        addUser: addUser,
        addBoardgame: function () {
            if (this.newBoardgameName.trim() === "") {
                notie.alert({ type: 'error', text: 'Boardgamename ist leer!' });
                return;
            }

            const params = new URLSearchParams();
            params.set('name', this.newBoardgameName.trim());
            if (this.newBoardgameDescription && this.newBoardgameDescription.trim() !== "") {
                params.set('description', this.newBoardgameDescription.trim());
            }

            fetch('/scoreboard/boardgame?' + params.toString(), { method: 'POST' })
                .then(response => {
                    if (response.status === 200) {
                        this.loadBoardGames();
                        this.newBoardgamePanelOpen = false;
                        this.newBoardgameName = "";
                        this.newBoardgameDescription = "";
                        notie.alert({ type: 'success', text: 'Boardgame angelegt!' });
                    } else if (response.status === 409) {
                        notie.alert({ type: 'error', text: 'Boardgame existiert schon!' });
                    } else {
                        notie.alert({ type: 'error', text: 'Boardgame konnte nicht angelegt werden!' });
                    }
                })
        },
        toggleColor: function () {
            if (this.ui.colorScheme === "fa-moon") {
                document.querySelector("#darkmode").disabled = "";
                this.ui.colorScheme = "fa-sun";
                localStorage.setItem('darkmode', 'on');
            } else {
                document.querySelector("#darkmode").disabled = "disabled";
                this.ui.colorScheme = "fa-moon";
                localStorage.setItem('darkmode', 'off');
            }
        },
        addGameSession: function () {
            if (!this.newSessionBoardgameId) {
                notie.alert({ type: 'error', text: 'Kein Spiel ausgewählt!' });
                return;
            }

            if (this.newSessionParticipants.length === 0) {
                notie.alert({ type: 'error', text: 'Keine Teilnehmer angegeben!' });
                return;
            }

            const winnerOrLoserCount = this.newSessionParticipants.filter(p => p.hasWon || p.hasLost).length;
            if (winnerOrLoserCount === 0) {
                notie.alert({ type: 'error', text: 'Es muss immer einen Gewinner (oder Verlierer) geben!' });
                return;
            }

            const dto = {
                date: new Date().toISOString().substring(0, 10),
                participants: this.newSessionParticipants.filter(p => p.name !== '').map(p => ({
                    name: p.name,
                    hasWon: p.hasWon,
                    hasLost: p.hasLost
                }))
            };

            fetch('/scoreboard/boardgame/' + encodeURIComponent(this.newSessionBoardgameId) + '/session', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(dto)
            })
                .then(response => {
                    if (response.ok) {
                        notie.alert({ type: 'success', text: 'Session gespeichert!' });
                        this.newSessionParticipants.forEach(p => {
                            if (p.name !== '') {
                                p.hasWon = false;
                                p.hasLost = false;
                            }
                        });
                        this.expandNewSessionParticipants();
                        this.loadGameSessions();
                    } else {
                        return response.text().then(text => {
                            notie.alert({ type: 'error', text: 'Fehler: ' + text });
                        });
                    }
                })
                .catch(error => {
                    console.error('Fetch error:', error);
                    notie.alert({ type: 'error', text: 'Verbindungsfehler beim Speichern!' });
                });
        },
        expandNewSessionParticipants: function () {
            let arr = this.newSessionParticipants;
            if (arr.length === 0 || arr[arr.length - 1].name !== '') {
                arr.push({name:'', hasWon: false, hasLost: false});
            }
        },
        getUnchosenUsersForNewSessionParticipants(participantRow) {
            const selectedNames = this.newSessionParticipants
                .filter(p => p !== participantRow)
                .map(p => p.name);

            return this.users.filter(u => !selectedNames.includes(u.name));
        },
        resetFilter: function() {
            this.filterBoardgameId = '';

            const today = new Date();
            const oneMonthAgo = new Date(today.getFullYear(), today.getMonth() - 1, today.getDate());
            this.filterDateFrom = oneMonthAgo.toISOString().substring(0, 10);
        },
        resetNewSessionForm: function() {
            this.newSessionBoardgameId = "";
            this.newSessionParticipants = [];
            this.expandNewSessionParticipants();
        }
    }
});

boardgame.mount("#app");
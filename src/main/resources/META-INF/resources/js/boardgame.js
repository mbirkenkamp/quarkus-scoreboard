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
            filterDateUntil: '',
            filterBoardgameId: '',

            newSessionPanelOpen: false,
            newSessionBoardgameId: "",
            newSessionParticipants: [],
            newSessionDate: '',
        }
    },
    created: function () {
        this.loadBoardGames();
        this.loadUsers();

        this.setFilterTo('last30Days');
        this.loadGameSessions();

        this.expandNewSessionParticipants();
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
                            score: 0,
                            total: 0,
                            winRate: 0,
                            lossRate: 0,
                            percentileScore: 0
                        };
                    }
                    scores[name].total += 1;
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
            for (const name in scores) {
                const score = scores[name];
                score.winRate = Math.round(score.wins / (score.wins + score.losses) * 100);
                score.lossRate = Math.round(score.losses / (score.wins + score.losses) * 100);
                score.percentileScore = Math.round((score.winRate - score.lossRate));
                if (isNaN(score.percentileScore)) {
                    score.percentileScore = 0;
                }
            }
            return Object.values(scores).sort((a, b) => b.percentileScore - a.percentileScore);
        },
        selectedBoardgame() {
            return this.boardgames.find(b => String(b.id) === String(this.newSessionBoardgameId));
        },
        selectedBoardgameDescription() {
            return this.boardgames.find(b => b.id === this.filterBoardgameId)?.description || "";
        },
        selectedNewSessionBoardgameDescription() {
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
                + `?boardgameId=${this.filterBoardgameId}&date-from=${this.filterDateFrom}&date-until=${this.filterDateUntil}`)
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
        openNewSessionPanel: function () {
            this.newSessionPanelOpen = true;
            this.newSessionDate = new Date().toISOString().substring(0, 10);
            if (this.filterBoardgameId) {
                this.newSessionBoardgameId = this.filterBoardgameId;
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

            let gameDate = this.newSessionDate;
            if (!gameDate) {
                gameDate = new Date().toISOString().substring(0, 10);
            }

            const dto = {
                date: gameDate,
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
        resetNewSessionForm: function() {
            this.newSessionBoardgameId = "";
            this.newSessionParticipants = [];
            this.expandNewSessionParticipants();
            this.newSessionDate = new Date().toISOString().substring(0, 10);
        },
        setFilterTo: function(filter) {
            const today = new Date();
            switch (filter) {
                case 'none':
                    this.filterDateFrom = '';
                    this.filterDateUntil = '';
                    break;
                case 'today':
                    this.filterDateFrom = today.toISOString().substring(0, 10);
                    this.filterDateUntil = today.toISOString().substring(0, 10);
                    break;
                case 'thisWeek':
                    const startOfWeek = new Date(today.getFullYear(), today.getMonth(), today.getDate() - today.getDay() + 2);
                    const endOfWeek = new Date(today.getFullYear(), today.getMonth(), today.getDate() - today.getDay() + 8);
                    this.filterDateFrom = startOfWeek.toISOString().substring(0, 10);
                    this.filterDateUntil = endOfWeek.toISOString().substring(0, 10);
                    break;
                case 'thisMonth':
                    const startOfMonth = new Date(today.getFullYear(), today.getMonth(), 1);
                    const endOfMonth = new Date(today.getFullYear(), today.getMonth() + 1, 0);
                    this.filterDateFrom = startOfMonth.toISOString().substring(0, 10);
                    this.filterDateUntil = endOfMonth.toISOString().substring(0, 10);
                    break;
                case 'last30Days':
                    const startOfLast30Days = new Date(today.getFullYear(), today.getMonth(), today.getDate() - 30);
                    this.filterDateFrom = startOfLast30Days.toISOString().substring(0, 10);
                    this.filterDateUntil = today.toISOString().substring(0, 10);
                    break;
                case 'lastWeek':
                    const startOfLastWeek = new Date(today.getFullYear(), today.getMonth(), today.getDate() - today.getDay() - 5);
                    const endOfLastWeek = new Date(today.getFullYear(), today.getMonth(), today.getDate() - today.getDay() + 1);
                    this.filterDateFrom = startOfLastWeek.toISOString().substring(0, 10);
                    this.filterDateUntil = endOfLastWeek.toISOString().substring(0, 10);
                    break;
                case 'lastMonth':
                    const startOfLastMonth = new Date(today.getFullYear(), today.getMonth() - 1, 1);
                    const endOfLastMonth = new Date(today.getFullYear(), today.getMonth(), 0);
                    this.filterDateFrom = startOfLastMonth.toISOString().substring(0, 10);
                    this.filterDateUntil = endOfLastMonth.toISOString().substring(0, 10);
                    break;
                case 'last60Days':
                    const startOfLast60Days = new Date(today.getFullYear(), today.getMonth(), today.getDate() - 60);
                    this.filterDateFrom = startOfLast60Days.toISOString().substring(0, 10);
                    this.filterDateUntil = today.toISOString().substring(0, 10);
                    break;
            }
            this.loadGameSessions();
        }
    }
});

boardgame.mount("#app");

// This is a little workaround, so the native DatePicker is more easily accessible
// as they normally only open up on explicit user interaction
// See https://developer.mozilla.org/de/docs/Web/API/HTMLInputElement/showPicker
if ("showPicker" in HTMLInputElement.prototype) {
    document.querySelectorAll('input[type="date"]').forEach(input => {
        input.addEventListener('focus', () => {
            input.showPicker();
        });
        input.addEventListener('click', () => {
            input.showPicker();
        });
    });
}

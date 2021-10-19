"use strict";

var app = Vue.createApp({
    data() {
        return {
            seasons: [],
            currentSeasonName: null,
            currentSeason: null,
            users: [],
            games: [],
            tableEntries: [],
            currChampion: "",
            hoverName: "",

            team1User1: "",
            team1User2: "",
            team2User1: "",
            team2User2: "",
            team1Score: 9,
            team2Score: 9,
            
            newUserPanelOpen: false,
            newUsername: "",

            newSeasonPanelOpen: false,
            newSeasonName: null,
            newSeasonTeamSize: 1,
            newSeasonType: 1,
            newSeasonIcon: null,
            newSeasonStart: null,
            newSeasonEnd: null,

            openGamePanel: false,

            ui: {
                colorScheme: "fa-moon"
            }
        }
    },    
    created: function () {
        this.loadSeasons();        
        this.loadUsers();        
    },
    computed: {
        isCurrentSeason: function(){
            if(this.currentSeason === null){
                return false;
            } else {
                let startDate = new Date(this.currentSeason.startDate.year, this.currentSeason.startDate.month-1, this.currentSeason.startDate.day);
                let endDate = new Date(this.currentSeason.endDate.year, this.currentSeason.endDate.month-1, this.currentSeason.endDate.day);
                return new Date() >= startDate && new Date() <= endDate;
            }            
        },
        getReversedGameList() {
            if(this.hoverName == ""){
                return this.games.reverse();
            } else {
                return this.games.reverse().filter(currGame => currGame.team1User1 == this.hoverName 
                                                            || currGame.team1User2 == this.hoverName
                                                            || currGame.team2User1 == this.hoverName
                                                            || currGame.team2User2 == this.hoverName);
            }
        },
        getTopScorer() {
            let topScorerArray = this.users.filter(currUser => currUser.mostGoals);
            if(topScorerArray.length == 1){
                return topScorerArray[0].name;
            }
            return null;
        },
        getTopWinner() {
            let winnerArray = this.users.filter(currUser => currUser.mostWins);
            if(winnerArray.length == 1){
                return winnerArray[0].name;
            }
            return null;
        }
    },
    methods: {  
        hoverTableEntry: function(event) {     
            if(event == null){
                this.hoverName = "";
            } else {   
                this.hoverName = event.target.innerText.trim();
            }
        },     
        loadSeasons: function(){
            fetch('/scoreboard/season')
            .then(response => response.json())
            .then(seasons => {
                this.seasons = seasons;
                this.loadCurrentSeason();
            });                
        },
        loadCurrentSeason: function(){  
            let currDate = new Date();
            let currentSeasons = this.seasons.filter(
                                    currSeason => 
                                        currDate >= new Date(currSeason.startDate.year, currSeason.startDate.month-1, currSeason.startDate.day) 
                                        && currDate <= new Date(currSeason.endDate.year, currSeason.endDate.month-1, currSeason.endDate.day)
                                );

            if(currentSeasons.length > 0){
                this.currentSeason = currentSeasons[0];
                this.currentSeasonName = this.currentSeason.name;
                this.loadTable();
                this.loadGames();
            }
        },
        changeSeason: function(){
            for(let index in this.seasons){
                if(this.seasons[index].name == this.currentSeasonName) {
                    this.currentSeason = this.seasons[index];
                }
            }
            this.currChampion = null;
            this.loadTable();
            this.loadGames();
        },
        loadUsers: function(){
            fetch('/scoreboard/user')
            .then(response => response.json())
            .then(users => {
                this.users = users;


            });
        },
        loadGames: function(){
            if(this.currentSeasonName === ""){
                return;
            }
            fetch('/scoreboard/season/' + this.currentSeasonName + '/game')
            .then(response => response.json())
            .then(games => {
                this.games = games;
            });
        },
        addDetailedMatch: function(season, team1user1, team1user2, team2user1, team2user2, team1Score, team2Score) {
            this.season = season;
            this.team1User1 = team1user1;
            this.team1User2 = team1user2;
            this.team2User1 = team2user1;
            this.team2User2 = team2user2;
            this.team1Score = team1Score;
            this.team2Score = team2Score;
            this.openGamePanel=false;
            this.addMatch();
        },
        addMatch: function(){            
            if(this.team1User1 == this.team2User1){
                notie.alert({
                    type: 'error',
                    text: 'Spiele gegen sich selbst werden nicht gewertet!'
                });
                return;
            }

            if(this.currentSeason.teamSize == 2){
                if(this.team1User1 == this.team2User2 || this.team1User2 == this.team2User1 || this.team1User2 == this.team2User2){
                    notie.alert({
                        type: 'error',
                        text: 'Spiele gegen sich selbst werden nicht gewertet!'
                    });
                    return;
                }
            }

            if(this.team1Score === ""){
                notie.alert({
                    type: 'error',
                    text: 'Anzahl Tore Spieler 1 fehlt!'
                });
                return;
            }

            if(this.team2Score === ""){
                notie.alert({
                    type: 'error',
                    text: 'Anzahl Tore Spieler 2 fehlt!'
                });
                return;
            }

            if(this.team1Score === this.team2Score){
                notie.alert({
                    type: 'error',
                    text: 'there can only be one! <img src="https://media04.meinbezirk.at/article/2017/02/03/4/9837064_XXL.jpg" height="500"/>'
                });
                return;
            }

            const options = {
                method: 'POST'                    
            }

            let url = '/scoreboard/season/' + this.currentSeasonName + '/game?team1user1=' + encodeURI(this.team1User1) + '&team2user1=' + encodeURI(this.team2User1) + '&score1=' + this.team1Score + '&score2=' + this.team2Score;
            if(this.currentSeason.teamSize ==2){
                url = '/scoreboard/season/' + this.currentSeasonName + '/teamgame?team1user1=' + encodeURI(this.team1User1) + '&team1user2=' + encodeURI(this.team1User2) + '&team2user1=' + encodeURI(this.team2User1) + '&team2user2=' + encodeURI(this.team2User2) + '&score1=' + this.team1Score + '&score2=' + this.team2Score;
            }

            fetch(url, options)
                .then(response => {
                    if(response.status == 200){                
                        this.loadTable();
                        this.loadGames();
                        notie.alert({
                            type: 'success',
                            text: 'Spiel gespeichert!'
                        });
                    } else {
                        notie.alert({
                            type: 'error',
                            text: 'Spiel konnte nicht gespeichert werden!'
                        });
                    }
                });
            
        },
        addUser: function(){
            if(this.newUsername == ""){
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
                if(response.status == 200){
                    this.loadUsers();
                    this.newUserPanelOpen = false;
                    notie.alert({
                        type: 'success',
                        text: 'Spieler angelegt!'
                    });
                } else if(response.status == 409){
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
        },
        addSeason: function(){
            if(this.newSeasonName == null){
                notie.alert({
                    type: 'error',
                    text: 'Saisonname ist Pflicht'
                });
                return;
            }

            if(this.newSeasonIcon == null){
                notie.alert({
                    type: 'error',
                    text: 'icon ist Pflicht'
                });
                return;
            }

            if(this.newSeasonStart == null){
                notie.alert({
                    type: 'error',
                    text: 'Saisonstart ist Pflicht'
                });
                return;
            }

            if(this.newSeasonEnd == null){
                notie.alert({
                    type: 'error',
                    text: 'Saisonende ist Pflicht'
                });
                return;
            }

            const options = {
                method: 'POST'                    
            }
            
            fetch('/scoreboard/season?name=' + this.newSeasonName + '&start_date=' + this.newSeasonStart + '&end_date=' + this.newSeasonEnd + '&icon=' + this.newSeasonIcon + '&team_size=' + this.newSeasonTeamSize, options)
            .then(response => {
                if(response.status == 200){
                    notie.alert({
                        type: 'success',
                        text: 'Saison gespeichert!'
                    });
                } else {
                    notie.alert({
                        type: 'error',
                        text: 'Saison konnte nicht gespeichert werden!'
                    });
                }
                this.newSeasonPanelOpen = false;
            })
        },
        loadTable: function(){        
            if(this.currentSeasonName === ""){
                return;
            }
            fetch('/scoreboard/season/' + this.currentSeasonName + '/table')
            .then(response => response.json())
            .then(table => {
                this.tableEntries = table;
                
                if(this.currChampion !== undefined && this.currChampion !== "" && table[0].name != this.currChampion && this.isCurrentSeason){
                    this.celebrateNewChampion(table[0].name);
                }
                this.currChampion = table[0].name;
            });
        },
        randomInRange: function(min, max) {
            return Math.random() * (max - min) + min;
        },
        celebrateNewChampion: function(newChampion){
            var animationEnd = Date.now();
            notie.force({
                type: 'error',
                text: '<span class="notie-fullscreen fa-4x">new champion:<br/>' + newChampion + ' <i class="fas fa-crown has-text-warning"></i></span>',
                buttonText: 'OK'
            }, function(){animationEnd = Date.now()});

            var duration = 60 * 1000;
            animationEnd = Date.now() + duration;

            let vueInstance = this;

            setInterval(
                function(){   
                    var timeLeft = animationEnd - Date.now();

                    if (timeLeft <= 0) {
                        return;      
                    }           
                    confetti({
                        angle: vueInstance.randomInRange(55, 125),
                        spread: vueInstance.randomInRange(50, 70),
                        particleCount: vueInstance.randomInRange(50, 100),
                        origin: { x: vueInstance.randomInRange(0.1, 0.9), y: vueInstance.randomInRange(0.1, 0.9) }
                    })
                }
            , 700);                
        },
        toggleColor: function() {
            if(this.ui.colorScheme == "fa-moon"){
                document.querySelector("#darkmode").disabled = "";
                this.ui.colorScheme = "fa-sun";
            } else {
                document.querySelector("#darkmode").disabled = "disabled";
                this.ui.colorScheme = "fa-moon";
            }
        }
    }
});
app.component('game-highlight', {
    props: ['season','team1user1','team1user2','team2user1','team2user2'],    
    emits: ['team1-goal','team2-goal','game-finished'], 
    data() {
        return {
            'team1score': 0,
            'team2score': 0
        }
    },   
    template: `
    <nav class="level">
        <div class="level-left">
            <div class="level-item is-size-1">
                {{team1user1}}<br />
                {{team1user2}}<br />
            </div>    
        </div>
        <div class="level-item has-text-centered is-size-1">            
            <button class="button is-primary is-size-1" @click="addTeam1Goal">+</button>&nbsp;{{team1score}}:{{team2score}}&nbsp;<button class="button is-primary is-size-1" @click="addTeam2Goal">+</button>
        </div>
        <!-- Right side -->
        <div class="level-right">
            <div class="level-item is-size-1">
                {{team2user1}}<br />
                {{team2user2}}<br />
            </div>
        </div>
    </nav>
    `,
    computed: {
        getWinner() {
            if(this.team1score > this.team2score){
                return this.team1user1;
            } else {
                return this.team2user1;
            }
        }
    },    
    methods: {
        addTeam1Goal: function() {
            this.team1score += 1;
            if(this.team1score == 9) {
                this.celebrateChampion();
            }
        },
        addTeam2Goal: function() {
            this.team2score += 1;
            if(this.team2score == 9) {
                this.celebrateChampion();
            }
        },
        celebrateChampion: function() {
            console.log("GOIL!!!! " + this.getWinner);
            this.$emit('game-finished', this.season, this.team1user1, this.team1user2, this.team2user1, this.team2user2, this.team1score, this.team2score);
        }
    }
});

app.mount("#app");
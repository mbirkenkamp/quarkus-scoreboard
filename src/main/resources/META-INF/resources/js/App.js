"use strict";

var app = new Vue({
    el: '#app',
    data: {
        seasons: [],
        currentSeasonName: null,
        currentSeason: null,
        users: [],
        tableEntries: [],
        currChampion: "",

        user1: "",
        user2: "",
        user1Score: 10,
        user2Score: 10,
        
        newUsername: "",

        newSeasonName: null,
        newSeasonIcon: null,
        newSeasonStart: null,
        newSeasonEnd: null
    },
    created: function () {
        this.loadSeasons();
        this.loadCurrentSeason();
        this.loadUsers();
    },
    methods: {          
        loadSeasons: function(){
            fetch('/scoreboard/season')
            .then(response => response.json())
            .then(seasons => {
                this.seasons = seasons;
            });                
        },
        loadCurrentSeason: function(){            
            fetch('/scoreboard/season/current')
            .then(response => response.json())
            .then(season => {
                this.currentSeason = season.value;
                this.currentSeasonName = season.value.name;
                this.loadSeasonIcon();
                this.loadTable();
            });                
        },
        loadSeasonIcon: function(){
            let seasonIconElem = document.querySelector("#currentSeasonIcon");
            seasonIconElem.classList.className = '';
            seasonIconElem.classList.add("fas");
            seasonIconElem.classList.add(this.currentSeason.icon);
        },
        changeSeason: function(){
            for(let index in this.seasons){
                if(this.seasons[index].name == this.currentSeasonName) {
                    this.currentSeason = this.seasons[index];
                }
            }
            this.loadSeasonIcon();
            this.loadTable();
            //this.displayProgress();
        },
        loadUsers: function(){
            fetch('/scoreboard/user')
            .then(response => response.json())
            .then(users => {
                this.users = users;
            });
        },
        addMatch: function(){
            if(this.user1 == this.user2){
                notie.alert({
                    type: 'error',
                    text: 'Spiele gegen sich selbst werden nicht gewertet!'
                });
                return;
            }

            if(this.user1Score === ""){
                notie.alert({
                    type: 'error',
                    text: 'Anzahl Tore Spieler 1 fehlt!'
                });
                return;
            }

            if(this.user2Score === ""){
                notie.alert({
                    type: 'error',
                    text: 'Anzahl Tore Spieler 2 fehlt!'
                });
                return;
            }

            if(this.user1Score === this.user2Score){
                notie.alert({
                    type: 'error',
                    text: 'there can only be one! <img src="https://media04.meinbezirk.at/article/2017/02/03/4/9837064_XXL.jpg" height="500"/>'
                });
                return;
            }

            const options = {
                method: 'POST'                    
            }

            fetch('/scoreboard/season/' + this.currentSeasonName + '/game?user1=' + this.user1 + '&user2=' + this.user2 + '&score1=' + this.user1Score + '&score2=' + this.user2Score, options)
            .then(response => {
                if(response.status == 200){                
                    this.loadTable();
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
            })
        },
        addUser: function(){
            if(this.newUsername == ""){
                notie.alert({
                    type: 'error',
                    text: 'Spielername ist leer!'
                });
                return;
            }

            if(this.newUsername.includes(" ")){
                notie.alert({
                    type: 'error',
                    text: 'Spielername darf keine Leerzeichen enthalten (SORRY)!'
                });
                return;
            }

            const options = {
                method: 'POST'                    
            }

            fetch('/scoreboard/user/' + this.newUsername, options)
            .then(response => {
                if(response.status == 200){
                    this.loadUsers();
                    document.querySelector('#newUserModal').classList.remove('is-active');
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
            
            fetch('/scoreboard/season?name=' + this.newSeasonName + '&start_date=' + this.newSeasonStart + '&end_date=' + this.newSeasonEnd + '&icon=' + this.newSeasonIcon, options)
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
                
                if(this.currChampion !== undefined && this.currChampion !== "" && table[0].name != this.currChampion){
                    this.celebrateNewChampion(table[0].name);
                }
                this.currChampion = table[0].name;
            });
        },
        displayProgress: function(){
            let progress = document.querySelector("#progressbar");
            if(this.currentSeason == null){
                progress.value = 0;
                progress.max = 100;
            } else {
                let daysInSeason = (this.currentSeason.endDate - this.currentSeason.startDate) / (1000 * 60 * 60 * 24);
                let remainingDaysInSeason = (this.currentSeason.endDate - new Date()) / (1000 * 60 * 60 * 24);
                progress.value = remainingDaysInSeason;
                progress.max = daysInSeason;
            }            
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

            setInterval(
                function(){   
                    var timeLeft = animationEnd - Date.now();

                    if (timeLeft <= 0) {
                        return;      
                    }           
                    confetti({
                        angle: this.randomInRange(55, 125),
                        spread: this.randomInRange(50, 70),
                        particleCount: this.randomInRange(50, 100),
                        origin: { x: this.randomInRange(0.1, 0.9), y: this.randomInRange(0.1, 0.9) }
                    })
                }
            , 700);                
        },
        randomInRange: function(min, max) {
            return Math.random() * (max - min) + min;
        }
    }
});
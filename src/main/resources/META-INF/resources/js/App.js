var app = new Vue({
    el: '#app',
    data: {
        seasons: [],
        currentSeason: null,
        users: [],
        tableEntries: [],
        currChampion: "",

        user1: "",
        user2: "",
        user1Score: 10,
        user2Score: 10,      
        
        newUsername: ""
    },
    created: function () {
        this.loadSeasons();
        this.loadCurrentSeasons();
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
        loadCurrentSeasons: function(){            
            fetch('/scoreboard/season/current')
            .then(response => response.json())
            .then(season => {
                this.currentSeason = season.value.name;
                this.loadTable();
            });                
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

            fetch('/scoreboard/season/current/game?user1=' + this.user1 + '&user2=' + this.user2 + '&score1=' + this.user1Score + '&score2=' + this.user2Score, options)
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
        loadTable: function(){        
            if(this.currentSeason === ""){
                return;
            }
            fetch('/scoreboard/season/' + this.currentSeason + '/table')
            .then(response => response.json())
            .then(table => {
                this.tableEntries = table;
                
                if(this.currChampion !== undefined && this.currChampion !== "" && table[0].name != this.currChampion){
                    this.celebrateNewChampion(table[0].name);
                }
                this.currChampion = table[0].name;
            });
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
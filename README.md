# quarkus-scoreboard

Hobby project to evaluate technologies.

### Features

- Foosball Tracking
  - Seasons, with support for Solo- or Teamplay
  - Winrate Ranking for Players with at least 3 Games
  - Highscores (Most Goals, Most Games)
- Boardgame Tracking
  - Supports most types of games
  - Mark Winners and/or Losers, define the rules yourself!
  - Dynamic Real-Time Ranking and Filtering
- Dark & Light Mode
- Data Storage as .csv
- E-Mail Backup

### Thanks:
- Quarkus (https://quarkus.io)
- SmallRye (https://smallrye.io)
- Vue (https://vuejs.org)
- Notie (https://github.com/jaredreich/notie)
- Bulma (https://bulma.io)
- canvas-confetti (https://github.com/catdad/canvas-confetti)
- Font Awesome (https://fontawesome.com)

Use this command to deploy the Docker Container:
```shell
docker run -d -p 8080:8091 --name quarkus-scoreboard ghcr.io/mbirkenkamp/quarkus-scoreboard:latest
```

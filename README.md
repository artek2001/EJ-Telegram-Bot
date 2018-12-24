![Ej bot Logo](https://i.imgur.com/dYftizx.png)

# EJ(Electronic Journal) Telegram Bot
#### Saying shortly, this bot helps students of Grodno State Medical University to obtain information from electronic journal on the website ej.grsmu.by

This bot uses:
* [telegrambots-api](https://github.com/rubenlagus/TelegramBots/) java library
* java SE 9
* jdbc MySql driver
* HikariCP for connection pooling
* Apache HttpComponents for making http requests
* Jaunt library for parsing html web pages
* Hibernate for ORM representation
* Google cloud sql as Database

Abilites of the bot:
- [x] Login via `/start login password` command
- [x] Get all marks via `/allmarks` command
- [x] Logout from the bot via `logout`
- [x] Using General Keyboard and Inline Keyboard
- [ ] Making 'Cache' feature to optimize sql queries

[Link](https://t.me/ejgrsmu_bot) to the bot

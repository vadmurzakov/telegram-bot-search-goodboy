Телеграм бот, который каждый день по запросу ищет в чате Пидора-Дня и Красавчика-Дня. Просто баловство, не более того

Для того, чтобы развернуть локально:
```
docker run -d --name pdr-search -e TOKEN='токен-бота-из-телеги' -e APPLICATION_PORT=8088 -p 8088:8088 imvad/pdr-search:1.0.0
```

пример реализация бота в телеграме: @vmPidarBot
чтобы запустить бота, необходимо добавить его в чат и зарегистрировать в игре через команду бота /reg (необходимо минимум два человека)

FROM openjdk:17-alpine3.14

ENV APPLICATION_PORT=8040
ENV TOKEN=TOKEN

COPY target/telegram-bot-search-goodboy-*.jar /opt/app/app.jar

EXPOSE $APPLICATION_PORT

CMD ["java", "-Xmx128m", "-jar", "/opt/app/app.jar", "--server.port=${APPLICATION_PORT}", "--telegram.token=${TOKEN}"]
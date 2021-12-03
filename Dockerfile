FROM adoptopenjdk/openjdk11:jre-11.0.11_9-alpine

ENV APPLICATION_PORT=8040
ENV TOKEN=TOKEN

COPY target/pidar-bot-*.jar /opt/app/app.jar

EXPOSE $APPLICATION_PORT

CMD ["java", "-jar", "/opt/app/app.jar", "--server.port=${APPLICATION_PORT}", "--telegram.token=${TOKEN}"]
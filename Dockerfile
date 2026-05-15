FROM eclipse-temurin:21-jre
WORKDIR /app
COPY target/BattleshipGamePlayer-2.0.jar app.jar
ENV JAVA_TOOL_OPTIONS="-Djava.awt.headless=true"
ENTRYPOINT ["java", "-Djava.awt.headless=true", "-Dlog4j2.configurationFile=log4j2.xml", "-cp", "app.jar", "battleship.Main"]

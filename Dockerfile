FROM eclipse-temurin:21-jre
WORKDIR /app
COPY target/BattleshipGamePlayer-2.0.jar app.jar
ENTRYPOINT ["java", "-Djava.awt.headless=true", "-jar", "app.jar"]

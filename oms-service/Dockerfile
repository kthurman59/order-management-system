# build stage
FROM eclipse-temurin:21-jdk AS build

WORKDIR /app
COPY pom.xml mvnw ./
COPY .mvn .mvn
RUN ./mvnw -q -B dependency:go-offline

COPY src src
RUN ./mvnw -q -B package -DskipTests

# runtime stage
FROM eclipse-temurin:21-jre

WORKDIR /app
COPY --from=build /app/target/oms-service-0.0.1-SNAPSHOT.jar app.jar

ENV JAVA_OPTS=""
ENTRYPOINT ["sh","-c","java $JAVA_OPTS -jar app.jar"]

# ----- Build -----
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app

# Cache deps
COPY pom.xml .
RUN mvn -q -DskipTests dependency:go-offline

# Build
COPY src ./src
RUN mvn -q -DskipTests clean package
# IMPORTANT: copy the Spring Boot fat jar (NOT the -jar-with-dependencies.jar)
# The Boot repackage output is the plain *-SNAPSHOT.jar
# The *.original is the thin jar; don't use it either.

# ----- Run -----
FROM eclipse-temurin:17-jre
WORKDIR /app
COPY --from=build /app/target/*SNAPSHOT.jar /app/app.jar
ENV PORT=8080
EXPOSE 8080
ENTRYPOINT ["java","-jar","/app/app.jar"]

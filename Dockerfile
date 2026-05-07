# syntax=docker/dockerfile:1

FROM eclipse-temurin:17-jdk-jammy AS builder
WORKDIR /app

COPY gradlew gradlew.bat settings.gradle build.gradle gradle.properties ./
COPY gradle ./gradle
COPY src ./src

RUN chmod +x gradlew \
	&& ./gradlew bootJar --no-daemon \
	&& cp "$(ls build/libs/*-SNAPSHOT.jar | grep -v plain)" application.jar

FROM eclipse-temurin:17-jre-jammy
WORKDIR /app

RUN groupadd --system spring && useradd --system spring --gid spring

COPY --from=builder /app/application.jar app.jar
RUN chown spring:spring app.jar

USER spring:spring
EXPOSE 8080

ENV JAVA_OPTS=""
ENTRYPOINT ["sh", "-c", "exec java $JAVA_OPTS -jar app.jar"]

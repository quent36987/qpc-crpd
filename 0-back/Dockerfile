FROM openjdk:21-slim

WORKDIR /app

COPY build/libs/*.jar /app/app.jar

ENTRYPOINT ["java","-jar","/app/app.jar"]

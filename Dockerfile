FROM openjdk:19

WORKDIR /app
ARG JAR=taskmanager-0.0.1-SNAPSHOT.jar
COPY target/$JAR /app/taskmanager.jar

EXPOSE 8080

ENTRYPOINT ["java", "-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:5005", "-jar", "taskmanager.jar"]
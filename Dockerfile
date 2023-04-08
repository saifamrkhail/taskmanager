FROM openjdk:19

WORKDIR /app
COPY target/taskmanager-*.jar /app/taskmanager.jar
EXPOSE 8080

ENTRYPOINT ["java", "-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:5005", "-jar", "taskmanager.jar"]
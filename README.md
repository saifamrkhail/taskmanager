# Taskmanager

The application simulates a simple Task Management System. Incoming tasks are stored in a DB.
The application is a REST API that allows to create, update, delete and list tasks.


## Docker
Build the Docker image for your application by running the following command in the root directory of your project:

```bash
docker build -t taskmanager .
docker run -p 8080:8080 taskmanager
```

This will create a Docker image called my_app based on the Dockerfile in the root directory of your project.
Run the Docker Compose file by running the following command in the root directory of your project:

```bash
docker-compose up -d
```

This will start both the app and db services and connect them together.
Access the application by navigating to http://localhost:8080 in your web browser. You should see the home page of your application.
That's it! You now have a fully functioning Spring Boot application with a PostgreSQL database running in Docker Compose.

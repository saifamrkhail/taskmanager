# Taskmanagement System

The application simulates a simple Task Management System. Incoming tasks are stored in a DB.
The application is a REST API that allows to create, update, delete and list tasks.


## Docker-Compose
Build and run docker-compose for Taskmanagement System by running the following commands in the root directory of your project:

```bash
docker-compose build
docker-compose up
```

This will start both the app and db services and connect them together.
Access the application by navigating to http://localhost:8080/api/v1/tasks in your web browser. You should see a list of tasks.
That's it! You now have a fully functioning Spring Boot application with a PostgreSQL database running in Docker Compose.

You can also use Postman to test the application.

## Stop the application
If you started docker-compose in detach mode and you want to stop the application, you can do so by running the following command in the root directory of your project:
```bash
docker-compose down
```

## PostgreSQL
Connect to the PostgreSQL database by running the following command in the root directory of your project:
```bash
docker-compose exec db psql -U task
```
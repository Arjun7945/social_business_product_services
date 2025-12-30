# Deployment Guide

## Prerequisites
*   Java 17+
*   Node.js (LTS)
*   Docker & Docker Compose

## Build

1.  **Clone the repository**:
    ```bash
    git clone <repository_url>
    cd <project_directory>
    ```

2.  **Package the application** (Production optimized):
    ```bash
    ./mvnw -Pprod clean verify
    ```
    This creates an executable JAR file in `target/`.

## Running with Docker (Recommended)

1.  **Start dependencies** (PostgreSQL, etc.):
    ```bash
    docker compose -f src/main/docker/services.yml up -d
    ```

2.  **Build Docker Image**:
    ```bash
    ./mvnw -Pprod verify jib:dockerBuild
    ```

3.  **Run Application Container**:
    ```bash
    docker compose -f src/main/docker/app.yml up -d
    ```

## Manual Execution (JAR)

1.  Ensure database is running.
2.  Run the JAR:
    ```bash
    java -jar target/*.jar --spring.profiles.active=prod
    ```

## Environment Variables

Ensure these variables are set in your environment or `application-prod.yml` overrides:
*   `SPRING_DATASOURCE_URL`
*   `SPRING_DATASOURCE_USERNAME`
*   `SPRING_DATASOURCE_PASSWORD`
*   `WHATSAPP_TOKEN`
*   `WHATSAPP_PHONE_NUMBER_ID`

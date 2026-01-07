# How to Run the Application

This guide provides step-by-step instructions on how to set up and run the application, including the Central License Server and the main Social Business Product Services application.

## 1. Prerequisites

Ensure you have the following installed on your system:

*   **Java Development Kit (JDK) 17**: Required for the backend.
*   **Node.js**: Version 22.12.0 or higher is recommended (based on `package.json` engines).
*   **Git**: For cloning the repository.
*   **Docker** (Optional but recommended): For running database services easily.

## 2. Cloning the Repository

Clone the project to your local machine:

```bash
git clone <repository-url>
git checkout reactClient/whatsappProductServicePro
git pull 
cd social_business_product_services
```

## 3. Running the Central License Server

The application requires a license server to be running to verify keys.

1.  Navigate to the license server directory:
    ```bash
    cd central-license-server
    ```

2.  Start the server:
    ```bash
    node server.js
    ```

    You should see output indicating the server is running, typically on **port 3000**.

    > **Note:** The valid licenses are stored in `licenses.json`. Ensure this file exists in the directory.

## 4. Running the Main Application (JHipster)

The main application is located in the `jhipster_social_business` directory.

1.  Open a new terminal and navigate to the application directory:
    ```bash
    cd jhipster_social_business
    ```

2.  **Option A: Run with Maven (Backend + Frontend bundled)**
    This will start the Spring Boot application and automatically build the frontend.
    
    **On Windows:**
    ```powershell
    mvn clean package "-Dmodernizer.skip=true"
    ```
    then run the script to run the application:
    ```powershell
    .\run_local.ps1
    ```

## 5. Accessing the Application

Once everything is up and running:

*   **Main Application**: [http://localhost:8080](http://localhost:8080) (or `http://localhost:8081` depending on configuration).
*   **License Server**: [http://localhost:3000](http://localhost:3000)
*   **Swagger API Docs**: [http://localhost:8081/swagger-ui/index.html](http://localhost:8081/swagger-ui/index.html)

## 6. Troubleshooting

*   **Database**: If the local database is not running, you can start it using Docker from the `jhipster_social_business` folder:
    ```bash
    npm run services:up
    ```
*   **Port Conflicts**: Ensure ports `8080/8081` (App) and `3000` (License Server) are free.

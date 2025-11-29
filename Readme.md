# 🚀 Rencanakan TalentPool API

A Spring Boot (Java 21) backend service for the Rencanakan TalentPool platform, packaged and deployed with Docker.

---

## 🔧 Requirements
- **Java**: 21  
- **Gradle**: Included via Wrapper  
- **Docker**: Installed and running  
- **PostgreSQL**: Local or remote instance  
- **.env file**: Required—contains DB credentials and JWT config  

---

## ▶️ How to Run Locally

### 1. Build the JAR
```bash
./gradlew clean bootJar
```

### 2. Build the JAR
```bash
docker build -t rencanakan-talentpool-api .
```

### 3. Build the JAR
Expose the API on localhost:8081:
```bash
docker run --rm -p 8081:8081 \
  --env-file .env \
  rencanakan-talentpool-api
```

---

## 📄 Environment Variables
Create a .env file in the project root:
```bash
PG_HOST=jdbc:postgresql://host.docker.internal:5432/<your_database_name>
PG_USER=<your_db_user>
PG_PASS=<your_db_password>

RESET_PW_BASE_URL=http://localhost:3000/reset-password
PRODUCTION=false
```

---

## 📘 API Documentation (Swagger UI)
Once the server is running, open:
👉 http://localhost:8081/swagger-ui/index.html
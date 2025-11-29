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

### 2. Build the Docker Image
```bash
docker build -t rencanakan-talentpool-api .
```

### 3. Update .env for Docker Compose Deployment
On the server, your .env might look like:
```bash
PG_DB=your-database-name
PG_USER=your-database-username
PG_PASS=your-database-password

RESET_PW_BASE_URL=https://your-frontend-domain/reset-password
JWT_KEY=your-very-secret-jwt-key
PRODUCTION=true
```

### 4. Run on the server
Expose the API:
```bash
docker compose up -d
```

---

## 📘 API Documentation (Swagger UI)
Once the server is running, open (dev only):
👉 http://localhost:8081/swagger-ui/index.html
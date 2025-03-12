FROM gradle:jdk21-alpine
# ARG PRODUCTION
# ARG JDBC_DATABASE_PASSWORD
# ARG JDBC_DATABASE_URL
# ARG JDBC_DATABASE_USERNAME


# ENV PRODUCTION ${PRODUCTION}
# ENV JDBC_DATABASE_PASSWORD ${JDBC_DATABASE_PASSWORD}
# ENV JDBC_DATABASE_URL ${JDBC_DATABASE_URL}
# ENV JDBC_DATABASE_USERNAME ${JDBC_DATABASE_USERNAME}
RUN apk update && apk upgrade binutils


RUN adduser -D -g '' nonRootUser

WORKDIR /app

USER nonRootUser


# 4. Copy file JAR ke dalam container
COPY ./talentpool-0.0.1-SNAPSHOT.jar /app

# 5. Buka port 8080
EXPOSE 8080

# 6. Jalankan aplikasi
CMD ["java", "-jar", "talentpool-0.0.1-SNAPSHOT.jar"]


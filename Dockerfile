FROM eclipse-temurin:17-jdk AS builder

WORKDIR /app

COPY . .

RUN chmod +x gradlew \
    && ./gradlew clean bootJar --no-daemon \
    && cp "$(find build/libs -maxdepth 1 -type f -name '*.jar' ! -name '*-plain.jar' | head -n 1)" app.jar

FROM eclipse-temurin:17-jre

WORKDIR /app

COPY --from=builder /app/app.jar app.jar

EXPOSE 8082

ENTRYPOINT ["java", "-jar", "app.jar"]
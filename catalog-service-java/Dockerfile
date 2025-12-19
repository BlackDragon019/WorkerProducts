# Build stage
FROM maven:3.9.5-eclipse-temurin-21 AS builder
WORKDIR /workspace
COPY . .
# Build the worker module
RUN mvn -f worker/pom.xml -B -DskipTests package

# Run stage
FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=builder /workspace/worker/target/*-SNAPSHOT.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","/app/app.jar"]
# Step 1: Build the application
FROM maven:3.8.5-openjdk-17 AS build
COPY . .
RUN mvn clean package -DskipTests

# Step 2: Run the application
FROM openjdk:17-jdk-slim
WORKDIR /app
COPY --from=build /target/*.jar app.jar

# Render එකේ port එකට ගැලපෙන ලෙස port 8080 expose කිරීම
EXPOSE 8080

# RAM එක 512MB නිසා Java memory සීමා කිරීම (Render Free Tier සඳහා ඉතා වැදගත්)
ENTRYPOINT ["java", "-Xmx380m", "-Xms256m", "-jar", "app.jar"]
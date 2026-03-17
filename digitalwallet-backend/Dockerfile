# 1. Aşama: Build
FROM maven:3.9.12-eclipse-temurin-21 AS build
WORKDIR /app
# Önce kütüphaneleri çekiyoruz (hızlı build için)
COPY pom.xml .
RUN mvn dependency:go-offline
# Sonra kodu kopyalayıp paketliyoruz
COPY src ./src
RUN mvn clean package -DskipTests

# 2. Aşama: Run
FROM eclipse-temurin:21-jre-jammy
WORKDIR /app
# Sadece lazım olan .jar dosyasını önceki aşamadan alıyoruz
COPY --from=build /app/target/*.jar app.jar
EXPOSE 8081
ENTRYPOINT ["java", "-jar", "app.jar"]

# ============================
# Stage 1: Build da aplicação
# ============================
FROM maven:3.9-eclipse-temurin-17 AS build

WORKDIR /app

# Copia primeiro o pom.xml para aproveitar cache de dependências
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copia o restante do código-fonte
COPY src ./src

# Compila e empacota a aplicação (sem executar os testes)
RUN mvn clean package -DskipTests -B

# ============================
# Stage 2: Imagem de execução
# ============================
FROM eclipse-temurin:17-jre-alpine

WORKDIR /app

# Copia apenas o jar gerado no stage de build
COPY --from=build /app/target/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]

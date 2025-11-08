# 1️⃣ Build Stage
FROM gradle:8.5-jdk17 AS builder
WORKDIR /app
COPY . .
RUN gradle clean bootJar -x test

# 2️⃣ Runtime Stage
FROM eclipse-temurin:17-jdk
WORKDIR /app
RUN mkdir -p /logs && chmod 777 /logs

COPY --from=builder /app/build/libs/*.jar app.jar

EXPOSE 8080

# 표준 출력/에러 로그 분리 저장
ENTRYPOINT ["sh", "-c", "java -jar /app/app.jar > /logs/app.out.log 2> /logs/app.err.log"]
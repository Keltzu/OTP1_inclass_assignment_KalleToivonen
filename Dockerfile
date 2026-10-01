FROM maven:3.9-eclipse-temurin-17

RUN apt-get update && apt-get install -y \
    libgtk-3-0 \
    libxtst6 \
    libxrender1 \
    libxi6 \
    libgl1 \
    libasound2t64 \
    && rm -rf /var/lib/apt/lists/*

WORKDIR /app
COPY . .
RUN mvn clean package -DskipTests

ENV DB_HOST=host.docker.internal

CMD ["mvn", "javafx:run"]
# Используем официальный образ Amazon Corretto 17
FROM maven:3.9.12-amazoncorretto-17

# Устанавливаем рабочую директорию внутри контейнера
WORKDIR /app

# Копируем файлы сборки (pom.xml и src) в рабочую директорию
COPY pom.xml .
COPY src ./src

# Собираем проект с помощью Maven
RUN mvn clean package -DskipTests

# Определяем команду для запуска приложения
CMD ["java", "-jar", "target/audio-extractor-backend.jar"]

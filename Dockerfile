FROM eclipse-temurin:21-jdk
WORKDIR /app
COPY /target/emotional-0.0.1-SNAPSHOT.jar emotional-api.jar
ENV MONGO_DB $MONGO_DB
ENV MONGO_HOST $MONGO_HOST
ENV MONGO_PASS $MONGO_PASS
ENV MONGO_PORT $MONGO_PORT
ENV MONGO_USER $MONGO_USER
ENV OPENAI_APIKEY $OPENAI_APIKEY
ENV OPENAI_MODEL $OPENAI_MODEL
EXPOSE 8080
LABEL authors = "luis.bolivar"
CMD ["java", "-jar", "emotional-api.jar"]
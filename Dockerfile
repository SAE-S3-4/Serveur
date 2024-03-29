FROM ubuntu:latest

WORKDIR /usr/src/app

COPY ["filesForTerminal/trame.txt", "filesForTerminal/tramessh.txt", "target/ServerSAES4-1.0-SNAPSHOT-jar-with-dependencies.jar", "myKeyStore.jks", "DockerfileTerminal","./"]

RUN apt-get update && apt-get install -y docker.io && apt install openjdk-17-jdk openjdk-17-jre -y

CMD docker build --file DockerfileTerminal -t terminal:latest .;java -jar ServerSAES4-1.0-SNAPSHOT-jar-with-dependencies.jar

EXPOSE 10013


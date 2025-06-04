FROM eclipse-temurin:17-jre

ARG ARG_JAVA_OPTS="-Xmx250m"

ENV JAVA_OPTS=$ARG_JAVA_OPTS

RUN mkdir /app

COPY lib /app/lib
COPY ${project.artifactId}-${project.version}.jar ${project.artifactId}-${project.version}-runner.jar /app/

WORKDIR /app

CMD ["sh", "-c", "java ${JAVA_OPTS} -jar /app/${project.artifactId}-${project.version}-runner.jar"]

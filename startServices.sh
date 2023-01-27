#!/bin/sh

PROJECT_VERSION=$(mvn help:evaluate -Dexpression=project.version -q -DforceStdout)
PROJECT_NAME=$(mvn help:evaluate -Dexpression=project.artifactId -q -DforceStdout)

echo "Project name: ${PROJECT_NAME}"
echo "Project version: ${PROJECT_VERSION}"

JAR_FILE="./target/${PROJECT_NAME}-${PROJECT_VERSION}.jar"

if [ "$JAR_FILE" ]
  then
    echo "$JAR_FILE exist."
  else
    echo "$JAR_FILE does not exist. Have you compiled the project? 'mvn clean package'"
    exit 1
fi

docker-compose -p goodboys -f ./docker-compose.yml up -d
# URL Shortener Server

## Description

This is the server: it gets command from RabbitMQ queue, operates business logic and persistence, then responds on a RabbitMQ topic

## Getting Started

These instructions will get you a copy of the project up and running on your local machine for development and testing purposes.

### Prerequisites

#### Build time

* [Adoptium JDK17](https://adoptium.net/temurin/releases/)
* [Maven 3.6.3](https://maven.apache.org/download.cgi)

#### Runtime dependencies

External dependencies needed to run the application are:
* RabbitMQ 3.12.4 or latest
* Redis 7.2.0

### Build

Starting from project root folder run the following command:

```shell script
mvn clean package
```

### Docker Image

After packaging the application, you can build the Docker image

Starting from folder `./target` run the following command:

```shell script
docker build -t url-shortener-server -f Dockerfile .
```

### Run

#### Local run

You can run your application in dev mode that enables live coding.
Starting from project root folder run the following command:

```shell script
mvn quarkus:dev
```
In order to debug the application you can simply use a remote java debugger connected to port 5005

#### Docker

In order to run an image previously built, you can simply run the application as follows:

```shell script
docker run --rm --name url-shortener-server -p 8081:8081 url-shortener-server
```

#### Configuration files
Application configuration is:

| Key                                      | Default Value | Note                                                      |
|------------------------------------------|---------------|-----------------------------------------------------------|
| quarkus.http.port                        | 8080          | The default listening port                                |
| quarkus.log.category."com.trimbit".level | INFO          | The application log level                                 |
| queue.host                               | localhost     | RabbitMQ host                                             |
| queue.port                               | 5672          | RabbitMQ port                                             |
| queue.user                               | guest         | RabbitMQ user                                             |
| queue.password                           | guest         | RabbitMQ password                                         |
| queue.vhost                              | /             | RabbitMQ virtual host                                     |
| queue.receive                            | aqueue        | The RabbitMQ queue from where to receive command messages |
| queue.topic                              | amq.topic     | The RabbitMQ topic exchange where to send responses       |
| redis.host                               | localhost     | Redis host                                                |
| redis.port                               | 6379          | Redis port                                                |

### Testing

Tests are done using JUnit and Mockito.

#### Unit Test

In order to run unit tests you can execute the following command in the project root:

```shell script
mvn test
```

## Built With

* [Maven](https://maven.apache.org/) - Dependency Management
* [Docker](https://www.docker.com/) - Enterprise Container Platform

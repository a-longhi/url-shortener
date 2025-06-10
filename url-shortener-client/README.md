# URL Shortener Client

## Description

This is the client: it exposes REST resources, asks for operations enqueuing objects in RabbitMQ and reads responses through the topic.

## Endpoints

### Insertion
- Gets a long URL, creates short URL from the system and persists the association:
  - Request:
    - Kind: PUT
    - path:
      - `/v1/urls`
    - body:
      - Json payload containing fields "url" and "userEmail"
        - example:
          -     {
                  "url": "https://acme.com/welcome?a=1&b=2",
                  "userEmail": "fancy@email.com"
                }
  - Response:
    - Response code: 200
    - Body: a random string composed by 6 random lower case letters and numbers representing the shortened URL

### Query
- Gets a short URL, creates short URL from the system and persists the association:
  - Request:
    - Kind: POST
    - path:
      - `/v1/urls/query`
    - body:
      - Json payload containing fields "url" and "userEmail"
        - example:
          -     {
                  "url": "abc123",
                  "userEmail": "fancy@email.com"
                }
  - Response:
    - Response code: 200
    - Body: the long URL associated with the requested short URL

### Statistics
- Gets the email of the user and returns: the number of insertions made; the number of times the short URLs have been asked.
  - Request:
    - Kind: POST
    - path:
      - `/v1/urls/stats`
    - body:
      - Json payload containing fields "userEmail"
        - example:
          -     {
                  "userEmail": "fancy@email.com"
                }
  - Response:
    - Response code: 200
    - Body: the JSON representation of the stats:
      - example:
        -     {
                "insertions": 5,
                "shortUrlCount": {
                  "abc123": 0,
                  "123abc": 1,
                  "1a2b3c": 2
                }
              }

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
docker build -t url-shortener-client -f Dockerfile .
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
docker run --rm --name url-shortener-client -p 8080:8080 url-shortener-client
```

#### Usage
You can use [Postman Collection](postman_collection.json) and [Postman Environment](postman_environment.json) for testing the service.

#### Configuration files
Application configuration is:

| Key                                      | Default Value | Note                                                     |
|------------------------------------------|---------------|----------------------------------------------------------|
| quarkus.http.port                        | 8080          | The default listening port                               |
| quarkus.log.category."com.trimbit".level | INFO          | The application log level                                |
| queue.host                               | localhost     | RabbitMQ host                                            |
| queue.port                               | 5672          | RabbitMQ port                                            |
| queue.user                               | guest         | RabbitMQ user                                            |
| queue.password                           | guest         | RabbitMQ password                                        |
| queue.vhost                              | /             | RabbitMQ virtual host                                    |
| queue.send                               | aqueue        | The RabbitMQ queue where to send command messages        |
| queue.topic                              | amq.topic     | The RabbitMQ topic exchange from which receive responses |

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

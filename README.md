# URL Shortener Suite

## Overview

Multi-user URL shortener system, composed of two main modules:

- **url-shortener-client**: Exposes REST APIs for users to shorten URLs, retrieve original URLs, and view usage statistics. Communicates with the server via RabbitMQ.
- **url-shortener-server**: Handles business logic, persistence, and communication with external systems (RabbitMQ, Redis). Processes requests from the client and manages URL mappings.

## Repository Structure

- `url-shortener-client`: REST API client for user interaction.
- `url-shortener-server`: Backend server for business logic and persistence.
- `url-shortener-model`: Common data model shared between client and server.
- `docker-compose.yml`: Orchestrates local development environment with RabbitMQ and Redis.

## Prerequisites

- [Adoptium JDK 17](https://adoptium.net/temurin/releases/)
- [Maven 3.6.3+](https://maven.apache.org/download.cgi)
- [Docker](https://www.docker.com/) (for running services locally)
- RabbitMQ 3.12.4+ and Redis 7.2.0+ (can be run via Docker Compose)

## Getting Started

1. **Clone the repository** and navigate to the project root.
2. **Build all modules**:
   ```sh
    mvn clean package
   ```
3. **Start services** using Docker Compose:
   ```sh
   docker-compose up -d
   ```

## Further Information

- See [Client Readme](url-shortener-client/README.md) for API details and usage examples.
- See [Server Readme](url-shortener-server/README.md) for deployment and advanced configuration.

## Contributing

Please read the [Definition of Done](missing) for details on our code of conduct, and the process for submitting pull requests.

## Versioning

This project uses [SemVer](http://semver.org/) for versioning.

## Authors

* [Andrea Longhi](https://github.com/a-longhi)

## License
CC BY-NC-ND

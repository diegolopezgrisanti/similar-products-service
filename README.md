# Similar Products Service

## Overview

This API allows you to retrieve a list of similar products based on a given product ID.

## Prerequisites

Ensure you have the following installed before starting:

- **Java** (JDK 21 or higher)  
  The application is built using Java, so you need to have JDK 21 or higher installed.

- **Maven** (for building and running the project)  
  Maven is used to manage project dependencies, build, and run the application.

## Local Environment Setup

### 1. Running the Service

To run the service locally:

1. Clone the repository and navigate to the project directory.
2. Start the service with the following command:

```bash
./mvnw spring-boot:run
```

Alternatively, you can use the green run button in your IDE for the `SimilarProductsServiceApplication` (main) class.

The service will be available at http://localhost:5000/

### 2. Running Tests

The project includes unit tests to ensure the accuracy of business logic. You can run all tests using Maven.

To run the tests:

```bash
./mvnw test
```
Alternatively, if Maven is installed locally, you can use:

```bash
mvn test
```
The test suite verifies key functionalities of the API, including retrieving similar products based on the product ID.

## Endpoints

### 1. Get Similar Products
- **Endpoint**: `GET /product/{productId}/similar`

This endpoint retrieves a list of similar products based on the provided product ID.

#### Example Request:
```http
GET /product/1/similar
```

#### Response Format
The response will return a list of similar products, including product ID, name, price, and availability.

#### Example Response:
```json
[
  {
    "id": "2",
    "name": "Dress",
    "price": 19.99,
    "availability": true
  },
  {
    "id": "3",
    "name": "Blazer",
    "price": 29.99,
    "availability": false
  },
  {
    "id": "4",
    "name": "Boots",
    "price": 39.99,
    "availability": true
  }
]
```
### Error Handling
In case of errors, the API will return a JSON object with an error message.

#### Example Error Response:
```json
{
  "message": "No similar products found for productId: 1234"
}
```
### Retry Logic

The `SimilarProductsClient` implements a retry mechanism using **Resilience4j** to handle failed requests. The system will attempt to call the external service up to **3 times** (this can be configured in the `application.yml` file). If the request fails due to certain exceptions (like `IOException` or `HttpServerErrorException`), the system will automatically retry up to the configured maximum attempts.

#### Retry Behavior
1. **First Attempt**: The request is made to the service.
2. **Second Attempt**: If the first attempt fails (with the specified retryable exceptions), a second attempt is made.
3. **Third Attempt**: If the second attempt also fails, a third and final attempt is made.

If all attempts fail, the system will return an empty result, ensuring no infinite retries.

#### Configurable Retry Parameters
- **Max Attempts**: The maximum number of retry attempts (default is 3).
- **Wait Duration**: The time to wait between each retry attempt (default is 2 seconds).


### Swagger API Documentation
The API is documented using SpringDoc OpenAPI. Once the application is running, you can access the Swagger UI to interact with the endpoints.

#### Access Swagger UI:
- URL: [http://localhost:5000/swagger-ui/index.html](http://localhost:5000/swagger-ui/index.html)

#### OpenAPI JSON:
- URL: [http://localhost:5000/v3/api-docs](http://localhost:5000/v3/api-docs)

## Key Dependencies

- **Spring Boot Starter Web**: Used to build web applications, including RESTful APIs.
- **Spring Boot DevTools**: Enables automatic application restart for faster development.
- **Spring Boot Starter Validation**: Provides support for validation annotations, used for validating input data.
- **Project Lombok**: Provides annotations to reduce boilerplate code like getters, setters, and constructors.
- **SpringDoc OpenAPI**: Provides integration with OpenAPI for auto-generating API documentation and Swagger UI.
- **Spring Boot Starter Test**: Used for testing the application, including unit and integration tests.
- **JUnit 5**: A popular testing framework used for writing tests in Java.
- **Mockito Core**: A framework used for mocking objects in tests.
- **WireMock**: Provides a standalone mock server for simulating external HTTP services during tests.
- **Resilience4j**: A library used for implementing retries, circuit breakers, and rate limiters in microservices and distributed systems.

## Additional Documentation

For more information, refer to the following resources:

- [Spring Boot Documentation](https://spring.io/projects/spring-boot)
- [SpringDoc OpenAPI Documentation](https://springdoc.org/)
- [WireMock Documentation](https://wiremock.org/docs/)
- [JUnit Documentation](https://junit.org/junit5/docs/current/user-guide/)
- [Resilience4j Retry Documentation](https://resilience4j.readme.io/docs/retry)

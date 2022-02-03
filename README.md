# Spring boot example with REST and spring data JPA
See [micronaut-rest-example](https://github.com/pavelfomin/micronaut-rest-example) for `Micronaut` implementation.

### Running tests
* Maven: `./mvnw clean test`
* Gradle: `./gradlew clean test`

### Endpoints

| Method | Url | Decription |
| ------ | --- | ---------- |
| GET    |/actuator/info  | info / heartbeat - provided by boot |
| GET    |/actuator/health| application health - provided by boot |
| GET    |/v2/api-docs    | swagger json |
| GET    |/swagger-ui.html| swagger html |
| GET    |/v1/person/{id}| get person by id |
| GET    |/v1/persons    | get N persons with an offset|
| PUT    |/v1/person     | add / update person|

### Change maven version
`mvn -N io.takari:maven:wrapper -Dmaven=3.8.4`
# Spring boot example with REST and spring data JPA


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

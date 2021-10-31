# Springboot - User CRUD MicroService

## Endpoints

- CREATE USER `POST /api/v1/users`
    ```json
      {
        "name": "sample",
        "email": "sample@test.com",
        "phoneNumber": "+90xxxxxxxx"
      }

    ```

- GET USERS  `GET /api/v1/users`
- GET USER `GET /api/v1/users/{id}`
- UPDATE USER `PUT /api/v1/users/{id}`
    ```json
       {
         "name": "sample",
         "age": 20,
         "phoneNumber": "+90xxxxxxxx"
       }
    ```
- DELETE USER `DELETE /api/v1/users/{id}`



## Database and Migration
  - PostgreSQL
  - H2
  - Liquibase


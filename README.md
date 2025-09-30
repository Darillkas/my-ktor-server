Сервер на Ktor с автоматической документацией Swagger, тестами и логированием.

Как запустить 

1.  Копируйте репозиторий:
    ```bash
    git clone <repository-url>
    cd my-ktor-server
    ```

2.  открыть проект в IntelliJ IDEA.

3.  подождать автоматической загрузки

4.  запустить приложение
    shift + f10

Документация API:
Swagger UI: http://localhost:8080/swagger

OpenAPI спецификация: http://localhost:8080/openapi/documentation.yaml

    

- `GET /users` - получить всех пользователей
- `GET /users/{id}` - получить пользователя по id 
- `POST /users` - создать пользователя (`{"name": "...", "email": "..."}`)
- `DELETE /users/{id}` - удалить пользователя по id
- `GET /search?name=...` - найти пользователя
-  'GET'	/health	Проверка сервера

Проверка 

Используйте Postman

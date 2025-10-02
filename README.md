Полнофункциональный REST API сервер на Ktor с аутентификацией, базой данных и real-time чатом

Как запустить 

1.  Копируйте репозиторий:
    ```bash
    git clone <your-repository-url>
    cd my-ktor-server
    ```

2.  открыть проект в IntelliJ IDEA.

3.  подождать автоматической загрузки

4.  запустить приложение
    shift + f10

Документация API:
Swagger UI: http://localhost:8080/swagger

OpenAPI спецификация: http://localhost:8080/openapi/documentation.yaml


POST /register	Регистрация нового пользователя
POST /login	Вход и получение JWT токена

GET	/users	Получить всех пользователей
GET	/protected	Защищенный маршрут (требует JWT)

GET	/admin-only	Только для администраторов
WS	/chat	WebSocket соединение для чата

GET	/health	Проверка здоровья сервера 
GET /users - получить всех пользователей

GET /users/{id} - получить пользователя по id 
POST /users - создать пользователя (`{"name": "...", "email": "..."}`)

DELETE /users/{id} - удалить пользователя по id
GET /search?name=... - найти пользователя

GET	/health	Проверка сервера

Доступ к защищенному маршруту:
curl -H "Authorization: Bearer YOUR_JWT_TOKEN" http://localhost:8080/protected

Используйте WebSocket клиент для подключения к ws://localhost:8080/chat

Локальная разработка (Docker):
docker run --name ktor-postgres -e POSTGRES_PASSWORD=password -e POSTGRES_DB=ktor_db -p 5432:5432 -d postgres:15

Проверка 

Используйте Postman
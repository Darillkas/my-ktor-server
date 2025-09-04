Сервер на ктор Простой сервер с get post delete 

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

    

- `GET /users` - получить всех пользователей
- `GET /users/{id}` - получить пользователя по id 
- `POST /users` - создать пользователя (`{"name": "...", "email": "..."}`)
- `DELETE /users/{id}` - удалить пользователя по id
- `GET /search?name=...` - найти пользователя

Проверка 

Используйте Postman
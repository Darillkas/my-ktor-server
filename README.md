Ktor E-Commerce API

REST API сервис для интернет-магазина с аутентификацией, товарами и заказами.

 Технологии

- Ktor - backend фреймворк
- PostgreSQL - база данных
- Exposed - ORM для Kotlin
- JWT - аутентификация
- Redis - кэширование
- RabbitMQ - очереди сообщений
- Swagger - документация API

Функциональность

- Роли: USER и ADMIN
- Управление товарами (только ADMIN)
- Создание и отмена заказов
- История покупок
- Кэширование товаров в Redis
- Очереди для уведомлений
- Аудит действий

Запуск проекта

Вариант 1: Запуск в IntelliJ IDEA

1. Откройте проект в IntelliJ IDEA
2. Дождитесь загрузки Gradle зависимостей
3. Запустите функцию `main()` в файле `Application.kt`
4. Сервер запустится на `http://localhost:8080`

Вариант 2: Запуск через Gradle

```bash

./gradlew run

Swagger: http://localhost:8080/swagger

RabbitMQ UI: http://localhost:15672 (логин: guest, пароль: guest)






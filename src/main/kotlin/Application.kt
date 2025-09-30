package com.example

import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.plugins.openapi.*
import io.ktor.server.plugins.statuspages.*
import io.ktor.server.plugins.swagger.*
import io.ktor.server.plugins.callloging.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.Serializable
import org.slf4j.event.Level


@Serializable
data class User(
    val id: Int? = null,
    val name: String,
    val email: String
)


class UserNotFoundException(message: String) : RuntimeException(message)
class InvalidUserDataException(message: String) : RuntimeException(message)


val userStorage = mutableListOf<User>()

fun Application.module() {

    userStorage.add(User(1, "Alice", "alice@example.com"))
    userStorage.add(User(2, "Bob", "bob@example.com"))
    userStorage.add(User(3, "Charlie", "charlie@example.com"))


    install(ContentNegotiation) {
        json()
    }


    install(CallLogging) {
        level = Level.INFO
    }


    install(StatusPages) {
        exception<UserNotFoundException> { call, cause ->
            call.respond(HttpStatusCode.NotFound, mapOf("error" to cause.message))
        }
        exception<InvalidUserDataException> { call, cause ->
            call.respond(HttpStatusCode.BadRequest, mapOf("error" to cause.message))
        }
        exception<Throwable> { call, cause ->
            call.respond(HttpStatusCode.InternalServerError, mapOf("error" to "Internal server error"))
        }
    }


    routing {

        swaggerUI(path = "swagger", swaggerFile = "openapi/documentation.yaml")


        openAPI(path = "openapi/documentation.yaml", swaggerFile = "openapi/documentation.yaml")


        get("/users") {
            call.respond(userStorage)
        }


        get("/users/{id}") {
            val id = call.parameters["id"]?.toIntOrNull()
                ?: throw InvalidUserDataException("Invalid user ID")

            val user = userStorage.find { it.id == id }
                ?: throw UserNotFoundException("User with id $id not found")

            call.respond(user)
        }


        post("/users") {
            val newUser = call.receive<User>()

            if (newUser.name.isBlank()) {
                throw InvalidUserDataException("User name cannot be empty")
            }
            if (newUser.email.isBlank()) {
                throw InvalidUserDataException("User email cannot be empty")
            }

            val nextId = (userStorage.maxOfOrNull { it.id ?: 0 } ?: 0) + 1
            val userToAdd = newUser.copy(id = nextId)
            userStorage.add(userToAdd)

            call.respond(HttpStatusCode.Created, userToAdd)
        }


        delete("/users/{id}") {
            val id = call.parameters["id"]?.toIntOrNull()
                ?: throw InvalidUserDataException("Invalid user ID")

            val removed = userStorage.removeIf { it.id == id }
            if (!removed) {
                throw UserNotFoundException("User with id $id not found")
            }

            call.respond(HttpStatusCode.NoContent)
        }


        get("/search") {
            val nameQuery = call.request.queryParameters["name"]

            if (nameQuery.isNullOrBlank()) {
                throw InvalidUserDataException("Query parameter 'name' is required")
            }

            val results = userStorage.filter {
                it.name.contains(nameQuery, ignoreCase = true)
            }
            call.respond(results)
        }


        get("/health") {
            call.respond(mapOf("status" to "OK", "service" to "Ktor User API"))
        }
    }
}


fun main() {
    embeddedServer(Netty, port = 8080, module = Application::module).start(wait = true)
}
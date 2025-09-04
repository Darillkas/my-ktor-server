package com.example

import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.Serializable


@Serializable
data class User(
    val id: Int? = null,
    val name: String,
    val email: String
)


val userStorage = mutableListOf<User>()


fun Application.module() {

    userStorage.add(User(1, "Natasha", "natasha@example.com"))
    userStorage.add(User(2, "Bob", "bob@example.com"))

    install(ContentNegotiation) {
        json()
    }
    routing {
        get("/users") {
            call.respond(userStorage)
        }

        get("/users/{id}") {
            val id = call.parameters["id"]?.toIntOrNull()
            val user = userStorage.find { it.id == id }
            if (user == null) {
                call.respond(HttpStatusCode.NotFound, "User not found")
            } else {
                call.respond(user)
            }
        }

        post("/users") {
            try {
                val newUser = call.receive<User>()
                val nextId = (userStorage.maxOfOrNull { it.id ?: 0 } ?: 0) + 1
                val userToAdd = newUser.copy(id = nextId)
                userStorage.add(userToAdd)
                call.respond(HttpStatusCode.Created, userToAdd)
            } catch (e: Exception) {
                call.respond(HttpStatusCode.BadRequest, "Invalid user data")
            }
        }

        delete("/users/{id}") {
            val id = call.parameters["id"]?.toIntOrNull()
            if (id == null) {
                call.respond(HttpStatusCode.BadRequest, "Invalid ID")
                return@delete
            }
            val removed = userStorage.removeIf { it.id == id }
            if (removed) {
                call.respond(HttpStatusCode.NoContent)
            } else {
                call.respond(HttpStatusCode.NotFound, "User not found")
            }
        }

        get("/search") {
            val nameQuery = call.request.queryParameters["name"]
            if (nameQuery.isNullOrBlank()) {
                call.respond(HttpStatusCode.BadRequest, "Query parameter 'name' is required")
                return@get
            }
            val results = userStorage.filter { it.name.contains(nameQuery, ignoreCase = true) }
            call.respond(results)
        }
    }
}


fun main() {

    embeddedServer(Netty, port = 8080, host = "0.0.0.0", module = Application::module)
        .start(wait = true)
}
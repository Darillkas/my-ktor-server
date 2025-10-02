package com.example

import com.example.auth.configureAuth
import com.example.auth.JwtConfig
import com.example.database.DatabaseFactory
import com.example.database.Users
import com.example.models.UserResponse
import com.example.routes.authRoutes
import com.example.routes.chatRoutes
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
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.websocket.*
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import org.slf4j.event.Level

fun main() {
    embeddedServer(Netty, port = System.getenv("PORT")?.toInt() ?: 8080, host = "0.0.0.0") {
        module()
    }.start(wait = true)
}

fun Application.module() {

    DatabaseFactory.init()

    install(ContentNegotiation) {
        json(Json {
            prettyPrint = true
            isLenient = true
        })
    }

    install(CallLogging) {
        level = Level.INFO
    }

    install(StatusPages) {
        exception<Throwable> { call, cause ->
            call.respond(HttpStatusCode.InternalServerError, mapOf("error" to "Internal server error"))
        }
    }

    install(WebSockets) {
        pingPeriod = java.time.Duration.ofSeconds(15)
        timeout = java.time.Duration.ofSeconds(15)
        maxFrameSize = Long.MAX_VALUE
        masking = false
    }


    configureAuth()

    routing {

        swaggerUI(path = "swagger", swaggerFile = "openapi/documentation.yaml")
        openAPI(path = "openapi/documentation.yaml", swaggerFile = "openapi/documentation.yaml")


        authRoutes()


        chatRoutes()

        get("/health") {
            call.respond(mapOf(
                "status" to "OK",
                "service" to "Ktor API with PostgreSQL, JWT & WebSockets",
                "timestamp" to System.currentTimeMillis()
            ))
        }


        get("/users") {
            val users = Users.getAllUsers()
            val userResponses = users.map { user ->
                UserResponse(user.id, user.username, user.email, user.role)
            }
            call.respond(userResponses)
        }




        authenticate("auth-jwt") {
            get("/protected") {
                val principal = call.principal<JWTPrincipal>()
                val username = principal?.getClaim("username", String::class)
                val role = principal?.getClaim("role", String::class)

                call.respond(mapOf(
                    "message" to "Hello, $username!",
                    "role" to role,
                    "protected" to true
                ))
            }



            get("/admin-only") {
                val principal = call.principal<JWTPrincipal>()
                val role = principal?.getClaim("role", String::class)

                if (role != "admin") {
                    call.respond(HttpStatusCode.Forbidden, mapOf("error" to "Admin access required"))
                    return@get
                }

                call.respond(mapOf("message" to "Welcome, admin!"))
            }

        }

    }
}
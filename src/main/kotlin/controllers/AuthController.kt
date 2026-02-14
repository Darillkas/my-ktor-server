package com.example.controllers

import com.example.domain.models.AuthRequest
import com.example.services.AuthService
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import com.example.domain.models.*
import io.ktor.server.auth.*





fun Route.authRoutes(authService: AuthService) {
    route("/auth") {
        /**
         * Register a new user
         * @body AuthRequest { username, password }
         * @response 201 Created with token and user info
         * @response 400 Bad Request if validation fails
         * @response 409 Conflict if user exists
         */
        post("/register") {
            val request = try {
                call.receive<AuthRequest>()
            } catch (e: Exception) {
                call.respond(HttpStatusCode.BadRequest, mapOf("error" to "Invalid request format"))
                return@post
            }

            try {
                val (user, token) = authService.register(
                    username = request.username,
                    email = "${request.username}@example.com", // Simplified
                    password = request.password
                )

                call.respond(HttpStatusCode.Created, mapOf(
                    "user" to user,
                    "token" to token
                ))
            } catch (e: IllegalArgumentException) {
                call.respond(HttpStatusCode.Conflict, mapOf("error" to e.message))
            } catch (e: Exception) {
                call.respond(HttpStatusCode.InternalServerError, mapOf("error" to "Registration failed"))
            }
        }

        /**
         * Login user
         * @body AuthRequest { username, password }
         * @response 200 OK with token and user info
         * @response 401 Unauthorized if credentials invalid
         */
        post("/login") {
            val request = try {
                call.receive<AuthRequest>()
            } catch (e: Exception) {
                call.respond(HttpStatusCode.BadRequest, mapOf("error" to "Invalid request format"))
                return@post
            }

            val result = authService.login(request.username, request.password)

            if (result != null) {
                val (user, token) = result
                call.respond(mapOf(
                    "user" to user,
                    "token" to token
                ))
            } else {
                call.respond(HttpStatusCode.Unauthorized, mapOf("error" to "Invalid credentials"))
            }
        }
    }
}
package com.example.routes

import com.example.database.Users
import com.example.models.AuthRequest
import com.example.models.AuthResponse
import com.example.models.UserResponse
import com.example.auth.JwtConfig
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.authRoutes() {
    post("/register") {
        val request = call.receive<AuthRequest>()

        val existingUser = Users.findUserByUsername(request.username)
        if (existingUser != null) {
            call.respond(HttpStatusCode.Conflict, mapOf("error" to "User already exists"))
            return@post
        }

        val userId = Users.createUser(request.username, "${request.username}@example.com", request.password)
        val user = Users.findUserByUsername(request.username)!!

        val userResponse = UserResponse(user.id, user.username, user.email, user.role)
        val token = JwtConfig.makeToken(userResponse)

        call.respond(HttpStatusCode.Created, AuthResponse(token, userResponse))
    }

    post("/login") {
        val request = call.receive<AuthRequest>()

        val user = Users.authenticate(request.username, request.password)
        if (user == null) {
            call.respond(HttpStatusCode.Unauthorized, mapOf("error" to "Invalid credentials"))
            return@post
        }

        val userResponse = UserResponse(user.id, user.username, user.email, user.role)
        val token = JwtConfig.makeToken(userResponse)

        call.respond(AuthResponse(token, userResponse))
    }
}
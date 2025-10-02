package com.example.auth

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.example.models.UserResponse
import java.util.*

object JwtConfig {
    private const val secret = "your-secret-key-change-in-production"
    private const val issuer = "ktor-app"
    private const val validityInMs = 36_000_00 * 24 // 24 hours

    private val algorithm = Algorithm.HMAC256(secret)

    val verifier = JWT.require(algorithm)
        .withIssuer(issuer)
        .build()

    fun makeToken(user: UserResponse): String {
        return JWT.create()
            .withSubject("Authentication")
            .withIssuer(issuer)
            .withClaim("id", user.id)
            .withClaim("username", user.username)
            .withClaim("role", user.role)
            .withExpiresAt(getExpiration())
            .sign(algorithm)
    }

    private fun getExpiration() = Date(System.currentTimeMillis() + validityInMs)
}
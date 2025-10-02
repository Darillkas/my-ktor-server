package com.example.models

import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.Table

@Serializable
data class User(
    val id: Int,
    val username: String,
    val email: String,
    val password: String,
    val role: String
)

@Serializable
data class UserResponse(
    val id: Int,
    val username: String,
    val email: String,
    val role: String
)

object Users : Table() {
    val id = integer("id").autoIncrement()
    val username = varchar("username", 50).uniqueIndex()
    val email = varchar("email", 100).uniqueIndex()
    val password = varchar("password", 100)
    val role = varchar("role", 20)

    override val primaryKey = PrimaryKey(id)
}
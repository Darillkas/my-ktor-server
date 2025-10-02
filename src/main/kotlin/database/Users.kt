package com.example.database

import com.example.database.DatabaseFactory
import at.favre.lib.crypto.bcrypt.BCrypt
import com.example.models.User
import com.example.models.Users as UsersTable
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.selectAll

object Users {
    private fun hashPassword(password: String): String {
        return BCrypt.withDefaults().hashToString(12, password.toCharArray())
    }

    private fun verifyPassword(password: String, hash: String): Boolean {
        return BCrypt.verifyer().verify(password.toCharArray(), hash).verified
    }

    private fun rowToUser(row: ResultRow): User {
        return User(
            id = row[UsersTable.id],
            username = row[UsersTable.username],
            email = row[UsersTable.email],
            password = row[UsersTable.password],
            role = row[UsersTable.role]
        )
    }

    suspend fun createUser(username: String, email: String, password: String, role: String = "user"): Int {
        return DatabaseFactory.dbQuery {
            UsersTable.insert {
                it[UsersTable.username] = username
                it[UsersTable.email] = email
                it[UsersTable.password] = hashPassword(password)
                it[UsersTable.role] = role
            } get UsersTable.id
        }
    }

    suspend fun findUserByUsername(username: String): User? {
        return DatabaseFactory.dbQuery {
            UsersTable.select { UsersTable.username eq username }
                .map(::rowToUser)
                .singleOrNull()
        }
    }

    suspend fun authenticate(username: String, password: String): User? {
        val user = findUserByUsername(username)
        return if (user != null && verifyPassword(password, user.password)) user else null
    }

    suspend fun getAllUsers(): List<User> {
        return DatabaseFactory.dbQuery {
            UsersTable.selectAll().map(::rowToUser)
        }
    }
}

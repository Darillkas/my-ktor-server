package com.example

import io.ktor.http.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.server.testing.*
import kotlin.test.*

class ApplicationTest {

    @Test
    fun testGetAllUsers() = testApplication {
        application {
            module()
        }
        client.get("/users").apply {
            assertEquals(HttpStatusCode.OK, status)
            assertTrue(bodyAsText().contains("Alice"))
        }
    }

    @Test
    fun testGetUserById() = testApplication {
        application {
            module()
        }
        client.get("/users/1").apply {
            assertEquals(HttpStatusCode.OK, status)
            assertTrue(bodyAsText().contains("alice@example.com"))
        }
    }

    @Test
    fun testGetUserNotFound() = testApplication {
        application {
            module()
        }
        client.get("/users/999").apply {
            assertEquals(HttpStatusCode.NotFound, status)
        }
    }

    @Test
    fun testCreateUser() = testApplication {
        application {
            module()
        }
        val response = client.post("/users") {
            contentType(ContentType.Application.Json)
            setBody("""{"name":"Test User", "email":"test@example.com"}""")
        }
        assertEquals(HttpStatusCode.Created, response.status)
        assertTrue(response.bodyAsText().contains("Test User"))
    }

    @Test
    fun testDeleteUser() = testApplication {
        application {
            module()
        }
        client.delete("/users/1").apply {
            assertEquals(HttpStatusCode.NoContent, status)
        }
    }

    @Test
    fun testSearchUsers() = testApplication {
        application {
            module()
        }
        client.get("/search?name=alice").apply {
            assertEquals(HttpStatusCode.OK, status)
            assertTrue(bodyAsText().contains("Alice"))
        }
    }
}
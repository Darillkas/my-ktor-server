package com.example.routes

import com.example.models.ChatMessage
import io.ktor.server.application.*
import io.ktor.server.routing.*
import io.ktor.server.websocket.*
import io.ktor.websocket.*
import kotlinx.coroutines.launch
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.util.*
import kotlin.collections.LinkedHashSet

class Connection(val session: DefaultWebSocketSession, val username: String, val userId: Int)

object ChatServer {
    private val connections = Collections.synchronizedSet(LinkedHashSet<Connection>())

    suspend fun sendMessage(message: ChatMessage) {
        val jsonMessage = Json.encodeToString(message)
        connections.forEach {
            try {
                it.session.send(Frame.Text(jsonMessage))
            } catch (e: Exception) {
                connections.remove(it)
            }
        }
    }

    fun addConnection(connection: Connection) {
        connections.add(connection)
    }

    fun removeConnection(connection: Connection) {
        connections.remove(connection)
    }
}

fun Route.chatRoutes() {
    webSocket("/chat") {
        val connection = Connection(this, "Anonymous", 0)
        ChatServer.addConnection(connection)

        try {
            for (frame in incoming) {
                if (frame is Frame.Text) {
                    val receivedText = frame.readText()
                    val message = ChatMessage(
                        userId = connection.userId,
                        username = connection.username,
                        message = receivedText
                    )
                    launch {
                        ChatServer.sendMessage(message)
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            ChatServer.removeConnection(connection)
        }
    }
}